package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.model.AppRemoteConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * RemoteConfigRepository handles safe, non-blocking remote configuration updates.
 *
 * ARCHITECTURE:
 * 1. Immediate initialization from Local Cached SharedPreferences (0ms delay, zero UI blocking).
 * 2. Asynchronous background fetch & activation from Firebase Remote Config.
 * 3. Safe try-catch boundaries: If Firebase or network is unavailable, defaults are cleanly maintained.
 * 4. Value validation & range bounding before any remote value is applied.
 * 5. Strict security: No sensitive data/secrets are ever handled here.
 */
class RemoteConfigRepository(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val tag = "RemoteConfigRepo"
    private val prefs: SharedPreferences = context.getSharedPreferences("app_remote_config_prefs", Context.MODE_PRIVATE)

    private val _config = MutableStateFlow(loadCachedConfig())
    val config: StateFlow<AppRemoteConfig> = _config.asStateFlow()

    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null

    init {
        // Initialize Firebase Remote Config safely in background
        scope.launch(Dispatchers.IO) {
            setupFirebaseRemoteConfig()
        }
    }

    private suspend fun setupFirebaseRemoteConfig() = withContext(Dispatchers.IO) {
        try {
            // Verify FirebaseApp is initialized before accessing FirebaseRemoteConfig
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(context)
                } catch (e: Exception) {
                    Log.w(tag, "FirebaseApp could not be initialized automatically: ${e.message}")
                }
            }

            val remoteConfig = FirebaseRemoteConfig.getInstance()
            firebaseRemoteConfig = remoteConfig

            // Configure fetch interval (e.g. 1 hour for production, with fast fallback)
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .setFetchTimeoutInSeconds(10)
                .build()

            remoteConfig.setConfigSettingsAsync(configSettings)

            // Set safe in-app defaults
            val defaultMap = HashMap<String, Any>().apply {
                put(AppRemoteConfig.KEY_CONFIG_VERSION, AppRemoteConfig.DEFAULTS.configVersion)
                put(AppRemoteConfig.KEY_ENABLE_FEATURED, AppRemoteConfig.DEFAULTS.enableFeaturedPrompts)
                put(AppRemoteConfig.KEY_ENABLE_TRENDING, AppRemoteConfig.DEFAULTS.enableTrendingPrompts)
                put(AppRemoteConfig.KEY_ENABLE_GALLERY, AppRemoteConfig.DEFAULTS.enableGalleryPreview)
                put(AppRemoteConfig.KEY_ENABLE_AI_TOOLS, AppRemoteConfig.DEFAULTS.enableAiTools)
                put(AppRemoteConfig.KEY_ENABLE_AI_APPS, AppRemoteConfig.DEFAULTS.enableAiApps)
                put(AppRemoteConfig.KEY_ENABLE_CREATOR, AppRemoteConfig.DEFAULTS.enableCreatorSection)
                put(AppRemoteConfig.KEY_HOME_HERO_HEADLINE, AppRemoteConfig.DEFAULTS.homeHeroHeadline)
                put(AppRemoteConfig.KEY_HOME_HERO_SUBTITLE, AppRemoteConfig.DEFAULTS.homeHeroSubtitle)
                put(AppRemoteConfig.KEY_ANNOUNCEMENT_TEXT, AppRemoteConfig.DEFAULTS.announcementText)
                put(AppRemoteConfig.KEY_ANNOUNCEMENT_VISIBLE, AppRemoteConfig.DEFAULTS.announcementVisible)
                put(AppRemoteConfig.KEY_ANNOUNCEMENT_URL, AppRemoteConfig.DEFAULTS.announcementUrl)
                put(AppRemoteConfig.KEY_MAX_FEATURED_COUNT, AppRemoteConfig.DEFAULTS.maxFeaturedCount.toLong())
                put(AppRemoteConfig.KEY_MAX_TRENDING_COUNT, AppRemoteConfig.DEFAULTS.maxTrendingCount.toLong())
                put(AppRemoteConfig.KEY_MAX_GALLERY_COUNT, AppRemoteConfig.DEFAULTS.maxGalleryCount.toLong())
                put(AppRemoteConfig.KEY_CARD_SIZE, AppRemoteConfig.DEFAULTS.cardSizePreference)
                put(AppRemoteConfig.KEY_ANIMATION_ENABLED, AppRemoteConfig.DEFAULTS.animationEnabled)
            }
            remoteConfig.setDefaultsAsync(defaultMap)

            // Listen for Real-Time configuration updates if supported
            try {
                remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
                    override fun onUpdate(configUpdate: ConfigUpdate) {
                        remoteConfig.activate().addOnCompleteListener {
                            applyActiveConfig(remoteConfig)
                        }
                    }

                    override fun onError(error: FirebaseRemoteConfigException) {
                        Log.w(tag, "Remote config real-time update error: ${error.message}")
                    }
                })
            } catch (e: Exception) {
                Log.w(tag, "Real-time config update listener not supported in this environment: ${e.message}")
            }

            // Initial fetch & activate in background
            fetchAndActivate()

        } catch (e: Exception) {
            Log.w(tag, "Firebase Remote Config safe fallback engaged: ${e.message}")
        }
    }

    /**
     * Silently fetches and activates the latest remote configuration.
     * Never throws exceptions or blocks the caller.
     */
    fun fetchAndActivate() {
        val rc = firebaseRemoteConfig ?: return
        try {
            rc.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    applyActiveConfig(rc)
                } else {
                    Log.d(tag, "Remote config fetch completed without updates, using current cached config.")
                }
            }
        } catch (e: Exception) {
            Log.w(tag, "Silent fetchAndActivate handled error: ${e.message}")
        }
    }

    /**
     * Reads active values from FirebaseRemoteConfig, sanitizes and validates all values,
     * updates the StateFlow, and caches them to SharedPreferences.
     */
    private fun applyActiveConfig(rc: FirebaseRemoteConfig) {
        try {
            val rawVersion = rc.getLong(AppRemoteConfig.KEY_CONFIG_VERSION)
            val rawEnableFeatured = rc.getBoolean(AppRemoteConfig.KEY_ENABLE_FEATURED)
            val rawEnableTrending = rc.getBoolean(AppRemoteConfig.KEY_ENABLE_TRENDING)
            val rawEnableGallery = rc.getBoolean(AppRemoteConfig.KEY_ENABLE_GALLERY)
            val rawEnableTools = rc.getBoolean(AppRemoteConfig.KEY_ENABLE_AI_TOOLS)
            val rawEnableApps = rc.getBoolean(AppRemoteConfig.KEY_ENABLE_AI_APPS)
            val rawEnableCreator = rc.getBoolean(AppRemoteConfig.KEY_ENABLE_CREATOR)
            val rawHeroHeadline = rc.getString(AppRemoteConfig.KEY_HOME_HERO_HEADLINE)
            val rawHeroSubtitle = rc.getString(AppRemoteConfig.KEY_HOME_HERO_SUBTITLE)
            val rawAnnouncement = rc.getString(AppRemoteConfig.KEY_ANNOUNCEMENT_TEXT)
            val rawAnnouncementVisible = rc.getBoolean(AppRemoteConfig.KEY_ANNOUNCEMENT_VISIBLE)
            val rawAnnouncementUrl = rc.getString(AppRemoteConfig.KEY_ANNOUNCEMENT_URL)
            val rawMaxFeatured = rc.getLong(AppRemoteConfig.KEY_MAX_FEATURED_COUNT)
            val rawMaxTrending = rc.getLong(AppRemoteConfig.KEY_MAX_TRENDING_COUNT)
            val rawMaxGallery = rc.getLong(AppRemoteConfig.KEY_MAX_GALLERY_COUNT)
            val rawCardSize = rc.getString(AppRemoteConfig.KEY_CARD_SIZE)
            val rawAnimation = rc.getBoolean(AppRemoteConfig.KEY_ANIMATION_ENABLED)

            val validatedConfig = AppRemoteConfig.sanitize(
                version = rawVersion,
                enableFeatured = rawEnableFeatured,
                enableTrending = rawEnableTrending,
                enableGallery = rawEnableGallery,
                enableTools = rawEnableTools,
                enableApps = rawEnableApps,
                enableCreator = rawEnableCreator,
                heroHeadline = rawHeroHeadline,
                heroSubtitle = rawHeroSubtitle,
                announcement = rawAnnouncement,
                announcementVisible = rawAnnouncementVisible,
                announcementUrl = rawAnnouncementUrl,
                maxFeatured = rawMaxFeatured,
                maxTrending = rawMaxTrending,
                maxGallery = rawMaxGallery,
                cardSize = rawCardSize,
                animations = rawAnimation
            )

            _config.value = validatedConfig
            saveCachedConfig(validatedConfig)
        } catch (e: Exception) {
            Log.e(tag, "Failed to apply remote config, keeping previous config: ${e.message}")
        }
    }

    private fun loadCachedConfig(): AppRemoteConfig {
        return try {
            AppRemoteConfig(
                configVersion = prefs.getLong(AppRemoteConfig.KEY_CONFIG_VERSION, 1L),
                enableFeaturedPrompts = prefs.getBoolean(AppRemoteConfig.KEY_ENABLE_FEATURED, true),
                enableTrendingPrompts = prefs.getBoolean(AppRemoteConfig.KEY_ENABLE_TRENDING, true),
                enableGalleryPreview = prefs.getBoolean(AppRemoteConfig.KEY_ENABLE_GALLERY, true),
                enableAiTools = prefs.getBoolean(AppRemoteConfig.KEY_ENABLE_AI_TOOLS, true),
                enableAiApps = prefs.getBoolean(AppRemoteConfig.KEY_ENABLE_AI_APPS, true),
                enableCreatorSection = prefs.getBoolean(AppRemoteConfig.KEY_ENABLE_CREATOR, true),
                homeHeroHeadline = prefs.getString(AppRemoteConfig.KEY_HOME_HERO_HEADLINE, AppRemoteConfig.DEFAULTS.homeHeroHeadline)
                    ?: AppRemoteConfig.DEFAULTS.homeHeroHeadline,
                homeHeroSubtitle = prefs.getString(AppRemoteConfig.KEY_HOME_HERO_SUBTITLE, AppRemoteConfig.DEFAULTS.homeHeroSubtitle)
                    ?: AppRemoteConfig.DEFAULTS.homeHeroSubtitle,
                announcementText = prefs.getString(AppRemoteConfig.KEY_ANNOUNCEMENT_TEXT, "") ?: "",
                announcementVisible = prefs.getBoolean(AppRemoteConfig.KEY_ANNOUNCEMENT_VISIBLE, false),
                announcementUrl = prefs.getString(AppRemoteConfig.KEY_ANNOUNCEMENT_URL, "") ?: "",
                maxFeaturedCount = prefs.getInt(AppRemoteConfig.KEY_MAX_FEATURED_COUNT, 8),
                maxTrendingCount = prefs.getInt(AppRemoteConfig.KEY_MAX_TRENDING_COUNT, 6),
                maxGalleryCount = prefs.getInt(AppRemoteConfig.KEY_MAX_GALLERY_COUNT, 6),
                cardSizePreference = prefs.getString(AppRemoteConfig.KEY_CARD_SIZE, "compact") ?: "compact",
                animationEnabled = prefs.getBoolean(AppRemoteConfig.KEY_ANIMATION_ENABLED, true)
            )
        } catch (e: Exception) {
            AppRemoteConfig.DEFAULTS
        }
    }

    private fun saveCachedConfig(config: AppRemoteConfig) {
        try {
            prefs.edit()
                .putLong(AppRemoteConfig.KEY_CONFIG_VERSION, config.configVersion)
                .putBoolean(AppRemoteConfig.KEY_ENABLE_FEATURED, config.enableFeaturedPrompts)
                .putBoolean(AppRemoteConfig.KEY_ENABLE_TRENDING, config.enableTrendingPrompts)
                .putBoolean(AppRemoteConfig.KEY_ENABLE_GALLERY, config.enableGalleryPreview)
                .putBoolean(AppRemoteConfig.KEY_ENABLE_AI_TOOLS, config.enableAiTools)
                .putBoolean(AppRemoteConfig.KEY_ENABLE_AI_APPS, config.enableAiApps)
                .putBoolean(AppRemoteConfig.KEY_ENABLE_CREATOR, config.enableCreatorSection)
                .putString(AppRemoteConfig.KEY_HOME_HERO_HEADLINE, config.homeHeroHeadline)
                .putString(AppRemoteConfig.KEY_HOME_HERO_SUBTITLE, config.homeHeroSubtitle)
                .putString(AppRemoteConfig.KEY_ANNOUNCEMENT_TEXT, config.announcementText)
                .putBoolean(AppRemoteConfig.KEY_ANNOUNCEMENT_VISIBLE, config.announcementVisible)
                .putString(AppRemoteConfig.KEY_ANNOUNCEMENT_URL, config.announcementUrl)
                .putInt(AppRemoteConfig.KEY_MAX_FEATURED_COUNT, config.maxFeaturedCount)
                .putInt(AppRemoteConfig.KEY_MAX_TRENDING_COUNT, config.maxTrendingCount)
                .putInt(AppRemoteConfig.KEY_MAX_GALLERY_COUNT, config.maxGalleryCount)
                .putString(AppRemoteConfig.KEY_CARD_SIZE, config.cardSizePreference)
                .putBoolean(AppRemoteConfig.KEY_ANIMATION_ENABLED, config.animationEnabled)
                .apply()
        } catch (e: Exception) {
            Log.e(tag, "Failed to cache remote config: ${e.message}")
        }
    }
}
