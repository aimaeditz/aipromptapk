package com.example.data.local

import com.example.data.remote.BloggerApiService
import com.example.data.remote.BloggerDataSource
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class PromptRepository(
    private val promptDao: PromptDao,
    private val favoriteDao: FavoriteDao,
    private val bloggerApiService: BloggerApiService? = null
) {
    val allPrompts: Flow<List<PromptItem>> = promptDao.getAllPrompts()
    val featuredPrompts: Flow<List<PromptItem>> = promptDao.getFeaturedPrompts()
    val trendingPrompts: Flow<List<PromptItem>> = promptDao.getTrendingPrompts()
    val allGalleryImages: Flow<List<GalleryImage>> = promptDao.getAllGalleryImages()
    val allFavorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()

    suspend fun initializeSeedData() = withContext(Dispatchers.IO) {
        val existingPrompts = promptDao.getAllPrompts().first()
        if (existingPrompts.isEmpty()) {
            promptDao.insertPrompts(InitialSeedData.PROMPTS)
            promptDao.insertGalleryImages(InitialSeedData.GALLERY_IMAGES)
            promptDao.insertTools(InitialSeedData.TOOLS)
            promptDao.insertTutorials(InitialSeedData.TUTORIALS)
        }
    }

    fun getPromptsByCategory(category: String): Flow<List<PromptItem>> {
        return promptDao.getPromptsByCategory(category)
    }

    suspend fun getPromptById(id: String): PromptItem? = withContext(Dispatchers.IO) {
        return@withContext promptDao.getPromptById(id)
    }

    fun searchPrompts(query: String): Flow<List<PromptItem>> {
        return promptDao.searchPrompts(query)
    }

    suspend fun savePrompt(prompt: PromptItem) = withContext(Dispatchers.IO) {
        promptDao.insertPrompt(prompt)
    }

    suspend fun deletePrompt(id: String) = withContext(Dispatchers.IO) {
        promptDao.deletePromptById(id)
    }

    fun isFavorite(itemId: String): Flow<Boolean> {
        return favoriteDao.isFavorite(itemId)
    }

    suspend fun toggleFavorite(prompt: PromptItem) = withContext(Dispatchers.IO) {
        val isFav = favoriteDao.isFavorite(prompt.id).first()
        if (isFav) {
            favoriteDao.deleteFavorite(prompt.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    itemId = prompt.id,
                    itemType = "PROMPT",
                    title = prompt.title,
                    subtitle = "${prompt.promptCode} • ${prompt.category}",
                    imageUrl = prompt.imageUrl
                )
            )
        }
    }

    /**
     * High-speed, non-blocking silent Blogger delta sync.
     * Compares remote items against local cache and writes only new/updated entries.
     */
    suspend fun syncBloggerFeeds(): Int = withContext(Dispatchers.IO) {
        if (bloggerApiService == null) return@withContext 0
        val dataSource = BloggerDataSource(bloggerApiService)

        val feedUrls = listOf(
            "https://aimaeditz.blogspot.com/feeds/posts/default?alt=json",
            "https://aipromptxpert.blogspot.com/feeds/posts/default?alt=json"
        )

        var totalSynced = 0
        val currentPrompts = promptDao.getAllPrompts().first()
        val currentPromptMap = currentPrompts.associateBy { it.id }

        for (url in feedUrls) {
            val remotePrompts = dataSource.fetchPromptsFromUrl(url)
            if (remotePrompts.isNotEmpty()) {
                val newOrUpdated = remotePrompts.filter { remote ->
                    val existing = currentPromptMap[remote.id]
                    existing == null || existing.exactPrompt != remote.exactPrompt || existing.imageUrl != remote.imageUrl
                }

                if (newOrUpdated.isNotEmpty()) {
                    promptDao.insertPrompts(newOrUpdated)
                    totalSynced += newOrUpdated.size
                }
            }
        }
        return@withContext totalSynced
    }
}
