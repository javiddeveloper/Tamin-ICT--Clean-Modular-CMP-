package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_agent_sparkle

/** The orb's size in the design (Figma 1916:1397); every metric below is a fraction of it. */
val AgentOrbDefaultSize: Dp = 52.dp

// Design metrics as fractions of the orb's diameter, so the orb draws identically at any size
// (the bottom bar shows it at 52 dp, the assistant's welcome screen at 148 dp, and a shared
// element transition scales one into the other).
private const val IconFraction = 22f / 52f
private const val ShadowOffsetYFraction = 10f / 52f
private const val ShadowBlurFraction = 24f / 52f
private const val GlossWidthFraction = 5f / 52f
private const val ShadeWidthFraction = 14f / 52f

private val ShadowColor = Color(0x611F4FA3)

// The conic base, `from 90deg`: azure → sky → cyan → light azure → violet → indigo → azure.
private val ConicStops = arrayOf(
    0.000f to Color(0xFF4E7FE8),
    0.125f to Color(0xFF47A3DE),
    0.250f to Color(0xFF3FC7D4),
    0.500f to Color(0xFF6FA8FF),
    0.750f to Color(0xFF8C7CF6),
    0.875f to Color(0xFF6D7EEF),
    1.000f to Color(0xFF4E7FE8),
)
/** The design's conic gradient is drawn rotated by this much (its `matrix(-1.3 2.25 …)`). */
private const val ConicRotation = 120f
/** One full drift of the conic base. */
private const val DriftPeriodMs = 8_000L

private val HighlightWhite = Color(0xFFFFFFFF)
private val HighlightCyan = Color(0xFF3FC7D4)
private val HighlightBlue = Color(0xFF78AFFF)
private val InnerShade = Color(0xFF173D7E)

/**
 * The assistant's orb (Figma 1916:1397), drawn layer by layer: a soft blue drop shadow, a
 * slowly drifting conic gradient base, three radial glints (white top-left, cyan bottom-right,
 * blue bottom-left), a glossy top inner highlight with a shaded bottom rim, a hairline inner
 * border, and the sparkle glyph. The colours are the design's own brand blues, not theme
 * tokens, so the orb looks the same in light and dark — like the app's gradient buttons.
 *
 * With [onClick] it is a button (ripple, press scale, [contentDescription] as its label);
 * without, a plain decoration. The drop shadow bleeds past [size] without taking up layout
 * space, so give it room rather than clipping the parent.
 *
 * Every orb on screen drifts in the same phase: the angle comes from the frame clock, not a
 * per-instance animation, so two orbs sharing an element transition hand over seamlessly.
 */
@Composable
fun AgentOrb(
    modifier: Modifier = Modifier,
    size: Dp = AgentOrbDefaultSize,
    onClick: (() -> Unit)? = null,
    contentDescription: String? = null,
) {
    var driftAngle by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { nanos ->
                driftAngle = (nanos / 1_000_000 % DriftPeriodMs) * 360f / DriftPeriodMs
            }
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 120, easing = FastOutSlowInEasing),
        label = "agent_orb_press",
    )

    Box(
        // Stays sized to the orb itself; Box doesn't clip children, so the oversized shadow
        // Canvas below can still bleed past it without nudging sibling layout.
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        // Sized to the orb, but draws past its own bounds — Compose does not clip a draw to
        // its layout node, so the shadow spreads without reserving layout space for itself.
        Canvas(modifier = Modifier.size(size)) {
            drawDropShadow()
        }

        Box(
            modifier = Modifier
                .size(size)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .clip(CircleShape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = ripple(bounded = true, color = Color.White),
                            role = Role.Button,
                            onClickLabel = contentDescription,
                            onClick = onClick,
                        )
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            // driftAngle is read inside the draw lambda so the drift only invalidates this
            // Canvas's draw instead of recomposing the orb every frame.
            Canvas(modifier = Modifier.size(size)) {
                rotate(ConicRotation + driftAngle) {
                    drawCircle(brush = Brush.sweepGradient(colorStops = ConicStops, center = center))
                }
                drawGlints()
                drawInnerShadows()
            }

            Image(
                painter = painterResource(Res.drawable.ic_agent_sparkle),
                contentDescription = null,
                modifier = Modifier.size(size * IconFraction),
            )
        }
    }
}

/** `box-shadow: 0 10 24 rgba(31,79,163,.38)` — a blurred disc offset below the orb. */
private fun DrawScope.drawDropShadow() {
    val orbDiameter = size.minDimension
    val orbRadius = orbDiameter / 2f
    val blur = orbDiameter * ShadowBlurFraction
    val shadowCenter = center + Offset(0f, orbDiameter * ShadowOffsetYFraction)
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to ShadowColor,
                (orbRadius - blur / 2f) / (orbRadius + blur) to ShadowColor,
                1f to ShadowColor.copy(alpha = 0f),
            ),
            center = shadowCenter,
            radius = orbRadius + blur,
        ),
        radius = orbRadius + blur,
        center = shadowCenter,
    )
}

/** The three radial glints layered over the base (Figma's stacked `radial-gradient`s). */
private fun DrawScope.drawGlints() {
    fun glint(color: Color, alpha: Float, cx: Float, cy: Float, radius: Float) {
        val c = Offset(size.width * cx, size.height * cy)
        val r = size.minDimension * radius
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha), color.copy(alpha = 0f)),
                center = c,
                radius = r,
            ),
            radius = r,
            center = c,
        )
    }
    // Back to front, as the design stacks them.
    glint(HighlightBlue, alpha = 0.90f, cx = 0.18f, cy = 0.78f, radius = 0.66f)
    glint(HighlightCyan, alpha = 0.92f, cx = 0.76f, cy = 0.72f, radius = 0.59f)
    glint(HighlightWhite, alpha = 0.92f, cx = 0.30f, cy = 0.22f, radius = 0.44f)
}

/**
 * The inset shadows: `0 2 3 white .85` (a gloss along the top rim), `0 -7 13 #173D7E .3`
 * (shade along the bottom rim) and `0 0 0 1 white .5` (a hairline inner border).
 */
private fun DrawScope.drawInnerShadows() {
    val diameter = size.minDimension
    val radius = diameter / 2f

    val glossWidth = diameter * GlossWidthFraction
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0f)),
            startY = 0f,
            endY = glossWidth * 1.6f,
        ),
        radius = radius - glossWidth / 2f,
        style = Stroke(width = glossWidth),
    )

    val shadeWidth = diameter * ShadeWidthFraction
    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(InnerShade.copy(alpha = 0f), InnerShade.copy(alpha = 0.3f)),
            startY = size.height - shadeWidth * 1.4f,
            endY = size.height,
        ),
        radius = radius - shadeWidth / 2f,
        style = Stroke(width = shadeWidth),
    )

    // A hairline in the design; it thickens a little with the orb but never past 1.5 dp,
    // so the large welcome-screen orb keeps a fine rim.
    val borderWidth = (diameter / AgentOrbDefaultSize.toPx()).coerceIn(1f, 1.5f) * 1.dp.toPx()
    drawCircle(
        color = Color.White.copy(alpha = 0.5f),
        radius = radius - borderWidth / 2f,
        style = Stroke(width = borderWidth),
    )
}
