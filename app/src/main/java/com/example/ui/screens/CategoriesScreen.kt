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

    // 100% data-driven categories extracted exclusively from real Blogger posts
    val categoriesList = remember(allPrompts) {
        val distinctLabels = allPrompts
            .map { it.category.trim() }
            .filter { it.isNotBlank() && it != "All" }
            .distinct()
            .sorted()

        distinctLabels.map { label ->
            CategoryItemInfo(
                name = label,
                description = "Browse real Blogger prompts in $label",
                icon = getCategoryIcon(label)
            )
        }
    }

    // Pre-calculate counts once when allPrompts updates
    val categoryCounts = remember(allPrompts, categoriesList) {
        categoriesList.associate { cat ->
            val count = allPrompts.count {
                it.category.equals(cat.name, ignoreCase = true) ||
                it.category.contains(cat.name, ignoreCase = true) ||
                it.tags.contains(cat.name, ignoreCase = true)
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
            text = if (categoriesList.isNotEmpty()) "${categoriesList.size} categories from Blogger feed" else "Syncing Blogger categories...",
            fontSize = 11.5.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (categoriesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Categories will appear here from your Blogger posts",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
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

                            // Title strictly locked to 1 line without wrapping
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
}

private fun getCategoryIcon(categoryName: String): ImageVector {
    val lower = categoryName.lowercase()
    return when {
        lower.contains("boy") -> Icons.Default.Person
        lower.contains("girl") -> Icons.Default.Face
        lower.contains("couple") -> Icons.Default.Favorite
        lower.contains("islamic") || lower.contains("mosque") -> Icons.Default.Mosque
        lower.contains("eid") -> Icons.Default.Nightlight
        lower.contains("car") || lower.contains("vehicle") -> Icons.Default.DirectionsCar
        lower.contains("cinematic") || lower.contains("movie") -> Icons.Default.Movie
        lower.contains("fashion") || lower.contains("dress") -> Icons.Default.Checkroom
        lower.contains("luxury") || lower.contains("royal") -> Icons.Default.Diamond
        lower.contains("portrait") || lower.contains("avatar") -> Icons.Default.Portrait
        lower.contains("kid") || lower.contains("child") -> Icons.Default.ChildCare
        lower.contains("wedding") -> Icons.Default.VolunteerActivism
        lower.contains("travel") || lower.contains("nature") -> Icons.Default.FlightTakeoff
        lower.contains("gemini") -> Icons.Default.AutoAwesome
        lower.contains("chatgpt") -> Icons.Default.Chat
        lower.contains("midjourney") -> Icons.Default.Palette
        lower.contains("photo") || lower.contains("edit") -> Icons.Default.AutoFixHigh
        else -> Icons.Default.FolderSpecial
    }
}
