package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.subtleGlow
import com.example.ui.viewmodel.MainViewModel

data class ToolDefinition(
    val id: String,
    val title: String,
    val shortLabel: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiToolsScreen(
    viewModel: MainViewModel,
    onNavigateToStudio: () -> Unit
) {
    var selectedToolIndex by remember { mutableStateOf(0) }

    val toolsList = remember {
        listOf(
            ToolDefinition("studio", "AI Prompt Builder", "Prompt Studio", Icons.Default.AutoFixHigh),
            ToolDefinition("enhancer", "Prompt Enhancer", "Enhancer", Icons.Default.AutoAwesome),
            ToolDefinition("shield", "Negative Shield", "Negative Shield", Icons.Default.Shield),
            ToolDefinition("aspect", "Aspect Ratio Calc", "Aspect Calc", Icons.Default.AspectRatio),
            ToolDefinition("hashtags", "Hashtag Indexer", "Hashtags", Icons.Default.Tag),
            ToolDefinition("cleaner", "Prompt Cleaner", "Cleaner", Icons.Default.CleaningServices)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Compact Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "AI CREATIVE TOOLSUITE",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "6 unified tools for prompt generation, enhancement & formatting",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Compact Tool Selector Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            itemsIndexed(toolsList, key = { _, tool -> tool.id }) { index, tool ->
                val isSelected = selectedToolIndex == index
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedToolIndex = index },
                    label = {
                        Text(
                            text = tool.shortLabel,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = tool.icon,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = if (isSelected) {
                        Modifier.subtleGlow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 4.dp,
                            alpha = 0.2f,
                            cornerRadius = 16.dp
                        )
                    } else Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active Tool Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            when (selectedToolIndex) {
                0 -> PromptBuilderTool(viewModel)
                1 -> PromptEnhancerTool(viewModel)
                2 -> NegativePromptTool(viewModel)
                3 -> AspectRatioTool(viewModel)
                4 -> HashtagTool(viewModel)
                5 -> PromptCleanerTool(viewModel)
            }
        }
    }
}

// 1. PROMPT BUILDER TOOL
@Composable
private fun PromptBuilderTool(viewModel: MainViewModel) {
    var subject by remember { mutableStateOf("Stylish Boy wearing kurta") }
    var selectedStyle by remember { mutableStateOf("3D Realistic Avatar") }
    var selectedLighting by remember { mutableStateOf("Cinematic Neon Studio") }
    var selectedPlatform by remember { mutableStateOf("Bing AI / Gemini") }
    var customKeyword by remember { mutableStateOf("8k resolution, detailed face, photorealistic") }

    val styles = listOf("3D Avatar", "Photorealistic", "Anime Manga", "Cinematic 8K", "Vector Art")
    val lightings = listOf("Cinematic Neon", "Golden Hour", "Soft Studio", "Moody Rim")
    val platforms = listOf("Bing AI / Gemini", "Midjourney", "DALL-E 3", "ChatGPT")

    val builtPrompt = remember(subject, selectedStyle, selectedLighting, selectedPlatform, customKeyword) {
        "A $selectedStyle of $subject. Lighting: $selectedLighting. Details: $customKeyword. Optimized for $selectedPlatform by AiPromptXpert."
    }

    GlassCard(cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "CUSTOM PROMPT BUILDER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject / Character", fontSize = 11.5.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Text(text = "Style Preset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(styles.size) { i ->
                    val s = styles[i]
                    FilterChip(
                        selected = selectedStyle == s,
                        onClick = { selectedStyle = s },
                        label = { Text(s, fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Text(text = "Lighting Preset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(lightings.size) { i ->
                    val l = lightings[i]
                    FilterChip(
                        selected = selectedLighting == l,
                        onClick = { selectedLighting = l },
                        label = { Text(l, fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            OutlinedTextField(
                value = customKeyword,
                onValueChange = { customKeyword = it },
                label = { Text("Custom Details / Keywords", fontSize = 11.5.sp) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Text(text = "Target AI Model", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(platforms.size) { i ->
                    val p = platforms[i]
                    FilterChip(
                        selected = selectedPlatform == p,
                        onClick = { selectedPlatform = p },
                        label = { Text(p, fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Output Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = builtPrompt,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }

            Button(
                onClick = { viewModel.copyPromptToClipboard(builtPrompt) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Copy Generated Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 2. PROMPT ENHANCER TOOL
@Composable
private fun PromptEnhancerTool(viewModel: MainViewModel) {
    var inputPrompt by remember { mutableStateOf("") }
    var enhancedOutput by remember { mutableStateOf("") }

    GlassCard(cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PROMPT ENHANCER & 8K UPSCALER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Transforms raw ideas into hyper-detailed cinematic prompts with studio lighting and lens parameters.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = inputPrompt,
                onValueChange = { inputPrompt = it },
                placeholder = { Text("Enter a simple prompt idea (e.g., boy in green hoodie with supercar)...", fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    val trimmed = inputPrompt.trim()
                    if (trimmed.isNotBlank()) {
                        enhancedOutput = "Hyper-realistic 8K photorealistic masterpiece of $trimmed, ultra detailed skin texture, dynamic cinematic studio rim lighting, 85mm lens f/1.4, volumetric atmospheric smoke, vivid color grading, octane render quality, trending on Artstation."
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Enhance to 8K Quality", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            if (enhancedOutput.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = enhancedOutput,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }

                FilledTonalButton(
                    onClick = { viewModel.copyPromptToClipboard(enhancedOutput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Copy Enhanced Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 3. NEGATIVE PROMPT SHIELD TOOL
@Composable
private fun NegativePromptTool(viewModel: MainViewModel) {
    val filters = remember {
        listOf(
            "Extra fingers / deformed limbs" to "extra fingers, mutated hands, poorly drawn hands, poorly drawn face, mutation, deformed",
            "Blurry & low resolution" to "blurry, bad anatomy, bad proportions, extra limbs, cloned face, disfigured, gross proportions",
            "Watermarks & logos" to "watermark, signature, text, logo, cropped, out of frame, low quality",
            "Over-saturation & dark shadows" to "oversaturated, bad shadow, bad lighting, grainy, low-res, jpeg artifacts"
        )
    }

    var selectedIndices by remember { mutableStateOf(setOf(0, 1, 2)) }

    val negativePrompt = remember(selectedIndices) {
        selectedIndices.mapNotNull { filters.getOrNull(it)?.second }.joinToString(", ")
    }

    GlassCard(cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "NEGATIVE PROMPT SHIELD",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Prevent distorted hands, blur, signatures, and artifacts in your AI artwork generation.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                filters.forEachIndexed { idx, (label, _) ->
                    val isChecked = selectedIndices.contains(idx)
                    FilterChip(
                        selected = isChecked,
                        onClick = {
                            selectedIndices = if (isChecked) selectedIndices - idx else selectedIndices + idx
                        },
                        label = { Text(label, fontSize = 11.5.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = "--no $negativePrompt",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 15.sp
                )
            }

            Button(
                onClick = { viewModel.copyPromptToClipboard("--no $negativePrompt") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Copy Negative Filter", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 4. ASPECT RATIO TOOL
@Composable
private fun AspectRatioTool(viewModel: MainViewModel) {
    val presets = remember {
        listOf(
            Triple("9:16 (Reels & TikTok)", "1080 x 1920", "--ar 9:16 --v 6.0"),
            Triple("16:9 (YouTube & 4K)", "3840 x 2160", "--ar 16:9 --v 6.0"),
            Triple("1:1 (Square Feed)", "1080 x 1080", "--ar 1:1 --v 6.0"),
            Triple("4:5 (Instagram Portrait)", "1080 x 1350", "--ar 4:5 --v 6.0")
        )
    }

    var selectedRatio by remember { mutableStateOf(presets[0]) }

    GlassCard(cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "ASPECT RATIO & RESOLUTION CALC",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Exact aspect ratio parameter tags and pixel dimensions for social media platforms.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                presets.forEach { preset ->
                    val isSelected = selectedRatio == preset
                    Surface(
                        onClick = { selectedRatio = preset },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = preset.first,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = preset.second,
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = preset.third,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { viewModel.copyPromptToClipboard(selectedRatio.third) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Copy Parameter Tag (${selectedRatio.third})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 5. HASHTAG TOOL
@Composable
private fun HashtagTool(viewModel: MainViewModel) {
    val tagGroups = remember {
        listOf(
            "Trending Viral" to "#AiPromptXpert #AiMAEditz #AIPrompt #AiArt #BingCreator #GeminiAI #ViralAI #DigitalArt",
            "Portrait & Boys" to "#BoyAI #3DCharacter #KurtaDesign #StylishBoyAI #CinematicPortrait #AIPhotoEditing #Avatar",
            "Photo Editing" to "#PhotoEditing #GeminiPrompt #LightroomAI #PhotoshopAI #AIArtCommunity #PromptShare"
        )
    }

    var selectedGroup by remember { mutableStateOf(tagGroups[0]) }

    GlassCard(cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "SMART HASHTAG INDEXER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Instant high-reach hashtags for sharing AI creations on TikTok, Instagram, and YouTube Shorts.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tagGroups.forEach { group ->
                    val isSelected = selectedGroup == group
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGroup = group },
                        label = { Text(group.first, fontSize = 11.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = selectedGroup.second,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.primary,
                    lineHeight = 16.sp
                )
            }

            Button(
                onClick = { viewModel.copyPromptToClipboard(selectedGroup.second) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Copy Hashtags", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// 6. PROMPT CLEANER TOOL
@Composable
private fun PromptCleanerTool(viewModel: MainViewModel) {
    var rawInput by remember { mutableStateOf("") }
    var cleanedResult by remember { mutableStateOf("") }

    GlassCard(cornerRadius = 14.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "PROMPT CLEANER & STRIPPER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Strips article intros, 'How to use' paragraphs, author credits, and hashtags from copied Blogger articles.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
                value = rawInput,
                onValueChange = { rawInput = it },
                placeholder = { Text("Paste article or raw prompt here to extract clean text...", fontSize = 12.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(10.dp)
            )

            Button(
                onClick = {
                    if (rawInput.isNotBlank()) {
                        cleanedResult = rawInput
                            .replace(Regex("(?i)#\\w+"), "")
                            .replace(Regex("(?i)^(Prompt:|AI Prompt:|Prompt Text:|Copy Prompt:)\\s*"), "")
                            .replace(Regex("(?i)(Created by|Powered by|Follow us).*"), "")
                            .trim()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Extract & Clean Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            if (cleanedResult.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            RoundedCornerShape(10.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = cleanedResult,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 16.sp
                    )
                }

                FilledTonalButton(
                    onClick = { viewModel.copyPromptToClipboard(cleanedResult) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Copy Pure Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
