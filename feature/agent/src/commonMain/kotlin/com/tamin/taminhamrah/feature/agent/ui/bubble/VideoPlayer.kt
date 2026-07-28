package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator

/**
 * Plays [url] with the platform's own video stack — ExoPlayer on Android, AVPlayer on
 * iOS — including its native transport controls.
 *
 * Fullscreen is handled by the caller ([FullscreenVideoDialog]) rather than the platform
 * player, so the experience is identical on both and the chat keeps its own chrome.
 *
 * @param autoPlay start as soon as the player is ready; used when opening fullscreen.
 * @param muted silences the clip. Inline bubbles start muted so a conversation never
 *   blurts audio while the user is scrolling; fullscreen plays with sound.
 * @param paused pauses the clip from the outside — used by [MediaPlaybackCoordinator]
 *   when something else takes over the audio.
 * @param onPlayingChanged reports transport changes so the caller can claim or release
 *   playback as the user starts and stops the clip.
 */
@Composable
expect fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false,
    muted: Boolean = false,
    paused: Boolean = false,
    onPlayingChanged: (Boolean) -> Unit = {}
)
