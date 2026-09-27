package com.tamin.taminhamrah.ui.toparea

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * Creates the [TopAreaState] for one screen.
 *
 * [expandedHeight] and [collapsedHeight] define how much drag it takes to fold the top area fully
 * (their difference). The top area's actual on-screen height is still tracked live via
 * [reportTopAreaHeight] / [topAreaContentSpacer], so the content never jumps even if a screen's
 * real rendered height doesn't match these numbers exactly.
 *
 * The coroutine scope for the release-to-edge snap ([TopAreaState.connection]'s `onPreFling`) is
 * captured once here, so [driveTopArea] can stay a plain, non-composable `Modifier` factory
 * instead of opening a `composed {}` sub-composition on the app's hottest, most-repeated
 * modifier chain.
 */
@Composable
fun rememberTopAreaState(expandedHeight: Dp, collapsedHeight: Dp): TopAreaState {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val maxOffsetPx = with(density) { (expandedHeight - collapsedHeight).toPx() }.coerceAtLeast(0f)
    val initialHeightPx = with(density) { expandedHeight.toPx() }.roundToInt()
    return remember(maxOffsetPx, initialHeightPx) { TopAreaState(maxOffsetPx, initialHeightPx, scope) }
}

/**
 * Creates the [TopAreaState] for one screen the same way [rememberTopAreaState] does, except the
 * drag budget -- how far the header must fold to go from expanded to collapsed -- is *measured*
 * from [header] itself at progress 0 and 1, instead of taking two guessed [Dp] constants. This is
 * what keeps the fold in sync with what the header actually renders: a longer string, a bigger
 * font, or an extra line all change the real collapse distance, and a hardcoded budget has no way
 * to know that changed.
 *
 * [header] is composed twice, off-screen and unplaced, purely to measure its natural height at
 * each extreme -- so it must be side-effect-free the same way any composable subject to Compose's
 * own recomposition/skip discipline already needs to be (no `LaunchedEffect` tied to first
 * composition, no analytics fired from the composable body). The caller still composes the real,
 * interactive header separately, with [reportTopAreaHeight] on it, exactly as with
 * [rememberTopAreaState] -- this only replaces how the drag budget is computed.
 *
 * The probe only re-measures when the available width changes (e.g. rotation) or [key] changes --
 * not on every recomposition -- so a screen dragging this header sixty times a second never pays
 * for the two extra measurements more than once. If a header's height can also change shape for a
 * reason unrelated to width (e.g. a status line that sometimes wraps to a second line), pass
 * something that captures that in [key] to force a re-probe when it does.
 */
@Composable
fun rememberMeasuredTopAreaState(
    key: Any? = null,
    header: @Composable (state: TopAreaState) -> Unit,
): TopAreaState {
    val scope = rememberCoroutineScope()
    var maxOffsetPx by remember { mutableFloatStateOf(0f) }
    var expandedHeightPx by remember { mutableIntStateOf(0) }
    // Plain mutable cache keys (not snapshot state) read/written only inside the measure lambda
    // below -- they gate the probe re-measurement, they don't drive recomposition themselves.
    val probedWidthPx = remember { intArrayOf(-1) }
    val probedKey = remember { arrayOf<Any?>(Unit) }

    SubcomposeLayout { constraints ->
        if (probedWidthPx[0] != constraints.maxWidth || probedKey[0] != key) {
            probedWidthPx[0] = constraints.maxWidth
            probedKey[0] = key
            val probeConstraints = Constraints(
                minWidth = 0,
                maxWidth = constraints.maxWidth,
                minHeight = 0,
                maxHeight = Constraints.Infinity,
            )

            val expandedPx = subcompose(TopAreaProbeSlot.Expanded) {
                header(remember { TopAreaState.probe(progress = 0f, scope = scope) })
            }.first().measure(probeConstraints).height

            val collapsedPx = subcompose(TopAreaProbeSlot.Collapsed) {
                header(remember { TopAreaState.probe(progress = 1f, scope = scope) })
            }.first().measure(probeConstraints).height

            maxOffsetPx = (expandedPx - collapsedPx).coerceAtLeast(0).toFloat()
            expandedHeightPx = expandedPx
        }
        // Never actually placed -- this node exists only to run the probe measurements above.
        layout(0, 0) {}
    }

    return remember(maxOffsetPx, expandedHeightPx) {
        TopAreaState(maxOffsetPx, expandedHeightPx, scope)
    }
}

private enum class TopAreaProbeSlot { Expanded, Collapsed }

/**
 * Snaps [state] fully collapsed the moment the IME appears, and fully expanded the moment it
 * hides -- one complete motion each way, never stopping mid-fold.
 *
 * The IME's own "bring the focused field into view" scroll dispatches with
 * [androidx.compose.ui.input.nestedscroll.NestedScrollSource.SideEffect], which
 * [TopAreaState.connection] deliberately ignores (see its kdoc) so a few stray px of that scroll
 * can't leave the header stuck mid-fold with nothing to spring it to an edge. That leaves the
 * header wherever the user's last drag put it when the keyboard opens or closes. Call this once
 * per screen, alongside [driveTopArea], to explicitly drive the header to an edge on every IME
 * visibility change instead.
 *
 * Keyed on [state] itself, not just [imeVisible]: [rememberMeasuredTopAreaState] hands out a
 * placeholder instance (`maxOffsetPx = 0`) on the very first composition, before its
 * `SubcomposeLayout` measures the real header and swaps in the correctly-budgeted instance. If the
 * IME's first-ever show in that screen landed inside that window, a `LaunchedEffect` keyed only on
 * [imeVisible] would bind to the placeholder and never rebind -- silently no-op'ing on a disconnected
 * object while the real header never got told to collapse.
 */
@Composable
fun TopAreaState.collapseWhileImeVisible() {
    val state = this
    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    LaunchedEffect(state, imeVisible) {
        if (imeVisible) state.collapseFully() else state.expandFully()
    }
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
