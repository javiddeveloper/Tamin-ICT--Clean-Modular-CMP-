package com.tamin.taminhamrah.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

data class TaminColors(
    // Backgrounds / surfaces
    val bgPage: Color,
    val bgSurface: Color,
    val border: Color,
    val divider: Color,
    val outerBorder: Color,

    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val textMuted: Color,
    val chevron: Color,

    // Status pills (background + foreground pairs)
    val greenBg: Color,
    val greenText: Color,
    val blueBg: Color,
    val blueText: Color,
    val orangeBg: Color,
    val orangeText: Color,
    val dangerBg: Color,
    val dangerBorder: Color,
    val dangerText: Color,
    val disabledAlpha: Float,

    // Medical / Teal
    val teal: Color,

    // Glass / tab-bar tokens (translucency layers for the bottom nav)
    val glassA1: Color,
    val glassA2: Color,
    val glassB1: Color,
    val glassB2: Color,
    val glassC1: Color,
    val glassC2: Color,
    val glassSolid: Color,
    val glassBorder: Color,
    val tabbarBorder: Color,
    val tabbarShine: Color,
    val tabActiveBg: Color,

    // Brand gradients (hero headers, feature cards)
    val heroGradient: Brush,
    val medicalGradient: Brush,
    val aiAssistantGradient: Brush,
)

val LightTaminColors = TaminColors(
    bgPage = TaminLightBgPage,
    bgSurface = TaminLightBgSurface,
    border = TaminLightBorder,
    divider = TaminLightDivider,
    outerBorder = TaminLightOuterBorder,
    textPrimary = TaminLightTextPrimary,
    textSecondary = TaminLightTextSecondary,
    textTertiary = TaminLightTextTertiary,
    textMuted = TaminLightTextMuted,
    chevron = TaminLightChevron,
    greenBg = TaminLightGreenBg,
    greenText = TaminGreenDark,
    blueBg = TaminLightBlueBg,
    blueText = TaminNavy700,
    orangeBg = TaminLightOrangeBg,
    orangeText = TaminOrange,
    dangerBg = TaminLightBgSurface,
    dangerBorder = TaminLightDangerBorder,
    dangerText = TaminRed,
    teal = TaminTeal700,
    glassA1 = Color(0x8CFFFFFF),
    glassA2 = Color(0x52FFFFFF),
    glassB1 = Color(0xADFFFFFF),
    glassB2 = Color(0x6BFFFFFF),
    glassC1 = Color(0x61FFFFFF),
    glassC2 = Color(0x24FFFFFF),
    glassSolid = Color(0xD9F8FAFC),
    glassBorder = Color(0x99FFFFFF),
    tabbarBorder = Color(0xD9FFFFFF),
    tabbarShine = Color(0x80FFFFFF),
    tabActiveBg = Color(0x1A1F4FA3),
    disabledAlpha = 0.38f,
    heroGradient = Brush.linearGradient(listOf(TaminNavy900, TaminNavy700)),
    medicalGradient = Brush.linearGradient(listOf(TaminTeal900, TaminTeal500)),
    aiAssistantGradient = Brush.linearGradient(
        listOf(TaminPurple900, TaminPurple700, Color(0xFF3F5BD9), TaminNavy700)
    ),
)

val DarkTaminColors = TaminColors(
    bgPage = TaminDarkBgPage,
    bgSurface = TaminDarkBgSurface,
    border = TaminDarkBorder,
    divider = TaminDarkDivider,
    outerBorder = TaminDarkOuterBorder,
    textPrimary = TaminDarkTextPrimary,
    textSecondary = TaminDarkTextSecondary,
    textTertiary = TaminDarkTextSecondary,
    textMuted = TaminDarkTextMuted,
    chevron = TaminDarkChevron,
    greenBg = TaminDarkGreenBg,
    greenText = TaminDarkGreenText,
    blueBg = TaminDarkBlueBg,
    blueText = TaminDarkBlueText,
    orangeBg = TaminDarkOrangeBg,
    orangeText = TaminDarkOrangeText,
    dangerBg = TaminDarkBgSurface,
    dangerBorder = TaminDarkDangerBorder,
    disabledAlpha = 0.38f,
    dangerText = TaminDarkDangerText,
    teal = TaminTeal500,
    glassA1 = Color(0x8C1E293B),
    glassA2 = Color(0x47111827),
    glassB1 = Color(0xA6283448),
    glassB2 = Color(0x66141C2D),
    glassC1 = Color(0x731E293B),
    glassC2 = Color(0x33111827),
    glassSolid = Color(0xD90A0F1A),
    glassBorder = Color(0x14FFFFFF),
    tabbarBorder = Color(0x17FFFFFF),
    tabbarShine = Color(0x0DFFFFFF),
    tabActiveBg = Color(0x295B9CFF),
    heroGradient = Brush.linearGradient(listOf(Color(0xFF10AEB9), Color(0xFF1E6FD0))),
    medicalGradient = Brush.linearGradient(listOf(TaminTeal900, TaminTeal500)),
    aiAssistantGradient = Brush.linearGradient(
        listOf(TaminPurple900, TaminPurple700, Color(0xFF3F5BD9), TaminNavy700)
    ),
)
