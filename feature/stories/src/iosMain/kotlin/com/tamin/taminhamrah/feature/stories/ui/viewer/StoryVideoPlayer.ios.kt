@file:OptIn(ExperimentalForeignApi::class)

package com.tamin.taminhamrah.feature.stories.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.currentItem
import platform.AVFoundation.duration
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.status
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIView

/** How often the clip is asked whether it knows its own length yet. */
private const val DURATION_POLL_MS = 100L

/**
 * AVPlayer behind an [AVPlayerViewController] with its controls switched off.
 *
 * The controller rather than a bare `AVPlayerLayer` because it handles the layer's geometry across
 * rotations and safe areas, which a hand-rolled `UIView` would have to reimplement.
 *
 * Readiness is polled rather than observed: AVFoundation reports it through KVO, which has no
 * ergonomic binding from Kotlin, and a tenth of a second of latency on a story slide is invisible.
 * The poll is bounded by the composition — leaving the slide cancels it.
 */
@Composable
actual fun StoryVideoPlayer(
    url: String,
    isPaused: Boolean,
    onReady: (durationMs: Long) -> Unit,
    onEnded: () -> Unit,
    onFailed: () -> Unit,
    modifier: Modifier,
) {
    val currentOnEnded by rememberUpdatedState(onEnded)

    // Keyed on the url, so moving to another clip builds a new player rather than reusing one
    // still bound to the previous item.
    val player = remember(url) {
        val nsUrl = NSURL.URLWithString(url) ?: NSURL.fileURLWithPath(url)
        AVPlayer(uRL = nsUrl)
    }

    val controller = remember(player) {
        AVPlayerViewController().apply {
            this.player = player
            showsPlaybackControls = false
            videoGravity = AVLayerVideoGravityResizeAspectFill
        }
    }

    LaunchedEffect(player) {
        // Reported once, as on Android: a rebuffer must not restart the slide.
        while (true) {
            val item = player.currentItem
            if (item != null) {
                when (item.status) {
                    AVPlayerItemStatusFailed -> {
                        onFailed()
                        return@LaunchedEffect
                    }

                    AVPlayerItemStatusReadyToPlay -> {
                        val seconds = CMTimeGetSeconds(item.duration)
                        // A live or still-unknown duration comes back as NaN; the viewer takes a
                        // non-positive length as "no length" and runs the ordinary slide time.
                        val durationMs = if (seconds.isNaN() || seconds <= 0.0) {
                            0L
                        } else {
                            (seconds * 1000.0).toLong()
                        }
                        onReady(durationMs)
                        return@LaunchedEffect
                    }

                    else -> Unit
                }
            }
            delay(DURATION_POLL_MS)
        }
    }

    DisposableEffect(player) {
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue,
            usingBlock = { currentOnEnded() },
        )
        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(observer)
            // Leaving the viewer has to give the decoder back, not just stop the picture.
            player.pause()
            player.replaceCurrentItemWithPlayerItem(null)
        }
    }

    LaunchedEffect(player, isPaused) {
        if (isPaused) player.pause() else player.play()
    }

    UIKitView(
        modifier = modifier,
        factory = { controller.view as UIView },
        update = { },
    )
}
