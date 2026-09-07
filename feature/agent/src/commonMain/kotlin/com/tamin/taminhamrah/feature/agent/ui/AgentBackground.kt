package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// ─── Agent backdrop ───────────────────────────────────────────────────────────
//
// A direct port of the Figma node 90:14 ("Background"). Every number below was read off
// that node against its 410 × 892 reference frame and re-expressed as a fraction of the
// real screen, so the composition scales to any device instead of being pinned to the
// design canvas.
//
// The node is a stack of seven layers; the constants below keep Figma's child order:
//
//   90:14  fill   — 168° linear gradient, navy → indigo → violet → near-black
//   90:15  wash   — conic sweep centred on the screen, 75% opacity
//   90:16  blob   — violet, top-right, mostly off-canvas
//   90:17  blob   — indigo, bottom-left, mostly off-canvas
//   90:18  blob   — deep blue, just below centre
//   90:19  blob   — pale blue, upper-left edge
//   90:20  scrim  — elliptical vignette anchored at the top centre, darkening downwards
//
// Figma applies a layer blur to 90:15–90:19 (30 px on the sweep, 7–10 px on the blobs).
// Those are deliberately not reproduced with `Modifier.blur`: each of those layers is
// already a smooth gradient whose falloff spans 100–230 px, so a Gaussian of that radius
// is visually negligible — while a RenderEffect layer is a no-op below API 31 (minSdk is
// 24 here) and would cost a saveLayer inside the Haze source this backdrop feeds.

/** Design canvas the Figma measurements were taken against. */
private const val REF_W = 410f
private const val REF_H = 892f

/** 90:14 — `linear-gradient(168deg, …)`. CSS angles run clockwise from "up". */
private const val BASE_ANGLE_DEG = 168f
private val BaseGradientStops = arrayOf(
    0.00f to Color(0xFF0A1633),
    0.36f to Color(0xFF11264D),
    0.68f to Color(0xFF231A5C),
    1.00f to Color(0xFF080F26),
)

/**
 * 90:15 — the conic wash. Figma authors it as `conic-gradient(from 90deg, …)` inside a
 * layer that is itself rotated -90°, so the ramp starts at 12 o'clock and runs clockwise.
 * `Brush.sweepGradient` starts at 3 o'clock instead, so every stop is shifted by -25% and
 * the wrap-around stop is repeated at 1.0 to close the ramp seamlessly.
 */
private val SweepStops = arrayOf(
    0.000f to Color(0x38BA6CFF),
    0.125f to Color(0x426B53C5),
    0.250f to Color(0x4D1B3A8A),
    0.500f to Color(0x24B6D0FF),
    0.750f to Color(0x4D5B46E4),
    1.000f to Color(0x38BA6CFF),
)
private const val SWEEP_ALPHA = 0.75f

/**
 * One of the four radial "blobs" (90:16 … 90:19).
 *
 * [centerX] and [radius] are fractions of the screen width, [centerY] a fraction of its
 * height — sizing the radius off the width alone keeps every blob circular on any aspect
 * ratio. [radius] already folds in the stop at which Figma's gradient reaches full
 * transparency (0.68-0.7 of the raw handle length), so the fill ramps [color] to clear
 * across exactly that distance.
 */
private data class Blob(
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val color: Color,
)

private val BackgroundBlobs = listOf(
    // 90:16 — 300 dp circle hung off the top-right corner (right -80, top -110).
    Blob(centerX = 325f / REF_W, centerY = 25f / REF_H, radius = 158.7f / REF_W, color = Color(0x9EBA6CFF)),
    // 90:17 — 320 dp circle hung off the bottom-left corner (left -110, bottom 60).
    Blob(centerX = 50f / REF_W, centerY = 672f / REF_H, radius = 158.4f / REF_W, color = Color(0x945B46E4)),
    // 90:18 — 250 dp circle a little below centre.
    Blob(centerX = 227.6f / REF_W, centerY = 428.3f / REF_H, radius = 123.8f / REF_W, color = Color(0x991B3A8A)),
    // 90:19 — 210 dp circle on the upper-left edge (left -40, top 12%).
    Blob(centerX = 65f / REF_W, centerY = 212f / REF_H, radius = 101f / REF_W, color = Color(0x57B6D0FF)),
)

