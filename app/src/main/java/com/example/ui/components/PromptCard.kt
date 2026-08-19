package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.data.model.PromptItem
import kotlinx.coroutines.delay

@Composable
fun PromptCard(
    prompt: PromptItem,
    isFavorite: Boolean,
    onPromptClick: () -> Unit,
    onCopyClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isCopied by remember { mutableStateOf(false) }

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(1400)
            isCopied = false
        }
    }

    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.outline.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onPromptClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, borderBrush),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column {
            // Image with Badges (Compact & balanced height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(155.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(prompt.imageUrl)
                        .crossfade(120)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = prompt.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Gradient scrim overlay on top for badge readability
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.50f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Top Prompt Code Badge & Platform
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.subtleGlow(
                            color = MaterialTheme.colorScheme.primary,
                            radius = 3.dp,
                            alpha = 0.25f,
                            cornerRadius = 6.dp
                        )
                    ) {
                        Text(
                            text = prompt.promptCode,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f)
                    ) {
                        Text(
                            text = prompt.platform,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Favorite Button Overlay
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(16.dp),
                        tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Content Details
            Column(modifier = Modifier.padding(11.dp)) {
                // Category Chip
                Text(
                    text = prompt.category.uppercase(),
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = prompt.title,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = prompt.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 14.5.sp
                )

                Spacer(modifier = Modifier.height(9.dp))

                // Action Buttons with Instant Copy Feedback Animation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            isCopied = true
                            onCopyClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .then(
                                if (isCopied) {
                                    Modifier.subtleGlow(
                                        color = MaterialTheme.colorScheme.primary,
                                        radius = 6.dp,
                                        alpha = 0.3f,
                                        cornerRadius = 8.dp
                                    )
                                } else Modifier
                            ),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        AnimatedContent(
                            targetState = isCopied,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(120)) togetherWith fadeOut(animationSpec = tween(100))
                            },
                            label = "copyAnimation"
                        ) { copied ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (copied) "Copied ✓" else "Copy Prompt",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = onPromptClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.size(34.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Visibility,
                            contentDescription = "View",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
