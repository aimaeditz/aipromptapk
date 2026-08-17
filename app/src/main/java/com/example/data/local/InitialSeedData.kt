package com.example.data.local

import com.example.data.model.AiTool
import com.example.data.model.GalleryImage
import com.example.data.model.PromptItem
import com.example.data.model.TutorialItem

object InitialSeedData {

    val PROMPTS = listOf(
        PromptItem(
            id = "prompt_119",
            promptCode = "#119",
            title = "3D AI Couple Eid Special Portrait",
            category = "Couple Prompts",
            platform = "Gemini",
            description = "A 3D realistic AI avatar couple dressed in elegant traditional embroidered kurta and saree standing under warm festive lights.",
            exactPrompt = "A realistic 3D digital illustration of an attractive young couple. The boy is wearing a royal blue embroidered designer Kurta, and the girl is wearing a pastel pink embellished Saree. They are standing hand in hand in a beautifully illuminated courtyard decorated with fairy lights and crescent moons. High detail 8K, cinematic lighting, photorealistic textures, soft focus background.",
            imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = true,
            tags = "Couple, Eid, Islamic, Blue Kurta, Gemini, 3D Avatar",
            sourceUrl = "https://aimaeditz.blogspot.com/2026/01/3d-couple-eid-prompts.html"
        ),
        PromptItem(
            id = "prompt_101",
            promptCode = "#101",
            title = "3D Stylish AI Boy Studio Creator Avatar",
            category = "Boy Prompts",
            platform = "Bing AI",
            description = "A modern 3D AI avatar boy wearing a stylish green hoodie with 'MAEDITZ' glowing neon sign in the studio.",
            exactPrompt = "Create a 3D realistic illustration of a 22-year-old stylish boy sitting at a modern tech creator desk with an Apple laptop and professional microphone. He is wearing a dark green hoodie, smiling confidently. In the background, there is a glowing violet neon light sign that says 'MAEDITZ' on a acoustic soundproof wall. Ultra HD 8k, detailed lighting, sharp focus.",
            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = true,
            tags = "Boy, 3D, MAEDITZ, Studio, Creator, Green Hoodie",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/3d-boy-creator-prompt.html"
        ),
        PromptItem(
            id = "prompt_104",
            promptCode = "#104",
            title = "Cinematic Luxury Fashion AI Girl",
            category = "Girl Prompts",
            platform = "Midjourney",
            description = "An ultra-chic fashion portrait of an AI girl in luxury haute couture outfit with moody neon rim lighting.",
            exactPrompt = "Full length hyper-realistic fashion photograph of an elegant 21-year-old female model in high-fashion black velvet attire, standing against a dark sleek futuristic cityscape reflection. Soft cyan and magenta ambient light highlights her features. Shot on 85mm lens f/1.4, cinematic color grading, magazine cover quality.",
            imageUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = false,
            tags = "Girl, Fashion, Luxury, Midjourney, Fashion Model",
            sourceUrl = "https://aimaeditz.blogspot.com/2026/01/fashion-girl-prompts.html"
        ),
        PromptItem(
            id = "prompt_106",
            promptCode = "#106",
            title = "Islamic Moonlit Mosque Reflection Portrait",
            category = "Islamic Prompts",
            platform = "DALL-E 3",
            description = "Serene Islamic AI concept featuring a majestic glowing golden dome mosque with serene water reflection.",
            exactPrompt = "A breathtaking 3D artistic composition of a grand Islamic mosque architecture illuminated with glowing golden crescent moon above. A young man in white traditional Thobe is standing respectfully in prayer on marble floor with marble reflections. Peaceful starry night sky background, cinematic depth of field, photorealistic 8k.",
            imageUrl = "https://images.unsplash.com/photo-1542810634-71277d95dcbb?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = true,
            tags = "Islamic, Mosque, Eid, Reflection, Golden Dome",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/islamic-mosque-prompts.html"
        ),
        PromptItem(
            id = "prompt_108",
            promptCode = "#108",
            title = "Royal Indian Wedding AI Bride & Groom",
            category = "Wedding Prompts",
            platform = "Gemini",
            description = "Exquisite royal wedding AI portrait with rich gold embroidered Sherwani and bridal Lehenga.",
            exactPrompt = "Cinematic 8K portrait of a handsome groom in royal ivory silk Sherwani with emerald necklace and a beautiful bride in maroon embroidered velvet Lehenga. They stand under a canopy of white roses and golden chandeliers. Professional wedding photography style, soft natural shadows, rich textures.",
            imageUrl = "https://images.unsplash.com/photo-1583939003579-730e3918a45a?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = false,
            tags = "Wedding, Bride, Groom, Royal, Sherwani, Lehenga",
            sourceUrl = "https://aimaeditz.blogspot.com/2026/01/royal-wedding-prompts.html"
        ),
        PromptItem(
            id = "prompt_109",
            promptCode = "#109",
            title = "Cinematic Cyberpunk Neon Street Photography",
            category = "Cinematic Prompts",
            platform = "Midjourney",
            description = "Moody cinematic street portrait bathed in rainy wet neon reflection in a futuristic metropolis.",
            exactPrompt = "Cinematic movie still of a lonely traveler walking through a futuristic Tokyo street at night during heavy rain. Neon street lights reflecting on wet asphalt puddles. Moody teal and orange atmosphere, shot on anamorphic lens, 35mm film grain, 8K resolution.",
            imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop",
            isFeatured = false,
            isTrending = true,
            tags = "Cinematic, Cyberpunk, Rain, Tokyo, Anamorphic",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/cinematic-neon-street.html"
        ),
        PromptItem(
            id = "prompt_111",
            promptCode = "#111",
            title = "Luxury Supercar Matte Black Sunset Concept",
            category = "Luxury Prompts",
            platform = "Bing AI",
            description = "A sleek matte black hypercar parked on a cliff side overlooking a golden hour coastal sunset.",
            exactPrompt = "A photorealistic commercial automotive shot of a futuristic matte black luxury supercar parked on a scenic mountain coastal highway during golden hour sunset. Warm dramatic sunlight catching aerodynamic carbon fiber curves. Crisp reflections, ultra sharp details, 8k resolution.",
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = true,
            tags = "Luxury, Cars, Supercar, Sunset, Matte Black",
            sourceUrl = "https://aimaeditz.blogspot.com/2026/01/luxury-car-prompts.html"
        ),
        PromptItem(
            id = "prompt_113",
            promptCode = "#113",
            title = "Google Gemini AI Photo Expansion & Color Grading",
            category = "AI Editing",
            platform = "Gemini",
            description = "Transform ordinary phone photos into studio-grade professional portraits with AI lighting prompts.",
            exactPrompt = "Enhance lighting and color spectrum: Apply warm golden studio rim light from top right, convert background to soft atmospheric dark bokeh gradient, sharpen face details, smooth skin texture while preserving natural pores, professional color balance in vintage cinematic tone.",
            imageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&auto=format&fit=crop",
            isFeatured = true,
            isTrending = true,
            tags = "AI Editing, Gemini, Color Grading, Studio Lighting",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/gemini-photo-editing.html"
        ),
        PromptItem(
            id = "prompt_115",
            promptCode = "#115",
            title = "Futuristic Sports Car Cyber Drift",
            category = "Cars",
            platform = "ChatGPT",
            description = "High speed action drift shot of a cyber sports car with smoke and glowing rim lights.",
            exactPrompt = "Action camera tracking shot of a glowing futuristic sports vehicle drifting around a high-speed circuit curve. Tire smoke illuminated by neon lights beneath the chassis, motion blur on wheels, crisp focus on front headlamps, 8k cinematic masterpiece.",
            imageUrl = "https://images.unsplash.com/photo-1542282088-72c9c27ed0cd?w=800&auto=format&fit=crop",
            isFeatured = false,
            isTrending = false,
            tags = "Cars, Drift, Action, Cyberpunk, Speed",
            sourceUrl = "https://aimaeditz.blogspot.com/2026/01/cyber-drift-prompts.html"
        ),
        PromptItem(
            id = "prompt_118",
            promptCode = "#118",
            title = "Tropical Island Paradise Aerial Travel Photography",
            category = "Travel",
            platform = "DALL-E 3",
            description = "Stunning aerial drone perspective of turquoise ocean waters surrounding a secluded palm island.",
            exactPrompt = "Top-down 4k drone photography of a small lush green tropical island surrounded by crystal clear turquoise ocean water and white coral sands. A wooden pier stretches into the calm water. Vibrant natural sunlight, vivid tropical colors, hyper detailed.",
            imageUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800&auto=format&fit=crop",
            isFeatured = false,
            isTrending = true,
            tags = "Travel, Ocean, Tropical, Drone, Island",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/travel-drone-prompts.html"
        )
    )

