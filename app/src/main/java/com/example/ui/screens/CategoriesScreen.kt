package com.example.ui.screens

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
import com.example.ui.components.GlassCard
import com.example.ui.components.premiumPressEffect
import com.example.ui.components.subtleGlow
import com.example.ui.viewmodel.MainViewModel

data class CategoryItemInfo(
    val name: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun CategoriesScreen(
    viewModel: MainViewModel,
    onCategorySelected: (String) -> Unit
) {
    val allPrompts by viewModel.allPrompts.collectAsState()

    val categoriesList = remember {
        listOf(
            CategoryItemInfo("Boy Prompts", "Cinematic 3D boy avatars & portraits", Icons.Default.Person),
            CategoryItemInfo("Girl Prompts", "Aesthetic fashion & glamour portraits", Icons.Default.Face),
            CategoryItemInfo("Couple Prompts", "Romantic & festive 3D couple compositions", Icons.Default.Favorite),
            CategoryItemInfo("Islamic Prompts", "Sacred mosques & Islamic artistic concepts", Icons.Default.Mosque),
            CategoryItemInfo("Eid Milad Prompts", "Eid celebrations, festive lights & art", Icons.Default.Nightlight),
            CategoryItemInfo("Cinematic Prompts", "Dramatic lighting & movie poster styles", Icons.Default.Movie),
            CategoryItemInfo("8K Prompts", "Ultra high-resolution photorealistic detail", Icons.Default.HighQuality),
            CategoryItemInfo("Luxury Prompts", "Supercars, mansions & royal lifestyles", Icons.Default.Diamond),
            CategoryItemInfo("Portrait Prompts", "Studio lighting & natural facial textures", Icons.Default.Portrait),
            CategoryItemInfo("Fashion Prompts", "Runway looks, streetwear & aesthetics", Icons.Default.Checkroom),
            CategoryItemInfo("Car Prompts", "Exotic hypercars & modified vehicles", Icons.Default.DirectionsCar),
            CategoryItemInfo("Kids Prompts", "Cute animated characters & 3D art", Icons.Default.ChildCare),
            CategoryItemInfo("Wedding Prompts", "Bridal couture & royal wedding themes", Icons.Default.VolunteerActivism),
            CategoryItemInfo("Travel Prompts", "Scenic landscapes & global wonders", Icons.Default.FlightTakeoff),
            CategoryItemInfo("AI Editing", "Photo retouching & background replacement", Icons.Default.AutoFixHigh),
            CategoryItemInfo("Cyberpunk", "Futuristic neon cities & holographic art", Icons.Default.ElectricBolt),
            CategoryItemInfo("Anime Art", "Studio Ghibli & manga character styles", Icons.Default.Brush),
            CategoryItemInfo("Vintage & Retro", "90s film grain, polaroid & analog vibes", Icons.Default.CameraAlt),
            CategoryItemInfo("Dark Fantasy", "Mythical creatures & epic medieval magic", Icons.Default.Shield),
            CategoryItemInfo("Nature & Wildlife", "Forests, oceans & majestic animal shots", Icons.Default.Forest),
            CategoryItemInfo("Google Gemini", "Gemini multimodal photo editing guides", Icons.Default.AutoAwesome),
            CategoryItemInfo("ChatGPT", "DALL-E 3 prompts & conversational styles", Icons.Default.Chat),
            CategoryItemInfo("Midjourney", "Hyper-detailed render styles & prompts", Icons.Default.Palette)
        )
    }

    // Pre-calculate counts once when allPrompts updates to guarantee zero frame drops during scrolling
    val categoryCounts = remember(allPrompts) {
        categoriesList.associate { cat ->
            val count = allPrompts.count {
                it.category.contains(cat.name, ignoreCase = true) ||
                it.tags.contains(cat.name, ignoreCase = true) ||
                (cat.name.contains("Gemini") && it.platform.contains("Gemini", ignoreCase = true)) ||
                (cat.name.contains("ChatGPT") && it.platform.contains("ChatGPT", ignoreCase = true)) ||
                (cat.name.contains("Midjourney") && it.platform.contains("Midjourney", ignoreCase = true)) ||
                (cat.name.contains("Cyberpunk") && (it.tags.contains("cyberpunk", ignoreCase = true) || it.title.contains("cyberpunk", ignoreCase = true))) ||
                (cat.name.contains("Anime") && (it.tags.contains("anime", ignoreCase = true) || it.title.contains("anime", ignoreCase = true))) ||
                (cat.name.contains("Car") && (it.category.contains("Car", ignoreCase = true) || it.tags.contains("car", ignoreCase = true)))
            }
            cat.name to count
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "PROMPT CATEGORIES",
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Browse ${categoriesList.size} authentic prompt collections by topic & style",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 145.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 20.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(categoriesList, key = { it.name }) { cat ->
                val promptCount = categoryCounts[cat.name] ?: 0

                GlassCard(
                    cornerRadius = 12.dp,
                    onClick = {
                        viewModel.setCategory(cat.name)
                        onCategorySelected(cat.name)
                    },
                    modifier = Modifier.premiumPressEffect()
                ) {
                    Column(
                        modifier = Modifier.padding(2.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = cat.name,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            }

                            if (promptCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "$promptCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }

                        // Title strictly locked to 1 line without wrapping or truncation issues
                        Text(
                            text = cat.name,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = cat.description,
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            lineHeight = 13.5.sp,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
