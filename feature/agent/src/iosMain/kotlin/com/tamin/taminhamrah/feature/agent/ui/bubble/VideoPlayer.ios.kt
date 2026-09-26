@file:OptIn(ExperimentalForeignApi::class)

package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.addPeriodicTimeObserverForInterval
import platform.AVFoundation.currentItem
import platform.AVFoundation.duration
import platform.AVFoundation.muted
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.removeTimeObserver
import platform.AVFoundation.setMuted
import platform.AVKit.AVPlayerViewController
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSURL
import platform.UIKit.UIView

@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean,
    muted: Boolean,
    paused: Boolean,
    showControls: Boolean,
    onPlayingChanged: (Boolean) -> Unit,
    onProgress: (positionMs: Long, durationMs: Long) -> Unit,
    onEnded: () -> Unit
) {
    val currentOnProgress by rememberUpdatedState(onProgress)
    val currentOnEnded by rememberUpdatedState(onEnded)

    // Keyed on the url so switching clips rebuilds the controller rather than leaving it
    // bound to the previous item.
    val controller = remember(url) {
        AVPlayerViewController().apply {
            player = AVPlayer(uRL = NSURL.URLWithString(url) ?: NSURL())
            showsPlaybackControls = showControls
            // Without controls the clip is a tile in a glass frame: fill it, as the poster
            // does, rather than letterbox it with black bands.
            if (!showControls) videoGravity = AVLayerVideoGravityResizeAspectFill
        }
    }

    DisposableEffect(controller, autoPlay, muted) {
        controller.player?.muted = muted
        if (autoPlay) {
            controller.player?.play()
            onPlayingChanged(true)
        }
        onDispose {
            controller.player?.pause()
            onPlayingChanged(false)
        }
    }

    // Paused from the outside (something else claimed the audio, or the bubble was tapped);
    // cleared again means resume.
    DisposableEffect(controller, paused) {
        if (paused) {
            controller.player?.pause()
            onPlayingChanged(false)
        } else if (autoPlay) {
            controller.player?.play()
            onPlayingChanged(true)
        }
        onDispose { }
    }

    // The caller's own progress bar; the native time bar is off for the inline bubble.
    DisposableEffect(controller) {
        val player = controller.player ?: return@DisposableEffect onDispose { }
        var endedReported = false
        val observer = player.addPeriodicTimeObserverForInterval(
            interval = CMTimeMakeWithSeconds(PROGRESS_INTERVAL_SECONDS, PROGRESS_TIMESCALE),
            queue = null,
        ) { time ->
            val position = CMTimeGetSeconds(time)
            val duration = player.currentItem?.duration?.let { CMTimeGetSeconds(it) } ?: 0.0
            val positionMs = position.takeIf { !it.isNaN() && it > 0 }?.times(1000)?.toLong() ?: 0L
            val durationMs = duration.takeIf { !it.isNaN() && it > 0 }?.times(1000)?.toLong() ?: 0L
            currentOnProgress(positionMs, durationMs)
            // AVPlayer stops on the last frame; reaching the end (within a tick) is the end.
            if (durationMs > 0 && positionMs >= durationMs - ENDED_TOLERANCE_MS && !endedReported) {
                endedReported = true
                currentOnEnded()
            }
        }
        onDispose { player.removeTimeObserver(observer) }
    }

    UIKitView(
        modifier = modifier,
        factory = { controller.view as UIView },
        update = { }
    )
}

private const val PROGRESS_INTERVAL_SECONDS = 0.25
private const val PROGRESS_TIMESCALE = 600
private const val ENDED_TOLERANCE_MS = 300L
