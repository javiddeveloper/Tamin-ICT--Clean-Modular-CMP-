package com.tamin.taminhamrah.ui.toparea

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
     */
    internal fun connection(contentCanScrollForward: () -> Boolean): NestedScrollConnection =
        object : NestedScrollConnection {

            // available.y < 0: content scrolling toward later content -- fold.
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val dy = available.y
                if (dy >= 0f || !contentCanScrollForward()) return Offset.Zero
                return Offset(0f, -collapseBy(-dy))
            }

            // available.y > 0: content scrolling back toward the beginning, with nothing left
            // for the content itself to consume (already at its own start) -- unfold.
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val dy = available.y
                return if (dy > 0f) Offset(0f, expandBy(dy)) else Offset.Zero
            }

            // On release mid-fold, spring to whichever end the gesture was heading for.
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (rawOffsetPx <= 0f || rawOffsetPx >= maxOffsetPx) return Velocity.Zero
                val target = when {
                    available.y < -FlingThreshold -> maxOffsetPx
                    available.y > FlingThreshold -> 0f
                    rawOffsetPx >= maxOffsetPx / 2f -> maxOffsetPx
                    else -> 0f
                }
                animate(
                    initialValue = rawOffsetPx,
                    targetValue = target,
                    animationSpec = SnapSpec,
                ) { value, _ -> rawOffsetPx = value }
                return available
            }
        }

    private companion object {
        /** Below this fling velocity, a release reads as "let go", not as a flick either way. */
        const val FlingThreshold = 200f

        val SnapSpec = spring<Float>(dampingRatio = 0.6f, stiffness = 320f)
    }
}
