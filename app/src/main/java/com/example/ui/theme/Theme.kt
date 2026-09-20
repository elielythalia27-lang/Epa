package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    themeColor: AppThemeColor = AppThemeColor.TEAL,
    content: @Composable () -> Unit
) {
    val darkPrimary = themeColor.primaryForTheme(isDarkTheme = true)
    val darkVariant = themeColor.primaryVariantForTheme(isDarkTheme = true)
    val darkOnPrimary = darkPrimary.contrastingTextColor()
    val darkOnPrimaryContainer = darkVariant.contrastingTextColor()

    val lightPrimary = themeColor.primaryForTheme(isDarkTheme = false)
    val lightVariant = themeColor.primaryVariantForTheme(isDarkTheme = false)
    val lightOnPrimary = lightPrimary.contrastingTextColor()
    val lightOnPrimaryContainer = lightVariant.contrastingTextColor()

    val darkColorScheme = darkColorScheme(
        primary = darkPrimary,
        onPrimary = darkOnPrimary,
        primaryContainer = if (darkPrimary.luminance() > 0.8f) Color(0xFF1E293B) else darkVariant,
        onPrimaryContainer = if (darkPrimary.luminance() > 0.8f) Color.White else darkOnPrimaryContainer,
        secondary = darkPrimary,
        onSecondary = darkOnPrimary,
        secondaryContainer = if (darkPrimary.luminance() > 0.8f) Color(0xFF334155) else darkPrimary.copy(alpha = 0.22f),
        onSecondaryContainer = Color.White,
        tertiary = CineYellow,
        onTertiary = Color.Black,
        error = CineRed,
        onError = Color.White,
        background = DarkBg,
        onBackground = DarkTextPrimary,
        surface = DarkSurface,
        onSurface = DarkTextPrimary,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkTextSecondary
    )

    val lightColorScheme = lightColorScheme(
        primary = lightPrimary,
        onPrimary = lightOnPrimary,
        primaryContainer = if (lightPrimary.luminance() < 0.2f) Color(0xFFE2E8F0) else lightVariant,
        onPrimaryContainer = if (lightPrimary.luminance() < 0.2f) Color(0xFF0F172A) else lightOnPrimaryContainer,
        secondary = lightPrimary,
        onSecondary = lightOnPrimary,
        secondaryContainer = if (lightPrimary.luminance() < 0.2f) Color(0xFFF1F5F9) else lightPrimary.copy(alpha = 0.15f),
        onSecondaryContainer = LightTextPrimary,
        tertiary = Color(0xFFB45309),
        onTertiary = Color.White,
        error = CineRed,
        onError = Color.White,
        background = LightBg,
        onBackground = LightTextPrimary,
        surface = LightSurface,
        onSurface = LightTextPrimary,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightTextSecondary
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
