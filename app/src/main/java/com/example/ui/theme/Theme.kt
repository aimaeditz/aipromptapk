package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

private val DarkColorScheme = darkColorScheme(
    primary = AiDarkPrimary,
    onPrimary = AiDarkBackground,
    primaryContainer = AiDarkSurfaceVariant,
    onPrimaryContainer = AiDarkPrimary,
    secondary = AiDarkSecondary,
    onSecondary = AiDarkTextPrimary,
    tertiary = AiDarkTertiary,
    background = AiDarkBackground,
    onBackground = AiDarkTextPrimary,
    surface = AiDarkSurface,
    onSurface = AiDarkTextPrimary,
    surfaceVariant = AiDarkSurfaceVariant,
    onSurfaceVariant = AiDarkTextSecondary,
    outline = AiDarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = AiLightPrimary,
    onPrimary = AiLightSurface,
    primaryContainer = AiLightSurfaceVariant,
    onPrimaryContainer = AiLightPrimary,
    secondary = AiLightSecondary,
    onSecondary = AiLightSurface,
    tertiary = AiLightTertiary,
    background = AiLightBackground,
    onBackground = AiLightTextPrimary,
    surface = AiLightSurface,
    onSurface = AiLightTextPrimary,
    surfaceVariant = AiLightSurfaceVariant,
    onSurfaceVariant = AiLightTextSecondary,
    outline = AiLightBorder
)

@Composable
fun AiPromptXpertTheme(
    themeMode: AppThemeMode = AppThemeMode.LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

