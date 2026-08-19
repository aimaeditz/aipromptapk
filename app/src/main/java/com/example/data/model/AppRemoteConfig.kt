package com.example.data.model

/**
 * Non-sensitive Remote Configuration Model for AiPromptXpert.
 *
 * SECURITY GUARANTEE:
 * This model contains strictly public, non-sensitive UI/feature flags and preferences.
 * Passwords, private tokens, API keys, credentials, and personal information are strictly forbidden.
 */
data class AppRemoteConfig(
    val configVersion: Long = 1L,
    val enableFeaturedPrompts: Boolean = true,
    val enableTrendingPrompts: Boolean = true,
    val enableGalleryPreview: Boolean = true,
    val enableAiTools: Boolean = true,
    val enableAiApps: Boolean = true,
    val enableCreatorSection: Boolean = true,
    val homeHeroHeadline: String = "CREATE BEYOND IMAGINATION",
    val homeHeroSubtitle: String = "Discover premium AI prompts, cinematic photo concepts, Gemini guides & creative tools.",
    val announcementText: String = "",
    val announcementVisible: Boolean = false,
    val announcementUrl: String = "",
    val maxFeaturedCount: Int = 8,
    val maxTrendingCount: Int = 6,
    val maxGalleryCount: Int = 6,
    val cardSizePreference: String = "compact",
    val animationEnabled: Boolean = true
) {
    companion object {
        val DEFAULTS = AppRemoteConfig()

        // Remote Config Key Constants
        const val KEY_CONFIG_VERSION = "remote_config_version"
        const val KEY_ENABLE_FEATURED = "enable_featured_prompts"
        const val KEY_ENABLE_TRENDING = "enable_trending_prompts"
        const val KEY_ENABLE_GALLERY = "enable_gallery_preview"
        const val KEY_ENABLE_AI_TOOLS = "enable_ai_tools"
        const val KEY_ENABLE_AI_APPS = "enable_ai_apps"
        const val KEY_ENABLE_CREATOR = "enable_creator_section"
        const val KEY_HOME_HERO_HEADLINE = "home_hero_headline"
        const val KEY_HOME_HERO_SUBTITLE = "home_hero_subtitle"
        const val KEY_ANNOUNCEMENT_TEXT = "announcement_text"
        const val KEY_ANNOUNCEMENT_VISIBLE = "announcement_visible"
        const val KEY_ANNOUNCEMENT_URL = "announcement_url"
        const val KEY_MAX_FEATURED_COUNT = "max_featured_count"
        const val KEY_MAX_TRENDING_COUNT = "max_trending_count"
        const val KEY_MAX_GALLERY_COUNT = "max_gallery_count"
        const val KEY_CARD_SIZE = "home_card_size"
        const val KEY_ANIMATION_ENABLED = "animation_enabled"

        /**
         * Safely validates and bounds values received from Remote Config
         * to prevent crashes or extreme layout breakage.
         */
        fun sanitize(
            version: Long,
            enableFeatured: Boolean,
            enableTrending: Boolean,
            enableGallery: Boolean,
            enableTools: Boolean,
            enableApps: Boolean,
            enableCreator: Boolean,
            heroHeadline: String,
            heroSubtitle: String,
            announcement: String,
            announcementVisible: Boolean,
            announcementUrl: String,
            maxFeatured: Long,
            maxTrending: Long,
            maxGallery: Long,
            cardSize: String,
            animations: Boolean
        ): AppRemoteConfig {
            return AppRemoteConfig(
                configVersion = if (version > 0) version else 1L,
                enableFeaturedPrompts = enableFeatured,
                enableTrendingPrompts = enableTrending,
                enableGalleryPreview = enableGallery,
                enableAiTools = enableTools,
                enableAiApps = enableApps,
                enableCreatorSection = enableCreator,
                homeHeroHeadline = heroHeadline.ifBlank { DEFAULTS.homeHeroHeadline }.take(80),
                homeHeroSubtitle = heroSubtitle.ifBlank { DEFAULTS.homeHeroSubtitle }.take(180),
                announcementText = announcement.trim().take(150),
                announcementVisible = announcementVisible && announcement.isNotBlank(),
                announcementUrl = announcementUrl.trim(),
                maxFeaturedCount = maxFeatured.toInt().coerceIn(1, 50),
                maxTrendingCount = maxTrending.toInt().coerceIn(1, 50),
                maxGalleryCount = maxGallery.toInt().coerceIn(1, 50),
                cardSizePreference = if (cardSize in listOf("compact", "default", "small")) cardSize else "compact",
                animationEnabled = animations
            )
        }
    }
}
