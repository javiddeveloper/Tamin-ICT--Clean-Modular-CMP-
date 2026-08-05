package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * A segmented radial gauge designed specifically for displaying storage usage or similar metrics.
 *
 * @param progress The current progress from 0.0 to 1.0. (Automatically animated internally)
 * @param modifier The modifier to be applied to the layout.
 * @param segmentWidth The tangential thickness of each capsule.
 * @param segmentHeight The radial length of each capsule.
 * @param segmentCount The total number of segments in the gauge.
 * @param progressColor The solid color used for active segments if [progressGradient] is not provided.
 * @param backgroundColor The color used for inactive background segments if [inactiveGradient] is not provided.
 * @param inactiveGradient The gradient brush used for inactive segments. Overrides [backgroundColor] if provided.
 * @param progressGradient The gradient brush used for active segments. Overrides [progressColor] if provided.
 * @param padding The outer padding to prevent glow clipping.
 * @param innerPadding Extra inner padding to reduce the radius of the gauge.
 * @param startAngle The starting angle of the gauge in degrees (default 180 is left).
 * @param sweepAngle The total sweep angle of the gauge in degrees (default 180 is a semi-circle).
 */
@Composable
fun SegmentedRadialGauge(
    progress: Float,
    modifier: Modifier = Modifier,
    morphProgress: Float = 0f,
    segmentWidth: Dp = 8.dp,
    segmentHeight: Dp = 24.dp,
    segmentCount: Int = 25,
    progressColor: Color = Color(0xFF00C4B4),
    backgroundColor: Color = Color(0xFFE2E8F0),
    expandedAspectRatio: Float = 1.9f,
    progressGradient: Brush? = null,
    inactiveGradient: Brush? = null,
    padding: Dp = 16.dp,
    innerPadding: Dp = 0.dp,
    startAngle: Float = 180f,
    sweepAngle: Float = 180f,
) {
    // Smoothly animate the target progress.
    val animatedProgressState = animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "GaugeProgressAnimation"
    )

    // Resolve brushes
    val activeBrush = remember(progressGradient, progressColor) {
        progressGradient ?: SolidColor(progressColor)
    }
    val backgroundBrush = remember(inactiveGradient, backgroundColor) {
        inactiveGradient ?: SolidColor(backgroundColor)
    }

    Box(
        modifier = modifier
            .padding(padding)
            .drawWithCache {
                val widthPx = segmentWidth.toPx()
                val heightPx = segmentHeight.toPx()
                val innerPaddingPx = innerPadding.toPx()
                val glowMaxSpreadPx = 10.dp.toPx()

                val usableWidth = size.width
                val usableHeight = size.height

                val bottomMargin = heightPx / 2f + glowMaxSpreadPx + innerPaddingPx

                val rFromWidth = (usableWidth / 2f) - heightPx / 2f - innerPaddingPx
                val referenceHeight = usableWidth / expandedAspectRatio
                val rFromHeightRef = referenceHeight - glowMaxSpreadPx - heightPx / 2f - bottomMargin
                val r = minOf(rFromWidth, rFromHeightRef).coerceAtLeast(0f)
                val centerArc = Offset(
                    x = usableWidth / 2f,
                    y = referenceHeight - bottomMargin
                )

                val centerLine = Offset(
                    x = usableWidth / 2f,
                    y = usableHeight - bottomMargin
                )

                val angleStep = if (segmentCount > 1) {
                    sweepAngle / (segmentCount - 1).toFloat()
                } else 0f

                val linearSpacing = if (segmentCount > 1) {
                    (rFromWidth * 2f) / (segmentCount - 1)
                } else 0f

                val totalWidth = linearSpacing * (segmentCount - 1)
                val startX = centerLine.x - totalWidth / 2f
                val y_B = centerLine.y

                val baseCapsulePath = Path().apply {
                    addRoundRect(
                        RoundRect(
                            left = -widthPx / 2f,
                            top = -heightPx / 2f,
                            right = widthPx / 2f,
                            bottom = heightPx / 2f,
                            cornerRadius = CornerRadius(widthPx / 2f)
                        )
                    )
                }

                val inactivePath = Path()
                val activePath = Path()

                val segmentPaths = Array(segmentCount) { index ->
                    val angle = startAngle + (index * angleStep)
                    val originalRot = angle - 90f
                    val originalRotRad = ((originalRot * kotlin.math.PI) / 180.0).toFloat()

                    val x_A = centerArc.x - r * kotlin.math.sin(originalRotRad)
                    val y_A = centerArc.y + r * kotlin.math.cos(originalRotRad)
                    val rot_A = angle - 270f

                    val x_B = startX + index * linearSpacing

                    val currentX = x_A + (x_B - x_A) * morphProgress
                    val currentY = y_A + (y_B - y_A) * morphProgress
                    val currentRot = rot_A * (1f - morphProgress)

                    val matrix = Matrix()
                    matrix.translate(x = currentX, y = currentY)
                    matrix.rotateZ(currentRot)

                    val path = Path()
                    path.addPath(baseCapsulePath)
                    path.transform(matrix)
                    path
                }

                val glowSteps = 4
                val glowStrokes = Array(glowSteps) { step ->
                    val spread = (glowMaxSpreadPx / glowSteps) * (step + 1)
                    Stroke(width = spread * 2, join = StrokeJoin.Round)
                }
                val glowAlphas = FloatArray(glowSteps) { step ->
                    0.15f * (1f - ((step + 1).toFloat() / glowSteps))
                }

                onDrawBehind {
                    val activeCount = (animatedProgressState.value * segmentCount).roundToInt().coerceIn(0, segmentCount)

                    inactivePath.reset()
                    activePath.reset()

                    for (i in 0 until segmentCount) {
                        if (i < activeCount) activePath.addPath(segmentPaths[i])
                        else inactivePath.addPath(segmentPaths[i])
                    }

                    if (activeCount < segmentCount) {
                        drawPath(path = inactivePath, brush = backgroundBrush)
                    }

                    if (activeCount > 0) {
                        for (step in 0 until glowSteps) {
                            drawPath(
                                path = activePath,
                                brush = activeBrush,
                                style = glowStrokes[step],
                                alpha = glowAlphas[step]
                            )
                        }
                        drawPath(path = activePath, brush = activeBrush)
                    }
                }
            }
    )
}

@Preview(showBackground = true)
@Composable
fun SegmentedRadialGaugePreview() {
    Box() {
        SegmentedRadialGauge(
            progress = 0.05f,
            modifier = Modifier.size(250.dp),
            segmentCount = 25,
            segmentWidth = 8.dp,
            segmentHeight = 30.dp,
            padding = 10.dp,
            progressGradient = Brush.sweepGradient(
                colors = listOf(Color(0xFF00C4B4), Color(0xFF007A70))
            ),
            inactiveGradient = Brush.sweepGradient(
                colorStops = arrayOf(
                    0.0f to Color(0xFFD1D9E6),
                    0.5f to Color(0xFFF2F4F8),
                    0.8f to Color(0xFFE5E7EB),
                    1.0f to Color(0xFFD1D9E6)
                )
            )
        )
    }
}
