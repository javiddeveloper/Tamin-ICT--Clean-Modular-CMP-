package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * How strongly the neon is lit, 0 (resting) to 1 (focused), eased in and out, and the angle of
 * its slowly turning gradient. The angle only runs while the field is focused.
 */
class NeonFocusState internal constructor(
    private val intensityState: State<Float>,
    private val angleState: State<Float>,
) {
    val intensity: Float get() = intensityState.value
    val angleDegrees: Float get() = angleState.value
}

@Composable
fun rememberNeonFocus(isFocused: Boolean): NeonFocusState {
    val intensity = animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(NEON_FADE_MS),
        label = "neon_intensity",
    )
    val angle = if (isFocused) {
        rememberInfiniteTransition(label = "neon_rotation").animateFloat(
            initialValue = 0f,
            targetValue = FULL_TURN_DEGREES,
            animationSpec = infiniteRepeatable(tween(NEON_TURN_MS, easing = LinearEasing), RepeatMode.Restart),
            label = "neon_angle",
        )
    } else {
        remember { mutableFloatStateOf(0f) }
    }
    return remember(intensity, angle) { NeonFocusState(intensity, angle) }
}

/**
 * The soft colored halo outside the field. Goes before the field's clip, or the clip cuts it off.
 */
fun Modifier.neonGlow(state: NeonFocusState, color: Color, cornerRadius: Dp): Modifier =
    coloredShadow(
        color = color.copy(alpha = NEON_GLOW_ALPHA * state.intensity),
        borderRadius = cornerRadius,
        blurRadius = NEON_GLOW_BLUR,
    )

/**
 * The field's border: [restingColor] at rest, crossfading into a gradient through [neonColors] that
 * turns around the field while it is focused. Drawn inset so the clip does not halve the stroke.
 * The angle and intensity are read while drawing, so the animation redraws without recomposing.
 */
fun Modifier.neonBorder(
    state: NeonFocusState,
    neonColors: List<Color>,
    restingColor: Color,
    cornerRadius: Dp,
    restingWidth: Dp,
): Modifier = drawWithContent {
    drawContent()
    val intensity = state.intensity
    val restingStroke = restingWidth.toPx()
    val neonStroke = Thickness.medium.toPx()

    fun strokeRect(width: Float, brush: Brush, alpha: Float) {
        val inset = width / 2
        drawRoundRect(
            brush = brush,
            topLeft = Offset(inset, inset),
            size = Size(size.width - width, size.height - width),
            cornerRadius = CornerRadius((cornerRadius.toPx() - inset).coerceAtLeast(0f)),
            style = Stroke(width),
            alpha = alpha,
        )
    }

    if (intensity < 1f) strokeRect(restingStroke, Brush.linearGradient(listOf(restingColor, restingColor)), 1f - intensity)
    if (intensity > 0f) {
        val radians = state.angleDegrees * PI.toFloat() / HALF_TURN_DEGREES
        val reach = hypot(size.width, size.height) / 2
        val direction = Offset(cos(radians), sin(radians)) * reach
        strokeRect(
            width = neonStroke,
            brush = Brush.linearGradient(neonColors, start = center - direction, end = center + direction),
            alpha = intensity,
        )
    }
}

private const val NEON_FADE_MS = 280
private const val NEON_TURN_MS = 3_600
private const val NEON_GLOW_ALPHA = 0.75f
private const val FULL_TURN_DEGREES = 360f
private const val HALF_TURN_DEGREES = 180f
private val NEON_GLOW_BLUR = 18.dp

