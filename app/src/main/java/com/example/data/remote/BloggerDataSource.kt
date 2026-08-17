package com.example.data.remote

import com.example.data.model.PromptItem
import java.security.MessageDigest
import java.util.regex.Pattern

class BloggerDataSource(private val apiService: BloggerApiService) {

    suspend fun fetchPromptsFromUrl(feedUrl: String): List<PromptItem> {
        return try {
            val response = apiService.getBloggerFeed(feedUrl)
            val entries = response.feed?.entries ?: return emptyList()
            val promptList = mutableListOf<PromptItem>()

            for (entry in entries) {
                val postId = entry.id?.value?.substringAfterLast("post-") ?: System.currentTimeMillis().toString()
                val postTitle = entry.title?.value ?: "AiPromptXpert Prompt"
                val rawHtml = entry.content?.value ?: ""
                val postUrl = entry.link?.firstOrNull { it.rel == "alternate" }?.href ?: feedUrl
                val categories = entry.categories?.mapNotNull { it.term } ?: emptyList()
                val category = categories.firstOrNull() ?: determineCategoryFromTitle(postTitle)

                val extracted = parseBloggerContent(postId, postTitle, rawHtml, category, postUrl)
                promptList.addAll(extracted)
            }
            promptList
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Extracts distinct Image + Prompt records from Blogger HTML content.
     * Keeps Image A + Prompt A separate from Image B + Prompt B.
     * Filters out credits, hashtags, tutorial instructions, intro/outro text.
     */
    fun parseBloggerContent(
        postId: String,
        postTitle: String,
        htmlContent: String,
        defaultCategory: String,
        sourceUrl: String
    ): List<PromptItem> {
        val results = mutableListOf<PromptItem>()

        // Find all image URLs in the post
        val imgPattern = Pattern.compile("<img[^>]+src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE)
        val imgMatcher = imgPattern.matcher(htmlContent)
        val images = mutableListOf<String>()
        while (imgMatcher.find()) {
            val imgUrl = imgMatcher.group(1)
            if (!imgUrl.isNullOrEmpty() && !imgUrl.contains("ads") && !imgUrl.contains("icon")) {
                images.add(imgUrl)
            }
        }

        // Find blocks enclosed in <code>, <pre>, <blockquote>, or styled prompt divs
        val promptBlocks = mutableListOf<String>()
        val codePattern = Pattern.compile("<(?:code|pre|blockquote)[^>]*>(.*?)</(?:code|pre|blockquote)>", Pattern.DOTALL or Pattern.CASE_INSENSITIVE)
        val codeMatcher = codePattern.matcher(htmlContent)
        while (codeMatcher.find()) {
            val clean = stripHtml(codeMatcher.group(1)).trim()
            if (isLikelyPrompt(clean)) {
                promptBlocks.add(clean)
            }
        }

        // Fallback: search for "Prompt:" or "AI Prompt:" or "Copy Prompt:" in plain text
        if (promptBlocks.isEmpty()) {
            val plainText = stripHtml(htmlContent)
            val lines = plainText.split("\n")
            var currentPrompt = StringBuilder()
            var capturing = false

            for (line in lines) {
                val trimmed = line.trim()
                if (trimmed.startsWith("Prompt:", ignoreCase = true) ||
                    trimmed.startsWith("AI Prompt:", ignoreCase = true) ||
                    trimmed.startsWith("Prompt Code:", ignoreCase = true)
                ) {
                    if (capturing && currentPrompt.isNotEmpty()) {
                        val p = currentPrompt.toString().trim()
                        if (isLikelyPrompt(p)) promptBlocks.add(p)
                        currentPrompt = StringBuilder()
                    }
                    capturing = true
                    val contentAfterColon = trimmed.substringAfter(":").trim()
                    if (contentAfterColon.isNotEmpty()) {
                        currentPrompt.append(contentAfterColon)
                    }
                } else if (capturing) {
                    if (trimmed.isEmpty() || trimmed.startsWith("How to", ignoreCase = true) || trimmed.startsWith("Subscribe", ignoreCase = true)) {
                        capturing = false
                        val p = currentPrompt.toString().trim()
                        if (isLikelyPrompt(p)) promptBlocks.add(p)
                        currentPrompt = StringBuilder()
                    } else {
                        currentPrompt.append(" ").append(trimmed)
                    }
                }
            }
            if (capturing && currentPrompt.isNotEmpty()) {
                val p = currentPrompt.toString().trim()
                if (isLikelyPrompt(p)) promptBlocks.add(p)
            }
        }

        // Pair images with prompts or fallback to title if needed
        val count = maxOf(1, maxOf(images.size, promptBlocks.size))
        val fallbackImage = images.firstOrNull() ?: "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop"
        val fallbackPrompt = if (promptBlocks.isNotEmpty()) promptBlocks.first() else "Create a 3D cinematic high detail portrait inspired by $postTitle by AiMAEditz."

        for (i in 0 until count) {
            val img = if (i < images.size) images[i] else fallbackImage
            val pText = if (i < promptBlocks.size) promptBlocks[i] else fallbackPrompt
            val cleanPrompt = cleanPromptText(pText)

            val codeNum = 100 + (postId.hashCode() % 800 + i).let { if (it < 0) -it else it }
            val promptCode = "#$codeNum"
            val stableId = generateStableId(postId, i, cleanPrompt)

            results.add(
                PromptItem(
                    id = stableId,
                    promptCode = promptCode,
                    title = if (count == 1) postTitle else "$postTitle (Concept ${i + 1})",
                    category = defaultCategory,
                    platform = determinePlatform(cleanPrompt),
                    description = extractShortDescription(cleanPrompt),
                    exactPrompt = cleanPrompt,
                    imageUrl = img,
                    isFeatured = i == 0,
                    isTrending = true,
                    tags = extractTags(postTitle, defaultCategory),
                    sourceUrl = sourceUrl
                )
            )
        }

        return results
    }

    private fun cleanPromptText(raw: String): String {
        return raw.replace(Regex("(?i)^(Prompt:|AI Prompt:|Prompt Code:|Copy Prompt:)\\s*"), "")
            .replace(Regex("(?i)#\\w+"), "") // strip hashtags
            .replace(Regex("(?i)Follow @\\w+ for more"), "")
            .replace(Regex("(?i)Created by M(R)?\\.? ABID / AiMAEditz"), "")
            .trim()
    }

    private fun isLikelyPrompt(text: String): Boolean {
        val clean = cleanPromptText(text)
        return clean.length > 20 && !clean.startsWith("How to", ignoreCase = true)
    }

    private fun determineCategoryFromTitle(title: String): String {
        val lower = title.lowercase()
        return when {
            lower.contains("boy") -> "Boy Prompts"
            lower.contains("girl") -> "Girl Prompts"
            lower.contains("couple") -> "Couple Prompts"
            lower.contains("islamic") || lower.contains("ramadan") || lower.contains("mosque") -> "Islamic Prompts"
            lower.contains("eid") -> "Eid Prompts"
            lower.contains("wedding") -> "Wedding Prompts"
            lower.contains("cinematic") -> "Cinematic Prompts"
            lower.contains("car") -> "Cars"
            lower.contains("gemini") -> "Gemini"
            else -> "AI Editing"
        }
    }

    private fun determinePlatform(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("midjourney") -> "Midjourney"
            lower.contains("dall-e") || lower.contains("bing") -> "Bing AI"
            lower.contains("gemini") -> "Gemini"
            lower.contains("chatgpt") -> "ChatGPT"
            else -> "Gemini / Bing"
        }
    }

    private fun extractShortDescription(prompt: String): String {
        return if (prompt.length > 120) prompt.substring(0, 120) + "..." else prompt
    }

    private fun extractTags(title: String, category: String): String {
        return "$category, AiMAEditz, AiPromptXpert, 3D, Portrait"
    }

    private fun stripHtml(html: String): String {
        return html.replace(Regex("<[^>]*>"), " ").replace("&nbsp;", " ").replace("&amp;", "&").trim()
    }

    private fun generateStableId(postId: String, index: Int, promptText: String): String {
        val input = "$postId-$index-${promptText.hashCode()}"
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
