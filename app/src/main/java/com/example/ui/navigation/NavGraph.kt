package com.example.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AppTopBar
import com.example.ui.components.BottomNavBar
import com.example.ui.components.Screen
import com.example.ui.screens.*
import com.example.ui.theme.AppThemeMode
import com.example.ui.viewmodel.MainViewModel

@Composable
fun AppNavigation(
    viewModel: MainViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val currentThemeMode by viewModel.themeMode.collectAsState()
    val selectedPrompt by viewModel.selectedPrompt.collectAsState()

    Scaffold(
        topBar = {
            if (currentRoute != "detail" && currentRoute != "search") {
                AppTopBar(
                    onSearchClick = { navController.navigate("search") },
                    onThemeToggleClick = {
                        val nextTheme = when (currentThemeMode) {
                            AppThemeMode.DARK -> AppThemeMode.LIGHT
                            AppThemeMode.LIGHT -> AppThemeMode.DARK
                            AppThemeMode.SYSTEM -> AppThemeMode.DARK
                        }
                        viewModel.setThemeMode(nextTheme)
                    },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onLogoClick = { viewModel.refreshContent(showToast = true) },
                    isDarkTheme = currentThemeMode == AppThemeMode.DARK
                )
            }
        },
        bottomBar = {
            if (currentRoute != "detail" && currentRoute != "search") {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(animationSpec = tween(140)) },
            exitTransition = { fadeOut(animationSpec = tween(120)) },
            popEnterTransition = { fadeIn(animationSpec = tween(140)) },
            popExitTransition = { fadeOut(animationSpec = tween(120)) }
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onSelectPrompt = { prompt ->
                        viewModel.selectPrompt(prompt)
                        navController.navigate("detail")
                    },
                    onNavigateToSearch = { navController.navigate("search") },
                    onNavigateToCategories = { navController.navigate(Screen.Categories.route) }
                )
            }

            composable(Screen.Prompts.route) {
                PromptLibraryScreen(
                    viewModel = viewModel,
                    onPromptDetailSelect = { prompt ->
                        viewModel.selectPrompt(prompt)
                        navController.navigate("detail")
                    }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesScreen(
                    viewModel = viewModel,
                    onCategorySelected = { category ->
                        viewModel.setCategory(category)
                        navController.navigate(Screen.Prompts.route)
                    }
                )
            }

            composable(Screen.Saved.route) {
                FavoritesScreen(
                    viewModel = viewModel,
                    onSelectPrompt = { prompt ->
                        viewModel.selectPrompt(prompt)
                        navController.navigate("detail")
                    }
                )
            }

            composable(Screen.Profile.route) {
                CreatorProfileScreen(viewModel = viewModel)
            }

            composable("search") {
                SearchScreen(
                    viewModel = viewModel,
                    onBackClick = { navController.popBackStack() },
                    onSelectPrompt = { prompt ->
                        viewModel.selectPrompt(prompt)
                        navController.navigate("detail")
                    },
                    onSelectCategory = { category ->
                        viewModel.setCategory(category)
                        navController.navigate(Screen.Prompts.route)
                    }
                )
            }

            composable("detail") {
                selectedPrompt?.let { prompt ->
                    PromptDetailScreen(
                        prompt = prompt,
                        viewModel = viewModel,
                        onBackClick = { navController.popBackStack() },
                        onSelectRelatedPrompt = { relPrompt ->
                            viewModel.selectPrompt(relPrompt)
                        }
                    )
                } ?: run {
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}
