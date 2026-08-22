package com.example

import com.example.data.category.SmartCategoryEngine
import com.example.data.model.PromptItem
import org.junit.Assert.*
import org.junit.Test

class SmartCategoryEngineTest {

    private val samplePrompts = listOf(
        PromptItem(
            id = "p1",
            promptCode = "#101",
            title = "Boy in Royal Black Kurta",
            category = "Boy Prompt",
            platform = "Bing Image Creator",
            description = "Stylish boy wearing royal black kurta",
            exactPrompt = "A realistic 8K photo of a young man wearing black embroidered kurta pajama in palace",
            imageUrl = "https://example.com/boy.jpg",
            tags = "boy, kurta, handsome, dp"
        ),
        PromptItem(
            id = "p2",
            promptCode = "#102",
            title = "Cute Boy and Girl Duo in Traditional Dress",
            category = "Boy Girl Prompt",
            platform = "Bing Image Creator",
            description = "Boy and girl together in festive attire",
            exactPrompt = "A photorealistic image of a young boy and a young girl standing side by side in festive attire",
            imageUrl = "https://example.com/boy_girl.jpg",
            tags = "boy, girl, traditional, festive"
        ),
        PromptItem(
            id = "p3",
            promptCode = "#103",
            title = "Romantic Couple Walking on Beach",
            category = "Couple Prompt",
            platform = "ChatGPT DALL-E 3",
            description = "Romantic couple holding hands at sunset",
            exactPrompt = "A cinematic shot of a romantic couple walking hand in hand along beach coastline at golden sunset",
            imageUrl = "https://example.com/couple.jpg",
            tags = "couple, romantic, sunset, beach"
        ),
        PromptItem(
            id = "p4",
            promptCode = "#104",
            title = "Couple Boy Girl in Modern Aesthetic Style",
            category = "Couple Boy Girl Prompt",
            platform = "Midjourney",
            description = "Couple boy girl aesthetic style",
            exactPrompt = "High quality 8K render of a modern stylish couple boy girl in trendy streetwear",
            imageUrl = "https://example.com/couple_boy_girl.jpg",
            tags = "couple boy girl, aesthetic, streetwear"
        ),
        PromptItem(
            id = "p5",
            promptCode = "#105",
            title = "Grand Mosque Illuminated for Eid Milad un Nabi",
            category = "Eid Milad un Nabi Photo Prompt",
            platform = "Midjourney",
            description = "Grand mosque illuminated for Eid Milad un Nabi celebration",
            exactPrompt = "Cinematic 8K shot of grand mosque with crescent moon in Eid Milad un Nabi, minarets glowing with green lights",
            imageUrl = "https://example.com/mosque.jpg",
            tags = "eid milad, islamic, mosque, eid"
        )
    )

    @Test
    fun testMaximumFiveRealBloggerCategories() {
        val index = SmartCategoryEngine.buildCategoryIndex(samplePrompts)

        // At most 5 categories exist
        assertTrue(index.size <= 5)
        assertTrue(index.isNotEmpty())

        // Every category returned must have promptCount > 0
        assertTrue(index.all { it.promptCount > 0 })

        // Check each of the 5 canonical real Blogger categories
        val boyCat = index.find { it.name == "Boy Prompt" || it.displayName == "Boys" }
        assertNotNull("Boy Prompt category must exist", boyCat)
        assertEquals(1, boyCat?.promptCount)

        val boyGirlCat = index.find { it.name == "Boy Girl Prompt" || it.displayName == "Girls + Boys" }
        assertNotNull("Boy Girl Prompt category must exist", boyGirlCat)
        assertEquals(1, boyGirlCat?.promptCount)

        val coupleCat = index.find { it.name == "Couple Prompt" || it.displayName == "Couples" }
        assertNotNull("Couple Prompt category must exist", coupleCat)
        assertEquals(1, coupleCat?.promptCount)

        val coupleBoyGirlCat = index.find { it.name == "Couple Boy Girl Prompt" || it.displayName == "Couple Boy Girl" }
        assertNotNull("Couple Boy Girl Prompt category must exist", coupleBoyGirlCat)
        assertEquals(1, coupleBoyGirlCat?.promptCount)

        val islamicCat = index.find { it.name == "Islamic" || it.displayName == "Islamic" }
        assertNotNull("Islamic category must exist", islamicCat)
        assertEquals(1, islamicCat?.promptCount)
    }

