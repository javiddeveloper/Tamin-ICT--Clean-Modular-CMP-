package com.tamin.taminhamrah.ui.toparea

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Single source of truth for a screen's scroll-driven top area: how far it has folded, and its
 * last actually-measured height.
 *
 * [progress] is 0 when fully expanded and 1 when fully collapsed. It's backed by snapshot state,
 * so it must only be read inside layout/draw phases (a `layout {}`, `graphicsLayer {}`, or
 * `drawBehind {}` lambda) via [progressProvider] -- reading it during composition would make every
 * scroll pixel recompose the whole top area.
 */
@Stable
class TopAreaState internal constructor(
    private val maxOffsetPx: Float,
    initialMeasuredHeightPx: Int,
    private val scope: CoroutineScope,
    /**
     * True only for the frozen, off-screen instances [rememberMeasuredTopAreaState] hands its
     * `header` lambda to measure natural height. A header must treat this as "don't run anything
     * that keeps requesting animation frames" -- e.g. an infinitely-repeating ring/shimmer -- since
     * two of these are composed on every probe and never actually placed or drawn.
     */
    val isMeasureProbe: Boolean = false,
) {
    var rawOffsetPx: Float by mutableFloatStateOf(0f)
        private set

    val progress: Float by derivedStateOf {
        if (maxOffsetPx <= 0f) 0f else (rawOffsetPx / maxOffsetPx).coerceIn(0f, 1f)
    }

    /** [progress] as the lambda that layout/draw-phase modifiers read, allocated once. */
    val progressProvider: () -> Float = { progress }

    /** The top area's last-measured height, kept live by [reportTopAreaHeight]. */
    var measuredHeightPx: Int by mutableIntStateOf(initialMeasuredHeightPx)
        internal set

    /** The in-flight release-to-edge snap, if any -- cancelled the moment a new drag arrives. */
    private var settleJob: Job? = null

    /** Folds the top area by [amount] (>= 0) px, clamped to [maxOffsetPx]. Returns the px actually applied. */
    private fun collapseBy(amount: Float): Float {
        val old = rawOffsetPx
        val new = (old + amount).coerceIn(0f, maxOffsetPx)
        rawOffsetPx = new
        return new - old
    }

    /** Unfolds the top area by [amount] (>= 0) px, clamped to zero. Returns the px actually applied. */
    private fun expandBy(amount: Float): Float {
        val old = rawOffsetPx
        val new = (old - amount).coerceIn(0f, maxOffsetPx)
        rawOffsetPx = new
        return old - new
    }

    private val isMidFold: Boolean
        get() = rawOffsetPx > 0f && rawOffsetPx < maxOffsetPx

    /**
     * Springs to fully expanded or fully collapsed (the nearer edge when [scrollVelocityY] is
     * below [FlingThreshold]). No-op when already at an edge. Safe to call from IME / inset
     * side-effects that may have left the header mid-fold without a fling.
     */
    fun settleToNearestEdge(scrollVelocityY: Float = 0f) {
        if (!isMidFold) return
        val target = when {
            scrollVelocityY < -FlingThreshold -> maxOffsetPx
            scrollVelocityY > FlingThreshold -> 0f
            rawOffsetPx >= maxOffsetPx / 2f -> maxOffsetPx
            else -> 0f
        }
        animateTo(target, initialVelocity = -scrollVelocityY)
    }

    /** Springs to the fully collapsed edge. No-op when already collapsed. */
    fun collapseFully() {
        if (rawOffsetPx >= maxOffsetPx) return
        animateTo(maxOffsetPx)
    }

    /** Springs to the fully expanded edge. No-op when already expanded. */
    fun expandFully() {
        if (rawOffsetPx <= 0f) return
        animateTo(0f)
    }

    private fun animateTo(target: Float, initialVelocity: Float = 0f) {
        settleJob?.cancel()
        settleJob = scope.launch {
            animate(
                initialValue = rawOffsetPx,
                targetValue = target,
                initialVelocity = initialVelocity,
                animationSpec = SnapSpec,
            ) { value, _ -> rawOffsetPx = value }
        }
    }

    /**
     * A [NestedScrollConnection] that folds the top area from the driven content's own drag,
     * before the content scrolls -- so the fold plays under the finger and works even when the
     * content is too short to scroll on its own.
     *
     * Sign convention (matches Compose's own `ScrollableState.scrollBy`, not "finger up/down"):
     * scrolling *toward later content* dispatches a **negative** `available.y`; scrolling *back
     * toward the beginning* dispatches a **positive** one. `onPreScroll` only ever folds
     * (`collapseBy`) on the negative branch; `onPostScroll` only ever unfolds (`expandBy`) on the
     * positive branch. Getting this backwards makes forward scroll expand and backward scroll
     * collapse, which is silent and easy to miss in a manual test -- so don't special-case around
     * these two branches without re-deriving the sign from `ScrollableState.scrollBy` first.
     *
     * [contentCanScrollForward] must reflect the driven scrollable's own `canScrollForward`. A
     * fold is only started when the content actually has more to reveal below -- this is what
     * keeps the top area fully expanded when the content can't scroll at all, with no separate
     * "not scrollable" special case needed anywhere else.
     *
     * Only [NestedScrollSource.UserInput] drives the fold. Programmatic scrolls (IME
     * bring-into-view, relocate, inset-driven adjustments) are ignored so they cannot leave the
     * header mid-fold with no fling to snap it to an edge.
     */
    internal fun connection(
        contentCanScrollForward: () -> Boolean,
    ): NestedScrollConnection =
        object : NestedScrollConnection {

            // available.y < 0: content scrolling toward later content -- fold.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) {
                    return Offset.Zero
                }
                val dy = available.y
                if (dy >= 0f || !contentCanScrollForward()) return Offset.Zero
                settleJob?.cancel()
                return Offset(0f, -collapseBy(-dy))
            }

            // available.y > 0: content scrolling back toward the beginning, with nothing left
            // for the content itself to consume (already at its own start) -- unfold.
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source != NestedScrollSource.UserInput) {
                    return Offset.Zero
                }
                val dy = available.y
                if (dy <= 0f) return Offset.Zero
                settleJob?.cancel()
                return Offset(0f, expandBy(dy))
            }

            // On release mid-fold, spring to whichever end the gesture was heading for. This is
            // fired into `scope` rather than suspended on directly: `onPreFling` runs on the same
            // coroutine as the driven scrollable's own fling dispatch, so awaiting a multi-hundred-
            // millisecond spring here would hold that scrollable's mutex the whole time -- and a
            // fast follow-up touch arriving in that window can end up silently dropped instead of
            // starting its own gesture. Returning immediately keeps the scrollable free the instant
            // the finger lifts, while the header still snaps to its edge on its own.
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!isMidFold) return Velocity.Zero
                settleToNearestEdge(scrollVelocityY = available.y)
                return available
            }
        }

    companion object {
        /** Below this fling velocity, a release reads as "let go", not as a flick either way. */
        private const val FlingThreshold = 200f

        private val SnapSpec = spring<Float>(stiffness = Spring.StiffnessLow)

        /**
         * A non-interactive [TopAreaState] frozen at [progress] (0f fully expanded, 1f fully
         * collapsed) -- used only to measure a header's real height at its two extremes, for
         * [rememberMeasuredTopAreaState]. Never wired to a live drag: `maxOffsetPx` is fixed at
         * 1f purely so [progress] reads back exactly the value it was frozen at.
         */
        internal fun probe(progress: Float, scope: CoroutineScope): TopAreaState =
            TopAreaState(maxOffsetPx = 1f, initialMeasuredHeightPx = 0, scope = scope, isMeasureProbe = true).also {
                it.rawOffsetPx = progress.coerceIn(0f, 1f)
            }
    }
}
