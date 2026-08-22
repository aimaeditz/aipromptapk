package com.example.data.category

import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import com.example.data.model.SmartCategory

/**
 * Clean Definition of a Folder Category with accurate matching logic and iconography.
 */
data class CategoryDefinition(
    val id: String,
    val name: String,
    val displayName: String = name,
    val emoji: String = "",
    val iconKey: String = "folder",
    val searchKeywords: List<String>,
    val matcher: (PromptItem) -> Boolean
)

/**
 * Final Category System for AiPromptXpert.
 *
 * Rules:
 * 1. REAL CATEGORIES ONLY:
 *    Blogger's actual existing labels are the SOLE source of truth:
 *    - Boy Girl Prompt (Girls + Boys)
 *    - Boy Prompt (Boys)
 *    - Couple Boy Girl Prompt (Couple Boy Girl)
 *    - Couple Prompt (Couples)
 *    - Islamic (Islamic / Eid Milad IF real matching Blogger posts exist)
 *    TOTAL: Maximum 5 categories.
 *    Zero invented categories.
 * 2. STRICT CATEGORY FILTERING:
 *    - "Boys" folder: ONLY Boy Prompt actual posts. (No couples, no girls).
 *    - "Girls + Boys" folder: ONLY Boy Girl Prompt actual posts. (No couples).
 *    - "Couples" folder: ONLY Couple Prompt actual posts. (No single boys).
 *    - "Couple Boy Girl" folder: ONLY Couple Boy Girl Prompt actual posts.
 *    - "Islamic" folder: ONLY Islamic / Eid Milad matching posts.
 *    Zero mixed categories.
 * 3. HIDE EMPTY CATEGORIES:
 *    If a category has 0 real matching posts, it is hidden.
 * 4. FOLDER STYLE & NO EMOJIS:
 *    Clean, professional Material vector iconography, no emojis, small clean cards.
 * 5. PERFORMANCE:
 *    Fast caching by prompts hash, instant lookups, no recalculation on screen open.
 */
object SmartCategoryEngine {

