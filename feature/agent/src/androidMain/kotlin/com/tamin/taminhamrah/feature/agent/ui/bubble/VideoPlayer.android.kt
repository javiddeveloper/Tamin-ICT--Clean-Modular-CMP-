package com.tamin.taminhamrah.feature.agent.ui.bubble

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("ClickableViewAccessibility")
@OptIn(UnstableApi::class)
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
    val context = LocalContext.current
    val currentOnProgress by rememberUpdatedState(onProgress)
    val currentOnEnded by rememberUpdatedState(onEnded)

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

    // Paused from the outside (something else claimed the audio, or the bubble was tapped);
    // cleared again means resume. A clip that has ended stays ended.
    LaunchedEffect(player, paused) {
        if (paused) {
            if (player.isPlaying) player.pause()
        } else if (autoPlay && !player.isPlaying && player.playbackState != Player.STATE_ENDED) {
            player.play()
        }
    }

    // The caller's own progress bar; the native time bar is off for the inline bubble.
    LaunchedEffect(player) {
        while (true) {
            val duration = player.duration.takeIf { it > 0 } ?: 0L
            currentOnProgress(player.currentPosition.coerceAtLeast(0L), duration)
            delay(PROGRESS_INTERVAL_MS.milliseconds)
        }
    }

    DisposableEffect(player, onPlayingChanged) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                onPlayingChanged(isPlaying)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) currentOnEnded()
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
                useController = showControls
                // Without controls the clip is a tile in a glass frame: fill it, as the
                // poster does, rather than letterbox it with black bands.
                if (!showControls) resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                // Fullscreen is driven from Compose, so the built-in button would be a
                // second, competing control.
                setShowNextButton(false)
                setShowPreviousButton(false)
                // The bubble sits in a scrolling list, whose parent would otherwise claim
                // the horizontal drag and leave the seek bar unusable. Only with the native
                // controls: without them there is no seek bar, and the list must keep scrolling
                // when a drag starts on the clip.
                if (showControls) {
                    setOnTouchListener { view, _ ->
                        view.parent?.requestDisallowInterceptTouchEvent(true)
                        false
                    }
                }
            }
        },
        onRelease = { it.player = null }
    )
}

private const val PROGRESS_INTERVAL_MS = 250L
