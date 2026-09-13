package com.tamin.taminhamrah.feature.stories.ui.viewer

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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
actual fun StoryVideoPlayer(
    url: String,
    isPaused: Boolean,
    onReady: (durationMs: Long) -> Unit,
    onEnded: () -> Unit,
    onFailed: () -> Unit,
    modifier: Modifier,
) {
    val context = LocalContext.current

    // The listener outlives a recomposition; reading the callbacks through this keeps it calling
    // the current ones without having to be torn down and rebuilt to pick them up.
    val currentOnReady by rememberUpdatedState(onReady)
    val currentOnEnded by rememberUpdatedState(onEnded)
    val currentOnFailed by rememberUpdatedState(onFailed)

    // Keyed on the url, so moving to another clip builds a new player rather than reusing one
    // still bound to the previous item.
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = !isPaused
        }
    }

    DisposableEffect(player) {
        // STATE_READY comes back after every rebuffer, not only the first time. Reporting a
        // length twice would restart the slide mid-play, so it is reported once.
        var durationReported = false

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        if (durationReported) return
                        durationReported = true
                        val duration = player.duration
                        currentOnReady(if (duration == C.TIME_UNSET) 0L else duration)
                    }

                    Player.STATE_ENDED -> currentOnEnded()
                }
            }

            override fun onPlayerError(error: PlaybackException) = currentOnFailed()
        }

        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            // Leaving the viewer has to give the decoder and the socket back, not just stop the
            // picture — this is the whole of "release the resource on exit".
            player.release()
        }
    }

    LaunchedEffect(player, isPaused) {
        if (isPaused) player.pause() else player.play()
    }

    AndroidView(
        modifier = modifier,
        factory = { viewContext ->
            PlayerView(viewContext).apply {
                this.player = player
                // The story owns its own chrome; the built-in controls would sit under the tap
                // columns and swallow them.
                useController = false
                // A story fills the screen, so the clip is cropped to it rather than letterboxed.
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        onRelease = { it.player = null },
    )
}
