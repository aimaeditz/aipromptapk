package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.SearchOff
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
import com.example.ui.components.subtleGlow
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptLibraryScreen(
    viewModel: MainViewModel,
    onPromptDetailSelect: (PromptItem) -> Unit
) {
    val allPrompts by viewModel.allPrompts.collectAsState()
    val smartCategories by viewModel.smartCategories.collectAsState()
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val favIds = favorites.map { it.itemId }.toSet()

    val categories = remember(smartCategories, allPrompts) {
        val catList = smartCategories
            .filter { it.promptCount > 0 }
            .map { it.displayName }
            .distinct()
        if (catList.isEmpty()) {
            val distinct = allPrompts
                .map { it.category.trim() }
                .filter { it.isNotBlank() && it != "All" }
                .distinct()
                .sorted()
            listOf("All") + distinct
        } else {
            listOf("All") + catList
        }
    }

    val platforms = remember(allPrompts) {
        val distinct = allPrompts
            .map { it.platform.trim() }
            .filter { it.isNotBlank() && it != "All" }
            .distinct()
            .sorted()
        if (distinct.isEmpty()) listOf("All") else listOf("All") + distinct
    }

    val filteredPrompts = remember(allPrompts, smartCategories, searchQuery, selectedCategory, selectedPlatform) {
        allPrompts.filter { prompt ->
            val matchesCategory = selectedCategory == "All" || viewModel.isPromptInSelectedCategory(prompt, selectedCategory)
            val matchesPlatform = selectedPlatform == "All" || prompt.platform.equals(selectedPlatform, ignoreCase = true) || prompt.platform.contains(selectedPlatform, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    prompt.title.contains(searchQuery, ignoreCase = true) ||
                    prompt.promptCode.contains(searchQuery, ignoreCase = true) ||
                    prompt.description.contains(searchQuery, ignoreCase = true) ||
                    prompt.category.contains(searchQuery, ignoreCase = true) ||
                    prompt.tags.contains(searchQuery, ignoreCase = true)

            matchesCategory && matchesPlatform && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar (Compact & properly aligned)
        SearchBarInput(
            query = searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onSearchExecute = {}
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Categories Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(end = 6.dp)
        ) {
            items(categories, key = { it }) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCategory(category) },
                    label = { Text(text = category, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = if (isSelected) {
                        Modifier.subtleGlow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 4.dp,
                            alpha = 0.2f,
                            cornerRadius = 16.dp
                        )
                    } else Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Platform Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(end = 6.dp)
        ) {
            items(platforms, key = { it }) { plat ->
                val isSelected = selectedPlatform == plat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setPlatform(plat) },
                    label = { Text(text = "Platform: $plat", fontSize = 10.5.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(16.dp),
                    modifier = if (isSelected) {
                        Modifier.subtleGlow(
                            color = MaterialTheme.colorScheme.secondary,
                            radius = 4.dp,
                            alpha = 0.2f,
                            cornerRadius = 16.dp
                        )
                    } else Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isInitialLoading && allPrompts.isEmpty()) {
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
        } else {
            // Header info count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredPrompts.size} AI Prompts",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (selectedCategory != "All" || selectedPlatform != "All" || searchQuery.isNotEmpty()) {
                    TextButton(
                        onClick = {
                            viewModel.setCategory("All")
                            viewModel.setPlatform("All")
                            viewModel.setSearchQuery("")
                        },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Clear Filters", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Prompts Grid (Instant, smooth scroll with stable item keys)
            if (filteredPrompts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(46.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No prompts found for selected filters",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Try clearing filters or searching another keyword",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredPrompts, key = { it.id }) { prompt ->
                        PromptCard(
                            prompt = prompt,
                            isFavorite = favIds.contains(prompt.id),
                            onPromptClick = {
                                viewModel.selectPrompt(prompt)
                                onPromptDetailSelect(prompt)
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
