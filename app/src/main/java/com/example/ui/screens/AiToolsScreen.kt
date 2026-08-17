package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.components.GlassCard
import com.example.ui.viewmodel.MainViewModel

data class BuiltInToolItem(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val actionText: String,
    val onAction: () -> Unit
)

@Composable
fun AiToolsScreen(
    viewModel: MainViewModel,
    onNavigateToStudio: () -> Unit
) {
    val context = LocalContext.current
    val favorites by viewModel.favorites.collectAsState()

    val builtInTools = listOf(
        BuiltInToolItem(
            id = "tool_studio",
            name = "AI Prompt Studio",
            category = "Prompt Builder",
            description = "Custom prompt generator with cinematic styles, lighting presets, lens focal lengths, and 8K rendering tags.",
            icon = Icons.Default.AutoFixHigh,
            actionText = "Open Studio",
            onAction = onNavigateToStudio
        ),
        BuiltInToolItem(
            id = "tool_gemini_helper",
            name = "Gemini Photo Prompt Formatter",
            category = "Prompt Optimization",
            description = "Format and structure photo editing instructions optimized specifically for Google Gemini multimodal models.",
            icon = Icons.Default.AutoAwesome,
            actionText = "Launch Formatter",
            onAction = onNavigateToStudio
        ),
        BuiltInToolItem(
            id = "tool_negative_prompts",
            name = "Negative Prompt Shield",
            category = "Quality Control",
            description = "Pre-configured negative prompt filters for removing artifacts, extra limbs, blur, watermark, and low quality.",
            icon = Icons.Default.Shield,
            actionText = "Open Shield",
            onAction = onNavigateToStudio
        ),
        BuiltInToolItem(
            id = "tool_aspect_ratio",
            name = "Aspect Ratio & Resolution Calc",
            category = "Rendering Helper",
            description = "Calculate exact pixel dimensions for 16:9, 9:16 Instagram Reels, 4:5 Portrait, and 1:1 Square prompt parameters.",
            icon = Icons.Default.AspectRatio,
            actionText = "Calculate",
            onAction = onNavigateToStudio
        ),
        BuiltInToolItem(
            id = "tool_tag_indexer",
            name = "Smart Prompt Tag Indexer",
            category = "SEO & Discovery",
            description = "Generate trending hashtags and categorization tags for sharing AI creations across social channels.",
            icon = Icons.Default.Tag,
            actionText = "Generate Tags",
            onAction = onNavigateToStudio
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "AI PROMPT TOOLS",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = "Interactive utilities & prompt generation tools by AiMAEditz",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(builtInTools, key = { it.id }) { tool ->
                GlassCard(
                    cornerRadius = 16.dp,
                    onClick = tool.onAction
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = tool.icon,
                                    contentDescription = tool.name,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = tool.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = tool.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(3.dp))

                            Text(
                                text = tool.description,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = tool.onAction,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp)
                            ) {
                                Text(text = tool.actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
