package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import org.jetbrains.compose.resources.painterResource
import kotlin.math.ceil
import kotlin.math.exp
import kotlin.math.roundToInt
import taminx.feature.agent.generated.resources.Res
import taminx.feature.agent.generated.resources.robot

// ─── Agent orb ────────────────────────────────────────────────────────────────
//
// The welcome-screen sphere, ported from the Figma node 90:40 ("Margin"):
//
//   90:41  Container       148 × 148, no fill — the only part that takes up layout space
//   90:43  Overlay+Shadow  188 dp circle, fill white @ 0.2%, drop shadow #BA6CFF @ 45%,
//                          offset 0, blur 70, "show behind node" OFF — a layer that exists
//                          purely to cast a ring of glow around the sphere
//   90:42  Gradient+Blur   160 dp circle, GRADIENT_ANGULAR fill, layer opacity 85%,
//                          layer blur 16
//   90:44  image-slot      an empty 3:2 slot; its only content sits at opacity 0, so it
//                          renders nothing and is not reproduced here
//
// What the old implementation got wrong: a 140 dp circle filled with a *linear* gradient.
// The design's fill is angular (conic), soft-edged, and ringed by a much wider halo.
//
// Both Figma effects here are gaussians, and both are reproduced as gradient ramps sampled
// off the gaussian rather than with `Modifier.blur` — a RenderEffect is a no-op below
// API 31 (minSdk is 24 here), which would leave older devices with a hard-edged pinwheel.

/** 90:41 — the sphere's layout footprint. 90:40 adds a further 16 dp of bottom margin. */
private val OrbSize = 148.dp

// ── 90:43, the halo ───────────────────────────────────────────────────────────

private val HaloColor = Color(0xFFBA6CFF)

/** 94 dp shape + 70 dp blur — the same 328 dp render box Figma reports for this layer. */
private val HaloRadius = 164.dp

/**
 * The drop shadow's alpha profile: `0.45 · Φ((94 - r) / 35)`, sampled every half sigma and
 * expressed as a fraction of [HaloRadius]. The pair of stops at 0.570/0.575 is the "show
 * shadow behind node" toggle being off — Figma punches the shadow out inside the layer's
 * own 188 dp circle, so the glow reads as a ring detached from the sphere rather than as a
 * purple blob sitting behind it.
 */
private val HaloStops = arrayOf(
    0.000f to HaloColor.copy(alpha = 0.000f),
    0.570f to HaloColor.copy(alpha = 0.000f),
    0.575f to HaloColor.copy(alpha = 0.225f),
    0.680f to HaloColor.copy(alpha = 0.137f),
    0.787f to HaloColor.copy(alpha = 0.071f),
    0.893f to HaloColor.copy(alpha = 0.031f),
    1.000f to HaloColor.copy(alpha = 0.010f),
)

// ── 90:42, the sphere ─────────────────────────────────────────────────────────

/** 160 dp of circle, plus the 2·sigma its 16 dp layer blur bleeds into on either side. */
private val DiscSize = 160.dp
private val DiscBlur = 16.dp
private val DiscCanvasSize = DiscSize + DiscBlur * 2
private const val DISC_ALPHA = 0.85f

// ── 90:44, the mascot ─────────────────────────────────────────────────────────

/** Native size of `robot.png`; width is fixed below and height follows this ratio. */
private const val RobotAspectRatio = 126f / 101f

/** Sized to sit well inside the 160 dp sphere with room for its rim glow to still show. */
private val RobotWidth = 148.dp

/**
 * The four corners of 90:42's angular fill, in `Brush.sweepGradient` order. Figma's gradient
 * handles put position 0 at 12 o'clock sweeping clockwise while a sweep gradient starts at
 * 3 o'clock, so the design's ramp is rotated by a quarter turn.
 *
 * (Note: `TopBarAvatarSweep` in AgentScreen.kt uses this same palette *unrotated*, so the
 * small avatar reads 90° off against the design. Left alone — not this component.)
 */
