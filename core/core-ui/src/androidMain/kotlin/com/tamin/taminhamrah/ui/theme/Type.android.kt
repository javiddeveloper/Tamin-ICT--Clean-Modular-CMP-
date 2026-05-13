package com.tamin.taminhamrah.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import org.jetbrains.compose.resources.Font
import taminx.core.core_ui.Res
import taminx.core.core_ui.regular

@Composable
actual fun applicationFont(): FontFamily {
    return FontFamily(Font(Res.font.regular))
}
