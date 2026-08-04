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
import androidx.compose.ui.geometry.Size
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

                // Calculate the bounding area
                val usableWidth = size.width
                val usableHeight = size.height
                val minDim = minOf(usableWidth, usableHeight)
                val center = Offset(usableWidth / 2f, usableHeight / 2f)

                // The inner radius where the capsules start
                val innerRadius = (minDim / 2f) - heightPx - innerPaddingPx
                val r = innerRadius + heightPx / 2f

                // Calculate the angular step between each segment
                val angleStep = if (segmentCount > 1) {
                    sweepAngle / (segmentCount - 1).toFloat()
                } else {
                    0f
                }

                // Spacing and position for State B (morphProgress = 1)
                val linearSpacing = r * ((angleStep * kotlin.math.PI) / 180.0).toFloat()
                val totalWidth = linearSpacing * (segmentCount - 1)
                val startX = center.x - totalWidth / 2f
                val y_B = center.y

                // Construct a base path for a single straight capsule positioned at (0, 0)
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

                // Construct combined paths for inactive and active segments to draw them in bulk
                val inactivePath = Path()
                val activePath = Path()

                // Pre-calculate transformed paths for all segments
                val segmentPaths = Array(segmentCount) { index ->
                    val angle = startAngle + (index * angleStep)

                    // State A (Circular position)
                    val originalRot = angle - 90f
                    val originalRotRad = ((originalRot * kotlin.math.PI) / 180.0).toFloat()
                    val x_A = center.x - r * kotlin.math.sin(originalRotRad)
                    val y_A = center.y + r * kotlin.math.cos(originalRotRad)
                    val rot_A = angle - 270f

                    // State B (Linear position)
                    val x_B = startX + index * linearSpacing

                    // Interpolate between State A and State B
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

                // Pre-create objects for the soft glow effect
                val glowSteps = 4
                val maxSpreadPx = 10.dp.toPx()
                val glowStrokes = Array(glowSteps) { step ->
                    val spread = (maxSpreadPx / glowSteps) * (step + 1)
                    Stroke(width = spread * 2, join = StrokeJoin.Round)
                }
                val glowAlphas = FloatArray(glowSteps) { step ->
                    0.15f * (1f - ((step + 1).toFloat() / glowSteps))
                }

                onDrawBehind {
                    // Rebuild combined paths based on the animated progress state
                    val activeCount = (animatedProgressState.value * segmentCount).roundToInt().coerceIn(0, segmentCount)

                    inactivePath.reset()
                    activePath.reset()

                    for (i in 0 until segmentCount) {
                        if (i < activeCount) {
                            activePath.addPath(segmentPaths[i])
                        } else {
                            inactivePath.addPath(segmentPaths[i])
                        }
                    }

                    // 1. Draw inactive background segments
                    if (activeCount < segmentCount) {
                        drawPath(
                            path = inactivePath,
                            brush = backgroundBrush
                        )
                    }

                    // 2. Draw soft glow/shadow for active segments
                    if (activeCount > 0) {
                        // We stroke the filled path with increasing widths and decreasing alpha
                        // to simulate a soft outward glow matching the capsule shape perfectly.
                        for (step in 0 until glowSteps) {
                            drawPath(
                                path = activePath,
                                brush = activeBrush,
                                style = glowStrokes[step],
                                alpha = glowAlphas[step]
                            )
                        }

                        // 3. Draw the solid filled active segments on top
                        drawPath(
                            path = activePath,
                            brush = activeBrush
                        )
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