private val OrbCorners = listOf(
    Color(0xFFBA6CFF), //  3 o'clock — Figma stop 25%
    Color(0xFF1B3A8A), //  6 o'clock — Figma stop 50%
    Color(0xFFB6D0FF), //  9 o'clock — Figma stop 75%
    Color(0xFF5B46E4), // 12 o'clock — Figma stop 0/100%
)

/**
 * Angular sigma of the 16 dp layer blur, in turns. The blur's sigma is 8 dp, which at radius
 * `r` smears the sweep over `8/r` radians; sampled at the disc's area-weighted mean radius
 * (⅔ × 80 dp ≈ 53 dp) that is `8/53 rad ≈ 0.024` turns.
 */
private const val ORB_SWEEP_SIGMA_TURNS = 0.024f
private const val ORB_SWEEP_STOPS = 72

private val OrbSweepStops = angularBlurredRamp(OrbCorners, ORB_SWEEP_SIGMA_TURNS, ORB_SWEEP_STOPS)

/**
 * One full turn of the colour sweep. Slow and linear, so the sphere itself never moves —
 * only the hues drift smoothly around its centre, like a stirred pot of colour rather than
 * a spinning object.
 */
private const val ORB_ROTATION_DURATION_MS = 12000

/**
 * What the layer blur does to the *middle* of an angular gradient: near the centre the
 * kernel spans the whole sweep, so every hue averages into one colour — `#7A6FDB`, the mean
 * of the four corners. Without this the ramp still converges on a pinwheel pinch at the
 * exact centre of the sphere, the most-looked-at pixel on the screen.
 *
 * The ramp is the blur's attenuation of the first angular harmonic, `exp(-(sigma/r)² / 2)`
 * with `sigma = 8 dp`, sampled at r = 0, 1, 2, 3 and 4 sigma.
 */
private val OrbCoreColor = Color(0xFF7A6FDB)
private val OrbCoreStops = arrayOf(
    0.000f to OrbCoreColor.copy(alpha = 1.00f),
    0.083f to OrbCoreColor.copy(alpha = 0.39f),
    0.167f to OrbCoreColor.copy(alpha = 0.12f),
    0.250f to OrbCoreColor.copy(alpha = 0.05f),
    0.333f to OrbCoreColor.copy(alpha = 0.03f),
    0.500f to OrbCoreColor.copy(alpha = 0.00f),
)

/**
 * The same blur seen at the *rim*: a hard edge at r = 80 dp becomes `Φ((80 - r) / 8)`,
 * sampled every sigma out to ±2 sigma (64 dp → 96 dp) and applied as an alpha mask with
 * [BlendMode.DstIn].
 */
private val OrbRimMask = arrayOf(
    0.000f to Color.Black,
    0.667f to Color.Black.copy(alpha = 0.977f),
    0.750f to Color.Black.copy(alpha = 0.841f),
    0.833f to Color.Black.copy(alpha = 0.500f),
    0.917f to Color.Black.copy(alpha = 0.159f),
    1.000f to Color.Black.copy(alpha = 0.023f),
)

/**
 * The "یارا" sphere on the agent welcome screen — Figma node 90:40.
 *
 * Occupies [OrbSize]; the halo and the sphere itself are both wider than that and
 * deliberately overflow, so the surrounding column lays out against the design's 148 dp box
 * rather than against the glow.
 */
