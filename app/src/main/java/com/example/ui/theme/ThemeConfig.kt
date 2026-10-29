package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeMode

data class CustomThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primaryNeon: Color,
    val secondaryNeon: Color,
    val accent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val cardBorder: Color,
    val focusGlow: Color
)

val NeonCyberColors = CustomThemeColors(
    background = Color(0xFF080C15),
    surface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF192238),
    primaryNeon = Color(0xFF00E5FF),
    secondaryNeon = Color(0xFFFF2A85),
    accent = Color(0xFF9D4EDD),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    cardBorder = Color(0xFF1E293B),
    focusGlow = Color(0xFF00E5FF)
)

val RetroJukeboxColors = CustomThemeColors(
    background = Color(0xFF180F08),
    surface = Color(0xFF281A10),
    surfaceVariant = Color(0xFF3B271A),
    primaryNeon = Color(0xFFFFB703),
    secondaryNeon = Color(0xFFFB8500),
    accent = Color(0xFFD62828),
    textPrimary = Color(0xFFFFF3E0),
    textSecondary = Color(0xFFD7CCC8),
    cardBorder = Color(0xFF5D4037),
    focusGlow = Color(0xFFFFB703)
)

val ModernMinimalColors = CustomThemeColors(
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceVariant = Color(0xFF2A2A2A),
    primaryNeon = Color(0xFF64B5F6),
    secondaryNeon = Color(0xFF81C784),
    accent = Color(0xFFFFB74D),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFB0BEC5),
    cardBorder = Color(0xFF333333),
    focusGlow = Color(0xFF64B5F6)
)

val LocalCustomTheme = compositionLocalOf { NeonCyberColors }

@Composable
fun getCustomThemeColors(mode: AppThemeMode): CustomThemeColors {
    return when (mode) {
        AppThemeMode.NEON_CYBER -> NeonCyberColors
        AppThemeMode.RETRO_JUKEBOX -> RetroJukeboxColors
        AppThemeMode.MODERN_MINIMAL -> ModernMinimalColors
    }
}
