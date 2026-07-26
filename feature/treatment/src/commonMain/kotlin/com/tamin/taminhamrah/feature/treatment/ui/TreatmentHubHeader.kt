package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlin.math.roundToInt

/** Drag distance over which the insured-person card folds all the way into the header. */
internal val HeaderCollapseDistance = 96.dp

/**
 * Drives the treatment hub's collapsing header the way a CollapsingToolbar does: a
 * [NestedScrollConnection] consumes the body's drag to fold the card *before* the content scrolls,
 * so the transition plays under the finger and works even when the hub's own content is short.
 *
 * [progress] runs 0 → 1 and is read only inside layout/draw lambdas, keeping the morph off the
 * composition path. On release mid-fold, [nestedScrollConnection] snaps to whichever end is nearer.
 */
@Stable
class TreatmentHeaderCollapse(private val maxCollapsePx: Float) {
    var offsetPx by mutableFloatStateOf(0f)
        private set

    val progress: Float
        get() = if (maxCollapsePx <= 0f) 0f else (offsetPx / maxCollapsePx).coerceIn(0f, 1f)

    val nestedScrollConnection = object : NestedScrollConnection {
        // Dragging the content up folds the card first, before the content itself scrolls.
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val dy = available.y
            if (dy < 0f && offsetPx < maxCollapsePx) {
                val applied = minOf(-dy, maxCollapsePx - offsetPx)
                offsetPx += applied
                return Offset(0f, -applied)
            }
            return Offset.Zero
        }

        // Dragging back down unfolds it, but only once the content has reached its own top.
        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            val dy = available.y
            if (dy > 0f && offsetPx > 0f) {
                val applied = minOf(dy, offsetPx)
                offsetPx -= applied
                return Offset(0f, applied)
            }
            return Offset.Zero
        }

        // On release mid-fold, smoothly snap based on gesture direction and velocity.
        override suspend fun onPreFling(available: Velocity): Velocity {
            if (offsetPx > 0f && offsetPx < maxCollapsePx) {
                val target = when {
                    available.y < -200f -> maxCollapsePx
                    available.y > 200f -> 0f
                    offsetPx >= maxCollapsePx / 2f -> maxCollapsePx
                    else -> 0f
                }
                animate(
                    initialValue = offsetPx,
                    targetValue = target,
                    animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
                ) { value, _ -> offsetPx = value }
                return available
            }
            return Velocity.Zero
        }
    }
}

@Composable
internal fun rememberTreatmentHeaderCollapse(): TreatmentHeaderCollapse {
    val maxCollapsePx = with(LocalDensity.current) { HeaderCollapseDistance.toPx() }
    return remember(maxCollapsePx) { TreatmentHeaderCollapse(maxCollapsePx) }
}

/**
 * The hub header: the gradient bar keeps its colors in place while the "درمان" title fades and the
 * insured-person [card] riding up into it morphs as [progress] runs 0 → 1. It sits anchored over
 * the scrollable body so content slides under it when collapsed.
 */
@Composable
internal fun TreatmentHubHeader(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    card: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = "درمان",
            centerTitle = false,
            bottomPadding = TreatmentDimens.cardOverlap + Spacing.xl,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val p = progress()
                    val overlapPx = lerp(
                        TreatmentDimens.cardOverlap.toPx(),
                        TreatmentDimens.collapsedCardOverlap.toPx(),
                        p,
                    )
                    val placeable = measurable.measure(constraints)
                    // Ride the card up into the header band and reclaim that overlap.
                    val reserved = (placeable.height - overlapPx)
                        .coerceAtLeast(0f)
                        .roundToInt()
                    layout(placeable.width, reserved) {
                        placeable.place(0, -overlapPx.roundToInt())
                    }
                },
        ) {
            card()
        }
    }
}
