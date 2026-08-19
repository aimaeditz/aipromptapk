package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.FavoriteEntity
import com.example.data.model.*
import com.example.data.repository.AdMobConfigRepository
import com.example.data.repository.PromptRepository
import com.example.data.repository.RemoteConfigRepository
import com.example.data.repository.ToolRepository
import com.example.data.repository.TutorialRepository
import com.example.ui.theme.AppThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import java.util.UUID
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    private val db = AppDatabase.getDatabase(application)
    private val promptRepository by lazy {
        PromptRepository(db.promptDao(), db.favoriteDao(), okHttpClient)
    }
    private val toolRepository = ToolRepository(db.promptDao(), db.favoriteDao())
    private val tutorialRepository = TutorialRepository(db.promptDao())
    val adMobConfigRepository = AdMobConfigRepository()
    private val remoteConfigRepository = RemoteConfigRepository(application, viewModelScope)

    // Remote Configuration State Flow (Safe, validated, local-first with background updates)
    val remoteConfig: StateFlow<AppRemoteConfig> = remoteConfigRepository.config

    // Offline / Network Connectivity State
    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> = _isOffline.asStateFlow()

    // Theme Mode State
    private val _themeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Selected Category & Platform Filters
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedPlatform = MutableStateFlow("All")
    val selectedPlatform: StateFlow<String> = _selectedPlatform.asStateFlow()

    // Selected Prompt for Detail Screen
    private val _selectedPrompt = MutableStateFlow<PromptItem?>(null)
    val selectedPrompt: StateFlow<PromptItem?> = _selectedPrompt.asStateFlow()

    // Selected Fullscreen Image for Gallery Viewer
    private val _selectedGalleryImage = MutableStateFlow<GalleryImage?>(null)
    val selectedGalleryImage: StateFlow<GalleryImage?> = _selectedGalleryImage.asStateFlow()

    // Admin / CMS Auth State
    private val _isCreatorAuthenticated = MutableStateFlow(false)
    val isCreatorAuthenticated: StateFlow<Boolean> = _isCreatorAuthenticated.asStateFlow()

    // Data Flows from Repository (Instant Room Cache Emission)
    val allPrompts: StateFlow<List<PromptItem>> = promptRepository.allPrompts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val featuredPrompts: StateFlow<List<PromptItem>> = promptRepository.featuredPrompts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val trendingPrompts: StateFlow<List<PromptItem>> = promptRepository.trendingPrompts
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val galleryImages: StateFlow<List<GalleryImage>> = promptRepository.allGalleryImages
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val aiTools: StateFlow<List<AiTool>> = toolRepository.allTools
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val tutorials: StateFlow<List<TutorialItem>> = tutorialRepository.allTutorials
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favorites: StateFlow<List<FavoriteEntity>> = promptRepository.allFavorites
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val distinctCategories: StateFlow<List<String>> = promptRepository.distinctCategories
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // Pull-to-refresh / manual refresh state
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    // Sync Status Text
    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    init {
        monitorNetworkConnectivity()

        viewModelScope.launch {
            // 1. Initialize local cache and purge any legacy fake mock data
            promptRepository.initializeSeedData()

            // 2. Perform initial silent background Blogger synchronization
            syncBloggerContentSilently()

            // 3. Periodic background sync loop (every 3 minutes) for future Blogger posts
            while (isActive) {
                delay(180_000L) // 3 minutes
                if (isInternetAvailable()) {
                    try {
                        promptRepository.syncBloggerFeeds()
                    } catch (_: Exception) {}
                }
            }
        }
    }

    fun refreshContent(showToast: Boolean = false) {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                if (isInternetAvailable()) {
                    remoteConfigRepository.fetchAndActivate()
                    val count = promptRepository.syncBloggerFeeds()
                    if (showToast) {
                        if (count > 0) {
                            Toast.makeText(getApplication(), "Synced $count new prompts ✓", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(getApplication(), "Content is up to date ✓", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    _isOffline.value = true
                    if (showToast) {
                        Toast.makeText(getApplication(), "Offline — Showing cached prompts", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                delay(300L) // smooth refresh animation completion
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun syncBloggerContentSilently() {
        try {
            if (isInternetAvailable()) {
                promptRepository.syncBloggerFeeds()
            } else {
                _isOffline.value = true
            }
        } catch (_: Exception) {
            _isOffline.value = true
        }
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        val activeNetwork = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun monitorNetworkConnectivity() {
        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return
        val builder = NetworkRequest.Builder()
        try {
            cm.registerNetworkCallback(
                builder.build(),
                object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        _isOffline.value = false
                        viewModelScope.launch {
                            try {
                                remoteConfigRepository.fetchAndActivate()
                                promptRepository.syncBloggerFeeds()
                            } catch (_: Exception) {}
                        }
                    }

                    override fun onLost(network: Network) {
                        _isOffline.value = true
                    }
                }
            )
        } catch (_: Exception) {}
    }

    fun setThemeMode(mode: AppThemeMode) {
        _themeMode.value = mode
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setPlatform(platform: String) {
        _selectedPlatform.value = platform
    }

    fun selectPrompt(prompt: PromptItem?) {
        _selectedPrompt.value = prompt
    }

    fun selectGalleryImage(image: GalleryImage?) {
        _selectedGalleryImage.value = image
    }

    // Copy Prompt Function - Strictly copies ONLY the actual clean prompt text
    fun copyPromptToClipboard(promptText: String) {
        val cleanPrompt = promptText.trim()
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("AI Prompt", cleanPrompt)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(
            getApplication(),
            "Prompt Copied ✓",
            Toast.LENGTH_SHORT
        ).show()
    }

    fun toggleFavorite(prompt: PromptItem) {
        viewModelScope.launch {
            promptRepository.toggleFavorite(prompt)
        }
    }

    fun toggleFavoriteTool(tool: AiTool) {
        viewModelScope.launch {
            toolRepository.toggleFavoriteTool(tool)
        }
    }

    // Admin / Creator CMS Login
    fun authenticateCreator(pin: String): Boolean {
        if (pin == "2900" || pin == "abid") {
            _isCreatorAuthenticated.value = true
            Toast.makeText(getApplication(), "Creator CMS Authenticated", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Toast.makeText(getApplication(), "Invalid Creator PIN", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun logoutCreator() {
        _isCreatorAuthenticated.value = false
    }

    // Creator CMS Actions
    fun addNewPrompt(
        promptCode: String,
        title: String,
        category: String,
        platform: String,
        description: String,
        exactPrompt: String,
        imageUrl: String
    ) {
        viewModelScope.launch {
            val id = "prompt_${UUID.randomUUID()}"
            val newPrompt = PromptItem(
                id = id,
                promptCode = if (promptCode.startsWith("#")) promptCode else "#$promptCode",
                title = title,
                category = category,
                platform = platform,
                description = description,
                exactPrompt = exactPrompt,
                imageUrl = imageUrl,
                isFeatured = true,
                isTrending = true,
                tags = "$category, Creator"
            )
            promptRepository.savePrompt(newPrompt)
            Toast.makeText(getApplication(), "Prompt added to library", Toast.LENGTH_SHORT).show()
        }
    }

    fun deletePrompt(id: String) {
        viewModelScope.launch {
            promptRepository.deletePrompt(id)
            Toast.makeText(getApplication(), "Prompt removed", Toast.LENGTH_SHORT).show()
        }
    }

    fun syncBloggerContent() {
        viewModelScope.launch {
            _syncMessage.value = "Synchronizing Blogger feeds..."
            val count = promptRepository.syncBloggerFeeds()
            if (count > 0) {
                _syncMessage.value = "Successfully synced $count new prompts from Blogger!"
                Toast.makeText(getApplication(), "Synced $count prompts from Blogger", Toast.LENGTH_SHORT).show()
            } else {
                _syncMessage.value = "All content up to date!"
                Toast.makeText(getApplication(), "All content up to date", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
