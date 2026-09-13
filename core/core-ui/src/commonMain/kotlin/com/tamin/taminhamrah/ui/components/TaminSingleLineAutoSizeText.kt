package com.tamin.taminhamrah.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Single-line text that shrinks its font size when the string would overflow the available width.
 * Once [minFontSize] is reached, remaining overflow is ellipsized.
 */
@Composable
fun TaminSingleLineAutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.titleLarge,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = TextAlign.Center,
    minFontSize: TextUnit = 12.sp,
) {
    AutoResizeText(
        text = text,
        style = style,
        modifier = modifier,
        color = color,
        textAlign = textAlign,
        maxLines = 1,
        minFontSize = minFontSize,
    )
}
