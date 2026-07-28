@file:OptIn(ExperimentalForeignApi::class)

package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.play
import platform.AVFoundation.pause
import platform.AVFoundation.setMuted
import platform.AVKit.AVPlayerViewController
import platform.Foundation.NSURL
import platform.UIKit.UIView

@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean,
    muted: Boolean
) {
    // Keyed on the url so switching clips rebuilds the controller rather than leaving it
    // bound to the previous item.
    val controller = remember(url) {
        AVPlayerViewController().apply {
            player = AVPlayer(uRL = NSURL.URLWithString(url) ?: NSURL())
            showsPlaybackControls = true
        }
    }

    DisposableEffect(controller, autoPlay, muted) {
        controller.player?.muted = muted
        if (autoPlay) controller.player?.play()
        onDispose { controller.player?.pause() }
    }

    UIKitView(
        modifier = modifier,
        factory = { controller.view as UIView },
        update = { }
    )
}
