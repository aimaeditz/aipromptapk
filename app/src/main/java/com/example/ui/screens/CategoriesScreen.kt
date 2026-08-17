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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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

    val categoriesList = listOf(
        CategoryItemInfo("Boy Prompts", "Cinematic portraits & stylish 3D character avatars", Icons.Default.Person),
        CategoryItemInfo("Girl Prompts", "Aesthetic fashion, glamour & portrait concepts", Icons.Default.Face),
        CategoryItemInfo("Couple Prompts", "Romantic & festive 3D couple compositions", Icons.Default.Favorite),
        CategoryItemInfo("Islamic Prompts", "Sacred mosque architecture & Islamic art", Icons.Default.Mosque),
        CategoryItemInfo("Eid Milad Prompts", "Eid celebrations, festive lights & greeting art", Icons.Default.Nightlight),
        CategoryItemInfo("Cinematic Prompts", "8K movie poster styles & dramatic lighting", Icons.Default.Movie),
        CategoryItemInfo("8K Prompts", "Ultra high-resolution photorealistic detail", Icons.Default.HighQuality),
        CategoryItemInfo("Luxury Prompts", "Supercars, mansions & luxury lifestyles", Icons.Default.Diamond),
        CategoryItemInfo("Portrait Prompts", "Studio lighting & natural facial textures", Icons.Default.Portrait),
        CategoryItemInfo("Kids Prompts", "Cute animated characters & playful 3D art", Icons.Default.ChildCare),
        CategoryItemInfo("Wedding Prompts", "Bridal couture, mehndi & royal wedding themes", Icons.Default.VolunteerActivism),
        CategoryItemInfo("Fashion Prompts", "Runway looks, streetwear & aesthetic outfits", Icons.Default.Checkroom),
        CategoryItemInfo("Car Prompts", "Exotic hypercars, modified rides & automotive", Icons.Default.DirectionsCar),
        CategoryItemInfo("Travel Prompts", "Scenic landscapes, wonders & tourist spots", Icons.Default.FlightTakeoff),
        CategoryItemInfo("AI Editing", "Photo manipulation, retouching & background swap", Icons.Default.AutoFixHigh),
        CategoryItemInfo("Google Gemini", "Gemini multimodal photo editing prompt guides", Icons.Default.AutoAwesome),
        CategoryItemInfo("ChatGPT", "DALL-E 3 prompts & creative conversational styles", Icons.Default.Chat),
        CategoryItemInfo("Midjourney", "Hyper-detailed render styles & photorealism", Icons.Default.Palette)
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
                imageVector = Icons.Default.Category,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "PROMPT CATEGORIES",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Browse authentic prompt collections by topic & style",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(categoriesList, key = { it.name }) { cat ->
                val promptCount = allPrompts.count {
                    it.category.contains(cat.name, ignoreCase = true) ||
                    it.tags.contains(cat.name, ignoreCase = true) ||
                    (cat.name.contains("Gemini") && it.platform.contains("Gemini", ignoreCase = true)) ||
                    (cat.name.contains("ChatGPT") && it.platform.contains("ChatGPT", ignoreCase = true)) ||
                    (cat.name.contains("Midjourney") && it.platform.contains("Midjourney", ignoreCase = true))
                }

                GlassCard(
                    cornerRadius = 14.dp,
                    onClick = {
                        viewModel.setCategory(cat.name)
                        onCategorySelected(cat.name)
                    },
                    modifier = Modifier.premiumPressEffect()
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .size(36.dp)
                                    .subtleGlow(
                                        color = MaterialTheme.colorScheme.primary,
                                        radius = 4.dp,
                                        alpha = 0.2f,
                                        cornerRadius = 8.dp
                                    )
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = cat.name,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
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
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = cat.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )

                        Text(
                            text = cat.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
