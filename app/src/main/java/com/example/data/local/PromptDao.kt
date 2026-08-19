package com.example.data.local

import androidx.room.*
import com.example.data.model.AiTool
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import com.example.data.model.TutorialItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao {
    @Query("SELECT * FROM prompts ORDER BY createdAt DESC")
    fun getAllPrompts(): Flow<List<PromptItem>>

    @Query("SELECT * FROM prompts WHERE isFeatured = 1 ORDER BY createdAt DESC LIMIT 12")
    fun getFeaturedPrompts(): Flow<List<PromptItem>>

    @Query("SELECT * FROM prompts WHERE isTrending = 1 ORDER BY createdAt DESC LIMIT 12")
    fun getTrendingPrompts(): Flow<List<PromptItem>>

    @Query("SELECT * FROM prompts WHERE category = :category ORDER BY createdAt DESC")
    fun getPromptsByCategory(category: String): Flow<List<PromptItem>>

    @Query("SELECT DISTINCT category FROM prompts WHERE category != '' ORDER BY category ASC")
    fun getDistinctCategories(): Flow<List<String>>

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: String): PromptItem?

    @Query("SELECT * FROM prompts WHERE title LIKE '%' || :query || '%' OR promptCode LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR exactPrompt LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchPrompts(query: String): Flow<List<PromptItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(prompts: List<PromptItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptItem)

    @Query("DELETE FROM prompts WHERE id = :id")
    suspend fun deletePromptById(id: String)

    @Query("DELETE FROM prompts WHERE imageUrl LIKE '%unsplash.com%' OR id LIKE 'prompt_10%' OR id LIKE 'prompt_11%' OR id = 'prompt_119'")
    suspend fun purgeFakeMockPrompts()

    @Query("DELETE FROM gallery_images WHERE imageUrl LIKE '%unsplash.com%' OR id LIKE 'gallery_1%' OR id LIKE 'gallery_2%' OR id LIKE 'gallery_3%' OR id LIKE 'gallery_4%' OR id LIKE 'gallery_5%'")
    suspend fun purgeFakeMockGallery()

    @Query("DELETE FROM prompts WHERE sourceUrl LIKE '%aimaeditz%' OR id LIKE '%aimaeditz%'")
    suspend fun purgeAimaeditzPrompts()

    @Query("DELETE FROM gallery_images WHERE id LIKE '%aimaeditz%'")
    suspend fun purgeAimaeditzGallery()

    @Query("DELETE FROM prompts WHERE id NOT IN (SELECT MIN(id) FROM prompts GROUP BY exactPrompt)")
    suspend fun purgeDuplicatePromptsByText()

    @Query("DELETE FROM prompts")
    suspend fun clearAllPrompts()

    @Query("DELETE FROM gallery_images")
    suspend fun clearAllGalleryImages()

    // Gallery Images
    @Query("SELECT * FROM gallery_images ORDER BY id DESC")
    fun getAllGalleryImages(): Flow<List<GalleryImage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGalleryImages(images: List<GalleryImage>)

    // AI Tools
    @Query("SELECT * FROM ai_tools")
    fun getAllTools(): Flow<List<AiTool>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTools(tools: List<AiTool>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTool(tool: AiTool)

    @Query("DELETE FROM ai_tools WHERE id = :id")
    suspend fun deleteToolById(id: String)

    // Tutorials
    @Query("SELECT * FROM tutorials")
    fun getAllTutorials(): Flow<List<TutorialItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTutorials(tutorials: List<TutorialItem>)
}
