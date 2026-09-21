package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import org.jetbrains.compose.resources.stringResource
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_stop_recording
import taminx.core.core_ui.agent_delete_recording
import taminx.core.core_ui.agent_pause
import taminx.core.core_ui.agent_play
import taminx.core.core_ui.agent_send
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.agent.ui.contract.VoicePreviewState
import com.tamin.taminhamrah.feature.agent.ui.contract.VoiceRecordingState
import com.tamin.taminhamrah.feature.agent.ui.contract.VOICE_MAX_DURATION_MS
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import dev.chrisbanes.haze.HazeState

// ─── Waveform ─────────────────────────────────────────────────────────────────

/**
 * Bar-style audio waveform. [amplitudes] are raw mic samples (0..~32767); [progress]
 * (0..1) colours the played portion with [activeColor], the rest with [inactiveColor].
 */
@Composable
fun VoiceWaveform(
    amplitudes: List<Int>,
    progress: Float,
    activeColor: Color,
    inactiveColor: Color,
    modifier: Modifier = Modifier,
    maxBars: Int = 48
) {
    Canvas(modifier = modifier) {
        val samples = downsample(amplitudes, maxBars)
        val barCount = samples.size
        if (barCount == 0) return@Canvas
        val gap = 3.dp.toPx()
        val barWidth = ((size.width - gap * (barCount - 1)) / barCount).coerceAtLeast(1f)
        val maxAmp = (samples.maxOrNull() ?: 1).coerceAtLeast(1)
        val minBar = 3.dp.toPx()
        val progressX = size.width * progress.coerceIn(0f, 1f)
        samples.forEachIndexed { i, amp ->
            val norm = amp.toFloat() / maxAmp
            val barH = (norm * size.height).coerceIn(minBar, size.height)
            val x = i * (barWidth + gap)
            val color = if (x <= progressX) activeColor else inactiveColor
            drawRoundRect(
                color = color,
                topLeft = Offset(x, (size.height - barH) / 2f),
                size = Size(barWidth, barH),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

/** Buckets [values] down to at most [target] averaged samples for a stable bar count. */
private fun downsample(values: List<Int>, target: Int): List<Int> {
    if (values.isEmpty()) return emptyList()
    if (values.size <= target) return values
    val bucket = values.size.toFloat() / target
    return (0 until target).map { i ->
        val from = (i * bucket).toInt()
        val to = ((i + 1) * bucket).toInt().coerceAtMost(values.size)
        if (to <= from) values[from] else values.subList(from, to).average().toInt()
    }
}

private fun formatMillis(ms: Long): String {
    val totalSec = (ms / 1000).toInt()
    val m = totalSec / 60
    val s = totalSec % 60
    val mm = if (m < 10) "0$m" else "$m"
    val ss = if (s < 10) "0$s" else "$s"
    return "$mm:$ss"
}

// ─── Recorder bar (while recording) ─────────────────────────────────────────────

/**
 * Replaces `AgentInputBar` while recording: the same 60dp frosted glass pill (Figma 90:120),
 * with a live waveform, a countdown and a stop button in the send button's slot.
 */
@Composable
fun VoiceRecorderBar(
    state: VoiceRecordingState,
    hazeState: HazeState,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remaining = (VOICE_MAX_DURATION_MS - state.elapsedMs).coerceAtLeast(0)
    val dangerColor = MaterialTheme.colorScheme.error
    val waveColor = if (state.isNearLimit) dangerColor else AgentGlass.accent

    VoiceGlassBar(hazeState = hazeState, modifier = modifier) {
        VoiceWaveform(
            amplitudes = state.amplitudes,
            progress = 1f,
            activeColor = waveColor,
            inactiveColor = waveColor.copy(alpha = 0.35f),
            modifier = Modifier.weight(1f).height(VoiceBarWaveHeight)
        )
        Text(
            text = formatMillis(remaining),
            style = MaterialTheme.typography.labelLarge,
            color = if (state.isNearLimit) dangerColor else AgentGlass.textSecondary
        )
        // Stop sits where the send button sits on the text bar, at the same 42dp size.
        VoiceGlassButton(
            icon = Icons.Default.Stop,
            contentDescription = stringResource(Res.string.agent_stop_recording),
            size = VoiceBarPrimaryButtonSize,
            background = dangerColor.copy(alpha = 0.18f),
            borderColor = Color.White.copy(alpha = 0.30f),
            iconTint = dangerColor,
            onClick = onStop
        )
    }
}

// ─── Preview bar (before send) ──────────────────────────────────────────────────

/**
 * Replaces `AgentInputBar` after recording: a seekable waveform with the clip length, delete
 * and play/pause as the bar's muted glass buttons, and the tinted send button.
 */
@Composable
fun VoicePreviewBar(
    state: VoicePreviewState,
    hazeState: HazeState,
    onDelete: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeek: (Int) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val dangerColor = MaterialTheme.colorScheme.error
    VoiceGlassBar(hazeState = hazeState, modifier = modifier) {
        SeekableWaveform(
            amplitudes = state.amplitudes,
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            onSeek = onSeek,
            activeColor = AgentGlass.accent,
            inactiveColor = Color.White.copy(alpha = 0.30f),
            modifier = Modifier.weight(1f).height(VoiceBarWaveHeight)
        )
        Text(
            text = formatMillis(state.durationMs.toLong()),
            style = MaterialTheme.typography.labelSmall,
            color = AgentGlass.textSecondary
        )
        VoiceGlassButton(
            icon = Icons.Default.Delete,
            contentDescription = stringResource(Res.string.agent_delete_recording),
            size = VoiceBarSecondaryButtonSize,
            background = Color.White.copy(alpha = 0.09f),
            borderColor = Color.White.copy(alpha = 0.14f),
            iconTint = dangerColor,
            onClick = onDelete
        )
        VoiceGlassButton(
            icon = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = stringResource(if (state.isPlaying) Res.string.agent_pause else Res.string.agent_play),
            size = VoiceBarSecondaryButtonSize,
            background = Color.White.copy(alpha = 0.09f),
            borderColor = Color.White.copy(alpha = 0.14f),
            iconTint = VoiceBarMutedIconTint,
            onClick = onTogglePlay
        )
        // Same fixed, never-mirrored left arrow as the text bar's send button, in its
        // "has something to send" tint.
        VoiceGlassButton(
            icon = Icons.Default.ArrowBack,
            contentDescription = stringResource(Res.string.agent_send),
            size = VoiceBarPrimaryButtonSize,
            background = taminColors.aiAssistantTint,
            borderColor = Color.White.copy(alpha = 0.30f),
            iconTint = Color.White,
            onClick = onSend
        )
    }
}

// ─── Voice chat bubble (in list) ────────────────────────────────────────────────

@Composable
fun VoiceChatBubble(
    filePath: String,
    durationMs: Long,
    isPlaying: Boolean,
    positionMs: Int,
    amplitudes: List<Int>,
    isUser: Boolean,
    onToggle: () -> Unit,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    // The user's clip sits on the brand gradient like the user's text bubble; the assistant's on the
    // app's surface card.
    val onGradient = taminColors.onGradient
    val background: Brush = if (isUser) taminTopAppBarGradient() else SolidColor(taminColors.bgSurface)
    val buttonBg: Brush = if (isUser) SolidColor(onGradient) else taminTopAppBarGradient()
    val buttonTint = if (isUser) taminColors.blueText else onGradient
    val activeColor = if (isUser) onGradient else taminColors.blueText
    val inactiveColor = if (isUser) onGradient.copy(alpha = 0.4f) else taminColors.chevron
    val timeColor = if (isUser) taminColors.textHeaderSubtitle else taminColors.textMuted
    val shape = RoundedCornerShape(com.tamin.taminhamrah.ui.theme.CornerRadius.xl)

    Row(
        modifier = modifier
            .clip(shape)
            .background(background)
            .then(if (isUser) Modifier else Modifier.border(Thickness.border, taminColors.border, shape))
            .padding(horizontal = Spacing.smPlus, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            tint = buttonTint,
            background = buttonBg,
            contentDescription = stringResource(if (isPlaying) Res.string.agent_pause else Res.string.agent_play),
            onClick = onToggle
        )
        Spacer(Modifier.width(10.dp))
        val dur = durationMs.toInt().coerceAtLeast(1)
        SeekableWaveform(
            amplitudes = amplitudes.ifEmpty { defaultWaveform() },
            positionMs = if (isPlaying || positionMs > 0) positionMs else 0,
            durationMs = dur,
            onSeek = onSeek,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            modifier = Modifier.width(150.dp).height(26.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = formatMillis(durationMs),
            style = MaterialTheme.typography.labelSmall,
            color = timeColor
        )
    }
}

/** A pleasant static bar pattern used when a voice clip has no recorded amplitudes. */
private fun defaultWaveform(): List<Int> =
    (0 until 40).map { i ->
        val base = 6000 + (kotlin.math.sin(i * 0.6) * 4000).toInt()
        base + ((i * 37) % 5000)
    }

// ─── Shared pieces ──────────────────────────────────────────────────────────────

@Composable
private fun SeekableWaveform(
    amplitudes: List<Int>,
    positionMs: Int,
    durationMs: Int,
    onSeek: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalTaminColors.current.blueText,
    inactiveColor: Color = LocalTaminColors.current.chevron
) {
    val progress = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f
    Box(
        modifier = modifier.pointerInput(durationMs) {
            detectHorizontalDragGestures { change, _ ->
                val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                onSeek((fraction * durationMs).toInt())
            }
        }
    ) {
        VoiceWaveform(
            amplitudes = amplitudes,
            progress = progress,
            activeColor = activeColor,
            inactiveColor = inactiveColor,
            modifier = Modifier.fillMaxWidth().height(26.dp)
        )
    }
}

// Mirrors AgentInputBar's card and button metrics (Figma 90:120) so swapping the text bar
// for the voice bars is seamless — same 60dp pill, 26dp radius, shadow, blur and padding.
private val VoiceBarCardShape = RoundedCornerShape(26.dp)
private val VoiceBarHeight = 60.dp
private val VoiceBarPrimaryButtonSize = 42.dp
private val VoiceBarSecondaryButtonSize = 38.dp
private val VoiceBarWaveHeight = 28.dp
private val VoiceBarMutedIconTint = Color(0xFFBFD0F0)

@Composable
private fun VoiceGlassBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 16.dp, top = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(VoiceBarHeight)
                .coloredShadow(
                    color = AgentGlass.shadowColor.copy(alpha = 0.55f),
                    borderRadius = 22.dp,
                    blurRadius = 24.dp,
                    offsetY = 10.dp
                )
                .agentFrostedGlassCard(VoiceBarCardShape, hazeState)
                .padding(start = 10.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            content = content
        )
    }
}

/** One glass circle button on the voice bars — same shape family as the text bar's buttons. */
@Composable
private fun VoiceGlassButton(
    icon: ImageVector,
    contentDescription: String?,
    size: Dp,
    background: Color,
    borderColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(background)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun RoundIconButton(
    icon: ImageVector,
    tint: Color,
    background: Brush,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(IconSize.largePlus)
            .clip(RoundedCornerShape(com.tamin.taminhamrah.ui.theme.CornerRadius.md))
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(IconSize.banner))
    }
}
