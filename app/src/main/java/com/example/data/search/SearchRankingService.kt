package com.example.data.search

import com.example.data.model.AiTool
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem

object SearchRankingService {

    /**
     * Executes ranking across prompts, gallery images, categories, and AI tools.
     */
    fun rankResults(
        query: String,
        intent: SearchIntent,
        allPrompts: List<PromptItem>,
        allImages: List<GalleryImage>,
        allTools: List<AiTool>,
        isAiInterpreted: Boolean
    ): UnifiedSearchResults {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return UnifiedSearchResults(
                query = "",
                smartSuggestions = listOf(
                    "3D Avatar",
                    "Boy Kurta",
                    "Girl Portrait",
                    "Islamic Mosque",
                    "Luxury Car",
                    "Cinematic 8K",
                    "Gemini Photo Edit",
                    "Midjourney Character"
                )
            )
        }

        val qLower = trimmed.lowercase()
        val keywords = intent.keywords.map { it.lowercase() }

        // --- 1. Rank Prompts ---
        val scoredPrompts = allPrompts.map { prompt ->
            val titleLower = prompt.title.lowercase()
            val promptLower = prompt.exactPrompt.lowercase()
            val categoryLower = prompt.category.lowercase()
            val platformLower = prompt.platform.lowercase()
            val codeLower = prompt.promptCode.lowercase()
            val tagsLower = prompt.tags.lowercase()
            val descLower = prompt.description.lowercase()

            var score = 0f
            var matchReason = ""

            // Exact Prompt Code match (e.g. #119)
            if (codeLower.contains(qLower) || qLower.contains(codeLower.removePrefix("#"))) {
                score += 150f
                matchReason = "Prompt Code"
            }

            // Exact Title match
            if (titleLower.contains(qLower)) {
                score += 100f
                matchReason = "Title match"
            }

            // Semantic subject match
            if (intent.subject != null) {
                val subjLower = intent.subject.lowercase()
                if (titleLower.contains(subjLower) || categoryLower.contains(subjLower) || tagsLower.contains(subjLower)) {
                    score += 60f
                }
            }

            // Semantic style match
            if (intent.style != null) {
                val styleLower = intent.style.lowercase()
                if (titleLower.contains(styleLower) || descLower.contains(styleLower) || promptLower.contains(styleLower)) {
                    score += 40f
                }
            }

            // Semantic platform match
            if (intent.platform != null) {
                val platLower = intent.platform.lowercase()
                if (platformLower.contains(platLower) || titleLower.contains(platLower)) {
                    score += 50f
                }
            }

            // Keyword overlaps
            for (kw in keywords) {
                if (titleLower.contains(kw)) score += 25f
                if (categoryLower.contains(kw)) score += 20f
                if (tagsLower.contains(kw)) score += 15f
                if (promptLower.contains(kw)) score += 10f
            }

            // Fuzzy match if score is still 0
            if (score == 0f) {
                val fuzzyScore = FuzzyMatcher.similarityScore(qLower, "$titleLower $categoryLower $tagsLower")
                if (fuzzyScore > 0.4f) {
                    score += fuzzyScore * 30f
                    matchReason = "Fuzzy match"
                }
            }

            SearchResultItem.PromptResult(prompt, score, matchReason)
        }.filter { it.relevanceScore > 0f }
            .sortedByDescending { it.relevanceScore }
            .map { it.prompt }

        // --- 2. Rank Images ---
        val scoredImages = allImages.map { img ->
            val titleLower = img.title.lowercase()
            val catLower = img.category.lowercase()
            val tagsLower = img.tags.lowercase()
            val codeLower = img.promptCode.lowercase()

            var score = 0f
            if (codeLower.contains(qLower)) score += 100f
            if (titleLower.contains(qLower)) score += 80f
            if (catLower.contains(qLower)) score += 50f

            for (kw in keywords) {
                if (titleLower.contains(kw)) score += 20f
                if (catLower.contains(kw)) score += 15f
                if (tagsLower.contains(kw)) score += 10f
            }

            if (score == 0f) {
                val fuzzy = FuzzyMatcher.similarityScore(qLower, "$titleLower $catLower")
                if (fuzzy > 0.4f) score += fuzzy * 20f
            }

            SearchResultItem.ImageResult(img, score)
        }.filter { it.relevanceScore > 0f }
            .sortedByDescending { it.relevanceScore }
            .map { it.image }

        // --- 3. Match Categories ---
        val distinctCategories = allPrompts.map { it.category.trim() }.filter { it.isNotBlank() && it != "All" }.distinct()
        val matchedCategories = distinctCategories.filter { cat ->
            val catLower = cat.lowercase()
            catLower.contains(qLower) || keywords.any { kw -> catLower.contains(kw) } ||
                    (intent.subject != null && catLower.contains(intent.subject.lowercase()))
        }

        // --- 4. Match AI Tools ---
        val matchedTools = allTools.filter { tool ->
            val nameLower = tool.name.lowercase()
            val catLower = tool.category.lowercase()
            val descLower = tool.description.lowercase()

            nameLower.contains(qLower) || catLower.contains(qLower) || descLower.contains(qLower) ||
                    keywords.any { kw -> nameLower.contains(kw) || catLower.contains(kw) }
        }

        // --- 5. Generate Smart Suggestions ---
        val smartSuggestions = generateSuggestions(query, intent, scoredPrompts)

        val totalCount = scoredPrompts.size + scoredImages.size + matchedCategories.size + matchedTools.size

        return UnifiedSearchResults(
            query = query,
            intent = intent,
            topPrompts = scoredPrompts,
            topImages = scoredImages,
            matchingCategories = matchedCategories,
            matchingTools = matchedTools,
            smartSuggestions = smartSuggestions,
            isAiInterpreted = isAiInterpreted,
            totalCount = totalCount
        )
    }

    private fun generateSuggestions(
        query: String,
        intent: SearchIntent,
        topPrompts: List<PromptItem>
    ): List<String> {
        val qLower = query.lowercase().trim()
        val suggestions = mutableListOf<String>()

        when {
            qLower.contains("girl") || qLower.contains("woman") -> {
                suggestions.addAll(listOf("Girl Portrait", "Girl Cinematic", "Girl Anime", "Girl Fashion", "Girl DP"))
            }
            qLower.contains("boy") || qLower.contains("man") -> {
                suggestions.addAll(listOf("Boy Kurta", "Boy 3D Avatar", "Boy Cinematic", "Boy Hoodie", "Boy DP"))
            }
            qLower.contains("car") -> {
                suggestions.addAll(listOf("Luxury Car", "Cinematic Car", "Supercar Render", "Car Photography"))
            }
            qLower.contains("islamic") || qLower.contains("mosque") -> {
                suggestions.addAll(listOf("Islamic Mosque", "Eid Milad", "Islamic Calligraphy", "Ramadan Lantern"))
            }
            qLower.contains("couple") -> {
                suggestions.addAll(listOf("Couple Portrait", "Wedding Couple", "Romantic Couple", "3D Couple Avatar"))
            }
            else -> {
                // If empty or generic, extract top categories/titles
                topPrompts.take(4).forEach { suggestions.add(it.title) }
                if (suggestions.isEmpty()) {
                    suggestions.addAll(listOf("3D Avatar", "Boy Prompts", "Girl Prompts", "Islamic Prompts", "Gemini AI", "Luxury"))
                }
            }
        }

        return suggestions.distinct().take(6)
    }
}
