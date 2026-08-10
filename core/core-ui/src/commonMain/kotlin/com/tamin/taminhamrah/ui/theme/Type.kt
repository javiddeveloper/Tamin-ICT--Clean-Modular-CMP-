package com.tamin.taminhamrah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * OpenType Font Features:
 * - "ss01": Stylistic Set 1. In most Persian fonts (IRANSans, Vazir, etc.), this maps Latin digits to Persian digits.
 * - "tnum": Tabular Numbers. Ensures all digits have the same width, preventing layout jumps in prices/dates.
 */
private const val FONT_FEATURE_PERSIAN_DIGITS = "ss01"
private const val FONT_FEATURE_TABULAR_NUMBERS = "tnum"
const val DEFAULT_FONT_FEATURES = "$FONT_FEATURE_TABULAR_NUMBERS, $FONT_FEATURE_PERSIAN_DIGITS"

@Composable
expect fun applicationFont(): FontFamily

@Composable
fun taminHamrahTypography(): Typography {
    val fontFamily = applicationFont()
    val defaultTypography = Typography()

    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(800),
            fontSize = 34.sp,
            lineHeight = 40.8.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = defaultTypography.displayMedium.withAppDefaults(fontFamily),
        displaySmall = defaultTypography.displaySmall.withAppDefaults(fontFamily),

        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(800),
            fontSize = 28.sp,
            lineHeight = 35.sp,
            letterSpacing = (-0.3).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 24.sp,
            lineHeight = 31.2.sp,
            letterSpacing = (-0.2).sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 20.sp,
            lineHeight = 27.sp,
            letterSpacing = 0.sp
        ),

        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 17.sp,
            lineHeight = 23.8.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = defaultTypography.titleMedium.withAppDefaults(fontFamily),
        titleSmall = defaultTypography.titleSmall.withAppDefaults(fontFamily),

        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(400),
            fontSize = 16.sp,
            lineHeight = 27.2.sp,
            letterSpacing = 0.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
            lineHeight = 23.8.sp,
            letterSpacing = 0.sp
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(400),
            fontSize = 13.sp,
            lineHeight = 20.8.sp,
            letterSpacing = 0.sp
        ),

        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(700),
            fontSize = 12.5.sp,
            lineHeight = 17.5.sp,
            letterSpacing = 0.2.sp
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontFeatureSettings = DEFAULT_FONT_FEATURES,
            fontWeight = FontWeight(500),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = defaultTypography.labelSmall.withAppDefaults(fontFamily)
    )
}

/**
 * Extension to apply standard application font and digit features to an existing [TextStyle].
 */
private fun TextStyle.withAppDefaults(fontFamily: FontFamily): TextStyle = this.copy(
    fontFamily = fontFamily,
    fontFeatureSettings = DEFAULT_FONT_FEATURES
)
