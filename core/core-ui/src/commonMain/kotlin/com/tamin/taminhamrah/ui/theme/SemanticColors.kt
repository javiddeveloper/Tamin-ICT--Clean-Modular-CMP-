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
    bgPage = TaminLightBackground,
    bgSurface = TaminLightSurface,
    border = CoreBorder,
    divider = CoreDivider,
    outerBorder = Gray300,
    textPrimary = TaminLightTextDefault,
    textSecondary = TaminLightTextSecondary,
    textTertiary = Gray400,
    textMuted = Gray400,
    chevron = Gray300,
    greenBg = Secondary50,
    greenText = TaminLightSuccess,
    blueBg = Primary50,
    blueText = TaminLightInfo,
    orangeBg = TaminLightOrangeBg,
    orangeText = TaminLightWarning,
    dangerBg = TaminLightSurface,
    dangerBorder = TaminLightDangerBorder,
    dangerText = TaminLightError,
    teal = Secondary700,
    disabledAlpha = 0.38f,
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
    heroGradient = Brush.linearGradient(listOf(Primary900, Primary700)),
    medicalGradient = Brush.linearGradient(listOf(Secondary500, Secondary700)),
    aiAssistantGradient = Brush.linearGradient(
        listOf(TaminPurple900, TaminPurple700, Color(0xFF3F5BD9), TaminNavy700)
    ),
)

val DarkTaminColors = TaminColors(
    bgPage = TaminDarkBackground,
    bgSurface = TaminDarkSurface,
    border = TaminDarkBorder,
    divider = TaminDarkDivider,
    outerBorder = TaminDarkOuterBorder,
    textPrimary = TaminDarkTextDefault,
    textSecondary = TaminDarkTextSecondary,
    textTertiary = TaminDarkTextSecondary,
    textMuted = TaminDarkTextMuted,
    chevron = TaminDarkChevron,
    greenBg = TaminDarkGreenBg,
    greenText = TaminDarkSuccess,
    blueBg = TaminDarkBlueBg,
    blueText = TaminDarkInfo,
    orangeBg = TaminDarkOrangeBg,
    orangeText = TaminDarkWarning,
    dangerBg = TaminDarkSurface,
    dangerBorder = TaminDarkDangerBorder,
    dangerText = TaminDarkError,
    teal = Secondary500,
    disabledAlpha = 0.38f,
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
    medicalGradient = Brush.linearGradient(listOf(Secondary500, Secondary700)),
    aiAssistantGradient = Brush.linearGradient(
        listOf(TaminPurple900, TaminPurple700, Color(0xFF3F5BD9), TaminNavy700)
    ),
)
