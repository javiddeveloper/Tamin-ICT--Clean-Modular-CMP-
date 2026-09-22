package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.agent.ui.AgentGlass
import com.tamin.taminhamrah.feature.agent.ui.agentFrostedGlassCard
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.Spacing
import dev.chrisbanes.haze.HazeState

/**
 * A compact one-line player pinned under the toolbar while a voice message plays, the way
 * messaging apps keep an audio message reachable.
 *
 * Without it the only transport lives inside the bubble, so scrolling away from a long
 * message leaves it playing with no way to pause or find it again. Tapping the bar
 * scrolls back to the message it belongs to.
 *
 * Same glass card family as the top/input bars ([agentFrostedGlassCard]), so it reads as a
 * strip of the toolbar rather than a stray Material card floating over the chat.
 * Layout, RTL start → end: play/pause · «پیام صوتی» · elapsed / total · close.
 */
@Composable
fun PinnedVoicePlayer(
    isPlaying: Boolean,
    positionMs: Int,
    durationMs: Int,
    hazeState: HazeState,
    onTogglePlay: () -> Unit,
    onStop: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetProgress = if (durationMs > 0) {
        (positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    } else 0f
    // Position ticks arrive in coarse steps; easing between them keeps the hairline from stuttering.
    val progress by animateFloatAsState(targetProgress, tween(PROGRESS_TWEEN_MS), label = "pinned_voice_progress")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = AgentGlass.shadowColor.copy(alpha = 0.45f),
                borderRadius = PinnedPlayerRadius,
                blurRadius = 18.dp,
                offsetY = 6.dp
            )
            .agentFrostedGlassCard(PinnedPlayerShape, hazeState)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(PinnedPlayerHeight)
                .padding(start = Spacing.xs, end = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bare icons, no tile behind them — the strip is meant to be quieter than the bars.
            PinnedIconButton(
                icon = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isPlaying) "توقف" else "پخش",
                tint = AgentGlass.iconTint,
                onClick = onTogglePlay
            )

            Text(
                text = "پیام صوتی",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = AgentGlass.textPrimary,
                maxLines = 1
            )

            Spacer(Modifier.width(Spacing.sm))

            NumericText(
                text = "${formatClock(positionMs)} / ${formatClock(durationMs)}",
                style = MaterialTheme.typography.labelSmall,
                color = AgentGlass.textSecondary,
                modifier = Modifier.weight(1f)
            )

            PinnedIconButton(
                icon = Icons.Rounded.Close,
                contentDescription = "بستن",
                tint = AgentGlass.textSecondary,
                onClick = onStop
            )
        }

        // Progress hairline along the bottom edge, like a media notification. Playback
        // progress always grows left → right (the way every media timeline reads), so it is
        // anchored with an absolute alignment rather than Start, which the app's global RTL
        // would flip to the right edge.
        Box(
            modifier = Modifier
                .align(AbsoluteAlignment.BottomLeft)
                .fillMaxWidth()
                .height(PinnedPlayerProgressHeight)
                .background(AgentGlass.tileFillSubtle)
        ) {
            Box(
                modifier = Modifier
                    .align(AbsoluteAlignment.CenterLeft)
                    .fillMaxWidth(progress)
                    .height(PinnedPlayerProgressHeight)
                    .background(AgentGlass.accent)
            )
        }
    }
}

@Composable
private fun PinnedIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    tint: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(PinnedPlayerButtonSize)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

private val PinnedPlayerRadius = 16.dp
private val PinnedPlayerShape = RoundedCornerShape(PinnedPlayerRadius)
private val PinnedPlayerHeight = 40.dp
private val PinnedPlayerButtonSize = 36.dp
private val PinnedPlayerProgressHeight = 2.dp
private const val PROGRESS_TWEEN_MS = 250

private fun formatClock(ms: Int): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
}
