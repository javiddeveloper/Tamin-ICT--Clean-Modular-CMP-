package com.tamin.taminhamrah.feature.history.ui.jobinfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun InfoChip(
    label: String,
    value: String,
    onCopy: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val borderColor = taminColors.blueText.copy(alpha = 0.4f)

    Column(
        modifier = modifier
            .drawBehind {
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerRadius.md.toPx())
                )
            }
            .clip(RoundedCornerShape(CornerRadius.md))
            .background(taminColors.chipBg)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted,
                textAlign = TextAlign.Center,
            )
            Icon(
                imageVector = Icons.Outlined.ContentCopy,
                contentDescription = "کپی $label",
                tint = taminColors.blueText,
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onCopy(value) }
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = taminColors.blueText,
            modifier = Modifier.fillMaxWidth()
        )

    }
}