@Composable
fun AgentOrb(modifier: Modifier = Modifier) {
    // Rotates only the sweep gradient's angle, not the sphere's position or size — the
    // orb sits still while its colours circulate through it.
    val rotation by rememberInfiniteTransition(label = "agent_orb_rotation").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(ORB_ROTATION_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "agent_orb_rotation_angle"
    )

    Box(
        modifier = modifier.size(OrbSize),
        contentAlignment = Alignment.Center,
    ) {
        // 90:43
        Box(
            modifier = Modifier
                .requiredSize(HaloRadius * 2)
                .drawBehind {
                    val radius = size.minDimension / 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            *HaloStops,
                            center = center,
                            radius = radius,
                        ),
                        radius = radius,
                    )
                }
        )

        // 90:42 — composited offscreen so the rim mask has something isolated to cut
        // against, and so the layer opacity applies to the finished result.
        Box(
            modifier = Modifier
                .requiredSize(DiscCanvasSize)
                .graphicsLayer {
                    alpha = DISC_ALPHA
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawBehind {
                    val radius = size.minDimension / 2f
                    // Only the sweep needs rotating — the core and rim layers below are
                    // radially symmetric, so spinning them would be a no-op.
                    rotate(degrees = rotation) {
                        drawCircle(
                            brush = Brush.sweepGradient(*OrbSweepStops, center = center),
                            radius = radius,
                        )
                    }
                    drawCircle(
                        brush = Brush.radialGradient(
                            *OrbCoreStops,
                            center = center,
                            radius = radius,
                        ),
                        radius = radius,
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            *OrbRimMask,
                            center = center,
                            radius = radius,
                        ),
                        radius = radius,
                        blendMode = BlendMode.DstIn,
                    )
                }
        )

        // 90:44 — the mascot face, fixed in place on top of the (rotating) sphere.
        Image(
            painter = painterResource(Res.drawable.robot),
            contentDescription = null,
            modifier = Modifier
                .width(RobotWidth)
                .aspectRatio(RobotAspectRatio)
        )
    }
}

/**
 * Resamples the cyclic ramp through [corners] into [stops] sweep-gradient stops, convolved
 * with a gaussian of [sigmaTurns] standard deviation measured in turns.
 *
 * A raw four-stop sweep is only C0-continuous: its slope breaks at every corner and the eye
 * reads those breaks as creases radiating from the centre at 12, 3, 6 and 9 o'clock. Figma
 * hides them under a layer blur; this does the same arithmetic up front, once, so the
 * shader itself carries the smoothed ramp. Interpolation is component-wise in sRGB to match
 * what the gradient shader would do between the stops it is handed.
 */
private fun angularBlurredRamp(
    corners: List<Color>,
    sigmaTurns: Float,
    stops: Int,
): Array<Pair<Float, Color>> {
    val samples = 720
    val ramp = Array(samples) { i ->
        val position = i.toFloat() / samples * corners.size
        val segment = position.toInt()
        val t = position - segment
        val from = corners[segment % corners.size]
        val to = corners[(segment + 1) % corners.size]
        floatArrayOf(
            from.red + (to.red - from.red) * t,
            from.green + (to.green - from.green) * t,
            from.blue + (to.blue - from.blue) * t,
        )
    }

    val sigma = sigmaTurns * samples
    val reach = ceil(3f * sigma).toInt()
    val kernel = FloatArray(2 * reach + 1) { i ->
        val d = (i - reach).toFloat()
        exp(-(d * d) / (2f * sigma * sigma))
    }
    val total = kernel.sum()

    val blurred = ArrayList<Pair<Float, Color>>(stops + 1)
    for (i in 0 until stops) {
        val at = (i.toFloat() / stops * samples).roundToInt()
        var red = 0f
        var green = 0f
        var blue = 0f
        for (d in -reach..reach) {
            val weight = kernel[d + reach]
            val sample = ramp[((at + d) % samples + samples) % samples]
            red += weight * sample[0]
            green += weight * sample[1]
            blue += weight * sample[2]
        }
        blurred += i.toFloat() / stops to Color(
            red = (red / total).coerceIn(0f, 1f),
            green = (green / total).coerceIn(0f, 1f),
            blue = (blue / total).coerceIn(0f, 1f),
        )
    }
    // Close the ramp on the colour it opened with so the sweep has no seam at 3 o'clock.
    blurred += 1f to blurred.first().second
    return blurred.toTypedArray()
}

@PreviewRtlTheme
@Composable
private fun AgentOrbPreview() {
    PreviewRtlThemeContent {
        Box(
            modifier = Modifier.size(340.dp),
            contentAlignment = Alignment.Center,
        ) {
            AgentBackground()
            AgentOrb()
        }
    }
}
