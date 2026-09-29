package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_verified


private val LeaderDashOn = 3.dp
private val LeaderDashOff = 3.dp
private val CodeChipDashOn = 3.dp
private val CodeChipDashOff = 3.dp
private val LeadingIconSize = 15.dp

private val StampRingSize = 54.dp
private val StampRingInnerSize = 39.dp
private val StampCheckSize = 21.dp
private val StampRingStroke = 1.5.dp
private val StampRingInnerStroke = 1.dp
private val StampDashOn = 3.dp
private val StampDashOff = 4.dp

private const val STAMP_RING_ALPHA = 0.35f
private const val STAMP_RING_INNER_ALPHA = 0.45f
private const val STAMP_SWEEP_DEGREES = 360f
private const val STAMP_PULSE_DEPTH = 0.5f
private const val STAMP_REVEAL_MILLIS = 2600
private const val HALF_TURN = 3.1415927f

/**
 * A tracking or receipt number: a label, a dashed rule leading across the gap, and the number in a
 * dashed chip that copies on tap.
 *
 * The leader is a `weight(1f)` spacer rather than a fixed run of dashes, so it stretches to
 * whatever the label and the number leave between them.
 */
@Composable
fun RowScope.TrackingCodeRow(
    label: String,
    code: String,
    onCopy: () -> Unit,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Icon(
        imageVector = leadingIcon,
        contentDescription = null,
        tint = colors.textMuted,
        modifier = Modifier.size(LeadingIconSize),
    )
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = colors.textMuted,
    )
    Box(
        modifier = Modifier
            .weight(1f)
            .height(Thickness.border)
            .drawBehind {
                drawLine(
                    color = colors.border,
                    start = Offset(0f, size.height / 2f),
                    end = Offset(size.width, size.height / 2f),
                    strokeWidth = size.height,
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(LeaderDashOn.toPx(), LeaderDashOff.toPx()),
                    ),
                )
            },
    )
    CopyCodeChip(
        text = code.toPersianDigits(),
        onCopy = onCopy,
        modifier = modifier,
    )
}

/**
 * The status stamp: a double check over its label, inside a dashed ring and a fainter inner one.
 *
 * With [animateOnAppear] the rings sweep once and swell gently the first time the stamp composes.
 * In a lazy list that is exactly when the row scrolls into view, and [LaunchedEffect] keyed on
 * nothing runs it once per item. The [Animatable] is never read during composition — [drawBehind]
 * samples it at draw time — so the animation costs no recompositions.
 */
@Composable
fun StatusStamp(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    animateOnAppear: Boolean = true,
) {
    val reveal = remember { Animatable(if (animateOnAppear) 0f else 1f) }
    LaunchedEffect(animateOnAppear) {
        if (animateOnAppear) {
            reveal.animateTo(
                targetValue = 1f,
                animationSpec = tween(STAMP_REVEAL_MILLIS, easing = LinearOutSlowInEasing),
            )
        }
    }

    Column(
        modifier = modifier.drawBehind {
            val progress = reveal.value
            // One shallow swell: base -> brighter -> base, never a fade to nothing.
            val pulse = 1f + STAMP_PULSE_DEPTH * kotlin.math.sin(progress * HALF_TURN)
            val outer = (STAMP_RING_ALPHA * pulse).coerceIn(0f, 1f)
            val inner = (STAMP_RING_ALPHA * STAMP_RING_INNER_ALPHA * pulse).coerceIn(0f, 1f)

            rotate(degrees = progress * STAMP_SWEEP_DEGREES) {
                drawCircle(
                    color = color.copy(alpha = outer),
                    radius = StampRingSize.toPx() / 2f,
                    style = Stroke(
                        width = StampRingStroke.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(StampDashOn.toPx(), StampDashOff.toPx()),
                        ),
                    ),
                )
            }
            drawCircle(
                color = color.copy(alpha = inner),
                radius = StampRingInnerSize.toPx() / 2f,
                style = Stroke(width = StampRingInnerStroke.toPx()),
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_verified),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(StampCheckSize),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
        )
    }
}

/** A dashed rounded outline, which Compose has no first-class modifier for. */
fun Modifier.dashedOutline(color: Color, cornerRadius: Dp, width: Dp): Modifier = drawBehind {
    val stroke = width.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(stroke / 2f, stroke / 2f),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(
            width = stroke,
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(CodeChipDashOn.toPx(), CodeChipDashOff.toPx()),
            ),
        ),
    )
}
