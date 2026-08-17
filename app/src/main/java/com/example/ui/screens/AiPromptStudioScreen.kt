package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GeneratorOptions
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPromptStudioScreen(
    viewModel: MainViewModel
) {
    var selectedToolTab by remember { mutableStateOf(0) } // 0 = Generator, 1 = Enhancer/Cleaner
    val toolTabs = listOf("AI Prompt Generator", "Prompt Enhancer & Tools")

    // Generator State
    var subject by remember { mutableStateOf("Stylish 22-year-old Boy") }
    var selectedStyle by remember { mutableStateOf("3D Realistic Avatar") }
    var selectedLighting by remember { mutableStateOf("Cinematic Studio Neon Lights") }
    var selectedCamera by remember { mutableStateOf("Eye Level Portrait, 85mm f/1.4 Lens") }
    var selectedEnvironment by remember { mutableStateOf("Modern Tech Studio with MAEDITZ Sign") }
    var selectedPlatform by remember { mutableStateOf("Bing AI / Gemini") }
    var customKeyword by remember { mutableStateOf("wearing dark green hoodie, smiling, 8k resolution") }

    // Enhancer State
    var inputPromptToEnhance by remember { mutableStateOf("") }
    var enhancedResult by remember { mutableStateOf("") }

    val styles = listOf("3D Realistic Avatar", "Photorealistic Portrait", "Anime Cyberpunk", "Vector Art", "Cinematic Movie Still")
    val lightings = listOf("Cinematic Studio Neon Lights", "Golden Hour Sunset", "Soft Natural Light", "Moody Rim Light")
    val platforms = listOf("Bing AI / Gemini", "Midjourney", "DALL-E 3", "ChatGPT")

    // Generated Prompt Computation
    val generatedPrompt = remember(
        subject, selectedStyle, selectedLighting, selectedCamera, selectedEnvironment, selectedPlatform, customKeyword
    ) {
        "A $selectedStyle of $subject. Environment: $selectedEnvironment. Lighting: $selectedLighting. Camera & Lens: $selectedCamera. Details: $customKeyword. Optimized for $selectedPlatform by AiPromptXpert."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = "AI PROMPT STUDIO", fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
        Text(text = "Craft, enhance & format custom AI image prompts for Gemini, Bing & Midjourney", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedToolTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            toolTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedToolTab == index,
                    onClick = { selectedToolTab = index },
                    text = { Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedToolTab == 0) {
            // PROMPT GENERATOR FORM
            GlassCard(cornerRadius = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "CUSTOM PROMPT GENERATOR", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = { Text("Subject / Character") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Style Selector
                    Text(text = "Art Style", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        styles.take(3).forEach { style ->
                            FilterChip(
                                selected = selectedStyle == style,
                                onClick = { selectedStyle = style },
                                label = { Text(style, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Lighting Selector
                    Text(text = "Lighting & Atmosphere", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        lightings.take(2).forEach { light ->
                            FilterChip(
                                selected = selectedLighting == light,
                                onClick = { selectedLighting = light },
                                label = { Text(light, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = selectedEnvironment,
                        onValueChange = { selectedEnvironment = it },
                        label = { Text("Environment / Background") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = customKeyword,
                        onValueChange = { customKeyword = it },
                        label = { Text("Clothing / Pose / Keywords") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Platform Selector
                    Text(text = "Target AI Platform", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        platforms.forEach { plat ->
                            FilterChip(
                                selected = selectedPlatform == plat,
                                onClick = { selectedPlatform = plat },
                                label = { Text(plat, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // GENERATED RESULT BOX
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "GENERATED PROMPT RESULT", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = generatedPrompt,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.copyPromptToClipboard(generatedPrompt) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Copy Generated Prompt", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // PROMPT ENHANCER & TOOLS TAB
            GlassCard(cornerRadius = 16.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "PROMPT ENHANCER & CLEANER", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = inputPromptToEnhance,
                        onValueChange = { inputPromptToEnhance = it },
                        placeholder = { Text("Paste raw or simple prompt here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (inputPromptToEnhance.isNotBlank()) {
                                    enhancedResult = "Hyper-realistic 8k masterpiece portrait of ${inputPromptToEnhance.trim()}, ultra detailed textures, studio rim lighting, 85mm lens f/1.4, cinematic atmosphere, octane render quality."
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Enhance Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                if (inputPromptToEnhance.isNotBlank()) {
                                    // Cleaner strips hashtags and intro fluff
                                    enhancedResult = inputPromptToEnhance
                                        .replace(Regex("(?i)#\\w+"), "")
                                        .replace(Regex("(?i)^(Prompt:|AI Prompt:|Copy Prompt:)\\s*"), "")
                                        .trim()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Clean Prompt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (enhancedResult.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "ENHANCED OUTPUT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = enhancedResult, fontSize = 13.sp, fontFamily = FontFamily.Monospace)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.copyPromptToClipboard(enhancedResult) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Copy Clean Result", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}
