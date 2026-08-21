package com.example.data.search

import com.example.data.model.AiTool
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchService(
    private val semanticSearchService: SemanticSearchService = SemanticSearchService()
) {

    /**
     * Executes unified search across all app contents using hybrid semantic + keyword + fuzzy pipeline.
     */
    fun performSearch(
        query: String,
        allPrompts: List<PromptItem>,
        allImages: List<GalleryImage>,
        allTools: List<AiTool>
    ): Flow<UnifiedSearchResults> = flow {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            emit(SearchRankingService.rankResults("", SearchIntent(""), allPrompts, allImages, allTools, false))
            return@flow
        }

        // Step 1: Immediate local heuristic search for 0ms instant UI feedback
        val localIntent = semanticSearchService.extractHeuristicIntent(trimmed)
        val initialResults = SearchRankingService.rankResults(
            query = trimmed,
            intent = localIntent,
            allPrompts = allPrompts,
            allImages = allImages,
            allTools = allTools,
            isAiInterpreted = false
        )
        emit(initialResults)

        // Step 2: Advanced AI semantic analysis (Gemini fallback layer) if query is descriptive (> 4 characters)
        if (trimmed.length > 4 && trimmed.contains(" ")) {
            try {
                val aiIntent = semanticSearchService.parseIntent(trimmed)
                if (aiIntent != localIntent) {
                    val aiResults = SearchRankingService.rankResults(
                        query = trimmed,
                        intent = aiIntent,
                        allPrompts = allPrompts,
                        allImages = allImages,
                        allTools = allTools,
                        isAiInterpreted = true
                    )
                    emit(aiResults)
                }
            } catch (_: Exception) {
                // Keep initial results seamlessly
            }
        }
    }
}
