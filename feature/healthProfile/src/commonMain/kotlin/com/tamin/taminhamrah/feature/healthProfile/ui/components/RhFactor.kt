package com.tamin.taminhamrah.feature.healthProfile.ui.components


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

@Composable
fun RhFactor(
    selectedRh: String?,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onRhSelected: (String) -> Unit
) {
    val rhFactors = listOf("+", "-")

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        rhFactors.forEach { rh ->
            RhCircle(
                symbol = rh,
                selected = rh == selectedRh,
                enabled = enabled,
                onClick = { onRhSelected(rh) }
            )
        }
    }
}

@Composable
private fun RhCircle(
    symbol: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalTaminColors.current

    val borderColor =
        if (selected) colors.blueText
        else colors.border

    val contentColor =
        if (selected) colors.blueText
        else colors.textSecondary

    Box(
        modifier = Modifier
            .size(64.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.matchParentSize()) {
            drawCircle(
                color = borderColor,
                style = Stroke(
                    width = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(6.dp.toPx(), 4.dp.toPx())
                    )
                )
            )
        }

        TaminText(
            text = symbol,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor
        )
    }
}
