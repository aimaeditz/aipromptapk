package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tutorials")
data class TutorialItem(
    @PrimaryKey val id: String,
    val title: String,
    val coverImageUrl: String,
    val introduction: String,
    val stepsJson: String, // List of step strings stored as JSON or newline delimited
    val relatedPromptId: String = "",
    val category: String = "AI Photo Editing",
    val sourceUrl: String = ""
)
