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
 * through the state on every frame. [segmentToken] is the only thing that restarts it, and the
 * ViewModel moves that exactly when the fill has to start over — a new slide, a clip that finally
 * reported its length, media that failed, a paused slide resuming. Between those moments this
 * composable never recomposes, and the whole bar is drawn in one [Canvas] that reads the animation
 * in the draw phase, so a running story costs redraws and neither recomposition nor relayout.
 *
 * Pausing works by cancellation: [isPlaying] going false ends the effect, which cancels
 * `animateTo` and leaves the fill exactly where it stood. Resuming re-enters with [elapsedMs] set
 * to what the clock had counted, so the segment finishes in the time it had left rather than
 * starting over.
 *
 * A buffering clip is the same mechanism: it is not playing, so the bar snaps to zero and holds
 * there until the player says how long the clip is.
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
    val progress = remember { Animatable(0f) }

    LaunchedEffect(segmentToken, isPlaying) {
        val from = if (durationMs > 0L) {
            (elapsedMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
        // Snapped before the play check on purpose: a slide that comes up already paused — a clip
        // still buffering — has to show an empty bar rather than the previous slide's full one.
        progress.snapTo(from)
        if (!isPlaying) return@LaunchedEffect

        val remaining = (durationMs - elapsedMs).coerceAtLeast(0L)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = remaining.toInt(), easing = LinearEasing),
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
                index == currentIndex -> progress.value
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
