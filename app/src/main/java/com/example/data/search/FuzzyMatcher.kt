package com.example.data.search

import kotlin.math.min

object FuzzyMatcher {

    // Common synonym dictionary for AI Prompt queries & spelling normalizations
    private val synonymMap = mapOf(
        "gurl" to "girl",
        "gril" to "girl",
        "femal" to "female",
        "womam" to "woman",
        "wman" to "woman",
        "boyy" to "boy",
        "boi" to "boy",
        "man" to "boy",
        "guy" to "boy",
        "kuurta" to "kurta",
        "kurti" to "kurta",
        "hijabi" to "hijab",
        "hijaab" to "hijab",
        "anme" to "anime",
        "aimne" to "anime",
        "anim" to "anime",
        "realstic" to "realistic",
        "relastic" to "realistic",
        "cinmatic" to "cinematic",
        "cinamatic" to "cinematic",
        "luxry" to "luxury",
        "luxuary" to "luxury",
        "portait" to "portrait",
        "portriat" to "portrait",
        "photograpy" to "photography",
        "fotography" to "photography",
        "weddin" to "wedding",
        "weding" to "wedding",
        "fashon" to "fashion",
        "fashin" to "fashion",
        "gemni" to "gemini",
        "gemin" to "gemini",
        "midjurny" to "midjourney",
        "midjurney" to "midjourney",
        "chatgpt" to "chatgpt",
        "dalle" to "dall-e",
        "dall-e" to "bing",
        "copilot" to "bing",
        "thumnail" to "thumbnail",
        "thumbnaill" to "thumbnail",
        "islmic" to "islamic",
        "islaam" to "islamic",
        "namaz" to "islamic",
        "masjid" to "mosque",
        "suitt" to "suit",
        "hoody" to "hoodie"
    )

    // Semantic tag associations
    private val semanticConceptMap = mapOf(
        "girl" to listOf("girl", "female", "woman", "portrait", "lady", "hijab", "bride", "model", "dp"),
        "boy" to listOf("boy", "male", "man", "guy", "kurta", "groom", "hoodie", "model", "dp"),
        "couple" to listOf("couple", "pair", "wedding", "husband", "wife", "love", "romantic"),
        "car" to listOf("car", "vehicle", "luxury", "supercar", "sports car", "bmw", "audi", "lamborghini"),
        "islamic" to listOf("islamic", "muslim", "mosque", "eid", "ramadan", "quran", "namaz", "duaa", "mecca"),
        "cinematic" to listOf("cinematic", "movie", "8k", "dramatic", "lighting", "unreal engine", "octane render"),
        "luxury" to listOf("luxury", "royal", "gold", "palace", "rich", "mansion", "expensive"),
        "portrait" to listOf("portrait", "headshot", "dp", "profile", "avatar", "face", "close-up"),
        "anime" to listOf("anime", "manga", "illustration", "2d", "chibi", "japanese style"),
        "coding" to listOf("coding", "programming", "developer", "react", "python", "javascript", "tech", "software"),
        "instagram" to listOf("instagram", "social media", "dp", "caption", "profile", "reel", "post"),
        "youtube" to listOf("youtube", "thumbnail", "cover", "channel", "banner"),
        "food" to listOf("food", "culinary", "delicious", "restaurant", "burger", "coffee", "beverage"),
        "nature" to listOf("nature", "mountain", "forest", "sunset", "travel", "landscape", "sea", "beach")
    )

    /**
     * Corrects spelling and extracts normalized keywords and synonym expansions.
     */
    fun normalizeAndExpandQuery(rawQuery: String): Pair<String, List<String>> {
        val tokens = rawQuery.lowercase()
            .replace(Regex("[^a-z0-9#\\s-]"), " ")
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }

        val normalizedTokens = mutableListOf<String>()
        val expandedKeywords = mutableSetOf<String>()

        for (token in tokens) {
            // Check direct synonym
            val corrected = synonymMap[token] ?: checkFuzzyMatch(token) ?: token
            normalizedTokens.add(corrected)
            expandedKeywords.add(corrected)

            // Add semantic concept words
            semanticConceptMap[corrected]?.let { synonyms ->
                expandedKeywords.addAll(synonyms)
            }
        }

        val normalizedQuery = normalizedTokens.joinToString(" ")
        return Pair(normalizedQuery, expandedKeywords.toList())
    }

    /**
     * Fuzzy matching for spelling correction using Levenshtein distance.
     */
    private fun checkFuzzyMatch(token: String): String? {
        if (token.length < 3) return null
        var bestCandidate: String? = null
        var minDistance = Int.MAX_VALUE

        for (knownWord in synonymMap.values.distinct()) {
            val dist = levenshteinDistance(token, knownWord)
            val maxAllowed = if (token.length <= 4) 1 else 2
            if (dist <= maxAllowed && dist < minDistance) {
                minDistance = dist
                bestCandidate = knownWord
            }
        }
        return bestCandidate
    }

    /**
     * Compute Levenshtein distance between two strings.
     */
    fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j

        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Calculates fuzzy score (0.0 to 1.0) between query and target string.
     */
    fun similarityScore(query: String, target: String): Float {
        val q = query.lowercase().trim()
        val t = target.lowercase().trim()

        if (t.contains(q)) return 1.0f

        val qWords = q.split(" ")
        val tWords = t.split(" ")

        var matches = 0
        for (qw in qWords) {
            if (tWords.any { tw -> tw.contains(qw) || levenshteinDistance(qw, tw) <= (if (qw.length > 4) 2 else 1) }) {
                matches++
            }
        }

        return if (qWords.isNotEmpty()) (matches.toFloat() / qWords.size) else 0.0f
    }
}