    @Test
    fun testStrictCategoryFilteringNoMixing() {
        val index = SmartCategoryEngine.buildCategoryIndex(samplePrompts)

        val boyPrompt = samplePrompts[0]
        val boyGirlPrompt = samplePrompts[1]
        val couplePrompt = samplePrompts[2]
        val coupleBoyGirlPrompt = samplePrompts[3]
        val islamicPrompt = samplePrompts[4]

        // 1. "Boys" folder: ONLY Boy Prompt, NO Couple, NO Boy-Girl, NO Islamic
        assertTrue(SmartCategoryEngine.isPromptInCategory(boyPrompt, "Boys", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(couplePrompt, "Boys", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(boyGirlPrompt, "Boys", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(coupleBoyGirlPrompt, "Boys", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(islamicPrompt, "Boys", index))

        // 2. "Couples" folder: ONLY Couple Prompt, NO single boy, NO Islamic
        assertTrue(SmartCategoryEngine.isPromptInCategory(couplePrompt, "Couples", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(boyPrompt, "Couples", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(islamicPrompt, "Couples", index))

        // 3. "Girls + Boys" folder: ONLY Boy Girl Prompt
        assertTrue(SmartCategoryEngine.isPromptInCategory(boyGirlPrompt, "Girls + Boys", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(boyPrompt, "Girls + Boys", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(couplePrompt, "Girls + Boys", index))

        // 4. "Couple Boy Girl" folder: ONLY Couple Boy Girl Prompt
        assertTrue(SmartCategoryEngine.isPromptInCategory(coupleBoyGirlPrompt, "Couple Boy Girl", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(boyPrompt, "Couple Boy Girl", index))

        // 5. "Islamic" folder: ONLY Islamic / Eid Milad prompt
        assertTrue(SmartCategoryEngine.isPromptInCategory(islamicPrompt, "Islamic", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(boyPrompt, "Islamic", index))
        assertFalse(SmartCategoryEngine.isPromptInCategory(couplePrompt, "Islamic", index))
    }

    @Test
    fun testCategoryHidingWhenZeroPosts() {
        // Prompts with ONLY Boy Prompts
        val onlyBoyPrompts = listOf(samplePrompts[0])
        val index = SmartCategoryEngine.buildCategoryIndex(onlyBoyPrompts)

        // Only Boys category should be returned, others hidden
        assertEquals(1, index.size)
        assertEquals("Boys", index[0].displayName)

        val coupleCat = index.find { it.displayName == "Couples" }
        assertNull("Couples folder must be hidden when 0 matching posts exist", coupleCat)

        val islamicCat = index.find { it.displayName == "Islamic" }
        assertNull("Islamic folder must be hidden when 0 matching posts exist", islamicCat)
    }

    @Test
    fun testCategorySearch() {
        val index = SmartCategoryEngine.buildCategoryIndex(samplePrompts)

        val searchBoys = SmartCategoryEngine.searchCategories("boy", index)
        assertTrue(searchBoys.any { it.displayName == "Boys" })

        val searchCouples = SmartCategoryEngine.searchCategories("couple", index)
        assertTrue(searchCouples.any { it.displayName == "Couples" })

        val searchIslamic = SmartCategoryEngine.searchCategories("eid", index)
        assertTrue(searchIslamic.any { it.displayName == "Islamic" })

        val searchNonExistent = SmartCategoryEngine.searchCategories("supercalifragilistic", index)
        assertTrue(searchNonExistent.isEmpty())
    }
}

