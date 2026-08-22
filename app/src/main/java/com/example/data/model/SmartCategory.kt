package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Smart discovered category representation with exact real prompt counts,
 * image thumbnails from actual prompts, and pre-indexed matching prompt IDs.
 */
data class SmartCategory(
    val id: String,
    val name: String,
    val displayName: String = name,
    val description: String = "",
    val promptCount: Int = 0,
    val imageUrl: String? = null,
    val iconKey: String = "folder",
    val emoji: String = "",
    val matchedPromptIds: Set<String> = emptySet(),
    val tags: List<String> = emptyList()
)