    val GALLERY_IMAGES = listOf(
        GalleryImage(
            id = "gallery_1",
            title = "3D Couple Eid Special",
            imageUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=800&auto=format&fit=crop",
            promptId = "prompt_119",
            promptCode = "#119",
            category = "Couple Prompts",
            exactPrompt = "A realistic 3D digital illustration of an attractive young couple. The boy is wearing a royal blue embroidered designer Kurta, and the girl is wearing a pastel pink embellished Saree.",
            tags = "Couple, Eid, 3D"
        ),
        GalleryImage(
            id = "gallery_2",
            title = "MAEDITZ Studio Creator",
            imageUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=800&auto=format&fit=crop",
            promptId = "prompt_101",
            promptCode = "#101",
            category = "Boy Prompts",
            exactPrompt = "Create a 3D realistic illustration of a 22-year-old stylish boy sitting at a modern tech creator desk with an Apple laptop and professional microphone.",
            tags = "Boy, 3D, Creator"
        ),
        GalleryImage(
            id = "gallery_3",
            title = "Luxury Fashion Girl",
            imageUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=800&auto=format&fit=crop",
            promptId = "prompt_104",
            promptCode = "#104",
            category = "Girl Prompts",
            exactPrompt = "Full length hyper-realistic fashion photograph of an elegant 21-year-old female model in high-fashion black velvet attire.",
            tags = "Girl, Fashion"
        ),
        GalleryImage(
            id = "gallery_4",
            title = "Golden Mosque Reflection",
            imageUrl = "https://images.unsplash.com/photo-1542810634-71277d95dcbb?w=800&auto=format&fit=crop",
            promptId = "prompt_106",
            promptCode = "#106",
            category = "Islamic Prompts",
            exactPrompt = "A breathtaking 3D artistic composition of a grand Islamic mosque architecture illuminated with glowing golden crescent moon above.",
            tags = "Islamic, Mosque"
        ),
        GalleryImage(
            id = "gallery_5",
            title = "Matte Black Supercar",
            imageUrl = "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=800&auto=format&fit=crop",
            promptId = "prompt_111",
            promptCode = "#111",
            category = "Luxury Prompts",
            exactPrompt = "A photorealistic commercial automotive shot of a futuristic matte black luxury supercar parked on a scenic mountain coastal highway.",
            tags = "Cars, Luxury"
        )
    )

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
            stepsJson = """["Open Bing Image Creator or Microsoft Copilot app.", "Copy prompt #101 or #119 from AiPromptXpert app.", "Paste the prompt into Bing text box.", "Replace 'MAEDITZ' or 'ABID' with your own name in quotation marks.", "Click 'Create' and download your HD 3D avatar."]""",
            relatedPromptId = "prompt_101",
            category = "3D Avatars",
            sourceUrl = "https://aimaeditz.blogspot.com/2026/01/bing-3d-avatar-tutorial.html"
        ),
        TutorialItem(
            id = "tutorial_2",
            title = "Master Google Gemini Photo Editing Prompts",
            coverImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=800&auto=format&fit=crop",
            introduction = "Transform low quality phone photos into professional studio portraits using Gemini text-to-photo editing prompts.",
            stepsJson = """["Upload your clear portrait photo to Gemini.", "Copy Gemini photo editing prompt #113.", "Add your specific lighting and background requirements.", "Generate and apply color grading presets."]""",
            relatedPromptId = "prompt_113",
            category = "Gemini AI",
            sourceUrl = "https://aipromptxpert.blogspot.com/2026/02/gemini-editing-guide.html"
        )
    )
}
