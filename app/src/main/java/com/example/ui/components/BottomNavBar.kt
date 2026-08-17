package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Home : Screen("home", "Home", Icons.Filled.Home, Icons.Outlined.Home)
    object Prompts : Screen("prompts", "Prompts", Icons.Filled.LibraryBooks, Icons.Outlined.LibraryBooks)
    object Categories : Screen("categories", "Categories", Icons.Filled.Category, Icons.Outlined.Category)
    object Tools : Screen("tools", "Tools", Icons.Filled.Build, Icons.Outlined.Build)
    object Apps : Screen("apps", "Apps", Icons.Filled.Apps, Icons.Outlined.Apps)
    object Saved : Screen("favorites", "Saved", Icons.Filled.Bookmark, Icons.Outlined.BookmarkBorder)
    object Images : Screen("images", "Images", Icons.Filled.Collections, Icons.Outlined.Collections)
    object Studio : Screen("studio", "Studio", Icons.Filled.AutoFixHigh, Icons.Outlined.AutoFixHigh)
    object Profile : Screen("profile", "Creator", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Screen.Home,
        Screen.Prompts,
        Screen.Categories,
        Screen.Tools,
        Screen.Apps,
        Screen.Saved
    )

    NavigationBar(
        modifier = Modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 4.dp
    ) {
        items.forEach { screen ->
            val isSelected = currentRoute == screen.route

            val iconScale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1.0f,
                animationSpec = tween(durationMillis = 140),
                label = "iconScale"
            )

            val indicatorColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                animationSpec = tween(durationMillis = 140),
                label = "indicatorColor"
            )

            NavigationBarItem(
                selected = isSelected,
                onClick = { if (currentRoute != screen.route) onNavigate(screen.route) },
                icon = {
                    Box(
                        modifier = Modifier
                            .scale(iconScale)
                            .then(
                                if (isSelected) {
                                    Modifier.subtleGlow(
                                        color = MaterialTheme.colorScheme.primary,
                                        radius = 4.dp,
                                        alpha = 0.2f,
                                        cornerRadius = 10.dp
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                            contentDescription = screen.title,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.title,
                        fontSize = if (screen == Screen.Categories) 9.5.sp else 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Clip
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = indicatorColor
                )
            )
        }
    }
}
