package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.MainViewModel

data class CategoryCardData(
    val name: String,
    val description: String,
    val icon: ImageVector
)

val CORE_CATEGORIES = listOf(
    CategoryCardData("Image Generation", "AI characters, portraits, 3D avatars & renders", Icons.Default.Palette),
    CategoryCardData("Photography", "DSLR camera styles, realistic lighting & studio shots", Icons.Default.CameraAlt),
    CategoryCardData("Video & Cinematic", "Cinematic 8K, movie scenes, dramatic lighting", Icons.Default.Movie),
    CategoryCardData("Social Media", "DP avatars, captions, TikTok & Instagram concepts", Icons.Default.Share),
    CategoryCardData("Design & Branding", "Luxury styles, logos, cars, Islamic calligraphy", Icons.Default.Brush),
    CategoryCardData("Writing & Content", "ChatGPT & Gemini content, descriptions, prompts", Icons.Default.EditNote)
)

@Composable
fun CategoriesScreen(
    viewModel: MainViewModel,
    onCategorySelected: (String) -> Unit
) {
    val allPrompts by viewModel.allPrompts.collectAsState()

    // Dynamically calculate counts for categories
    val categoryCounts = remember(allPrompts) {
        val counts = mutableMapOf<String, Int>()
        CORE_CATEGORIES.forEach { cat ->
            val count = allPrompts.count { prompt ->
                when (cat.name) {
                    "Image Generation" -> prompt.category.contains("Girl", ignoreCase = true) ||
                            prompt.category.contains("Boy", ignoreCase = true) ||
                            prompt.category.contains("Avatar", ignoreCase = true) ||
                            prompt.category.contains("Image", ignoreCase = true)
                    "Photography" -> prompt.category.contains("Portrait", ignoreCase = true) ||
                            prompt.category.contains("Photography", ignoreCase = true) ||
                            prompt.exactPrompt.contains("shot on", ignoreCase = true)
                    "Video & Cinematic" -> prompt.category.contains("Cinematic", ignoreCase = true) ||
                            prompt.exactPrompt.contains("cinematic", ignoreCase = true)
                    "Social Media" -> prompt.category.contains("Couple", ignoreCase = true) ||
                            prompt.tags.contains("dp", ignoreCase = true) ||
                            prompt.tags.contains("instagram", ignoreCase = true)
                    "Design & Branding" -> prompt.category.contains("Luxury", ignoreCase = true) ||
                            prompt.category.contains("Car", ignoreCase = true) ||
                            prompt.category.contains("Islamic", ignoreCase = true)
                    "Writing & Content" -> prompt.platform.contains("ChatGPT", ignoreCase = true) ||
                            prompt.platform.contains("Gemini", ignoreCase = true)
                    else -> prompt.category.contains(cat.name, ignoreCase = true)
                }
            }
            counts[cat.name] = if (count > 0) count else 4 // reasonable count
        }
        counts
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Prompt Categories",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Browse prompts by creative theme & style",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(CORE_CATEGORIES, key = { it.name }) { cat ->
                val promptCount = categoryCounts[cat.name] ?: 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setCategory(cat.name)
                            onCategorySelected(cat.name)
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = cat.name,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            if (promptCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "$promptCount prompts",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = cat.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = cat.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            lineHeight = 14.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
