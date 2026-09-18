package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class AppThemeColor(
    val id: String,
    val displayName: String,
    val primary: Color,
    val primaryVariant: Color,
    val glowColor: Color
) {
    val isCustom: Boolean
        get() = id.startsWith("custom_", ignoreCase = true)

    companion object {
        val BLUE = AppThemeColor(
            id = "blue",
            displayName = "Azul Eléctrico",
            primary = Color(0xFF2AABEE),
            primaryVariant = Color(0xFF1565C0),
            glowColor = Color(0x662AABEE)
        )
        val RED = AppThemeColor(
            id = "red",
            displayName = "Rojo Cine",
            primary = Color(0xFFE50914),
            primaryVariant = Color(0xFF991B1B),
            glowColor = Color(0x66E50914)
        )
        val EMERALD = AppThemeColor(
            id = "emerald",
            displayName = "Verde Esmeralda",
            primary = Color(0xFF10B981),
            primaryVariant = Color(0xFF047857),
            glowColor = Color(0x6610B981)
        )
        val PURPLE = AppThemeColor(
            id = "purple",
            displayName = "Violeta Neón",
            primary = Color(0xFF8B5CF6),
            primaryVariant = Color(0xFF6D28D9),
            glowColor = Color(0x668B5CF6)
        )
        val AMBER = AppThemeColor(
            id = "amber",
            displayName = "Ámbar Dorado",
            primary = Color(0xFFF59E0B),
            primaryVariant = Color(0xFFB45309),
            glowColor = Color(0x66F59E0B)
        )
        val CYAN = AppThemeColor(
            id = "cyan",
            displayName = "Cian Océano",
            primary = Color(0xFF06B6D4),
            primaryVariant = Color(0xFF0E7490),
            glowColor = Color(0x6606B6D4)
        )
        val PINK = AppThemeColor(
            id = "pink",
            displayName = "Rosa Neón",
            primary = Color(0xFFEC4899),
            primaryVariant = Color(0xFFBE185D),
            glowColor = Color(0x66EC4899)
        )
        val ORANGE = AppThemeColor(
            id = "orange",
            displayName = "Naranja Fuego",
            primary = Color(0xFFFF6D00),
            primaryVariant = Color(0xFFD84315),
            glowColor = Color(0x66FF6D00)
        )
        val TEAL = AppThemeColor(
            id = "teal",
            displayName = "Verde Turquesa",
            primary = Color(0xFF14B8A6),
            primaryVariant = Color(0xFF0F766E),
            glowColor = Color(0x6614B8A6)
        )
        val INDIGO = AppThemeColor(
            id = "indigo",
            displayName = "Índigo Profundo",
            primary = Color(0xFF6366F1),
            primaryVariant = Color(0xFF4338CA),
            glowColor = Color(0x666366F1)
        )

        val entries: List<AppThemeColor> = listOf(
            BLUE, RED, EMERALD, PURPLE, AMBER, CYAN, PINK, ORANGE, TEAL, INDIGO
        )

        fun fromCustomColor(color: Color): AppThemeColor {
            val r = (color.red * 255f).toInt().coerceIn(0, 255)
            val g = (color.green * 255f).toInt().coerceIn(0, 255)
            val b = (color.blue * 255f).toInt().coerceIn(0, 255)
            val hex = String.format("%02X%02X%02X", r, g, b)
            val darkVariant = Color(
                (r * 0.72f).toInt().coerceIn(0, 255),
                (g * 0.72f).toInt().coerceIn(0, 255),
                (b * 0.72f).toInt().coerceIn(0, 255)
            )
            return AppThemeColor(
                id = "custom_$hex",
                displayName = "Personalizado #$hex",
                primary = color,
                primaryVariant = darkVariant,
                glowColor = color.copy(alpha = 0.4f)
            )
        }

        fun fromId(id: String?): AppThemeColor {
            if (id.isNullOrEmpty()) return TEAL
            val match = entries.find { it.id.equals(id, ignoreCase = true) }
            if (match != null) return match

            val cleanHex = id.removePrefix("custom_").removePrefix("CUSTOM_").removePrefix("#")
            if (cleanHex.length == 6 || cleanHex.length == 8) {
                try {
                    val parsedInt = android.graphics.Color.parseColor("#$cleanHex")
                    val color = Color(parsedInt)
                    return fromCustomColor(color)
                } catch (_: Exception) {}
            }
            return TEAL
        }
    }
}
