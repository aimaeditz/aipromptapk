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

    @Query("SELECT * FROM prompts WHERE id = :id")
    suspend fun getPromptById(id: String): PromptItem?

    @Query("SELECT * FROM prompts WHERE title LIKE '%' || :query || '%' OR promptCode LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchPrompts(query: String): Flow<List<PromptItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompts(prompts: List<PromptItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrompt(prompt: PromptItem)

    @Query("DELETE FROM prompts WHERE id = :id")
    suspend fun deletePromptById(id: String)

    // Gallery Images
    @Query("SELECT * FROM gallery_images")
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
