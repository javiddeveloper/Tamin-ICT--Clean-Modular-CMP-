package com.tamin.taminhamrah.ui.toparea

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.util.lerp
import kotlin.math.roundToInt

private fun localProgress(global: Float, startProgress: Float, endProgress: Float): Float {
    if (endProgress <= startProgress) return if (global >= endProgress) 1f else 0f
    return ((global - startProgress) / (endProgress - startProgress)).coerceIn(0f, 1f)
}

/**
 * Marks a top-area child as never moving, fading, or resizing with scroll. A no-op modifier --
 * its only purpose is to document intent at the call site, next to children that do use one of
 * the behaviors below.
 */
fun Modifier.topAreaFixed(): Modifier = this

/** Fades a child from [from] to [to] alpha as [state]'s progress moves from [startProgress] to [endProgress]. */
fun Modifier.topAreaAlpha(
    state: TopAreaState,
    from: Float = 1f,
    to: Float = 0f,
    startProgress: Float = 0f,
    endProgress: Float = 1f,
): Modifier = graphicsLayer {
    alpha = lerp(from, to, localProgress(state.progress, startProgress, endProgress))
}

/** Translates a child vertically by up to [distance] as [state] folds. */
fun Modifier.topAreaTranslateY(
    state: TopAreaState,
    distance: Dp,
    startProgress: Float = 0f,
    endProgress: Float = 1f,
): Modifier = graphicsLayer {
    val p = localProgress(state.progress, startProgress, endProgress)
    translationY = -(distance.toPx() * p)
}

/** Scales a child from [from] to [to] as [state] folds. */
fun Modifier.topAreaScale(
    state: TopAreaState,
    from: Float = 1f,
    to: Float = 0.85f,
    startProgress: Float = 0f,
    endProgress: Float = 1f,
    transformOrigin: TransformOrigin = TransformOrigin.Center,
): Modifier = graphicsLayer {
    val scale = lerp(from, to, localProgress(state.progress, startProgress, endProgress))
    scaleX = scale
    scaleY = scale
    this.transformOrigin = transformOrigin
}

/**
 * Fades a child out and shrinks its height to zero as [state] folds, so it also stops occupying
 * layout space and can't be tapped once invisible -- for pieces that only belong to the expanded
 * top area (a summary card, a subtitle row).
 *
 * Width is left untouched (unlike height) so a centered child doesn't get horizontally
 * re-centered into a shrinking box as it folds -- that read as the child sliding sideways instead
 * of fading in place.
 */
fun Modifier.topAreaHide(
    state: TopAreaState,
    startProgress: Float = 0f,
    endProgress: Float = 1f,
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val visible = 1f - localProgress(state.progress, startProgress, endProgress)
    layout(placeable.width, (placeable.height * visible).roundToInt()) {
        placeable.placeRelativeWithLayer(0, 0) { alpha = visible }
    }
}

/** Feeds the top area's real rendered height into [state], for [topAreaContentSpacer] to track. */
fun Modifier.reportTopAreaHeight(state: TopAreaState): Modifier =
    onSizeChanged { state.measuredHeightPx = it.height }

/**
 * Reserves [state]'s last-reported height at the caller's full width -- stands in for the
 * floating top area as the first item of the scrollable content it overlays.
 *
 * The height is read at layout time, so the top area folding re-lays out the reserved space
 * without recomposing anything, and the content never jumps.
 */
fun Modifier.topAreaContentSpacer(state: TopAreaState): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints.fixed(constraints.maxWidth, state.measuredHeightPx.coerceAtLeast(0)),
    )
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

/**
 * Shifts this child up by [overlap] to overlap the previous sibling's bottom edge -- e.g. a
 * summary/stats card riding up into the reserved bottom space of a folding hero header above it --
 * while reporting a height reduced by that same amount, so a parent measuring total column height
 * (typically via [reportTopAreaHeight]) sees the true visual footprint instead of double-counting
 * the overlap as reserved space.
 *
 * Previously re-declared file-local in three screens (`LegalRepresentativeWorkshopsScreen`,
 * `ObjectionStatusScreen`, `EmployerOnlineServicesScreen`) per this file's own precedent of
 * extracting on a fourth use -- `CalculateWagePensionScreen` is that fourth use.
 */
fun Modifier.straddlePreviousSibling(overlap: Dp): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val overlapPx = overlap.roundToPx()
    val reportedHeight = (placeable.height - overlapPx).coerceAtLeast(0)
    layout(placeable.width, reportedHeight) {
        placeable.placeRelative(0, -overlapPx)
    }
}

/**
 * Alternative to [topAreaContentSpacer] for callers that would rather reserve the top area's
 * height via `contentPadding` than via a leading spacer item -- avoids a spacer participating in
 * a `LazyColumn`'s `verticalArrangement.spacedBy`, which would otherwise add one extra gap between
 * the spacer and the first real item on top of the reserved height.
 *
 * The returned [PaddingValues]' top inset is read from [state] at layout time (same mechanism as
 * [topAreaContentSpacer]), so it re-measures as the top area folds without recomposing the caller.
 * [rest] supplies every other edge (e.g. bottom inset for the navigation bar).
 */
@Composable
fun topAreaContentPadding(state: TopAreaState, rest: PaddingValues = PaddingValues()): PaddingValues {
    val density = LocalDensity.current
    return remember(state, density, rest) {
        object : PaddingValues {
            override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
                rest.calculateLeftPadding(layoutDirection)

            override fun calculateTopPadding(): Dp =
                with(density) { state.measuredHeightPx.coerceAtLeast(0).toDp() }

            override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
                rest.calculateRightPadding(layoutDirection)

            override fun calculateBottomPadding(): Dp = rest.calculateBottomPadding()
        }
    }
}
