package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.agent.ui.contract.VoicePreviewState
import com.tamin.taminhamrah.feature.agent.ui.contract.VoiceRecordingState
import com.tamin.taminhamrah.feature.agent.ui.contract.VOICE_MAX_DURATION_MS
import com.tamin.taminhamrah.ui.theme.LocalTaminColors

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

@Composable
fun VoiceRecorderBar(
    state: VoiceRecordingState,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val remaining = (VOICE_MAX_DURATION_MS - state.elapsedMs).coerceAtLeast(0)
    val waveColor = if (state.isNearLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    PillContainer(modifier) {
        // Countdown
        Text(
            text = formatMillis(remaining),
            style = MaterialTheme.typography.labelLarge,
            color = if (state.isNearLimit) MaterialTheme.colorScheme.error else taminColors.textSecondary
        )
        Spacer(Modifier.width(12.dp))
        VoiceWaveform(
            amplitudes = state.amplitudes,
            progress = 1f,
            activeColor = waveColor,
            inactiveColor = waveColor.copy(alpha = 0.35f),
            modifier = Modifier.weight(1f).height(28.dp)
        )
        Spacer(Modifier.width(12.dp))
        RoundIconButton(
            icon = Icons.Default.Stop,
            tint = Color.White,
            background = MaterialTheme.colorScheme.error,
            contentDescription = "توقف ضبط",
            onClick = onStop
        )
    }
}

// ─── Preview bar (before send) ──────────────────────────────────────────────────

@Composable
fun VoicePreviewBar(
    state: VoicePreviewState,
    onDelete: () -> Unit,
    onTogglePlay: () -> Unit,
    onSeek: (Int) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    PillContainer(modifier) {
        RoundIconButton(
            icon = Icons.Default.Delete,
            tint = MaterialTheme.colorScheme.error,
            background = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
            contentDescription = "حذف",
            onClick = onDelete
        )
        Spacer(Modifier.width(8.dp))
        RoundIconButton(
            icon = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            tint = MaterialTheme.colorScheme.primary,
            background = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            contentDescription = if (state.isPlaying) "توقف" else "پخش",
            onClick = onTogglePlay
        )
        Spacer(Modifier.width(8.dp))
        SeekableWaveform(
            amplitudes = state.amplitudes,
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            onSeek = onSeek,
            modifier = Modifier.weight(1f).height(28.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = formatMillis(state.durationMs.toLong()),
            style = MaterialTheme.typography.labelSmall,
            color = taminColors.textMuted
        )
        Spacer(Modifier.width(8.dp))
        RoundIconButton(
            icon = Icons.Default.ArrowUpward,
            tint = Color.White,
            background = MaterialTheme.colorScheme.primary,
            contentDescription = "ارسال",
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
    // User bubble is a filled primary pill (white content); agent bubble is neutral.
    val bg = if (isUser) MaterialTheme.colorScheme.primary else taminColors.bgSurface
    val buttonBg = if (isUser) Color.White else MaterialTheme.colorScheme.primary
    val buttonTint = if (isUser) MaterialTheme.colorScheme.primary else Color.White
    val activeColor = if (isUser) Color.White else MaterialTheme.colorScheme.primary
    val inactiveColor = if (isUser) Color.White.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
    val timeColor = if (isUser) Color.White.copy(alpha = 0.85f) else taminColors.textMuted

    Row(
        modifier = modifier
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
            .background(bg)
            .then(
                if (isUser) Modifier
                else Modifier.border(
                    BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                    androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
                )
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            tint = buttonTint,
            background = buttonBg,
            contentDescription = if (isPlaying) "توقف" else "پخش",
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
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.20f)
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

@Composable
private fun PillContainer(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(32.dp))
            .background(taminColors.bgSurface)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        content = content
    )
}

@Composable
private fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    background: Color,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(20.dp))
    }
}
