package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gallery_images")
data class GalleryImage(
    @PrimaryKey val id: String,
    val title: String,
    val imageUrl: String,
    val promptId: String,
    val promptCode: String,
    val category: String,
    val exactPrompt: String,
    val tags: String = ""
)
