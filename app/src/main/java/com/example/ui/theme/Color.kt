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
    return if (this.luminance() > 0.42f) Color(0xFF0F172A) else Color.White
}

/**
 * Adjusts an accent color if needed so that it remains easily readable against a given background.
 */
fun Color.ensureReadableAccent(isDarkBackground: Boolean): Color {
    val lum = this.luminance()
    return if (isDarkBackground) {
        if (lum < 0.20f) {
            // Lighten the color so it does not blend into dark background
            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), hsv)
            hsv[1] = (hsv[1] * 0.75f).coerceIn(0.1f, 1f)
            hsv[2] = 0.85f.coerceAtLeast(hsv[2])
            Color(android.graphics.Color.HSVToColor(hsv))
        } else {
            this
        }
    } else {
        if (lum > 0.65f) {
            // Darken slightly so it doesn't get lost on white background
            val hsv = FloatArray(3)
            android.graphics.Color.RGBToHSV((red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt(), hsv)
            hsv[2] = (hsv[2] * 0.70f).coerceIn(0f, 0.65f)
            Color(android.graphics.Color.HSVToColor(hsv))
        } else {
            this
        }
    }
}
