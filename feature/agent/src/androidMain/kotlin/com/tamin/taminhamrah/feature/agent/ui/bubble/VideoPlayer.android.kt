package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
actual fun VideoPlayer(
    url: String,
    modifier: Modifier,
    autoPlay: Boolean,
    muted: Boolean,
    paused: Boolean,
    onPlayingChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // Keyed on the url so switching clips rebuilds the player rather than reusing one
    // that is still bound to the previous media item.
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = autoPlay
        }
    }

    LaunchedEffect(player, muted) {
        player.volume = if (muted) 0f else 1f
    }

    // Something else claimed the audio; stop rather than talk over it.
    LaunchedEffect(player, paused) {
        if (paused && player.isPlaying) player.pause()
    }

    DisposableEffect(player, onPlayingChanged) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                onPlayingChanged(isPlaying)
            }
        }
        player.addListener(listener)
        onDispose { player.removeListener(listener) }
    }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
                // Fullscreen is driven from Compose, so the built-in button would be a
                // second, competing control.
                setShowNextButton(false)
                setShowPreviousButton(false)
                // The bubble sits in a scrolling list, whose parent would otherwise claim
                // the horizontal drag and leave the seek bar unusable.
                setOnTouchListener { view, event ->
                    view.parent?.requestDisallowInterceptTouchEvent(true)
                    false
                }
            }
        },
        onRelease = { it.player = null }
    )
}
