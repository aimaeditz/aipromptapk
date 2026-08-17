package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_tools")
data class AiTool(
    @PrimaryKey val id: String,
    val name: String,
    val iconUrl: String,
    val category: String, // "AI Image Generation", "AI Photo Editing", "AI Video", "Writing AI", "Prompt Tools", "Design", "Productivity", "Other"
    val description: String,
    val websiteUrl: String,
    val isFeatured: Boolean = false,
    val badge: String = "FREE"
)
