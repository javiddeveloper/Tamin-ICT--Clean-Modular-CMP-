package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator
import org.koin.compose.koinInject

/**
 * Plays a clip fullscreen over the chat.
 *
 * Driven from Compose rather than the platform players so both platforms behave the
 * same and the close affordance matches the rest of the app. `usePlatformDefaultWidth =
 * false` is what lets the dialog actually fill the screen instead of being inset like a
 * regular alert.
 */
@Composable
fun FullscreenVideoDialog(
    url: String,
    onDismiss: () -> Unit,
    coordinator: MediaPlaybackCoordinator = koinInject()
) {
    val owner = remember(url) { MediaPlaybackCoordinator.videoOwner(url) }
    // Fullscreen plays with sound, so it takes the audio for as long as it is open.
    DisposableEffect(owner) {
        coordinator.claim(owner)
        onDispose { coordinator.release(owner) }
    }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            VideoPlayer(
                url = url,
                autoPlay = true,
                onPlayingChanged = { if (it) coordinator.claim(owner) },
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(12.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "بستن",
                    tint = Color.White
                )
            }
        }
    }
}