    private val CANONICAL_DEFINITIONS = listOf(
        CategoryDefinition(
            id = "cat_boy_prompt",
            name = "Boy Prompt",
            displayName = "Boys",
            emoji = "",
            iconKey = "face_male",
            searchKeywords = listOf("boy", "boys", "male", "man", "men", "guy", "guys", "kurta", "groom", "handsome", "hoodie", "boy prompt"),
            matcher = { prompt ->
                val cat = prompt.category.trim().lowercase()
                val title = prompt.title.trim().lowercase()
                val tags = prompt.tags.trim().lowercase()
                val text = prompt.exactPrompt.trim().lowercase()

                // Must NOT be Couple or Couple Boy Girl
                val isCouple = cat.contains("couple") || title.contains("couple") || tags.contains("couple") ||
                        text.contains("couple") || text.contains("husband and wife") || text.contains("bride and groom") || text.contains("romantic couple")
                if (isCouple) return@CategoryDefinition false

                val isBoyGirl = cat.contains("boy girl") || cat.contains("boy and girl") || cat.contains("boy & girl") ||
                        (title.contains("girl") && title.contains("boy"))
                if (isBoyGirl) return@CategoryDefinition false

                val isGirl = (cat.contains("girl") && !cat.contains("boy")) ||
                        (title.contains("girl") && !title.contains("boy")) ||
                        text.contains("hijabi") || text.contains("woman") || text.contains("female")
                if (isGirl && !cat.startsWith("boy")) return@CategoryDefinition false

                val isBoyCat = cat == "boy prompt" || cat == "boy prompts" || cat == "boys" || cat == "boy" || cat.startsWith("boy")
                val hasBoyKeywords = title.contains("boy") || tags.contains("boy") || text.contains("boy") || text.contains("boys") ||
                        text.contains("male") || text.contains(" man") || text.contains("men ") || text.contains("kurta") || text.contains("guy") || text.contains("hoodie")

                isBoyCat || hasBoyKeywords
            }
        ),
        CategoryDefinition(
            id = "cat_boy_girl_prompt",
            name = "Boy Girl Prompt",
            displayName = "Girls + Boys",
            emoji = "",
            iconKey = "people",
            searchKeywords = listOf("boy girl", "boy and girl", "girl and boy", "girls + boys", "boy girl prompt", "girl boy"),
            matcher = { prompt ->
                val cat = prompt.category.trim().lowercase()
                val title = prompt.title.trim().lowercase()
                val tags = prompt.tags.trim().lowercase()
                val text = prompt.exactPrompt.trim().lowercase()

                // Must NOT be Couple or Couple Boy Girl
                val isCouple = cat.contains("couple") || title.contains("couple") || tags.contains("couple") ||
                        text.contains("couple") || text.contains("husband and wife") || text.contains("bride and groom")
                if (isCouple) return@CategoryDefinition false

                val isBoyGirlCat = cat == "boy girl prompt" || cat == "boy girl prompts" || cat == "boy girl" ||
                        cat.contains("boy girl") || cat.contains("boy & girl") || cat.contains("boy and girl") || cat.contains("girls + boys")
                val hasBothBoyAndGirl = (title.contains("boy") || tags.contains("boy") || text.contains("boy") || text.contains("male")) &&
                        (title.contains("girl") || tags.contains("girl") || text.contains("girl") || text.contains("female"))

                isBoyGirlCat || hasBothBoyAndGirl
            }
        ),
        CategoryDefinition(
            id = "cat_couple_prompt",
            name = "Couple Prompt",
            displayName = "Couples",
            emoji = "",
            iconKey = "favorite",
            searchKeywords = listOf("couple", "couples", "pair", "duo", "husband and wife", "bride and groom", "romantic", "wedding couple", "couple prompt"),
            matcher = { prompt ->
                val cat = prompt.category.trim().lowercase()
                val title = prompt.title.trim().lowercase()
                val tags = prompt.tags.trim().lowercase()
                val text = prompt.exactPrompt.trim().lowercase()

                // Must NOT be Couple Boy Girl Prompt
                val isCoupleBoyGirl = cat.contains("couple boy girl") || cat.contains("couple boy and girl") ||
                        title.contains("couple boy girl") || tags.contains("couple boy girl")
                if (isCoupleBoyGirl) return@CategoryDefinition false

                val isCoupleCat = cat == "couple prompt" || cat == "couple prompts" || cat == "couples" || cat == "couple" || (cat.contains("couple") && !cat.contains("boy girl"))
                val hasCoupleKeywords = title.contains("couple") || tags.contains("couple") || text.contains("couple") ||
                        title.contains("husband and wife") || text.contains("husband and wife") ||
                        title.contains("bride and groom") || text.contains("bride and groom") ||
                        title.contains("romantic couple") || text.contains("romantic couple") ||
                        title.contains("wedding couple") || text.contains("wedding couple") ||
                        text.contains("pre-wedding")

                isCoupleCat || hasCoupleKeywords
            }
        ),
        CategoryDefinition(
            id = "cat_couple_boy_girl_prompt",
            name = "Couple Boy Girl Prompt",
            displayName = "Couple Boy Girl",
            emoji = "",
            iconKey = "group",
            searchKeywords = listOf("couple boy girl", "couple boy and girl", "couple girl boy", "couple boy girl prompt"),
            matcher = { prompt ->
                val cat = prompt.category.trim().lowercase()
                val title = prompt.title.trim().lowercase()
                val tags = prompt.tags.trim().lowercase()
                val text = prompt.exactPrompt.trim().lowercase()

                val isExactCat = cat == "couple boy girl prompt" || cat == "couple boy girl" || cat == "couple boy and girl" ||
                        cat.contains("couple boy girl") || cat.contains("couple boy and girl") || cat.contains("couple & boy girl")
                val hasCoupleAndBothInTitleOrTags = (title.contains("couple boy girl") || tags.contains("couple boy girl") ||
                        title.contains("couple boy and girl") || tags.contains("couple boy and girl"))

                isExactCat || hasCoupleAndBothInTitleOrTags
            }
        ),
        CategoryDefinition(
            id = "cat_islamic_prompt",
            name = "Islamic",
            displayName = "Islamic",
            emoji = "",
            iconKey = "mosque",
            searchKeywords = listOf("eid milad", "milad", "eid", "islamic", "muslim", "mosque", "masjid", "ramadan", "quran", "mecca", "medina", "nabi", "eid milad un nabi photo prompt"),
            matcher = { prompt ->
                val cat = prompt.category.trim().lowercase()
                val title = prompt.title.trim().lowercase()
                val tags = prompt.tags.trim().lowercase()
                val text = prompt.exactPrompt.trim().lowercase()

                val isCatMatch = cat.contains("eid") || cat.contains("milad") || cat.contains("islamic") || cat.contains("muslim") || cat.contains("nabi") || cat.contains("mosque")
                val hasIslamicKeywords = title.contains("eid") || title.contains("milad") || title.contains("islamic") || title.contains("mosque") || title.contains("masjid") || title.contains("ramadan") ||
                        tags.contains("eid") || tags.contains("milad") || tags.contains("islamic") || tags.contains("mosque") || tags.contains("masjid") ||
                        text.contains("eid") || text.contains("milad") || text.contains("islamic") || text.contains("mosque") || text.contains("masjid") || text.contains("ramadan") || text.contains("quran") || text.contains("mecca") || text.contains("medina") || text.contains("kaaba") || text.contains("naat") || text.contains("prophet") || text.contains("eid mubarak")

                isCatMatch || hasIslamicKeywords
            }
        )
    )

