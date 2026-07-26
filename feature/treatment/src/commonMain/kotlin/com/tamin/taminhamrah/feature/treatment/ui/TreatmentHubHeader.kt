package com.tamin.taminhamrah.feature.treatment.ui

import androidx.compose.animation.core.animate
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

        // On release mid-fold, commit to the nearer end so the header never rests half-collapsed.
        override suspend fun onPreFling(available: Velocity): Velocity {
            if (offsetPx > 0f && offsetPx < maxCollapsePx) {
                val target = if (offsetPx >= maxCollapsePx / 2f) maxCollapsePx else 0f
                animate(initialValue = offsetPx, targetValue = target) { value, _ -> offsetPx = value }
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
 * insured-person [card] riding up into it morphs as [progress] runs 0 → 1. It sits in normal flow
 * above the body, so as the card shrinks the body simply rises to meet it — no reserved gap.
 */
@Composable
internal fun TreatmentHubHeader(
    progress: () -> Float,
    card: @Composable () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TaminTopAppBar(
            title = "درمان",
            centerTitle = false,
            // Deep enough for the card to ride up into the color band below the title.
            bottomPadding = TreatmentDimens.cardOverlap + Spacing.xl
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .layout { measurable, constraints ->
                    val overlap = TreatmentDimens.cardOverlap.toPx()
                    val placeable = measurable.measure(constraints)
                    // Ride the card up into the header band and reclaim that overlap.
                    val reserved = (placeable.height - overlap)
                        .coerceAtLeast(0f)
                        .roundToInt()
                    layout(placeable.width, reserved) {
                        placeable.place(0, -overlap.roundToInt())
                    }
                },
        ) {
            card()
        }
    }
}
