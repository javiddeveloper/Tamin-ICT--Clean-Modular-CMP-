package com.tamin.taminhamrah.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import taminx.core.core_ui.Res
import taminx.core.core_ui.light
import taminx.core.core_ui.medium
import taminx.core.core_ui.regular
import taminx.core.core_ui.semi_bold

@Composable
actual fun applicationFont(): FontFamily {
    return FontFamily(
        Font(Res.font.regular, FontWeight.Normal),
        Font(Res.font.light, FontWeight.Light),
        Font(Res.font.medium, FontWeight.Medium),
        Font(Res.font.semi_bold, FontWeight.SemiBold)
    )
}