/**
 * 90:20 — an ellipse centred on the top edge, transparent across its inner 40% and
 * reaching 55% opacity at its rim. Its radii are 1.2 × width and 0.8 × height.
 */
private val VignetteColor = Color(0xFF080F26)
private const val VIGNETTE_RX = 1.2f
private const val VIGNETTE_RY = 0.8f
private const val VIGNETTE_INNER_STOP = 0.4f
private const val VIGNETTE_MAX_ALPHA = 0.55f

/**
 * Full-bleed backdrop for the agent screen — see the layer table above. Draws in a single
 * pass with no layout children, so it is safe to hand straight to `safeHazeSource`.
 */
@Composable
fun AgentBackground(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(brush = cssLinearGradient(size, BASE_ANGLE_DEG, BaseGradientStops))
        drawRect(
            brush = Brush.sweepGradient(*SweepStops, center = center),
            alpha = SWEEP_ALPHA,
        )
        BackgroundBlobs.forEach { drawBlob(it) }
        drawTopVignette()
    }
}

/**
 * Resolves a CSS `linear-gradient(<angle>, …)` onto [size]. CSS measures the angle
 * clockwise from "up" and sizes the gradient line so the two ends land on the corners
 * perpendicular to it — neither is expressible with Compose's `Brush.linearGradient`
 * shorthands, hence the explicit endpoints.
 */
private fun cssLinearGradient(
    size: Size,
    angleDeg: Float,
    stops: Array<Pair<Float, Color>>,
): Brush {
    val radians = angleDeg * PI.toFloat() / 180f
    val dirX = sin(radians)
    val dirY = -cos(radians)
    val halfLine = (abs(size.width * dirX) + abs(size.height * dirY)) / 2f
    val cx = size.width / 2f
    val cy = size.height / 2f
    return Brush.linearGradient(
        *stops,
        start = Offset(cx - dirX * halfLine, cy - dirY * halfLine),
        end = Offset(cx + dirX * halfLine, cy + dirY * halfLine),
    )
}

private fun DrawScope.drawBlob(blob: Blob) {
    val blobCenter = Offset(size.width * blob.centerX, size.height * blob.centerY)
    val blobRadius = size.width * blob.radius
    drawCircle(
        brush = Brush.radialGradient(
            0f to blob.color,
            1f to blob.color.copy(alpha = 0f),
            center = blobCenter,
            radius = blobRadius,
        ),
        radius = blobRadius,
        center = blobCenter,
    )
}

/**
 * Compose's radial gradients are circular, so the vignette is drawn at its horizontal
 * radius and then stretched vertically about the top-centre pivot. The rect is
 * pre-divided by that factor so it still covers the whole screen once scaled.
 */
private fun DrawScope.drawTopVignette() {
    val radiusX = size.width * VIGNETTE_RX
    val radiusY = size.height * VIGNETTE_RY
    val pivot = Offset(size.width / 2f, 0f)
    val stretchY = radiusY / radiusX
    scale(scaleX = 1f, scaleY = stretchY, pivot = pivot) {
        drawRect(
            brush = Brush.radialGradient(
                VIGNETTE_INNER_STOP to VignetteColor.copy(alpha = 0f),
                1f to VignetteColor.copy(alpha = VIGNETTE_MAX_ALPHA),
                center = pivot,
                radius = radiusX,
            ),
            topLeft = Offset.Zero,
            size = Size(size.width, size.height / stretchY),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AgentBackgroundPreview() {
    PreviewRtlThemeContent {
        AgentBackground(modifier = Modifier.fillMaxSize())
    }
}
