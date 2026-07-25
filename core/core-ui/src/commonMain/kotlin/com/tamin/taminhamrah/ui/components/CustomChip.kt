package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun CustomChip(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = MaterialTheme.colorScheme.primary,
    border: BorderStroke? = null,
) {
    val shape = RoundedCornerShape(CornerRadius.lg)

    Box(
        modifier = modifier
            .then(
                if (border != null) {
                    Modifier.border(border, shape)
                } else {
                    Modifier
                }
            )
            .background(containerColor, shape)
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CustomChipPreview() {
    PreviewRtlThemeContent {
        CustomChip(
            text = "۲۴ خدمت",
            border = BorderStroke(
                width = 1.dp,
                color = Color.Blue
            ),
            modifier = Modifier.padding(Spacing.lg)
        )
    }
}
