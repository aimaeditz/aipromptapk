package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            delay(1200)
            isCopied = false
        }
    }

    val displayTitle = remember(prompt.title) {
        sanitizeDisplayString(prompt.title)
    }

    val displayPreview = remember(prompt.exactPrompt) {
        sanitizeDisplayString(prompt.exactPrompt)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .saasElevatedShadow(cornerRadius = 14.dp, elevationLevel = 1)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPromptClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            // Clean Compact Image (No bulky overlays or technical badges)
            if (prompt.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(prompt.imageUrl)
                        .crossfade(120)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = displayTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
            }

            // Compact Content Area
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                // Prompt Title
                Text(
                    text = displayTitle,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Short Prompt Preview
                Text(
                    text = displayPreview,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.5.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Action Bar: Copy | Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Copy Button with brand gradient feel
                    Button(
                        onClick = {
                            isCopied = true
                            onCopyClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCopied) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
                    ) {
                        AnimatedContent(
                            targetState = isCopied,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(90)) togetherWith fadeOut(animationSpec = tween(70))
                            },
                            label = "copyAnim"
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
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (copied) "Copied ✓" else "Copy",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Save Button
                    OutlinedButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.20f)
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isFavorite) MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = if (isFavorite) "Saved" else "Save",
                                modifier = Modifier.size(14.dp),
                                tint = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFavorite) "Saved" else "Save",
                                fontSize = 11.5.sp,
                                fontWeight = if (isFavorite) FontWeight.Bold else FontWeight.Medium,
                                color = if (isFavorite) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Sanitizes any raw technical artifacts, backslashes, escape sequences, or random letters
 */
fun sanitizeDisplayString(raw: String): String {
    return raw
        .replace(Regex("""\\[a-zA-Z0-9_]+"""), " ")
        .replace("\\", "")
        .replace(Regex("""(?i)^begin\b"""), "")
        .replace(Regex("""(?i)\bbegin\b"""), "")
        .replace(Regex("""\b[BE3]\b"""), "")
        .replace(Regex("""#\d+"""), "")
        .replace(Regex("""\s+"""), " ")
        .trim()
}
