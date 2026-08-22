package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.category.SmartCategoryEngine
import com.example.data.model.SmartCategory
import com.example.ui.components.SearchBarInput
import com.example.ui.viewmodel.MainViewModel

@Composable
fun CategoriesScreen(
    viewModel: MainViewModel,
    onCategorySelected: (String) -> Unit
) {
    val smartCategories by viewModel.smartCategories.collectAsState()
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()
    var categorySearchQuery by remember { mutableStateOf("") }

    // ONLY show real categories that actually have matching prompts in the app
    val realCategories = remember(smartCategories) {
        smartCategories.filter { it.promptCount > 0 }
    }

    // Filter against real category index
    val displayCategories = remember(realCategories, categorySearchQuery) {
        if (categorySearchQuery.isBlank()) {
            realCategories
        } else {
            SmartCategoryEngine.searchCategories(categorySearchQuery, realCategories)
        }
    }

    val totalPromptsCovered = remember(realCategories) {
        realCategories.sumOf { it.promptCount }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("categories_screen")
    ) {
        // Header Row: Title & Total Real Categories Counter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Text(
                    text = "Categories",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (!isInitialLoading || realCategories.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "${displayCategories.size} folders",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar for Category Filter
        SearchBarInput(
            query = categorySearchQuery,
            onQueryChange = { categorySearchQuery = it },
            onSearchExecute = {},
            placeholderText = "Search categories (Boys, Girls + Boys, Couples, Islamic...)"
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (isInitialLoading && realCategories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Loading prompts...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (displayCategories.isEmpty()) {
            // Clean Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FolderOff,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Text(
                        text = if (categorySearchQuery.isNotBlank()) "No matching category found" else "No categories available yet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (categorySearchQuery.isNotBlank()) {
                            "Try searching for Boys, Girls + Boys, Couples, or Islamic"
                        } else {
                            "Categories will appear automatically once prompts are synchronized."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    if (categorySearchQuery.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedButton(
                            onClick = { categorySearchQuery = "" },
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Clear Search", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Mobile: 2 Small Category Folders per row, Responsive on larger screens
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val columnCount = when {
                    maxWidth >= 900.dp -> 4
                    maxWidth >= 600.dp -> 3
                    else -> 2 // 2 folders per row on mobile
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnCount),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayCategories, key = { it.id }) { category ->
                        CategoryFolderCard(
                            category = category,
                            onClick = {
                                viewModel.setCategory(category.displayName)
                                onCategorySelected(category.displayName)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact Folder-Style Category Card
 *
 * Requirements:
 * - Small, compact, folder/collection appearance
 * - Clean white/surface background with subtle border
 * - Black/on-surface text
 * - Small icon/emoji
 * - Small prompt count ("X prompts")
 * - 0 large photos or repeated full previews
 */
@Composable
fun CategoryFolderCard(
    category: SmartCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("category_folder_${category.name.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.22f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            // Top Row: Small Folder Icon / Emoji + Prompt Count Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Folder / Subject Visual Icon
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val icon = getCategoryVectorIcon(category.iconKey)
                        Icon(
                            imageVector = icon,
                            contentDescription = category.displayName,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Prompt Count Pill
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "${category.promptCount} ${if (category.promptCount == 1) "prompt" else "prompts"}",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Category Display Name
            Text(
                text = category.displayName,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Subtle "Explore collection" link text or folder hint
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = "View collection",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

/**
 * Maps category iconKey to appropriate Material 3 Icon vector
 */
private fun getCategoryVectorIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "face_female" -> Icons.Default.Face
        "face_male" -> Icons.Default.Person
        "favorite" -> Icons.Default.Favorite
        "mosque", "account_balance" -> Icons.Default.AccountBalance
        "portrait" -> Icons.Default.Portrait
        "movie" -> Icons.Default.Movie
        "fashion" -> Icons.Default.Checkroom
        "car" -> Icons.Default.DirectionsCar
        "landscape" -> Icons.Default.Landscape
        "avatar_3d" -> Icons.Default.FaceRetouchingNatural
        "auto_awesome" -> Icons.Default.AutoAwesome
        "brush" -> Icons.Default.Brush
        "celebration" -> Icons.Default.Celebration
        "diamond" -> Icons.Default.Diamond
        "flight" -> Icons.Default.Flight
        "pets" -> Icons.Default.Pets
        "restaurant" -> Icons.Default.Restaurant
        "draw" -> Icons.Default.Draw
        "smart_toy" -> Icons.Default.SmartToy
        else -> Icons.Default.Folder
    }
}
