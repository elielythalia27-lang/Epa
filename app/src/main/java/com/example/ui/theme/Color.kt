package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Brand palette according to specifications
val CineBlue = Color(0xFF2A7DE1)
val CineBlueDark = Color(0xFF1B59A7)
val CineBlueLight = Color(0xFF63A0F3)
val CineGreen = Color(0xFF2ECC71)
val CineYellow = Color(0xFFF1C40F)
val CineRed = Color(0xFFE74C3C)

// Dark Theme Colors (default background #0B1120)
val DarkBg = Color(0xFF0B1120)
val DarkSurface = Color(0xFF131D33)
val DarkSurfaceVariant = Color(0xFF1D2B4A)
val DarkSurfaceElevated = Color(0xFF24365D)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextMuted = Color(0xFF64748B)

// Light Theme Colors
val LightBg = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFE2E8F0)
val LightSurfaceElevated = Color(0xFFCBD5E1)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF94A3B8)

/**
 * Calculates WCAG relative luminance (0.0 to 1.0).
 */
fun Color.luminance(): Float {
    fun channel(c: Float): Float {
        return if (c <= 0.03928f) c / 12.92f else Math.pow(((c + 0.055) / 1.055).toDouble(), 2.4).toFloat()
    }
    return 0.2126f * channel(red) + 0.7152f * channel(green) + 0.0722f * channel(blue)
}

/**
 * Returns either high-contrast dark text or crisp white text depending on the background luminance.
 */
fun Color.contrastingTextColor(): Color {
    return if (this.luminance() > 0.45f) Color(0xFF0F172A) else Color.White
}

/**
 * Ensures a color has sufficient contrast for the given theme without unnecessary alterations:
 * - In Dark Theme: Keeps colors as-is unless they are too dark to be visible against dark backgrounds.
 *   If too dark/black, brightens chromatic colors to luminous/neon tones or resolves to pure white.
 * - In Light Theme: Keeps colors as-is unless they are too pale/bright to be visible against light backgrounds.
 *   If too light/white/yellow, deepens chromatic colors to rich deep tones or resolves to slate black.
 */
fun Color.clampColorForTheme(isDarkTheme: Boolean): Color {
    val lum = this.luminance()
    val hsv = FloatArray(3)
    val r = (red * 255).toInt().coerceIn(0, 255)
    val g = (green * 255).toInt().coerceIn(0, 255)
    val b = (blue * 255).toInt().coerceIn(0, 255)
    android.graphics.Color.RGBToHSV(r, g, b, hsv)
    val hue = hsv[0]
    val sat = hsv[1]
    val value = hsv[2]

    if (isDarkTheme) {
        // Dark theme: elements must be visible against black/dark slate background
        val isLowContrast = lum < 0.22f || (value < 0.50f && sat > 0.15f)
        if (isLowContrast) {
            if (sat < 0.15f) {
                // Monochrome: dark gray/black -> crisp White
                return Color.White
            }
            // Chromatic: boost brightness and saturation for a vivid neon/electric look
            val adjustedSat = sat.coerceIn(0.45f, 0.88f)
            val adjustedVal = 0.90f
            return Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, adjustedSat, adjustedVal)))
        }
        return this
    } else {
        // Light theme: elements must be visible against white/light slate background
        // Colors with luminance > 0.38f (or high value with low saturation) wash out against white
        val isLowContrast = lum > 0.38f || (value > 0.70f && sat < 0.40f) || (value > 0.85f)
        if (isLowContrast) {
            if (sat < 0.15f) {
                // Monochrome: pale gray/white -> Deep Slate Black
                return Color(0xFF0F172A)
            }
            // Chromatic: deepen value and ensure rich saturation for solid, high-contrast primary
            val adjustedSat = (sat * 1.25f).coerceIn(0.70f, 1.0f)
            val adjustedVal = 0.45f
            return Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, adjustedSat, adjustedVal)))
        }
        return this
    }
}

fun Color.toAdaptivePrimary(isDarkTheme: Boolean): Color {
    return this.clampColorForTheme(isDarkTheme)
}

/**
 * Adjusts an accent color if needed so that it remains easily readable against a given background.
 */
fun Color.ensureReadableAccent(isDarkBackground: Boolean): Color {
    return this.clampColorForTheme(isDarkBackground)
}
