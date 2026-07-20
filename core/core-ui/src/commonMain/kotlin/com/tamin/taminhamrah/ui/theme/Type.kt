package com.tamin.taminhamrah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
expect fun applicationFont(): FontFamily

@Composable
fun taminHamrahTypography(): Typography {
    val fontFamily = applicationFont()
    val defaultTypography = Typography()

    return Typography(
        // Display
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(800),
            fontSize = 34.sp,
            lineHeight = 40.8.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = fontFamily),
        // H1
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(800),
            fontSize = 28.sp,
            lineHeight = 35.sp,
            letterSpacing = (-0.3).sp
        ),
        // H2
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 24.sp,
            lineHeight = 31.2.sp,
            letterSpacing = (-0.2).sp
        ),
        // H3
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 20.sp,
            lineHeight = 27.sp,
            letterSpacing = 0.sp
        ),
        // H4
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 17.sp,
            lineHeight = 23.8.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = fontFamily),
        // Body Large
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 16.sp,
            lineHeight = 27.2.sp,
            letterSpacing = 0.sp
        ),
        // Body Medium
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 14.sp,
            lineHeight = 23.8.sp,
            letterSpacing = 0.sp
        ),
        // Body Small
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(400),
            fontSize = 13.sp,
            lineHeight = 20.8.sp,
            letterSpacing = 0.sp
        ),
        // Label
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(700),
            fontSize = 12.5.sp,
            lineHeight = 17.5.sp,
            letterSpacing = 0.2.sp
        ),
        // Caption
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight(500),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.sp
        ),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = fontFamily)
    )
}
