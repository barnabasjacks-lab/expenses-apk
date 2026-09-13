package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

private val DarkColorScheme = darkColorScheme(
    primary = TzGreenDark,
    onPrimary = Color(0xFF003820),
    primaryContainer = TzGreenContainerDark,
    onPrimaryContainer = TzOnGreenContainerDark,
    secondary = TzBlueDark,
    onSecondary = Color(0xFF003254),
    secondaryContainer = TzBlueContainerDark,
    onSecondaryContainer = Color(0xFFC7E7FF),
    tertiary = TzGoldDark,
    onTertiary = Color(0xFF412D00),
    tertiaryContainer = TzGoldContainerDark,
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = Color(0xFF6B7B71)
)

private val LightColorScheme = lightColorScheme(
    primary = TzGreenLight,
    onPrimary = Color.White,
    primaryContainer = TzGreenContainerLight,
    onPrimaryContainer = TzOnGreenContainerLight,
    secondary = TzBlueLight,
    onSecondary = Color.White,
    secondaryContainer = TzBlueContainerLight,
    onSecondaryContainer = Color(0xFF001E33),
    tertiary = TzGoldLight,
    onTertiary = Color.White,
    tertiaryContainer = TzGoldContainerLight,
    onTertiaryContainer = Color(0xFF281900),
    background = BackgroundLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = Color(0xFF86978C)
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
