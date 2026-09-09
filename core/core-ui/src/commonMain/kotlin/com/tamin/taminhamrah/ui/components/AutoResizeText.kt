package com.tamin.taminhamrah.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp

@Composable
fun AutoResizeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit = 8.sp,
) {
    var resizedTextStyle by remember(text, style) { mutableStateOf(style) }
    var readyToDraw by remember(text, style) { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        textAlign = textAlign,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        style = resizedTextStyle,
        softWrap = maxLines > 1,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        onTextLayout = { result ->
            val fontSize = resizedTextStyle.fontSize
            val overflows = result.didOverflowHeight || result.didOverflowWidth
            if (
                overflows &&
                !fontSize.isUnspecified &&
                fontSize > minFontSize
            ) {
                val shrunk = fontSize * 0.95f
                resizedTextStyle = resizedTextStyle.copy(
                    fontSize = if (shrunk < minFontSize) minFontSize else shrunk,
                )
            } else {
                readyToDraw = true
            }
        }
    )
}
