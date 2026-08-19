package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.PromptItem
import com.example.ui.components.AdMobBanner
import com.example.ui.components.GlassCard
import com.example.ui.components.PromptCard
import com.example.ui.components.subtleGlow
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToLibrary: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToImages: () -> Unit,
    onNavigateToTools: () -> Unit,
    onNavigateToApps: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onNavigateToTutorials: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (PromptItem) -> Unit
) {
    val allPrompts by viewModel.allPrompts.collectAsState()
    val featuredPrompts by viewModel.featuredPrompts.collectAsState()
    val trendingPrompts by viewModel.trendingPrompts.collectAsState()
    val galleryImages by viewModel.galleryImages.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isOffline by viewModel.isOffline.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val remoteConfig by viewModel.remoteConfig.collectAsState()

    val favIds = favorites.map { it.itemId }.toSet()

    val quickCategories = allPrompts
        .map { it.category.trim() }
        .filter { it.isNotBlank() && it != "All" }
        .distinct()
        .take(12)

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshContent(showToast = true) },
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // NON-BLOCKING OFFLINE INDICATOR
            AnimatedVisibility(
                visible = isOffline,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 3.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudOff,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Offline — Showing saved prompts.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // REMOTE CONFIG ANNOUNCEMENT BANNER (if enabled and configured remotely)
            AnimatedVisibility(
                visible = remoteConfig.announcementVisible && remoteConfig.announcementText.isNotBlank(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = remoteConfig.announcementText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // HERO BANNER SECTION (Compact & balanced on mobile screens)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .subtleGlow(
                        color = MaterialTheme.colorScheme.primary,
                        radius = 6.dp,
                        alpha = 0.08f,
                        cornerRadius = 16.dp
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    )
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(13.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.padding(bottom = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "POWERED BY AiMAEDITZ",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }

                    Text(
                        text = remoteConfig.homeHeroHeadline,
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = remoteConfig.homeHeroSubtitle,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 14.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hero Action Buttons Grid (Well-proportioned for mobile)
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Button(
                                onClick = onNavigateToLibrary,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(text = "Explore Prompts", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onNavigateToCategories,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Text(text = "Categories", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (remoteConfig.enableAiTools || remoteConfig.enableAiApps) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                if (remoteConfig.enableAiTools) {
                                    OutlinedButton(
                                        onClick = onNavigateToTools,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Build, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "AI Tools", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (remoteConfig.enableAiApps) {
                                    FilledTonalButton(
                                        onClick = onNavigateToApps,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Apps, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "AI Apps", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // QUICK CATEGORY SHORTCUTS
            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POPULAR CATEGORIES",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.6.sp
                    )

                    TextButton(
                        onClick = onNavigateToCategories,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text(
                            text = "VIEW ALL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    items(quickCategories, key = { it }) { category ->
                        FilterChip(
                            selected = false,
                            onClick = {
                                viewModel.setCategory(category)
                                onNavigateToLibrary()
                            },
                            label = { Text(text = category, fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold) },
                            shape = RoundedCornerShape(14.dp),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Tag,
                                    contentDescription = null,
                                    modifier = Modifier.size(11.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // FEATURED PROMPTS SECTION (Bounded to remoteConfig.maxFeaturedCount)
            if (remoteConfig.enableFeaturedPrompts) {
                SectionHeader(
                    title = "Featured AI Prompts",
                    subtitle = "Handpicked high-quality prompt templates",
                    onSeeMore = onNavigateToLibrary
                )

                val displayFeatured = featuredPrompts.take(remoteConfig.maxFeaturedCount)
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    displayFeatured.forEach { prompt ->
                        PromptCard(
                            prompt = prompt,
                            isFavorite = favIds.contains(prompt.id),
                            onPromptClick = {
                                viewModel.selectPrompt(prompt)
                                onNavigateToDetail(prompt)
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

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ADMOB BANNER
            AdMobBanner(modifier = Modifier.padding(horizontal = 12.dp))

            Spacer(modifier = Modifier.height(10.dp))

            // TRENDING PROMPTS SECTION (Bounded to remoteConfig.maxTrendingCount)
            if (remoteConfig.enableTrendingPrompts) {
                SectionHeader(
                    title = "Trending Prompts",
                    subtitle = "Viral AI prompt concepts",
                    onSeeMore = onNavigateToLibrary
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trendingPrompts.take(remoteConfig.maxTrendingCount), key = { it.id }) { prompt ->
                        Box(modifier = Modifier.width(225.dp)) {
                            PromptCard(
                                prompt = prompt,
                                isFavorite = favIds.contains(prompt.id),
                                onPromptClick = {
                                    viewModel.selectPrompt(prompt)
                                    onNavigateToDetail(prompt)
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

                Spacer(modifier = Modifier.height(12.dp))
            }

            // AI IMAGE GALLERY PREVIEW (Bounded to remoteConfig.maxGalleryCount)
            if (remoteConfig.enableGalleryPreview) {
                SectionHeader(
                    title = "AI Image Gallery",
                    subtitle = "Authentic AiPromptXpert artwork renders",
                    onSeeMore = onNavigateToImages
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    items(galleryImages.take(remoteConfig.maxGalleryCount), key = { it.id }) { img ->
                    Card(
                        onClick = onNavigateToImages,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .width(115.dp)
                            .height(148.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(img.imageUrl)
                                    .crossfade(100)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = img.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color.Black.copy(alpha = 0.72f)
                                            )
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(7.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                ) {
                                    Text(
                                        text = img.promptCode,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Text(
                                    text = img.title,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

            // CREATOR PROFILE PREVIEW
            if (remoteConfig.enableCreatorSection) {
                Card(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .subtleGlow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 4.dp,
                            alpha = 0.08f,
                            cornerRadius = 14.dp
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.secondary
                                        )
                                    )
                                )
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "MA",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.6.sp
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "M ABID",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.6.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Creator & Founder of AiPromptXpert",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Powered by AiMAEditz",
                                fontSize = 9.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    onSeeMore: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TextButton(
            onClick = onSeeMore,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
        ) {
            Text(
                text = "SEE MORE",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
