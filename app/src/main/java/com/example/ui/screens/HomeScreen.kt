package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.components.AdMobBanner
import com.example.ui.components.PromptCard
import com.example.ui.viewmodel.MainViewModel

// The 6 clean core categories specified by the user
val HOME_MAIN_CATEGORIES = listOf(
    "All",
    "Image Generation",
    "Photography",
    "Video & Cinematic",
    "Social Media",
    "Design & Branding",
    "Writing & Content"
)

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onSelectPrompt: (PromptItem) -> Unit,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {}
) {
    val allPrompts by viewModel.allPrompts.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favIds = remember(favorites) { favorites.map { it.itemId }.toSet() }

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()

    // Filter prompts according to selected category and live search query
    val filteredPrompts = remember(allPrompts, selectedCategory, searchQuery) {
        var list = allPrompts

        // Category filter
        if (selectedCategory != "All") {
            list = list.filter { prompt ->
                when (selectedCategory) {
                    "Image Generation" -> prompt.category.contains("Girl", ignoreCase = true) ||
                            prompt.category.contains("Boy", ignoreCase = true) ||
                            prompt.category.contains("Avatar", ignoreCase = true) ||
                            prompt.category.contains("Image", ignoreCase = true) ||
                            prompt.tags.contains("image", ignoreCase = true)
                    "Photography" -> prompt.category.contains("Portrait", ignoreCase = true) ||
                            prompt.category.contains("Photography", ignoreCase = true) ||
                            prompt.tags.contains("dslr", ignoreCase = true) ||
                            prompt.tags.contains("camera", ignoreCase = true) ||
                            prompt.exactPrompt.contains("shot on", ignoreCase = true)
                    "Video & Cinematic" -> prompt.category.contains("Cinematic", ignoreCase = true) ||
                            prompt.category.contains("Movie", ignoreCase = true) ||
                            prompt.tags.contains("cinematic", ignoreCase = true) ||
                            prompt.tags.contains("8k", ignoreCase = true) ||
                            prompt.exactPrompt.contains("cinematic", ignoreCase = true)
                    "Social Media" -> prompt.category.contains("Couple", ignoreCase = true) ||
                            prompt.tags.contains("instagram", ignoreCase = true) ||
                            prompt.tags.contains("tiktok", ignoreCase = true) ||
                            prompt.tags.contains("dp", ignoreCase = true) ||
                            prompt.tags.contains("avatar", ignoreCase = true)
                    "Design & Branding" -> prompt.category.contains("Luxury", ignoreCase = true) ||
                            prompt.category.contains("Car", ignoreCase = true) ||
                            prompt.category.contains("Islamic", ignoreCase = true) ||
                            prompt.tags.contains("logo", ignoreCase = true) ||
                            prompt.tags.contains("branding", ignoreCase = true)
                    "Writing & Content" -> prompt.platform.contains("ChatGPT", ignoreCase = true) ||
                            prompt.platform.contains("Gemini", ignoreCase = true) ||
                            prompt.tags.contains("caption", ignoreCase = true) ||
                            prompt.tags.contains("content", ignoreCase = true) ||
                            prompt.tags.contains("seo", ignoreCase = true)
                    else -> prompt.category.equals(selectedCategory, ignoreCase = true)
                }
            }
        }

        // Live search filter
        val query = searchQuery.trim().lowercase()
        if (query.isNotEmpty()) {
            list = list.filter { prompt ->
                prompt.title.lowercase().contains(query) ||
                        prompt.exactPrompt.lowercase().contains(query) ||
                        prompt.category.lowercase().contains(query) ||
                        prompt.tags.lowercase().contains(query) ||
                        prompt.promptCode.lowercase().contains(query)
            }
        }

        list
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        // Responsive columns:
        // Mobile (< 600dp) -> 1 column
        // Tablet / Laptop (600dp..950dp) -> 2 columns
        // Wide Desktop (>= 950dp) -> 3 columns
        val columnCount = when {
            maxWidth >= 950.dp -> 3
            maxWidth >= 600.dp -> 2
            else -> 1
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(columnCount),
            state = gridState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Section: Compact Search Bar + Category Chips (Spans full width)
            item(span = { GridItemSpan(columnCount) }) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Compact Search Bar
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                        shadowElevation = 0.5.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text(
                                        text = "Search prompts, images, ideas...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                ),
                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                                modifier = Modifier.weight(1f)
                            )
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Six Clean Categories in a compact horizontal chip row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HOME_MAIN_CATEGORIES.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }

                    // 3. Compact Header showing count
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategory == "All") "All Prompts" else selectedCategory,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${filteredPrompts.size} prompts",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Empty State if no prompts match
            if (filteredPrompts.isEmpty()) {
                item(span = { GridItemSpan(columnCount) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No prompts found",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Try searching for girl, boy, kurta, cinematic, or 3d avatar",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                // Display ALL available useful prompts in the responsive grid
                items(filteredPrompts, key = { it.id }) { prompt ->
                    PromptCard(
                        prompt = prompt,
                        isFavorite = favIds.contains(prompt.id),
                        onPromptClick = { onSelectPrompt(prompt) },
                        onCopyClick = { viewModel.copyPromptToClipboard(prompt.exactPrompt) },
                        onFavoriteClick = { viewModel.toggleFavorite(prompt) }
                    )
                }
            }

            // Compact AdMob Banner at bottom
            item(span = { GridItemSpan(columnCount) }) {
                AdMobBanner(modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}
