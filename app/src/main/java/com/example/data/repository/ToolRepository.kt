package com.example.data.repository

import com.example.data.local.FavoriteDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.PromptDao
import com.example.data.model.AiTool
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ToolRepository(
    private val promptDao: PromptDao,
    private val favoriteDao: FavoriteDao
) {
    val allTools: Flow<List<AiTool>> = promptDao.getAllTools()

    suspend fun addTool(tool: AiTool) {
        promptDao.insertTool(tool)
    }

    suspend fun deleteTool(id: String) {
        promptDao.deleteToolById(id)
    }

    suspend fun toggleFavoriteTool(tool: AiTool) {
        val isFav = favoriteDao.isFavorite(tool.id).first()
        if (isFav) {
            favoriteDao.deleteFavorite(tool.id)
        } else {
            favoriteDao.insertFavorite(
                FavoriteEntity(
                    itemId = tool.id,
                    itemType = "TOOL",
                    title = tool.name,
                    subtitle = tool.category,
                    imageUrl = tool.iconUrl
                )
            )
        }
    }
}
