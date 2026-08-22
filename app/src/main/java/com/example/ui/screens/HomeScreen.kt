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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.category.SmartCategoryEngine
import com.example.data.model.PromptItem
import com.example.ui.components.AdMobBanner
import com.example.ui.components.PromptCard
import com.example.ui.viewmodel.MainViewModel

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onSelectPrompt: (PromptItem) -> Unit,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {}
) {
    val allPrompts by viewModel.allPrompts.collectAsState()
    val smartCategories by viewModel.smartCategories.collectAsState()
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favIds = remember(favorites) { favorites.map { it.itemId }.toSet() }

    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val gridState = rememberLazyGridState()

    // Real categories derived exclusively from Blogger posts that exist
    val homeCategories = remember(smartCategories) {
        val realCats = smartCategories.filter { it.promptCount > 0 }.map { it.displayName }
        listOf("All") + realCats
    }

    // Filter prompts according to selected category and live search query
    val filteredPrompts = remember(allPrompts, selectedCategory, searchQuery, smartCategories) {
        var list = allPrompts

        // Category filter using SmartCategoryEngine strictly with real index
        if (selectedCategory != "All") {
            list = list.filter { prompt ->
                SmartCategoryEngine.isPromptInCategory(prompt, selectedCategory, smartCategories)
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

        // 1. First-Load State: While loading initial Blogger/RSS data and DB is empty, show clean loading state
        if (isInitialLoading && allPrompts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
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
        } else {
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

                        // 2. Real Blogger Categories in a compact horizontal chip row
                        if (homeCategories.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                homeCategories.forEach { category ->
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
                        }

                        // 3. Compact Header showing real count
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

                // Empty State only when loading is complete and genuinely zero results match
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
                                    text = "Try searching for boy, girl, couple, or islamic",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    // Display ALL available real prompts in the responsive grid
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
}

