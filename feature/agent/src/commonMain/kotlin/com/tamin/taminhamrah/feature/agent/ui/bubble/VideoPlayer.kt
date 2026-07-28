package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Plays [url] with the platform's own video stack — ExoPlayer on Android, AVPlayer on
 * iOS — including its native transport controls.
 *
 * Fullscreen is handled by the caller ([FullscreenVideoDialog]) rather than the platform
 * player, so the experience is identical on both and the chat keeps its own chrome.
 *
 * @param autoPlay start as soon as the player is ready; used when opening fullscreen.
 */
@Composable
expect fun VideoPlayer(
    url: String,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = false
)
