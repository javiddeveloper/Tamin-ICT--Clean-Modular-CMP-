package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * The dots under a pager: a static row of inactive dots with a single "worm" pill drawn on top,
 * which stretches its leading edge toward the next dot's center first, then catches up with its
 * trailing edge — an inchworm crawl rather than a per-dot resize.
 *
 * Everything is drawn in one [Canvas], which always paints in physical (LTR) coordinates
 * regardless of [LocalLayoutDirection] — unlike a normal Modifier chain, it does not auto-mirror.
 * So every x-position here goes through [xForPage], which flips the index under RTL, rather than
 * computing `page * step` directly.
 */
@Composable
fun TaminPageIndicator(
    pageCount: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val activeColor = colors.teal
    val inactiveColor = colors.chevron
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val step = DotSize + DotGap
    val trackWidth = step * pageCount - DotGap

    LaunchedEffect(pagerState.currentPage, pageCount, isRtl) {
        val stepPx = with(density) { step.toPx() }
        // The scroll strip itself already mirrors under RTL (it's a normal Modifier chain), so
        // its scroll offset stays logical — scroll to the *logical* index, not the mirrored one.
        scrollState.animateScrollTo((pagerState.currentPage * stepPx).roundToInt())
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(inactiveColor.copy(alpha = TrackAlpha))
                .padding(horizontal = TrackPaddingHorizontal, vertical = TrackPaddingVertical)
                .widthIn(max = MaxWidth)
                .horizontalScroll(scrollState),
        ) {
            Canvas(modifier = Modifier.width(trackWidth).height(DotSize)) {
                val stepPx = step.toPx()
                val dotRadius = DotSize.toPx() / 2f

                // Physical x-center for a logical page index: mirrored under RTL so page 0 sits
                // at the right edge of the track instead of the left.
                fun xForPage(page: Int): Float {
                    val visualIndex = if (isRtl) pageCount - 1 - page else page
                    return visualIndex * stepPx + dotRadius
                }

                repeat(pageCount) { page ->
                    drawCircle(
                        color = inactiveColor,
                        radius = dotRadius,
                        center = Offset(xForPage(page), size.height / 2f),
                    )
                }

                val rawOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
                val fromPage = floor(rawOffset).toInt().coerceIn(0, pageCount - 1)
                val toPage = (fromPage + 1).coerceAtMost(pageCount - 1)
                val progress = (rawOffset - fromPage).coerceIn(0f, 1f)

                val fromCenter = xForPage(fromPage)
                val toCenter = xForPage(toPage)

                val leadFraction = (progress * 2f).coerceIn(0f, 1f)
                val trailFraction = ((progress - 0.5f) * 2f).coerceIn(0f, 1f)

                // Under RTL, "from" sits to the right of "to" in physical space, so the pill's
                // leading edge is the min-x side and trailing is max-x — the reverse of LTR.
                // minOf/maxOf keep the fraction math above unchanged and just resolve which
                // center owns which physical edge.
                val nearEdge = { center: Float -> center - dotRadius }
                val farEdge = { center: Float -> center + dotRadius }

                val fromLeft = minOf(nearEdge(fromCenter), nearEdge(toCenter))
                val fromRight = maxOf(farEdge(fromCenter), farEdge(toCenter))

                val leftEdge: Float
                val rightEdge: Float
                if (fromCenter <= toCenter) {
                    leftEdge = lerp(nearEdge(fromCenter), nearEdge(toCenter), trailFraction)
                    rightEdge = lerp(farEdge(fromCenter), farEdge(toCenter), leadFraction)
                } else {
                    leftEdge = lerp(nearEdge(fromCenter), nearEdge(toCenter), leadFraction)
                    rightEdge = lerp(farEdge(fromCenter), farEdge(toCenter), trailFraction)
                }

                drawRoundRect(
                    color = activeColor,
                    topLeft = Offset(leftEdge, 0f),
                    size = Size(rightEdge - leftEdge, size.height),
                    cornerRadius = CornerRadius(dotRadius),
                )
            }
        }
    }
}

private const val TrackAlpha = 0.30f

private val DotSize = 7.dp
private val DotGap = 7.dp
private val TrackPaddingHorizontal = 11.dp
private val TrackPaddingVertical = 7.dp
private val MaxWidth = 140.dp
