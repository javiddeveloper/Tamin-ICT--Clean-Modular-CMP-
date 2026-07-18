package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.TaminNavy300
import com.tamin.taminhamrah.ui.theme.TaminNavy700

@Composable
fun CuustomChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFEFF6FF),
    textColor: Color = TaminNavy700,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp? = null,
    borderColor: Color? = null
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .background(
                color = containerColor,
                shape = shape
            )
            .then(
                if (borderWidth != null && borderColor != null) {
                    Modifier.border(
                        width = borderWidth,
                        color = borderColor,
                        shape = shape
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CuustomChipPreview() {
    PreviewRtlThemeContent {
        CuustomChip(
            text = "۲۴ خدمت",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CuustomChipBorderPreview() {
    PreviewRtlThemeContent {
        CuustomChip(
            text = "جدید",
            modifier = Modifier.padding(16.dp),
            borderWidth = 1.dp,
            borderColor = TaminNavy300
        )
    }
}
