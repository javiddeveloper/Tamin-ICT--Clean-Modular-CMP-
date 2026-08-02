package com.tamin.taminhamrah.ui.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.theme.Easing
import kotlin.math.roundToInt

/**
 * Drives a collapsing header the way a CollapsingToolbar does: a [NestedScrollConnection] consumes
 * the body's drag to fold the header *before* the content scrolls, so the transition plays under
 * the finger and works even when the body is too short to scroll on its own.
 *
 * [progress] runs 0 → 1 as the header folds. It is backed by a snapshot state, so reading it inside
 * a `layout {}` / `graphicsLayer {}` / `drawBehind {}` lambda re-runs only that phase — the fold
 * never costs a recomposition. Reading it during composition would undo exactly that, so don't.
 *
 * Under the finger the header tracks the drag exactly. On release mid-fold it springs to whichever
 * end the gesture was heading for, overshooting a little and settling back, so the fold lands with
 * the same soft give as an iOS header rather than stopping dead on a timed curve.
 */
@Stable
class CollapsingHeaderState(private val maxCollapsePx: Float) {

    /** How far the header is folded, in pixels. A drag keeps it within 0..`maxCollapsePx`. */
    var offsetPx by mutableFloatStateOf(0f)
        private set

    /**
     * 0 when open, 1 when folded — with a little slack past each end, so the spring's overshoot
     * shows as the header giving slightly rather than being clipped flat against the limit.
     */
    val progress: Float
        get() = if (maxCollapsePx <= 0f) {
            0f
        } else {
            (offsetPx / maxCollapsePx).coerceIn(-OVERSHOOT, 1f + OVERSHOOT)
        }

    /**
     * [progress] as the lambda the layout and draw modifiers take. Allocated once with the state,
     * so handing it down costs no call site a `remember` of its own.
     */
    val progressProvider: () -> Float = { progress }

    val nestedScrollConnection: NestedScrollConnection = object : NestedScrollConnection {

        // Dragging the content up folds the header first, before the content itself scrolls.
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val dy = available.y
            if (dy >= 0f || offsetPx >= maxCollapsePx) return Offset.Zero
            val applied = minOf(-dy, maxCollapsePx - offsetPx)
            offsetPx += applied
            return Offset(0f, -applied)
        }

        // Dragging back down unfolds it, but only once the content has reached its own top.
        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            val dy = available.y
            if (dy <= 0f || offsetPx <= 0f) return Offset.Zero
            val applied = minOf(dy, offsetPx)
            offsetPx -= applied
            return Offset(0f, applied)
        }

        // On release mid-fold, spring to the end the gesture was heading for.
        override suspend fun onPreFling(available: Velocity): Velocity {
            if (offsetPx <= 0f || offsetPx >= maxCollapsePx) return Velocity.Zero
            val target = when {
                available.y < -FLING_THRESHOLD -> maxCollapsePx
                available.y > FLING_THRESHOLD -> 0f
                offsetPx >= maxCollapsePx / 2f -> maxCollapsePx
                else -> 0f
            }
            // The fling's own velocity is deliberately not fed in: the overshoot then depends only
            // on the distance left to travel, so a hard flick can never fling the header far past
            // its stop and crawl back.
            animate(
                initialValue = offsetPx,
                targetValue = target,
                animationSpec = SnapBack,
            ) { value, _ -> offsetPx = value }
            return available
        }
    }

    private companion object {
        /** Below this, a release reads as "let go", not as a flick either way. */
        const val FLING_THRESHOLD = 200f

        /** How far past its stop the header may give, as a fraction of the fold. Tune with [SnapBack]. */
        const val OVERSHOOT = 0.08f

        /** Bouncy enough to read as give, damped enough to settle in one pass. */
        val SnapBack = spring<Float>(dampingRatio = 0.6f, stiffness = 320f)
    }
}

/** A [CollapsingHeaderState] that folds over [collapseDistance] of drag. */
@Composable
fun rememberCollapsingHeaderState(collapseDistance: Dp): CollapsingHeaderState {
    val maxCollapsePx = with(LocalDensity.current) { collapseDistance.toPx() }
    return remember(maxCollapsePx) { CollapsingHeaderState(maxCollapsePx) }
}

/**
 * Reserves [heightPx] pixels of vertical space at the caller's full width.
 *
 * The height is read at layout time, so a spacer standing in for a measured, animating header
 * re-lays out without recomposing anything.
 */
fun Modifier.reservedHeight(heightPx: () -> Int): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints.fixed(constraints.maxWidth, heightPx().coerceAtLeast(0)),
    )
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

/**
 * Fades a piece out as the header folds, [rate] times faster than the fold itself — so pieces that
 * only belong to the expanded state have cleared before the collapsed one forms.
 *
 * [progress] is read inside the draw lambda, so a frame of the fold costs no recomposition.
 */
fun Modifier.vanishOnCollapse(progress: () -> Float, rate: Float = 2f): Modifier = graphicsLayer {
    alpha = (1f - progress() * rate).coerceIn(0f, 1f)
}

/**
 * Like [vanishOnCollapse], but the piece also gives back the space it holds as it goes, so what
 * follows it closes up over the fade instead of jumping once the piece is gone.
 *
 * For a label that only belongs to the expanded state and sits next to something that stays.
 */
fun Modifier.collapseAway(progress: () -> Float, rate: Float = 2f): Modifier =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val fade = (1f - progress() * rate).coerceIn(0f, 1f)
        layout((placeable.width * fade).roundToInt(), (placeable.height * fade).roundToInt()) {
            placeable.placeRelativeWithLayer(0, 0) { alpha = fade }
        }
    }

/**
 * Shrinks a piece toward [minScale] as it travels, anchored to its start edge so it keeps its
 * place in the collapsed bar rather than drifting toward the middle.
 */
fun Modifier.shrinkOnCollapse(
    progress: () -> Float,
    minScale: Float,
    rtl: Boolean,
): Modifier = graphicsLayer {
    val scale = lerp(1f, minScale, Easing.standard.transform(progress()))
    scaleX = scale
    scaleY = scale
    transformOrigin = TransformOrigin(if (rtl) 1f else 0f, 0.5f)
}

/**
 * Lets content ride up into the header above it, by [expandedOverlap] when open and
 * [collapsedOverlap] when folded.
 *
 * The overlap is applied during layout, so the ride costs a re-layout rather than a recomposition,
 * and the space the content reserves below shrinks by exactly what it borrows above.
 */
fun Modifier.rideUpIntoHeader(
    progress: () -> Float,
    expandedOverlap: Dp,
    collapsedOverlap: Dp,
): Modifier = layout { measurable, constraints ->
    val overlapPx = lerp(expandedOverlap.toPx(), collapsedOverlap.toPx(), progress())
    val placeable = measurable.measure(constraints)
    val reserved = (placeable.height - overlapPx).coerceAtLeast(0f).roundToInt()
    layout(placeable.width, reserved) {
        placeable.placeRelative(0, -overlapPx.roundToInt())
    }
}
