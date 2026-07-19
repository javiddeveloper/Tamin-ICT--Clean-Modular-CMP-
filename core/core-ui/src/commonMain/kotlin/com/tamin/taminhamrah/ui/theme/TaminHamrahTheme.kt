package com.tamin.taminhamrah.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

val LocalTaminColors = compositionLocalOf { LightTaminColors }
val LocalAppLanguage = compositionLocalOf { "fa" }

private val DarkColorScheme = darkColorScheme(
    primary = DarkTaminColors.blueText,
    onPrimary = DarkTaminColors.bgSurface,
    primaryContainer = DarkTaminColors.blueBg,
    onPrimaryContainer = DarkTaminColors.blueText,
    secondary = DarkTaminColors.teal,
    tertiary = DarkTaminColors.greenText,
    error = DarkTaminColors.dangerText,
    errorContainer = DarkTaminColors.dangerBg,
    background = DarkTaminColors.bgPage,
    onBackground = DarkTaminColors.textPrimary,
    surface = DarkTaminColors.bgSurface,
    onSurface = DarkTaminColors.textPrimary,
    surfaceVariant = DarkTaminColors.divider,
    onSurfaceVariant = DarkTaminColors.textTertiary,
    outline = DarkTaminColors.border,
    outlineVariant = DarkTaminColors.outerBorder,
)

private val LightColorScheme = lightColorScheme(
    primary = LightTaminColors.blueText,
    onPrimary = LightTaminColors.bgSurface,
    primaryContainer = LightTaminColors.blueBg,
    onPrimaryContainer = LightTaminColors.blueText,
    secondary = LightTaminColors.teal,
    tertiary = LightTaminColors.greenText,
    error = LightTaminColors.dangerText,
    errorContainer = LightTaminColors.dangerBg,
    background = LightTaminColors.bgPage,
    onBackground = LightTaminColors.textPrimary,
    surface = LightTaminColors.bgSurface,
    onSurface = LightTaminColors.textPrimary,
    surfaceVariant = LightTaminColors.divider,
    onSurfaceVariant = LightTaminColors.textTertiary,
    outline = LightTaminColors.border,
    outlineVariant = LightTaminColors.outerBorder,
)

@Composable
fun TaminHamrahTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    language: String = "fa",
    content: @Composable () -> Unit,
) {
    val extendedColors = if (darkTheme) DarkTaminColors else LightTaminColors
    val materialColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val layoutDirection = if ((language == "fa") || (language == "ar")) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalTaminColors provides extendedColors,
        LocalLayoutDirection provides layoutDirection,
        LocalAppLanguage provides language,
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = taminHamrahTypography(),
            shapes = TaminHamrahShapes,
            content = content
        )
    }
}
