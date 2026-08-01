package com.tamin.taminhamrah.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp

private const val SHIMMER_DURATION_MS = 1200

@Composable
fun Modifier.shimmer(): Modifier {
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

    val colorBase = MaterialTheme.colorScheme.surfaceVariant
    val colorHighlight = MaterialTheme.colorScheme.surface

    val brush = Brush.linearGradient(
        colors = listOf(colorBase, colorHighlight, colorBase),
        start = Offset(translateX * 1000f, 0f),
        end = Offset(translateX * 1000f + 1000f, 0f),
    )

    return this.background(brush)
}

/**
 * A shimmering block standing in for something that has not arrived yet.
 *
 * Sized by the caller, so the same thing stands in for a line of digits or for a whole card, and
 * a skeleton is a handful of these rather than a bespoke box each time.
 */
@Composable
fun ShimmerBlock(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadius.sm,
) {
    Box(modifier.clip(RoundedCornerShape(cornerRadius)).shimmer())
}
