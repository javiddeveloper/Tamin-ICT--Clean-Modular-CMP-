package com.tamin.taminhamrah.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color

private const val SHIMMER_DURATION_MS = 1200

fun Modifier.shimmer(
    colorBase: Color = Color.Unspecified,
    colorHighlight: Color = Color.Unspecified,
): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateX by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shimmerTranslateX",
    )

    val base = if (colorBase != Color.Unspecified) colorBase else MaterialTheme.colorScheme.surfaceVariant
    val highlight = if (colorHighlight != Color.Unspecified) colorHighlight else MaterialTheme.colorScheme.surface

    drawBehind {
        val brush = Brush.linearGradient(
            colors = listOf(base, highlight, base),
            start = Offset(translateX * size.width, 0f),
            end = Offset(translateX * size.width + size.width, 0f),
        )
        drawRect(brush = brush)
    }
}
