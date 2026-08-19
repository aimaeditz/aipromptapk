package com.example.data.repository

import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.InitialSeedData
import com.example.data.local.PromptDao
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import com.example.data.remote.BloggerDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient

class PromptRepository(
    private val promptDao: PromptDao,
    private val favoriteDao: FavoriteDao,
    private val okHttpClient: OkHttpClient
) {
    val allPrompts: Flow<List<PromptItem>> = promptDao.getAllPrompts()
    val featuredPrompts: Flow<List<PromptItem>> = promptDao.getFeaturedPrompts()
    val trendingPrompts: Flow<List<PromptItem>> = promptDao.getTrendingPrompts()
    val allGalleryImages: Flow<List<GalleryImage>> = promptDao.getAllGalleryImages()
    val allFavorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites()
    val distinctCategories: Flow<List<String>> = promptDao.getDistinctCategories()

    private val bloggerDataSource = BloggerDataSource(okHttpClient)

    suspend fun initializeSeedData() = withContext(Dispatchers.IO) {
        // 1. Purge any legacy sample data, mock data, and remove the other Blogger source completely
        promptDao.purgeFakeMockPrompts()
        promptDao.purgeFakeMockGallery()
        promptDao.purgeAimaeditzPrompts()
        promptDao.purgeAimaeditzGallery()
        promptDao.purgeDuplicatePromptsByText()

        // 2. Initialize tools and tutorials if not yet present
        val existingTools = promptDao.getAllTools().first()
        if (existingTools.isEmpty()) {
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
     * Automatic silent background sync from Blogger:
     * SOLE SOURCE OF TRUTH: https://aipromptxpert.blogspot.com/feeds/posts/default
     *
     * Imports all real prompts from oldest to newest.
     * Prevents duplicate prompts by enforcing unique exact prompt text and ID.
     */
    suspend fun syncBloggerFeeds(): Int = withContext(Dispatchers.IO) {
        val singleFeedUrl = "https://aipromptxpert.blogspot.com/feeds/posts/default"

        var totalImported = 0
        val currentPrompts = promptDao.getAllPrompts().first()
        val currentMap = currentPrompts.associateBy { it.id }
        val currentPromptTexts = currentPrompts.map { it.exactPrompt.trim().lowercase() }.toMutableSet()

        val allFetchedPrompts = mutableListOf<PromptItem>()
        val allFetchedGallery = mutableListOf<GalleryImage>()

        try {
            val (prompts, gallery) = bloggerDataSource.fetchPromptsFromFeedUrl(singleFeedUrl)
            
            // Deduplicate fetched prompts in-memory so each prompt is unique
            val seenInBatch = mutableSetOf<String>()
            for (p in prompts) {
                val normalizedText = p.exactPrompt.trim().lowercase()
                if (normalizedText.isNotBlank() && seenInBatch.add(normalizedText)) {
                    allFetchedPrompts.add(p)
                }
            }

            val seenGalleryUrls = mutableSetOf<String>()
            for (g in gallery) {
                if (g.imageUrl.isNotBlank() && seenGalleryUrls.add(g.imageUrl)) {
                    allFetchedGallery.add(g)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (allFetchedPrompts.isNotEmpty()) {
            // Filter only genuinely new or updated prompts to prevent unnecessary DB writes
            val newOrUpdatedPrompts = allFetchedPrompts.filter { fetched ->
                val existing = currentMap[fetched.id]
                val normalized = fetched.exactPrompt.trim().lowercase()
                existing == null || existing.exactPrompt != fetched.exactPrompt || existing.imageUrl != fetched.imageUrl
            }

            if (newOrUpdatedPrompts.isNotEmpty()) {
                promptDao.insertPrompts(newOrUpdatedPrompts)
                totalImported += newOrUpdatedPrompts.size
            }

            if (allFetchedGallery.isNotEmpty()) {
                promptDao.insertGalleryImages(allFetchedGallery)
            }

            // Cleanup any duplicate entries if they ever existed
            promptDao.purgeDuplicatePromptsByText()
        }

        return@withContext totalImported
    }
}
