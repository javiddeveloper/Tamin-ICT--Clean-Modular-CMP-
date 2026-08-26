package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import kotlin.math.PI
import kotlin.math.sin

private val LiquidWaveAmplitudeMax = 8.dp
private const val LiquidWaveSampleCount = 32
private const val LiquidFillDurationMillis = 1600

/**
 * Decorative fake-progress fill used to communicate an in-flight upload where real byte-level
 * progress isn't tracked. Fills over a fixed duration and keeps an idle wave animation afterward
 * until the caller removes it from composition.
 */
@Composable
fun LiquidWaveProgressBar(
    modifier: Modifier = Modifier,
    onFillComplete: () -> Unit = {},
) {
    val colors = LocalTaminColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidFill")

    val fillFraction = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fillFraction.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = LiquidFillDurationMillis, easing = LinearEasing),
        )
        onFillComplete()
    }
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "LiquidWavePhase",
    )

    val fillColor = colors.blueText.copy(alpha = 0.18f)
    val waveColor = colors.blueText.copy(alpha = 0.3f)

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val leadingEdgeX = canvasWidth - canvasWidth * fillFraction.value
        val amplitude = (canvasHeight * 0.05f).coerceAtMost(LiquidWaveAmplitudeMax.toPx())
        val waveLength = canvasHeight * 1.4f
        val step = (canvasHeight / LiquidWaveSampleCount).coerceAtLeast(1f)

        fun edgeX(y: Float): Float =
            leadingEdgeX + amplitude * sin((y / waveLength) * 2f * PI.toFloat() + wavePhase)
        val edgeYs = buildList {
            var y = 0f
            while (y < canvasHeight) {
                add(y)
                y += step
            }
            add(canvasHeight)
        }

        val fillPath = Path().apply {
            moveTo(canvasWidth, 0f)
            lineTo(canvasWidth, canvasHeight)
            for (y in edgeYs.asReversed()) {
                lineTo(edgeX(y), y)
            }
            close()
        }
        drawPath(path = fillPath, color = fillColor)

        val wavePath = Path().apply {
            edgeYs.forEachIndexed { index, y ->
                val x = edgeX(y)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        drawPath(
            path = wavePath,
            color = waveColor,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}
