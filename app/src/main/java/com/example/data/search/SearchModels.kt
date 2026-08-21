package com.example.data.search

import com.example.data.model.AiTool
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem

/**
 * Structured search intent extracted from natural language queries.
 */
data class SearchIntent(
    val query: String,
    val subject: String? = null,
    val style: String? = null,
    val type: String? = null,
    val platform: String? = null,
    val keywords: List<String> = emptyList(),
    val categories: List<String> = emptyList()
)

/**
 * Result item with ranking score and explanation.
 */
sealed class SearchResultItem(open val relevanceScore: Float) {
    data class PromptResult(
        val prompt: PromptItem,
        override val relevanceScore: Float,
        val matchType: String
    ) : SearchResultItem(relevanceScore)

    data class ImageResult(
        val image: GalleryImage,
        override val relevanceScore: Float
    ) : SearchResultItem(relevanceScore)

    data class CategoryResult(
        val category: String,
        val promptCount: Int,
        override val relevanceScore: Float
    ) : SearchResultItem(relevanceScore)

    data class ToolResult(
        val tool: AiTool,
        override val relevanceScore: Float
    ) : SearchResultItem(relevanceScore)
}

/**
 * Unified Search Results encompassing all content types.
 */
data class UnifiedSearchResults(
    val query: String,
    val intent: SearchIntent? = null,
    val topPrompts: List<PromptItem> = emptyList(),
    val topImages: List<GalleryImage> = emptyList(),
    val matchingCategories: List<String> = emptyList(),
    val matchingTools: List<AiTool> = emptyList(),
    val smartSuggestions: List<String> = emptyList(),
    val isAiInterpreted: Boolean = false,
    val fallbackUsed: Boolean = false,
    val totalCount: Int = 0
)

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

