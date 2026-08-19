package com.example.data.local

import com.example.data.model.AiTool
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import com.example.data.model.TutorialItem

object InitialSeedData {

    // Strictly empty by default - prompts are populated 100% exclusively from real Blogger feeds
    val PROMPTS = emptyList<PromptItem>()
    val GALLERY_IMAGES = emptyList<GalleryImage>()

    val TOOLS = listOf(
        AiTool(
            id = "tool_1",
            name = "Bing Image Creator",
            iconUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120&auto=format&fit=crop",
            category = "AI Image Generation",
            description = "Powered by DALL-E 3. Generate stunning 3D avatars, photo concepts, and artistic images for free.",
            websiteUrl = "https://www.bing.com/create",
            isFeatured = true,
            badge = "FREE"
        ),
        AiTool(
            id = "tool_2",
            name = "Google Gemini AI",
            iconUrl = "https://images.unsplash.com/photo-1677442136019-21780ecad995?w=120&auto=format&fit=crop",
            category = "Writing AI",
            description = "Google's powerful multimodal AI model for prompt expansion, photo editing logic, and creative brainstorming.",
            websiteUrl = "https://gemini.google.com",
            isFeatured = true,
            badge = "FREE"
        ),
        AiTool(
            id = "tool_3",
            name = "Midjourney",
            iconUrl = "https://images.unsplash.com/photo-1620712943543-bcc4688e7485?w=120&auto=format&fit=crop",
            category = "AI Image Generation",
            description = "Industry standard for photorealistic AI imagery, hyper-realistic character design, and cinematic renders.",
            websiteUrl = "https://www.midjourney.com",
            isFeatured = true,
            badge = "PAID"
        ),
        AiTool(
            id = "tool_4",
            name = "ChatGPT",
            iconUrl = "https://images.unsplash.com/photo-1684369175833-2895f8bc8789?w=120&auto=format&fit=crop",
            category = "Writing AI",
            description = "OpenAI's conversational AI assistant. Perfect for crafting detailed DALL-E prompts and creative scripts.",
            websiteUrl = "https://chatgpt.com",
            isFeatured = false,
            badge = "FREE / PRO"
        ),
        AiTool(
            id = "tool_5",
            name = "Leonardo.Ai",
            iconUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=120&auto=format&fit=crop",
            category = "AI Photo Editing",
            description = "Custom fine-tuned AI models for game assets, character art, motion generation, and prompt alchemy.",
            websiteUrl = "https://leonardo.ai",
            isFeatured = false,
            badge = "FREEMIUM"
        )
    )

    val TUTORIALS = listOf(
        TutorialItem(
            id = "tutorial_1",
            title = "How to Create 3D AI Avatars on Bing Image Creator (Step-by-Step)",
            coverImageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop",
            introduction = "Learn how to use AiMAEditz prompt templates to generate customized 3D realistic avatars with your name on hoodies and studio signboards.",
            stepsJson = """["Open Bing Image Creator or Microsoft Copilot app.", "Copy prompt from AiPromptXpert app.", "Paste the prompt into Bing text box.", "Replace 'MAEDITZ' or 'ABID' with your own name in quotation marks.", "Click 'Create' and download your HD 3D avatar."]""",
            relatedPromptId = "",
            category = "3D Avatars",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/01/bing-3d-avatar-tutorial.html"
        ),
        TutorialItem(
            id = "tutorial_2",
            title = "Master Google Gemini Photo Editing Prompts",
            coverImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&auto=format&fit=crop",
            introduction = "Transform low quality phone photos into professional studio portraits using Gemini text-to-photo editing prompts.",
            stepsJson = """["Upload your clear portrait photo to Gemini.", "Copy Gemini photo editing prompt from AiPromptXpert.", "Add your specific lighting and background requirements.", "Generate and apply color grading presets."]""",
            relatedPromptId = "",
            category = "Gemini AI",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/gemini-editing-guide.html"
        )
    )
}
