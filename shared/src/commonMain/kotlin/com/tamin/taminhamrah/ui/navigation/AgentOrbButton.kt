package com.tamin.taminhamrah.ui.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/** Matches the glass pill's height so the two read as one row. */
private val OrbSize = 60.dp

/** How far the breathing glow is allowed to bleed past the orb's edge at its widest. */
private val GlowSpread = 16.dp

/**
 * The Agent entry point, rendered as a standalone circular button that sits beside the bottom
 * navigation pill rather than inside it.
 *
 * Keeping it outside the pill is deliberate: the pill's tab count and its animated highlight are
 * then completely independent of whether the Agent is available, so toggling `FeatureFlag.AGENT`
 * adds or removes this one button and nothing about the bar itself has to change.
 *
 * Layered back to front: a breathing coloured glow, a slowly rotating sweep-gradient ring, the
 * gradient disc, and a four-point spark glyph. All colours come from the theme, so it follows
 * light/dark automatically.
 */
@Composable
internal fun AgentOrbButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "agent_orb")

    // The only cue that the button is "alive" rather than a fifth static tab.
    val glowRadius by transition.animateFloat(
        initialValue = 6f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glow_radius",
    )
    val auraRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "aura_rotation",
    )

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "press_scale",
    )

    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    Box(
        // Stays sized to the orb itself; Box doesn't clip children, so the oversized glow
        // Canvas below can still bleed past it without nudging sibling layout.
        modifier = modifier.size(OrbSize),
        contentAlignment = Alignment.Center,
    ) {
        // glowRadius is read inside this draw lambda (not the composable body) so the pulse
        // only invalidates this Canvas's draw, instead of recomposing AgentOrbButton every frame.
        Canvas(modifier = Modifier.size(OrbSize + GlowSpread * 2)) {
            val orbRadius = OrbSize.toPx() / 2f
            val glowExtent = glowRadius.dp.toPx()
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primary.copy(alpha = 0.5f), primary.copy(alpha = 0f)),
                    center = center,
                    radius = orbRadius + glowExtent,
                ),
                radius = orbRadius + glowExtent,
            )
        }

        Box(
            modifier = Modifier
                .size(OrbSize)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .clip(CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(bounded = true, color = onPrimary),
                    role = Role.Button,
                    onClickLabel = contentDescription,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(OrbSize)) {
                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(primary, secondary),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                    ),
                )
                drawSparkPath(color = onPrimary)
            }

            Canvas(
                modifier = Modifier
                    .size(OrbSize)
                    .graphicsLayer { rotationZ = auraRotation },
            ) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            onPrimary.copy(alpha = 0f),
                            onPrimary.copy(alpha = 0.45f),
                            onPrimary.copy(alpha = 0f),
                        ),
                        center = center,
                    ),
                    radius = (size.minDimension - 3.dp.toPx()) / 2f,
                    style = Stroke(width = 1.5.dp.toPx()),
                )
            }
        }
    }
}

/**
 * Draws the four-point "spark" glyph that stands in for the assistant.
 *
 * The concave sides come from quadratic curves whose control points sit close to the centre; a
 * smaller companion spark offset toward the top-left keeps the mark from reading as a plain star.
 */
private fun DrawScope.drawSparkPath(color: Color) {
    fun spark(centerX: Float, centerY: Float, radius: Float): Path {
        val waist = radius * 0.14f
        return Path().apply {
            moveTo(centerX, centerY - radius)
            quadraticTo(centerX + waist, centerY - waist, centerX + radius, centerY)
            quadraticTo(centerX + waist, centerY + waist, centerX, centerY + radius)
            quadraticTo(centerX - waist, centerY + waist, centerX - radius, centerY)
            quadraticTo(centerX - waist, centerY - waist, centerX, centerY - radius)
            close()
        }
    }

    val main = size.minDimension * 0.24f
    drawPath(
        path = spark(center.x + main * 0.16f, center.y + main * 0.16f, main),
        color = color,
    )
    drawPath(
        path = spark(center.x - main * 0.62f, center.y - main * 0.70f, main * 0.40f),
        color = color.copy(alpha = 0.85f),
    )
}
