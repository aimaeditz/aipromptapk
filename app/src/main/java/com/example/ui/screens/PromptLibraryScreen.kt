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
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedPlatform by viewModel.selectedPlatform.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    val favIds = favorites.map { it.itemId }.toSet()

    val categories = listOf(
        "All", "Boy Prompts", "Girl Prompts", "Couple Prompts", "Islamic Prompts",
        "Eid Prompts", "Wedding Prompts", "Cinematic Prompts", "Portrait Prompts",
        "Luxury Prompts", "Fashion Prompts", "AI Editing", "Photography",
        "Cars", "Kids", "Nature", "Travel", "Trending", "Gemini", "Other"
    )

    val platforms = listOf("All", "Gemini", "Bing AI", "Midjourney", "DALL-E 3", "ChatGPT")

    val filteredPrompts = remember(allPrompts, searchQuery, selectedCategory, selectedPlatform) {
        allPrompts.filter { prompt ->
            val matchesCategory = selectedCategory == "All" || prompt.category.equals(selectedCategory, ignoreCase = true)
            val matchesPlatform = selectedPlatform == "All" || prompt.platform.equals(selectedPlatform, ignoreCase = true)
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
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        SearchBarInput(
            query = searchQuery,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onSearchExecute = {}
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Chips with glowing active indicator
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(categories, key = { it }) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setCategory(category) },
                    label = { Text(text = category, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = if (isSelected) {
                        Modifier.subtleGlow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 6.dp,
                            alpha = 0.25f,
                            cornerRadius = 20.dp
                        )
                    } else Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Platform Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(end = 8.dp)
        ) {
            items(platforms, key = { it }) { plat ->
                val isSelected = selectedPlatform == plat
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setPlatform(plat) },
                    label = { Text(text = "Platform: $plat", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = if (isSelected) {
                        Modifier.subtleGlow(
                            color = MaterialTheme.colorScheme.secondary,
                            radius = 5.dp,
                            alpha = 0.25f,
                            cornerRadius = 20.dp
                        )
                    } else Modifier
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Header info count
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredPrompts.size} AI Prompts",
                fontSize = 13.sp,
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
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(text = "Clear Filters", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No prompts found for selected filters",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Try clearing filters or searching another keyword",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
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
