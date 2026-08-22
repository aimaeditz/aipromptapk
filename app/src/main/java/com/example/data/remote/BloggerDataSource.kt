package com.example.data.remote

import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.regex.Pattern

class BloggerDataSource(
    private val okHttpClient: OkHttpClient
) {
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Fetches all real posts from the Blogger feed URL.
     * Parses all images and their associated exact AI prompts.
     * Enforces unique post identity priority: Blogger Post ID -> Post URL -> Prompt Code.
     * Strictly prevents duplicates across feeds and pagination.
     */
    fun fetchPromptsFromFeedUrl(rawFeedUrl: String): Pair<List<PromptItem>, List<GalleryImage>> {
        val prompts = mutableListOf<PromptItem>()
        val gallery = mutableListOf<GalleryImage>()

        val urlWithParams = if (rawFeedUrl.contains("alt=json")) {
            rawFeedUrl
        } else {
            val separator = if (rawFeedUrl.contains("?")) "&" else "?"
            "$rawFeedUrl${separator}alt=json&max-results=500"
        }

        try {
            val request = Request.Builder()
                .url(urlWithParams)
                .cacheControl(CacheControl.Builder().noCache().build())
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 14) AiPromptXpert/1.0")
                .header("Accept", "application/json, text/javascript, */*")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return Pair(emptyList(), emptyList())
            }

            val bodyString = response.body?.string() ?: return Pair(emptyList(), emptyList())
            val jsonObject = JSONObject(bodyString)
            val feedObj = jsonObject.optJSONObject("feed") ?: return Pair(emptyList(), emptyList())
            val entriesArray = feedObj.optJSONArray("entry") ?: return Pair(emptyList(), emptyList())

            val rawEntries = mutableListOf<BloggerPostEntry>()
            val seenEntryPostIds = mutableSetOf<String>()
            val seenEntryUrls = mutableSetOf<String>()

            for (i in 0 until entriesArray.length()) {
                val entryObj = entriesArray.optJSONObject(i) ?: continue

                val idObj = entryObj.optJSONObject("id")
                val rawId = idObj?.optString("\$t") ?: ""
                val postId = if (rawId.contains("post-")) {
                    rawId.substringAfterLast("post-").trim()
                } else if (rawId.isNotBlank()) {
                    rawId.trim()
                } else {
                    "post_$i"
                }

                // Extract post alternate URL
                var postUrl = ""
                val linkArray = entryObj.optJSONArray("link")
                if (linkArray != null) {
                    for (l in 0 until linkArray.length()) {
                        val lObj = linkArray.optJSONObject(l)
                        if (lObj?.optString("rel") == "alternate") {
                            postUrl = lObj.optString("href", "")
                            break
                        }
                    }
                }
                if (postUrl.isBlank()) {
                    postUrl = "$rawFeedUrl#$postId"
                }

                // Deduplicate feed entries by Blogger Post ID or Post URL
                if (postId.isNotBlank() && postId != "post_$i" && !seenEntryPostIds.add(postId)) {
                    continue
                }
                if (postUrl.isNotBlank() && !seenEntryUrls.add(postUrl.lowercase())) {
                    continue
                }

                val titleObj = entryObj.optJSONObject("title")
                val postTitle = titleObj?.optString("\$t")?.trim() ?: "AI Prompt"

                val contentObj = entryObj.optJSONObject("content")
                val htmlContent = contentObj?.optString("\$t") ?: ""

                val publishedObj = entryObj.optJSONObject("published")
                val publishedStr = publishedObj?.optString("\$t") ?: ""
                val updatedObj = entryObj.optJSONObject("updated")
                val updatedStr = updatedObj?.optString("\$t") ?: ""
                val timestampStr = if (publishedStr.isNotBlank()) publishedStr else updatedStr
                val publishedTimestamp = parseTimestamp(timestampStr)

                // Extract Blogger labels / categories exactly as defined on Blogger
                val categoriesList = mutableListOf<String>()
                val catArray = entryObj.optJSONArray("category")
                if (catArray != null) {
                    for (c in 0 until catArray.length()) {
                        val catObj = catArray.optJSONObject(c)
                        val term = catObj?.optString("term")?.trim()
                        if (!term.isNullOrBlank()) {
                            categoriesList.add(term)
                        }
                    }
                }

                rawEntries.add(
                    BloggerPostEntry(
                        postId = postId,
                        title = postTitle,
                        htmlContent = htmlContent,
                        publishedTimestamp = publishedTimestamp,
                        categories = categoriesList,
                        postUrl = postUrl
                    )
                )
            }

            // Global deduplication trackers for prompts and images
            val globalSeenPromptTexts = mutableSetOf<String>()
            val globalSeenImageUrls = mutableSetOf<String>()

            // Process posts and extract accurate prompts and high-res images
            for (entry in rawEntries) {
                val (extractedPrompts, extractedGallery) = parseBloggerPost(
                    entry = entry,
                    seenPrompts = globalSeenPromptTexts,
                    seenImages = globalSeenImageUrls
                )
                prompts.addAll(extractedPrompts)
                gallery.addAll(extractedGallery)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return Pair(prompts, gallery)
    }

    /**
     * Parses a single Blogger post, extracting ONLY:
     * 1. The main image(s) with high resolution.
     * 2. The exact AI image prompt associated with each image.
     *
     * Unique ID Priority:
     * 1. Blogger Post ID
     * 2. Post URL
     * 3. Prompt Code / Hash
     */
    private fun parseBloggerPost(
        entry: BloggerPostEntry,
        seenPrompts: MutableSet<String>,
        seenImages: MutableSet<String>
    ): Pair<List<PromptItem>, List<GalleryImage>> {
        val prompts = mutableListOf<PromptItem>()
        val gallery = mutableListOf<GalleryImage>()

        val images = extractHighResImages(entry.htmlContent)
        val promptTexts = extractExactPrompts(entry.htmlContent)

        // If no prompt text was found and no images, do not invent anything
        if (promptTexts.isEmpty() && images.isEmpty()) {
            return Pair(emptyList(), emptyList())
        }

        val primaryCategory = entry.categories.firstOrNull() ?: "AI Prompts"
        val tagsString = if (entry.categories.isNotEmpty()) {
            entry.categories.joinToString(", ")
        } else {
            primaryCategory
        }

        val defaultImage = images.firstOrNull() ?: ""

        // If multiple prompt texts are extracted (e.g., 2, 4, 14, 40)
        if (promptTexts.isNotEmpty()) {
            for (i in promptTexts.indices) {
                val promptText = promptTexts[i]
                val cleanedPrompt = cleanPromptText(promptText)
                if (cleanedPrompt.length < 15) continue // Skip non-prompt artifacts

                val normalizedText = normalizePromptForDedup(cleanedPrompt)
                if (!seenPrompts.add(normalizedText)) {
                    // Already processed this exact prompt text across feeds
                    continue
                }

                val imgUrl = if (i < images.size) images[i] else defaultImage
                val promptCode = generateDeterministicPromptCode(entry.postId, entry.postUrl, i)
                val stableId = generatePriorityUniqueId(entry.postId, entry.postUrl, promptCode, i, cleanedPrompt)
                val itemTitle = if (promptTexts.size > 1) "${entry.title} (#${i + 1})" else entry.title
                val platform = determinePlatform(cleanedPrompt)

                val promptItem = PromptItem(
                    id = stableId,
                    promptCode = promptCode,
                    title = itemTitle,
                    category = primaryCategory,
                    platform = platform,
                    description = extractShortSummary(cleanedPrompt),
                    exactPrompt = cleanedPrompt,
                    imageUrl = imgUrl,
                    isFeatured = i == 0,
                    isTrending = true,
                    tags = tagsString,
                    createdAt = entry.publishedTimestamp + i,
                    sourceUrl = entry.postUrl
                )

                prompts.add(promptItem)

                if (imgUrl.isNotBlank() && seenImages.add(imgUrl.lowercase())) {
                    gallery.add(
                        GalleryImage(
                            id = "gallery_${stableId}",
                            title = itemTitle,
                            imageUrl = imgUrl,
                            promptId = stableId,
                            promptCode = promptCode,
                            category = primaryCategory,
                            exactPrompt = cleanedPrompt,
                            tags = tagsString
                        )
                    )
                }
            }
        } else if (images.isNotEmpty()) {
            // Post has images but prompts couldn't be extracted in standard code blocks
            // Try extracting from paragraph text
            val fallbackPrompt = extractFallbackPromptFromText(entry.htmlContent)
            if (fallbackPrompt.isNotBlank()) {
                val cleanedPrompt = cleanPromptText(fallbackPrompt)
                val normalizedText = normalizePromptForDedup(cleanedPrompt)

                for (i in images.indices) {
                    val imgUrl = images[i]
                    if (imgUrl.isBlank() || !seenImages.add(imgUrl.lowercase())) {
                        continue
                    }

                    val promptDedupKey = if (images.size > 1) "$normalizedText-img-$i" else normalizedText
                    if (!seenPrompts.add(promptDedupKey)) {
                        continue
                    }

                    val promptCode = generateDeterministicPromptCode(entry.postId, entry.postUrl, i)
                    val stableId = generatePriorityUniqueId(entry.postId, entry.postUrl, promptCode, i, "$cleanedPrompt-$i")
                    val itemTitle = if (images.size > 1) "${entry.title} (Image ${i + 1})" else entry.title
                    val platform = determinePlatform(cleanedPrompt)

                    val promptItem = PromptItem(
                        id = stableId,
                        promptCode = promptCode,
                        title = itemTitle,
                        category = primaryCategory,
                        platform = platform,
                        description = extractShortSummary(cleanedPrompt),
                        exactPrompt = cleanedPrompt,
                        imageUrl = imgUrl,
                        isFeatured = i == 0,
                        isTrending = true,
                        tags = tagsString,
                        createdAt = entry.publishedTimestamp + i,
                        sourceUrl = entry.postUrl
                    )

                    prompts.add(promptItem)

                    gallery.add(
                        GalleryImage(
                            id = "gallery_${stableId}",
                            title = itemTitle,
                            imageUrl = imgUrl,
                            promptId = stableId,
                            promptCode = promptCode,
                            category = primaryCategory,
                            exactPrompt = cleanedPrompt,
                            tags = tagsString
                        )
                    )
                }
            }
        }

        return Pair(prompts, gallery)
    }

    private fun normalizePromptForDedup(text: String): String {
        return text.lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .trim()
    }

    private fun generateDeterministicPromptCode(postId: String, postUrl: String, index: Int): String {
        val seed = if (postId.isNotBlank() && !postId.startsWith("post_")) {
            postId.hashCode()
        } else if (postUrl.isNotBlank()) {
            postUrl.hashCode()
        } else {
            (index + 1).hashCode()
        }
        val pos = (seed.let { if (it < 0) -it else it } % 899) + 100 + index
        return "#$pos"
    }

    /**
     * Generates a unique, deterministic ID following the required priority:
     * 1. Blogger Post ID -> "blogger_${postId}_$index"
     * 2. Post URL -> "post_${urlHash}_$index"
     * 3. Prompt Code -> "code_${promptCode}_$index"
     */
    private fun generatePriorityUniqueId(
        postId: String,
        postUrl: String,
        promptCode: String,
        index: Int,
        promptText: String
    ): String {
        return if (postId.isNotBlank() && !postId.startsWith("post_")) {
            if (index == 0) "blogger_$postId" else "blogger_${postId}_$index"
        } else if (postUrl.isNotBlank()) {
            val md5Url = md5(postUrl)
            if (index == 0) "url_$md5Url" else "url_${md5Url}_$index"
        } else if (promptCode.isNotBlank()) {
            val cleanCode = promptCode.removePrefix("#")
            if (index == 0) "code_$cleanCode" else "code_${cleanCode}_$index"
        } else {
            val md5Text = md5(promptText)
            "hash_${md5Text}_$index"
        }
    }

    private fun md5(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            input.hashCode().toString()
        }
    }

    /**
     * Extracts all high-resolution images from the HTML content.
     */
    private fun extractHighResImages(html: String): List<String> {
        val images = mutableListOf<String>()
        val imgPattern = Pattern.compile("<img[^>]+src=[\"']([^\"']+)[\"']", Pattern.CASE_INSENSITIVE)
        val matcher = imgPattern.matcher(html)

        while (matcher.find()) {
            val rawSrc = matcher.group(1) ?: continue
            if (isValidPostImage(rawSrc)) {
                val highResSrc = upgradeToHighRes(rawSrc)
                if (!images.contains(highResSrc)) {
                    images.add(highResSrc)
                }
            }
        }
        return images
    }

    /**
     * Upgrades Blogger thumbnail URLs to full-resolution /s1600/.
     */
    private fun upgradeToHighRes(url: String): String {
        return url
            .replace(Regex("/s[0-9]+(-[a-z0-9]+)?/"), "/s1600/")
            .replace(Regex("/w[0-9]+-h[0-9]+(-[a-z0-9]+)?/"), "/s1600/")
    }

    private fun isValidPostImage(url: String): Boolean {
        val lower = url.lowercase()
        return !lower.contains("ad_") &&
                !lower.contains("banner") &&
                !lower.contains("icon") &&
                !lower.contains("logo") &&
                !lower.contains("button") &&
                !lower.contains("pixel.gif") &&
                (lower.contains("blogger.googleusercontent.com") ||
                        lower.contains("bp.blogspot.com") ||
                        lower.contains(".jpg") ||
                        lower.contains(".jpeg") ||
                        lower.contains(".png") ||
                        lower.contains(".webp"))
    }

    /**
     * Extracts exact AI image prompts from HTML content.
     * Looks for code boxes, pre blocks, blockquotes, styled divs, or "Prompt:" lines.
     * Prevents internal duplication through normalized text tracking.
     */
    private fun extractExactPrompts(html: String): List<String> {
        val prompts = mutableListOf<String>()
        val seenNormalized = mutableSetOf<String>()

        fun addIfValid(rawText: String) {
            val cleaned = cleanPromptText(rawText)
            val normalized = cleaned.lowercase().replace(Regex("\\s+"), " ").trim()
            if (isLikelyPromptText(cleaned) && normalized.length >= 20 && seenNormalized.add(normalized)) {
                prompts.add(cleaned)
            }
        }

        // 1. Check for standard prompt containers: <pre>, <code>, <blockquote>, <textarea>, or <div class="code-box">
        val containerPattern = Pattern.compile(
            "<(?:pre|code|blockquote|textarea)[^>]*>(.*?)</(?:pre|code|blockquote|textarea)>",
            Pattern.DOTALL or Pattern.CASE_INSENSITIVE
        )
        val containerMatcher = containerPattern.matcher(html)
        while (containerMatcher.find()) {
            val rawBlock = stripHtmlTags(containerMatcher.group(1)).trim()
            addIfValid(rawBlock)
        }

        // 2. Check for divs or paragraphs specifically styled with prompt classes
        val divPattern = Pattern.compile(
            "<div[^>]+class=[\"'][^\"']*(?:prompt|code|copy|text-box)[^\"']*[\"'][^>]*>(.*?)</div>",
            Pattern.DOTALL or Pattern.CASE_INSENSITIVE
        )
        val divMatcher = divPattern.matcher(html)
        while (divMatcher.find()) {
            val rawBlock = stripHtmlTags(divMatcher.group(1)).trim()
            addIfValid(rawBlock)
        }

        // 3. Fallback searching for "Prompt:", "Prompt 1:", "Prompt 2:", etc. if no container prompts found
        if (prompts.isEmpty()) {
            val plainText = stripHtmlTags(html)
            val lines = plainText.split("\n")
            var capturing = false
            val currentPromptBuilder = StringBuilder()

            for (line in lines) {
                val trimmed = line.trim()
                if (isPromptHeaderLine(trimmed)) {
                    if (capturing && currentPromptBuilder.isNotEmpty()) {
                        addIfValid(currentPromptBuilder.toString())
                        currentPromptBuilder.clear()
                    }
                    capturing = true
                    val afterColon = extractContentAfterHeader(trimmed)
                    if (afterColon.isNotBlank()) {
                        currentPromptBuilder.append(afterColon)
                    }
                } else if (capturing) {
                    if (isBoilerplateStart(trimmed)) {
                        capturing = false
                        addIfValid(currentPromptBuilder.toString())
                        currentPromptBuilder.clear()
                    } else if (trimmed.isNotBlank()) {
                        currentPromptBuilder.append(" ").append(trimmed)
                    }
                }
            }

            if (capturing && currentPromptBuilder.isNotEmpty()) {
                addIfValid(currentPromptBuilder.toString())
            }
        }

        return prompts
    }

    private fun extractFallbackPromptFromText(html: String): String {
        val plainText = stripHtmlTags(html)
        val lines = plainText.split("\n")
        for (line in lines) {
            val trimmed = line.trim()
            if (trimmed.length >= 35 && isLikelyPromptText(trimmed)) {
                return trimmed
            }
        }
        return ""
    }

    private fun isPromptHeaderLine(line: String): Boolean {
        val lower = line.lowercase()
        return lower.startsWith("prompt") ||
                lower.startsWith("ai prompt") ||
                lower.startsWith("bing prompt") ||
                lower.startsWith("gemini prompt") ||
                lower.startsWith("midjourney prompt") ||
                lower.startsWith("copy prompt") ||
                lower.startsWith("prompt 1") ||
                lower.startsWith("prompt 2") ||
                lower.startsWith("prompt 3") ||
                lower.startsWith("prompt 4")
    }

    private fun extractContentAfterHeader(line: String): String {
        val colonIdx = line.indexOf(':')
        if (colonIdx != -1 && colonIdx < line.length - 1) {
            return line.substring(colonIdx + 1).trim()
        }
        val dashIdx = line.indexOf('-')
        if (dashIdx != -1 && dashIdx < line.length - 1) {
            return line.substring(dashIdx + 1).trim()
        }
        return ""
    }

    private fun isBoilerplateStart(line: String): Boolean {
        val lower = line.lowercase()
        return lower.startsWith("how to") ||
                lower.startsWith("steps to") ||
                lower.startsWith("follow us") ||
                lower.startsWith("subscribe") ||
                lower.startsWith("join our") ||
                lower.startsWith("credit:") ||
                lower.startsWith("author:") ||
                lower.startsWith("related posts") ||
                lower.startsWith("share this") ||
                lower.startsWith("disclaimer") ||
                lower.startsWith("tags:")
    }

    private fun isLikelyPromptText(text: String): Boolean {
        val lower = text.lowercase()
        if (lower.startsWith("how to create") ||
            lower.startsWith("step 1") ||
            lower.startsWith("in this tutorial") ||
            lower.startsWith("welcome to") ||
            lower.startsWith("hello friends") ||
            lower.startsWith("download now")
        ) {
            return false
        }
        return lower.contains("photo") ||
                lower.contains("image") ||
                lower.contains("portrait") ||
                lower.contains("cinematic") ||
                lower.contains("render") ||
                lower.contains("realistic") ||
                lower.contains("boy") ||
                lower.contains("girl") ||
                lower.contains("man") ||
                lower.contains("woman") ||
                lower.contains("wearing") ||
                lower.contains("lighting") ||
                lower.contains("background") ||
                lower.contains("4k") ||
                lower.contains("8k") ||
                lower.contains("3d") ||
                lower.contains("camera") ||
                lower.contains("sitting") ||
                lower.contains("standing") ||
                lower.contains("avatar") ||
                lower.contains("hyper-realistic")
    }

    private fun cleanPromptText(raw: String): String {
        var text = stripHtmlTags(raw)
        text = text.replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&lt;", "<")
            .replace("&gt;", ">")

        // Remove leading "Prompt:", "Prompt 1:", etc.
        val headerRegex = Regex("^(?:AI\\s+)?Prompt(?:\\s+\\d+)?[\\s:-]+", RegexOption.IGNORE_CASE)
        text = text.replace(headerRegex, "").trim()

        // Remove surrounding quotes if present
        if (text.startsWith("\"") && text.endsWith("\"") && text.length > 2) {
            text = text.substring(1, text.length - 1).trim()
        }
        return text
    }

    private fun stripHtmlTags(html: String): String {
        return html.replace(Regex("<br\\s*/?>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("<p[^>]*>", RegexOption.IGNORE_CASE), "\n")
            .replace(Regex("<[^>]+>"), "")
            .trim()
    }

    private fun determinePlatform(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("bing") || lower.contains("copilot") || lower.contains("dall-e") -> "Bing AI"
            lower.contains("gemini") || lower.contains("google ai") -> "Gemini"
            lower.contains("midjourney") || lower.contains("--v ") || lower.contains("--ar ") -> "Midjourney"
            lower.contains("chatgpt") || lower.contains("gpt-4") -> "ChatGPT"
            lower.contains("leonardo") -> "Leonardo.Ai"
            else -> "Bing / DALL-E 3"
        }
    }

    private fun extractShortSummary(prompt: String): String {
        val firstSentence = prompt.split(Regex("[.!?\n]")).firstOrNull()?.trim() ?: prompt
        return if (firstSentence.length > 110) {
            firstSentence.substring(0, 107) + "..."
        } else {
            firstSentence
        }
    }

    private fun parseTimestamp(dateStr: String): Long {
        if (dateStr.isBlank()) return System.currentTimeMillis()
        return try {
            val clean = dateStr.trim()
            val isoClean = if (clean.length >= 19) clean.substring(0, 19).replace("T", " ") else clean
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(isoClean)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }
}

data class BloggerPostEntry(
    val postId: String,
    val title: String,
    val htmlContent: String,
    val publishedTimestamp: Long,
    val categories: List<String>,
    val postUrl: String
)

