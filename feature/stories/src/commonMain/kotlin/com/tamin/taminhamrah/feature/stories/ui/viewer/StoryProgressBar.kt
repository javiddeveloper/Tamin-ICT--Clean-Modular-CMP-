package com.tamin.taminhamrah.feature.stories.ui.viewer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.LayoutDirection
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryDimens
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryOnBackdrop
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryProgressTrack

/**
 * The row of segments across the top of the viewer: one per slide in the channel, filled behind
 * the reader, filling on the current one, empty ahead.
 *
 * ### Why it animates itself
 *
 * The fill is an [Animatable] driven from the segment description rather than a number pushed
 * through the state on every frame. [segmentToken] is what starts a new one, and the ViewModel
 * moves it exactly when the fill has to begin again — a new slide, a clip that finally reported
 * its length, media that failed. Between those moments this composable never recomposes, and the
 * whole bar is drawn in one [Canvas] that reads the animation in the draw phase, so a running
 * story costs redraws and neither recomposition nor relayout.
 *
 * Pausing works by cancellation: [isPlaying] going false ends the effect, which cancels
 * `animateTo` and leaves the fill exactly where it stood. Resuming re-enters on the same
 * [Animatable] and animates the rest of the way, so the segment finishes in the time it had left
 * rather than starting over. That is why a hold needs no token of its own.
 *
 * A buffering clip is the same mechanism: it is not playing, so its fresh [Animatable] sits at
 * zero until the player says how long the clip is.
 */
@Composable
internal fun StoryProgressBar(
    segmentCount: Int,
    currentIndex: Int,
    segmentToken: Int,
    durationMs: Long,
    elapsedMs: Long,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val startFraction = if (durationMs > 0L) {
        (elapsedMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    // A new Animatable per segment, built **during composition** at the fraction that segment
    // starts from.
    //
    // The alternative — one long-lived Animatable snapped back to zero from the effect below —
    // has a visible flaw: `segmentToken` and `currentIndex` change together in the composition
    // pass, but an effect body only runs after it. For that one frame the incoming segment was
    // drawn with the outgoing one's fill, which read as the new bar starting part-filled and
    // instantly correcting itself. Creating the value alongside the index it belongs to closes
    // that window rather than racing it.
    val fill = remember(segmentToken) { Animatable(startFraction) }

    LaunchedEffect(fill, isPlaying) {
        if (!isPlaying) return@LaunchedEffect
        // Measured from where the fill actually stands rather than from the clock's own tally, so
        // resuming carries on from the pixel the reader was looking at. The two agree to within
        // one tick; this is the one that is on screen.
        val remainingMs = ((1f - fill.value) * durationMs).toLong().coerceAtLeast(0L)
        fill.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = remainingMs.toInt(), easing = LinearEasing),
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(StoryDimens.progressHeight),
    ) {
        if (segmentCount <= 0) return@Canvas

        val gap = StoryDimens.progressGap.toPx()
        val segmentWidth = ((size.width - gap * (segmentCount - 1)) / segmentCount)
            .coerceAtLeast(0f)
        val radius = CornerRadius(size.height / 2f)
        val rtl = layoutDirection == LayoutDirection.Rtl

        repeat(segmentCount) { index ->
            // The first segment belongs to the first slide, which under a right-to-left page is
            // the rightmost one. Drawn in pixel coordinates, which never mirror on their own.
            val offset = index * (segmentWidth + gap)
            val left = if (rtl) size.width - offset - segmentWidth else offset

            drawRoundRect(
                color = StoryProgressTrack,
                topLeft = Offset(left, 0f),
                size = Size(segmentWidth, size.height),
                cornerRadius = radius,
            )

            val fraction = when {
                index < currentIndex -> 1f
                index == currentIndex -> fill.value
                else -> 0f
            }
            if (fraction <= 0f) return@repeat

            val fillWidth = segmentWidth * fraction
            // A segment fills from its own start edge, which under RTL is its right.
            val fillLeft = if (rtl) left + segmentWidth - fillWidth else left
            drawRoundRect(
                color = StoryOnBackdrop,
                topLeft = Offset(fillLeft, 0f),
                size = Size(fillWidth, size.height),
                cornerRadius = radius,
            )
        }
    }
}
