package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prompts")
data class PromptItem(
    @PrimaryKey val id: String,
    val promptCode: String, // e.g. "#119", "#101"
    val title: String,
    val category: String, // e.g. "Boy Prompts", "Girl Prompts", "Couple Prompts", "Islamic Prompts", "Gemini"
    val platform: String, // e.g. "Gemini", "Midjourney", "DALL-E 3", "Bing AI", "ChatGPT"
    val description: String,
    val exactPrompt: String,
    val imageUrl: String,
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val tags: String = "", // Comma-separated tags
    val createdAt: Long = System.currentTimeMillis(),
    val sourceUrl: String = ""
)

data class PromptCategory(
    val name: String,
    val iconName: String,
    val promptCount: Int = 0
)
