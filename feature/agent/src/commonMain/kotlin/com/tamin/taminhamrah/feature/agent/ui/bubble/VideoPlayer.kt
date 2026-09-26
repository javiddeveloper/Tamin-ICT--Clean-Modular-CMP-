package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Plays [url] with the platform's own video stack — ExoPlayer on Android, AVPlayer on
 * iOS.
 *
 * Fullscreen is handled by the caller ([FullscreenVideoDialog]) rather than the platform
 * player, so the experience is identical on both and the chat keeps its own chrome.
 *
 * @param autoPlay start as soon as the player is ready; used when opening fullscreen.
 * @param muted silences the clip. Inline bubbles start muted so a conversation never
 *   blurts audio while the user is scrolling; fullscreen plays with sound.
 * @param paused pauses the clip from the outside and resumes it when cleared — used by
 *   [MediaPlaybackCoordinator] when something else takes over the audio, and by the
 *   inline bubble's tap-to-pause.
 * @param showControls the platform's native transport controls. Fullscreen keeps them; the
 *   inline bubble turns them off and draws its own progress bar, because the native strip
 *   does not fit a 140dp glass frame. Without controls the clip also fills the frame
 *   (cropping, like its poster) instead of letterboxing.
 * @param onPlayingChanged reports transport changes so the caller can claim or release
 *   playback as the user starts and stops the clip.
 * @param onProgress the current position and total length in milliseconds, a few times a
 *   second while the player is alive; the duration is 0 until it is known.
 * @param onEnded the clip played through to its end. A player that has ended does not
 *   resume from [paused]; the caller offers a replay, which starts a fresh player.
 */
@Composable
expect fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    muted: Boolean = false,
    paused: Boolean = false,
    showControls: Boolean = true,
    onPlayingChanged: (Boolean) -> Unit = {},
    onProgress: (positionMs: Long, durationMs: Long) -> Unit = { _, _ -> },
    onEnded: () -> Unit = {}
)
