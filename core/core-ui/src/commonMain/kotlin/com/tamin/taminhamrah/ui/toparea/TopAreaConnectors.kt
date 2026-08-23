package com.tamin.taminhamrah.ui.toparea

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * Creates the [TopAreaState] for one screen.
 *
 * [expandedHeight] and [collapsedHeight] define how much drag it takes to fold the top area fully
 * (their difference). The top area's actual on-screen height is still tracked live via
 * [reportTopAreaHeight] / [topAreaContentSpacer], so the content never jumps even if a screen's
 * real rendered height doesn't match these numbers exactly.
 */
@Composable
fun rememberTopAreaState(expandedHeight: Dp, collapsedHeight: Dp): TopAreaState {
    val density = LocalDensity.current
    val maxOffsetPx = with(density) { (expandedHeight - collapsedHeight).toPx() }.coerceAtLeast(0f)
    val initialHeightPx = with(density) { expandedHeight.toPx() }.roundToInt()
    return remember(maxOffsetPx, initialHeightPx) { TopAreaState(maxOffsetPx, initialHeightPx) }
}

/**
 * Drives [state] from a [LazyColumn][androidx.compose.foundation.lazy.LazyColumn]'s own drag.
 * Apply to the `LazyColumn` itself, alongside its `state = listState`.
 */
fun Modifier.driveTopArea(state: TopAreaState, listState: LazyListState): Modifier =
    nestedScroll(state.connection { listState.canScrollForward })

/**
 * Drives [state] from a [LazyVerticalGrid][androidx.compose.foundation.lazy.grid.LazyVerticalGrid]'s
 * own drag. Apply to the grid itself, alongside its `state = gridState`.
 */
fun Modifier.driveTopArea(state: TopAreaState, gridState: LazyGridState): Modifier =
    nestedScroll(state.connection { gridState.canScrollForward })

/**
 * Drives [state] from a `Column(Modifier.verticalScroll(scrollState))`'s own drag. Apply to that
 * `Column`.
 */
fun Modifier.driveTopArea(state: TopAreaState, scrollState: ScrollState): Modifier =
    nestedScroll(state.connection { scrollState.canScrollForward })