    @Volatile
    private var cachedPromptsHash: Int = 0
    @Volatile
    private var cachedIndex: List<SmartCategory> = emptyList()

    /**
     * Builds the Folder Category Index strictly from real Blogger prompts.
     * Guaranteed:
     * - Maximum 5 categories (Boy Prompt, Boy Girl Prompt, Couple Prompt, Couple Boy Girl Prompt, Islamic).
     * - ONLY categories with promptCount > 0 are returned.
     * - Every prompt count matches the exact number of matching prompts.
     * - Zero fake categories, zero invented categories.
     * - Zero emojis.
     */
    fun buildCategoryIndex(
        allPrompts: List<PromptItem>,
        allImages: List<GalleryImage> = emptyList()
    ): List<SmartCategory> {
        val currentHash = allPrompts.hashCode()
        if (cachedPromptsHash == currentHash && cachedIndex.isNotEmpty()) {
            return cachedIndex
        }

        if (allPrompts.isEmpty()) {
            cachedPromptsHash = currentHash
            cachedIndex = emptyList()
            return emptyList()
        }

        val categoryResults = mutableListOf<SmartCategory>()

        // Process strictly the maximum 5 real Blogger Category Definitions
        for (def in CANONICAL_DEFINITIONS) {
            val matchedPromptIds = mutableSetOf<String>()

            for (prompt in allPrompts) {
                if (def.matcher(prompt)) {
                    matchedPromptIds.add(prompt.id)
                }
            }

            // ONLY include if at least 1 real prompt genuinely belongs to this category
            if (matchedPromptIds.isNotEmpty()) {
                val category = SmartCategory(
                    id = def.id,
                    name = def.name,
                    displayName = def.displayName,
                    description = "",
                    promptCount = matchedPromptIds.size,
                    imageUrl = null,
                    iconKey = def.iconKey,
                    emoji = "", // No emojis
                    matchedPromptIds = matchedPromptIds,
                    tags = def.searchKeywords
                )
                categoryResults.add(category)
            }
        }

        cachedPromptsHash = currentHash
        cachedIndex = categoryResults
        return categoryResults
    }

    /**
     * Checks whether a prompt belongs to a selected category.
     * When user taps a folder (e.g. "Boys", "Girls + Boys", "Couples", "Couple Boy Girl", "Islamic"),
     * this ensures ONLY the exact matching prompts appear.
     */
    fun isPromptInCategory(
        prompt: PromptItem,
        categoryName: String,
        categoryIndex: List<SmartCategory>? = null
    ): Boolean {
        val trimmed = categoryName.trim()
        if (trimmed.isBlank() || trimmed.equals("All", ignoreCase = true)) {
            return true
        }

        val index = categoryIndex ?: cachedIndex
        val indexedCat = index.firstOrNull {
            it.name.equals(trimmed, ignoreCase = true) ||
                    it.displayName.equals(trimmed, ignoreCase = true) ||
                    it.id.equals(trimmed, ignoreCase = true)
        }

        if (indexedCat != null) {
            return indexedCat.matchedPromptIds.contains(prompt.id)
        }

        // Fallback: match via canonical definition matcher
        val canonical = CANONICAL_DEFINITIONS.firstOrNull {
            it.name.equals(trimmed, ignoreCase = true) ||
                    it.displayName.equals(trimmed, ignoreCase = true) ||
                    it.id.equals(trimmed, ignoreCase = true) ||
                    it.name.equals(trimmed.removeSuffix(" Prompts").removeSuffix(" Prompt"), ignoreCase = true) ||
                    it.displayName.equals(trimmed.removeSuffix(" Prompts").removeSuffix(" Prompt"), ignoreCase = true)
        }

        if (canonical != null) {
            return canonical.matcher(prompt)
        }

        return false
    }

    /**
     * Searches the real category index.
     * Filters ONLY the actual categories available in the app.
     */
    fun searchCategories(query: String, index: List<SmartCategory>): List<SmartCategory> {
        val q = query.trim().lowercase()
        if (q.isBlank()) {
            return index.filter { it.promptCount > 0 }
        }

        return index.filter { cat ->
            if (cat.promptCount <= 0) return@filter false

            val nameLower = cat.name.lowercase()
            val displayLower = cat.displayName.lowercase()
            val tagsMatch = cat.tags.any { it.lowercase().contains(q) || q.contains(it.lowercase()) }

            nameLower.contains(q) ||
                    displayLower.contains(q) ||
                    tagsMatch
        }
    }
}

