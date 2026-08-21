package com.example.data.search

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class SemanticSearchService(
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()
) {

    /**
     * Extracts search intent using Gemini AI when online, or heuristic parsing as reliable fallback.
     */
    suspend fun parseIntent(query: String): SearchIntent = withContext(Dispatchers.IO) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return@withContext SearchIntent(query = "")
        }

        // Try Gemini AI extraction if key is present
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val aiIntent = fetchGeminiSearchIntent(trimmed, apiKey)
                if (aiIntent != null) {
                    return@withContext aiIntent
                }
            } catch (e: Exception) {
                Log.w("SemanticSearch", "AI search interpretation fallback: ${e.message}")
            }
        }

        // Fast, accurate heuristic fallback
        return@withContext extractHeuristicIntent(trimmed)
    }

    /**
     * Calls Gemini 3.5 Flash for natural language query interpretation.
     */
    private fun fetchGeminiSearchIntent(query: String, apiKey: String): SearchIntent? {
        val systemPrompt = """
            You are a search query interpreter for an AI Prompt Library called AiPromptXpert.
            Extract structured intent from the user search query in JSON format.
            JSON schema:
            {
              "subject": "main subject e.g. girl, boy, car, anime, landscape, etc or null",
              "style": "style e.g. cinematic, realistic, 3d, anime, luxury, 8k, vintage, or null",
              "type": "type e.g. portrait, avatar, wallpaper, logo, coding, caption, or null",
              "platform": "platform e.g. Gemini, ChatGPT, Midjourney, Bing AI, DALL-E, or null",
              "keywords": ["keyword1", "keyword2", "keyword3"],
              "categories": ["Boy Prompts", "Girl Prompts", "Couple Prompts", "Islamic Prompts", "Cinematic", "Gemini", "AI Editing"]
            }
            Return ONLY raw JSON, no markdown formatting.
        """.trimIndent()

        val requestJson = JSONObject().apply {
            put("contents", org.json.JSONArray().put(JSONObject().apply {
                put("parts", org.json.JSONArray().put(JSONObject().apply {
                    put("text", "Interpret search query: \"$query\"")
                }))
            }))
            put("systemInstruction", JSONObject().apply {
                put("parts", org.json.JSONArray().put(JSONObject().apply {
                    put("text", systemPrompt)
                }))
            })
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.1)
                put("topK", 20)
                put("responseMimeType", "application/json")
            })
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
        val request = Request.Builder()
            .url(url)
            .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) return null

        val responseBody = response.body?.string() ?: return null
        val root = JSONObject(responseBody)
        val candidate = root.optJSONArray("candidates")?.optJSONObject(0)
        val text = candidate?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text") ?: return null

        val cleaned = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val parsed = JSONObject(cleaned)

        val keywordsList = mutableListOf<String>()
        val kwArray = parsed.optJSONArray("keywords")
        if (kwArray != null) {
            for (i in 0 until kwArray.length()) {
                keywordsList.add(kwArray.optString(i))
            }
        }

        val catList = mutableListOf<String>()
        val catArray = parsed.optJSONArray("categories")
        if (catArray != null) {
            for (i in 0 until catArray.length()) {
                catList.add(catArray.optString(i))
            }
        }

        return SearchIntent(
            query = query,
            subject = parsed.optString("subject").takeIf { it.isNotBlank() && it != "null" },
            style = parsed.optString("style").takeIf { it.isNotBlank() && it != "null" },
            type = parsed.optString("type").takeIf { it.isNotBlank() && it != "null" },
            platform = parsed.optString("platform").takeIf { it.isNotBlank() && it != "null" },
            keywords = if (keywordsList.isNotEmpty()) keywordsList else listOf(query),
            categories = catList
        )
    }

    /**
     * Local rule-based Heuristic Semantic Intent Parser for instant offline search.
     */
    fun extractHeuristicIntent(rawQuery: String): SearchIntent {
        val (normalizedQuery, expandedKeywords) = FuzzyMatcher.normalizeAndExpandQuery(rawQuery)
        val lower = normalizedQuery.lowercase()

        var subject: String? = null
        when {
            lower.contains("girl") || lower.contains("female") || lower.contains("woman") || lower.contains("hijab") -> subject = "Girl"
            lower.contains("boy") || lower.contains("male") || lower.contains("man") || lower.contains("kurta") -> subject = "Boy"
            lower.contains("couple") || lower.contains("wedding") || lower.contains("bride") || lower.contains("groom") -> subject = "Couple"
            lower.contains("car") || lower.contains("vehicle") || lower.contains("audi") || lower.contains("bmw") -> subject = "Car"
            lower.contains("islamic") || lower.contains("mosque") || lower.contains("eid") || lower.contains("ramadan") -> subject = "Islamic"
            lower.contains("anime") || lower.contains("manga") -> subject = "Anime"
            lower.contains("coding") || lower.contains("react") || lower.contains("python") || lower.contains("javascript") -> subject = "Coding"
            lower.contains("food") || lower.contains("burger") || lower.contains("coffee") -> subject = "Food"
            lower.contains("travel") || lower.contains("nature") || lower.contains("landscape") -> subject = "Travel"
        }

        var style: String? = null
        when {
            lower.contains("cinematic") || lower.contains("movie") || lower.contains("dramatic") -> style = "Cinematic"
            lower.contains("realistic") || lower.contains("photorealistic") || lower.contains("8k") || lower.contains("dslr") -> style = "Realistic"
            lower.contains("anime") || lower.contains("illustration") -> style = "Anime"
            lower.contains("luxury") || lower.contains("royal") || lower.contains("gold") -> style = "Luxury"
            lower.contains("3d") || lower.contains("render") || lower.contains("avatar") -> style = "3D Render"
            lower.contains("vintage") || lower.contains("retro") || lower.contains("analog") -> style = "Vintage"
        }

        var type: String? = null
        when {
            lower.contains("portrait") || lower.contains("dp") || lower.contains("avatar") || lower.contains("face") -> type = "Portrait"
            lower.contains("thumbnail") || lower.contains("youtube") -> type = "Thumbnail"
            lower.contains("caption") || lower.contains("instagram") -> type = "Social Media"
            lower.contains("logo") || lower.contains("icon") || lower.contains("branding") -> type = "Logo"
            lower.contains("wallpaper") || lower.contains("background") -> type = "Wallpaper"
        }

        var platform: String? = null
        when {
            lower.contains("gemini") -> platform = "Google Gemini"
            lower.contains("chatgpt") || lower.contains("gpt") -> platform = "ChatGPT"
            lower.contains("midjourney") -> platform = "Midjourney"
            lower.contains("bing") || lower.contains("dall-e") || lower.contains("copilot") -> platform = "Bing AI"
            lower.contains("leonardo") -> platform = "Leonardo.Ai"
        }

        val categories = mutableListOf<String>()
        if (subject != null) categories.add("$subject Prompts")
        if (style != null) categories.add(style)
        if (platform != null) categories.add(platform)

        return SearchIntent(
            query = rawQuery,
            subject = subject,
            style = style,
            type = type,
            platform = platform,
            keywords = expandedKeywords.ifEmpty { listOf(rawQuery) },
            categories = categories.distinct()
        )
    }
}
