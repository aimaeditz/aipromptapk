package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PromptItem
import com.example.ui.components.PromptCard
import com.example.ui.components.SearchBarInput
import com.example.ui.components.saasBackgroundGlow
import com.example.ui.viewmodel.MainViewModel

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onBackClick: () -> Unit,
    onSelectPrompt: (PromptItem) -> Unit,
    onSelectCategory: ((String) -> Unit)? = null
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.unifiedSearchResults.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val favIds = remember(favorites) { favorites.map { it.itemId }.toSet() }

    val quickTags = remember {
        listOf(
            "Girl Portrait",
            "Boy Kurta",
            "3D Avatar",
            "Islamic Mosque",
            "Luxury Car",
            "Cinematic 8K",
            "Couple Wedding",
            "YouTube Thumbnail"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .saasBackgroundGlow()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Search Prompts",
                fontSize = 17.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Input
        SearchBarInput(
            query = searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onSearchExecute = {},
            placeholderText = "Search prompts, images, ideas..."
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Suggestion Chips (when query is short or empty)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 6.dp)
        ) {
            items(quickTags, key = { it }) { tag ->
                val isSelected = searchQuery.equals(tag, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSearchQuery(tag) },
                    label = { Text(text = tag, fontSize = 11.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    shape = RoundedCornerShape(10.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        selectedContainerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f),
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        selectedLabelColor = MaterialTheme.colorScheme.secondary
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                )
            }
        }

        // Search Content / Results
        if (searchQuery.isBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Search Prompt Library",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Type any topic e.g. \"girl\", \"boy kurta\", \"luxury car\", \"cinematic\"",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (searchResults.topPrompts.isEmpty() && searchResults.matchingCategories.isEmpty()) {
            // Empty Search with Smart Suggestions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SearchOff,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No matches for \"$searchQuery\"",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try these popular prompt topics:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        searchResults.smartSuggestions.take(3).forEach { suggestion ->
                            AssistChip(
                                onClick = { viewModel.setSearchQuery(suggestion) },
                                label = { Text(text = suggestion, fontSize = 11.sp) },
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Clean Search Results Grid
            BoxWithConstraints(modifier = Modifier.weight(1f)) {
                val columnCount = when {
                    maxWidth >= 950.dp -> 3
                    maxWidth >= 600.dp -> 2
                    else -> 1
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columnCount),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // Matching Categories Row (if any)
                    if (searchResults.matchingCategories.isNotEmpty()) {
                        item(span = { GridItemSpan(columnCount) }) {
                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "CATEGORIES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(searchResults.matchingCategories) { cat ->
                                        SuggestionChip(
                                            onClick = {
                                                viewModel.setCategory(cat)
                                                onSelectCategory?.invoke(cat)
                                            },
                                            label = { Text(text = cat, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = SuggestionChipDefaults.suggestionChipColors(
                                                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Count header
                    item(span = { GridItemSpan(columnCount) }) {
                        Text(
                            text = "${searchResults.topPrompts.size} matching prompts",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    // Prompt Cards
                    items(searchResults.topPrompts, key = { it.id }) { prompt ->
                        PromptCard(
                            prompt = prompt,
                            isFavorite = favIds.contains(prompt.id),
                            onPromptClick = {
                                viewModel.selectPrompt(prompt)
                                onSelectPrompt(prompt)
                            },
                            onCopyClick = {
                                viewModel.copyPromptToClipboard(prompt.exactPrompt)
                            },
                            onFavoriteClick = {
                                viewModel.toggleFavorite(prompt)
                            }
                        )
                    }
                }
            }
        }
    }
}
