package com.tamin.taminhamrah.feature.stories.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Plays a story's clip on the platform's own stack — ExoPlayer on Android, AVPlayer on iOS —
 * with no transport controls of its own: a story is driven by the tap columns over it, and a
 * scrub bar under the reader's thumb would fight them.
 *
 * ### Not the chat player
 *
 * `feature/agent` has a `VideoPlayer` of its own, and this is deliberately not it. Feature modules
 * cannot depend on each other, and the two contracts are different anyway: the chat's player shows
 * the platform's controls and reports only whether it is playing, while a story needs the opposite
 * — no controls, and the three facts below, without which the progress bar cannot be driven by the
 * clip at all.
 *
 * ### The contract
 *
 * @param isPaused holds the clip where it stands. The viewer sets it from the same finger-down
 *   that pauses the segment clock, so picture and progress stop together.
 * @param onReady the clip's length, reported **once**, as soon as the player knows it. Until then
 *   the viewer treats the slide as buffering and the bar does not move. A player that reports a
 *   length of zero or less is treated as having no length, and the slide falls back to the
 *   ordinary duration.
 * @param onEnded the clip played out. The viewer moves on immediately rather than waiting for its
 *   own clock, so the two cannot disagree by a frame at the end of a slide.
 * @param onFailed the clip could not be played. The slide keeps its gradient, says so, and still
 *   moves on.
 *
 * Every implementation releases the player when it leaves composition. Only one slide is composed
 * at a time, so only one player exists at a time.
 */
@Composable
expect fun StoryVideoPlayer(
    url: String,
    isPaused: Boolean,
    onReady: (durationMs: Long) -> Unit,
    onEnded: () -> Unit,
    onFailed: () -> Unit,
    modifier: Modifier = Modifier,
)
