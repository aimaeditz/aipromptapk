package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val itemId: String,
    val itemType: String, // "PROMPT", "IMAGE", "TOOL"
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis()
)
