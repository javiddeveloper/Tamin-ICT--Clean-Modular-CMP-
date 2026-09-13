package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import kotlin.math.roundToInt

/**
 * The dots under a pager: one per page, the current one widened into a pill, all of them inside a
 * translucent track.
 *
 * Takes the [pagerState] rather than the current page so that swiping recomposes the dots only —
 * reading `currentPage` where the pager itself is composed would recompose the pager and every page
 * with it.
 *
 * A scrolling [Row] rather than a `LazyRow`: the design puts the dots inside a track, and the track
 * has to hug them. A lazy list measures to its constraints, so it would stretch the track across the
 * whole width. Dot counts are small — one per card — so nothing is gained by keeping them lazy, and
 * the strip still scrolls to hold the active dot in view.
 */
@Composable
fun TaminPageIndicator(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val selectedPage = pagerState.currentPage
    val scrollState = rememberScrollState()
    val density = LocalDensity.current

    LaunchedEffect(selectedPage, pageCount) {
        val step = with(density) { (DotSize + DotGap).toPx() }
        scrollState.animateScrollTo((selectedPage * step).roundToInt())
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Row(
            modifier = Modifier
                .clip(CircleShape)
                // A plain gray track, no outline: a tinted rim reads as a stray border against
                // the light page. Alpha over the neutral so it holds up in both themes.
                .background(colors.chevron.copy(alpha = TrackAlpha))
                .padding(horizontal = TrackPaddingHorizontal, vertical = TrackPaddingVertical)
                .widthIn(max = MaxWidth)
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(DotGap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pageCount) { page ->
                val isSelected = page == selectedPage
                Box(
                    modifier = Modifier
                        .size(
                            width = if (isSelected) SelectedWidth else DotSize,
                            height = DotSize,
                        )
                        .background(
                            color = if (isSelected) colors.teal else colors.chevron,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

private const val TrackAlpha = 0.30f

private val DotSize = 7.dp
private val SelectedWidth = 22.dp
private val DotGap = 7.dp
private val TrackPaddingHorizontal = 11.dp
private val TrackPaddingVertical = 7.dp

/** Past this the strip scrolls rather than growing the track off the card. */
private val MaxWidth = 140.dp
