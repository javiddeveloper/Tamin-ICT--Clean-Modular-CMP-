package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.Easing
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

/**
 * The phase [borderTrace] draws from: 0 to 1, over and over. The stroke is drawn in over the first
 * half and erased from its tail over the second, each half one [Duration.extraSlow] on the
 * standard easing.
 *
 * Under reduced motion it holds half-way, which draws the whole border and leaves it there.
 */
@Composable
fun rememberBorderTracePhase(): () -> Float {
    if (isReducedMotionEnabled()) return FullBorderPhase
    val phase = rememberInfiniteTransition(label = "BorderTrace").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = TRACE_CYCLE_MILLIS, easing = LinearEasing),
        ),
        label = "BorderTracePhase",
    )
    return remember(phase) { { phase.value } }
}

/**
 * Traces [color] round the element's rounded border while something is in progress — the stroke
 * [animatedErrorBorder] draws an error with, kept going: drawn in, erased from its tail, again.
 *
 * [phase] comes from [rememberBorderTracePhase] and is read only while drawing, so a frame of the
 * trace costs no recomposition. The path is inset and stroked exactly as the error trace's is.
 */
fun Modifier.borderTrace(
    phase: () -> Float,
    color: Color,
    borderWidth: Dp,
    cornerRadius: Dp,
): Modifier = drawWithCache {
    val strokeWidthPx = borderWidth.toPx()
    val halfStroke = strokeWidthPx / 2f
    val cornerRadiusPx = cornerRadius.toPx()
    val border = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(
                    left = halfStroke,
                    top = halfStroke,
                    right = size.width - halfStroke,
                    bottom = size.height - halfStroke,
                ),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadiusPx, cornerRadiusPx),
            ),
        )
    }
    val measure = PathMeasure().apply { setPath(border, forceClosed = false) }
    val length = measure.length
    val stroke = Stroke(width = strokeWidthPx * TRACE_STROKE_SCALE)
    val trace = Path()

    onDrawWithContent {
        drawContent()

        val cycle = phase()
        val head = Easing.standard.transform((cycle / HALF_CYCLE).coerceIn(0f, 1f))
        val tail = Easing.standard.transform(((cycle - HALF_CYCLE) / HALF_CYCLE).coerceIn(0f, 1f))
        if (head <= tail) return@onDrawWithContent

        trace.reset()
        measure.getSegment(
            startDistance = tail * length,
            stopDistance = head * length,
            destination = trace,
            startWithMoveTo = true,
        )
        drawPath(path = trace, color = color, style = stroke)
    }
}

/** One draw-in and one erase. */
private const val TRACE_CYCLE_MILLIS = 2 * Duration.extraSlow

/** Where the draw-in ends and to erase begins. */
private const val HALF_CYCLE = 0.5f

/** The error trace is stroked half as heavy again as the border it runs along; so is this one. */
private const val TRACE_STROKE_SCALE = 1.5f

private val FullBorderPhase: () -> Float = { HALF_CYCLE }

@PreviewRtlTheme
@Composable
private fun BorderTracePreview() {
    PreviewRtlThemeContent {
        val colors = LocalTaminColors.current
        TaminOutlinedButton(
            text = "ویرایش",
            onClick = {},
            shape = RoundedCornerShape(CornerRadius.listRow),
            borderColor = colors.blueBorder,
            contentColor = colors.blueText,
            modifier = Modifier.borderTrace(
                phase = rememberBorderTracePhase(),
                color = colors.blueText,
                borderWidth = 1.dp,
                cornerRadius = CornerRadius.listRow,
            ),
        )
    }
}
