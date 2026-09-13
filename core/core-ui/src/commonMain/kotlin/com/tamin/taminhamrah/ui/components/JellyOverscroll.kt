package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs
import kotlin.math.sign

/**
 * iOS-style rubber-band overscroll — the "jelly" pull the system apps have.
 *
 * Past the edge the content keeps following the finger, but with resistance that grows the further
 * it goes, and it springs back the moment the finger lifts. A fling that reaches the edge throws
 * the content out and pulls it home. This replaces the platform's own edge effect (Android's glow
 * or stretch) wherever it is used, so the feel is the same on both platforms.
 *
 * Use it by handing it to the scrollable, which then both feeds and draws it:
 *
 * ```
 * Column(Modifier.verticalScroll(scrollState, overscrollEffect = rememberJellyOverscroll()))
 * ```
 *
 * The pull is held in a snapshot float read only inside the draw lambda, so a stretch costs a
 * redraw and never a recomposition or a re-layout — the content is translated, not measured again.
 */
@Stable
class JellyOverscrollEffect(
    /** Which way the scroller it is handed to runs. The band only ever pulls along that axis. */
    private val orientation: Orientation = Orientation.Vertical,
) : OverscrollEffect {

    private val vertical: Boolean get() = orientation == Orientation.Vertical

    /** Signed pixels the content is pulled past its edge; positive is down, or toward the end. */
    private var pullPx by mutableFloatStateOf(0f)

    /**
     * The scroller's own extent along [orientation], learned while drawing: the band never
     * stretches beyond it.
     */
    private var viewportPx = 0f

    override val isInProgress: Boolean
        get() = pullPx != 0f

    override fun applyToScroll(
        delta: Offset,
        source: NestedScrollSource,
        performScroll: (Offset) -> Offset,
    ): Offset {
        // A programmatic scroll (a snap, a scrollTo) should land where it was told to, not wobble.
        if (source != NestedScrollSource.UserInput) return performScroll(delta)

        // Dragging back toward the content releases the band one-to-one first, as a real one would.
        val along = if (vertical) delta.y else delta.x
        val released = release(along)
        val offered = if (vertical) Offset(delta.x, along - released) else Offset(along - released, delta.y)
        val taken = performScroll(offered)
        val consumed = if (vertical) taken.y else taken.x
        // Whatever is left over nobody wanted — not the scroller, not the header above it — so it
        // goes into the band.
        val stretched = stretch(along - released - consumed)
        val total = released + consumed + stretched
        return if (vertical) Offset(taken.x, total) else Offset(total, taken.y)
    }

    override suspend fun applyToFling(velocity: Velocity, performFling: suspend (Velocity) -> Velocity) {
        val performed = performFling(velocity)
        val leftover = if (vertical) velocity.y - performed.y else velocity.x - performed.x
        if (pullPx == 0f && leftover == 0f) return
        // The leftover throw carries the band out; the spring brings it home. Critically damped, so
        // it settles without crossing back past the edge and flashing a gap on the other side.
        animate(
            initialValue = pullPx,
            targetValue = 0f,
            initialVelocity = leftover * FingerFollowRate,
            animationSpec = SpringBack,
        ) { value, _ -> pullPx = value }
    }

    /** Gives back tension against the drag, returning how much of [delta] that took. */
    private fun release(delta: Float): Float {
        if (pullPx == 0f || sign(delta) == sign(pullPx)) return 0f
        val released = if (abs(delta) > abs(pullPx)) -pullPx else delta
        pullPx += released
        return released
    }

    /**
     * Pulls the band by [delta], returning how much of it was absorbed.
     *
     * The content moves by less and less as the pull grows — at the very edge it follows the finger
     * at [FingerFollowRate], and it can never be dragged further than the viewport's own height.
     */
    private fun stretch(delta: Float): Float {
        if (delta == 0f || viewportPx <= 0f) return 0f
        val slack = (1f - abs(pullPx) / viewportPx).coerceIn(0f, 1f)
        pullPx += delta * FingerFollowRate * slack * slack
        return delta
    }

    override val node: DelegatableNode = JellyNode()

    private inner class JellyNode : Modifier.Node(), DrawModifierNode {
        override fun ContentDrawScope.draw() {
            viewportPx = if (vertical) size.height else size.width
            val pull = pullPx
            when {
                pull == 0f -> drawContent()
                vertical -> translate(top = pull) { this@draw.drawContent() }
                else -> translate(left = pull) { this@draw.drawContent() }
            }
        }
    }

    private companion object {
        /**
         * How much of the finger's travel the content follows once past the edge. Both the pull and
         * the fling that starts the bounce are scaled by it — iOS uses about this much.
         */
        const val FingerFollowRate = 0.55f

        val SpringBack = spring<Float>(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
    }
}

/**
 * A [JellyOverscrollEffect] for one scrollable. Hand it to the scrollable that will drive it:
 *
 * ```
 * Column(Modifier.verticalScroll(state, overscrollEffect = rememberJellyOverscroll()))
 * Row(Modifier.horizontalScroll(state, overscrollEffect = rememberJellyOverscroll(Horizontal)))
 * ```
 *
 * One effect belongs to one scrollable — it holds that scroller's own pull — so call this once per
 * scrollable rather than hoisting a single instance across several.
 */
@Composable
fun rememberJellyOverscroll(
    orientation: Orientation = Orientation.Vertical,
): JellyOverscrollEffect = remember(orientation) { JellyOverscrollEffect(orientation) }
