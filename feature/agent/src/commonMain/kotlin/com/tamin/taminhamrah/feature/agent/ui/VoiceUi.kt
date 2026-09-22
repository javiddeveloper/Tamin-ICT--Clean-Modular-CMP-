package com.tamin.taminhamrah.feature.agent.ui

import androidx.compose.ui.graphics.Brush
import org.jetbrains.compose.resources.stringResource
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing
import taminx.core.core_ui.Res
import taminx.core.core_ui.agent_stop_recording
import taminx.core.core_ui.agent_delete_recording
import taminx.core.core_ui.agent_pause
import taminx.core.core_ui.agent_play
import taminx.core.core_ui.agent_send
import taminx.core.core_ui.agent_input_disclaimer
import taminx.core.core_ui.agent_recording_in_progress
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.Mic
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.util.lerp
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
import androidx.compose.ui.draw.clipToBounds
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
    Canvas(modifier = modifier.clipToBounds()) {
        if (amplitudes.isEmpty()) return@Canvas
        val gap = 3.dp.toPx()
        val minBarWidth = 2.dp.toPx()
        // Only as many bars as fit at the minimum width: with a fixed count the gaps alone
        // could exceed a narrow waveform (the 150dp chat bubble) and the bars spilled past
        // the canvas edge, under the play/pause button beside it.
        val fitBars = ((size.width + gap) / (minBarWidth + gap)).toInt().coerceAtLeast(1)
        val samples = downsample(amplitudes, minOf(maxBars, fitBars))
        val barCount = samples.size
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
 *
 * "Something is live" is signalled twice, so it can't be missed: the pill's hairline turns
 * the glass palette's red ([AgentGlass.danger]) and breathes, and a red mic — in the slot
 * the mic button occupies on the text bar — pulses with an expanding halo, like a call
 * recorder's REC light.
 */
@Composable
fun VoiceRecorderBar(
    state: VoiceRecordingState,
    hazeState: HazeState,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remaining = (VOICE_MAX_DURATION_MS - state.elapsedMs).coerceAtLeast(0)
    val dangerColor = AgentGlass.danger
    val waveColor = if (state.isNearLimit) dangerColor else AgentGlass.accent

    val pulse = rememberInfiniteTransition(label = "recording_pulse")
    // One shared clock for the border and the mic so the two beat together.
    val beat by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(RECORDING_PULSE_PERIOD_MS, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "recording_beat"
    )
    // Halo ring: a separate one-way loop, so it always expands outward and fades.
    val halo by pulse.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(RECORDING_HALO_PERIOD_MS, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "recording_halo"
    )
    val borderColor = dangerColor.copy(alpha = lerp(0.55f, 1f, beat))

    VoiceGlassBar(
        hazeState = hazeState,
        modifier = modifier,
        borderColor = borderColor,
        borderWidth = RecordingBorderWidth
    ) {
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
        RecordingPulseIndicator(beat = beat, halo = halo, color = dangerColor)
        // Stop sits where the send button sits on the text bar, at the same size.
        VoiceGlassButton(
            icon = Icons.Default.Stop,
            contentDescription = stringResource(Res.string.agent_stop_recording),
            size = ComposerButtonSize,
            background = dangerColor.copy(alpha = 0.18f),
            borderColor = AgentColors.ink.copy(alpha = 0.30f),
            iconTint = dangerColor,
            onClick = onStop
        )
    }
}

/**
 * The live mic in the recorder bar: a red-tinted glass tile whose mic glyph breathes with
 * [beat] (0..1, ping-pong) while a ring driven by [halo] (0..1, restart) grows out of it
 * and fades. Purely decorative — stopping is the stop button's job.
 */
@Composable
private fun RecordingPulseIndicator(beat: Float, halo: Float, color: Color) {
    Box(
        modifier = Modifier
            .size(ComposerButtonSize)
            .drawBehind {
                val ringScale = lerp(1f, RECORDING_HALO_MAX_SCALE, halo)
                val ringAlpha = (1f - halo) * 0.45f
                drawCircle(
                    color = color.copy(alpha = ringAlpha),
                    radius = size.minDimension / 2f * ringScale
                )
            }
            .clip(CircleShape)
            .background(color.copy(alpha = lerp(0.14f, 0.28f, beat)))
            .border(1.dp, color.copy(alpha = lerp(0.35f, 0.8f, beat)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = stringResource(Res.string.agent_recording_in_progress),
            tint = color,
            modifier = Modifier
                .size(18.dp)
                .graphicsLayer {
                    val s = lerp(1f, RECORDING_MIC_MAX_SCALE, beat)
                    scaleX = s
                    scaleY = s
                }
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
    val dangerColor = AgentGlass.danger
    VoiceGlassBar(hazeState = hazeState, modifier = modifier) {
        SeekableWaveform(
            amplitudes = state.amplitudes,
            positionMs = state.positionMs,
            durationMs = state.durationMs,
            onSeek = onSeek,
            activeColor = AgentGlass.accent,
            inactiveColor = AgentColors.ink.copy(alpha = 0.30f),
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
            size = ComposerButtonSize,
            background = AgentColors.ink.copy(alpha = 0.09f),
            borderColor = AgentColors.ink.copy(alpha = 0.14f),
            iconTint = dangerColor,
            onClick = onDelete
        )
        VoiceGlassButton(
            icon = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = stringResource(if (state.isPlaying) Res.string.agent_pause else Res.string.agent_play),
            size = ComposerButtonSize,
            background = AgentColors.ink.copy(alpha = 0.09f),
            borderColor = AgentColors.ink.copy(alpha = 0.14f),
            iconTint = ComposerMutedIconTint,
            onClick = onTogglePlay
        )
        // Same fixed, never-mirrored left arrow as the text bar's send button, in its
        // "has something to send" tint.
        VoiceGlassButton(
            icon = Icons.Default.ArrowBack,
            contentDescription = stringResource(Res.string.agent_send),
            size = ComposerButtonSize,
            background = AgentColors.composerSendActive,
            borderColor = AgentColors.ink.copy(alpha = 0.30f),
            iconTint = AgentColors.ink,
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
    // The user's clip is drawn inside the same gradient card as the user's text (the caller
    // provides that card), so it only lays out its row; the assistant's clip draws its own
    // glass card — the same one as the reply's tables and charts, since it sits on the dark
    // AgentBackground where the theme's surface would be a white block.
    val onGradient = AgentColors.onBubble
    val activeColor = if (isUser) onGradient else AgentGlass.accent
    val inactiveColor = if (isUser) onGradient.copy(alpha = 0.4f) else AgentColors.ink.copy(alpha = 0.25f)
    val timeColor = if (isUser) onGradient.copy(alpha = 0.7f) else AgentGlass.textSecondary
    val shape = RoundedCornerShape(com.tamin.taminhamrah.ui.theme.CornerRadius.xl)
    val container = if (isUser) {
        Modifier
    } else {
        Modifier
            .agentGlassCard(shape)
            .padding(horizontal = Spacing.smPlus, vertical = Spacing.sm)
    }

    Row(
        modifier = modifier.then(container),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val playDescription = stringResource(if (isPlaying) Res.string.agent_pause else Res.string.agent_play)
        val playIcon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow
        if (isUser) {
            // Same muted glass play/pause as the voice preview on the input bar.
            VoiceGlassButton(
                icon = playIcon,
                contentDescription = playDescription,
                size = ComposerButtonSize,
                background = AgentColors.ink.copy(alpha = 0.09f),
                borderColor = AgentColors.ink.copy(alpha = 0.14f),
                iconTint = ComposerMutedIconTint,
                onClick = onToggle
            )
        } else {
            // The reply's button gradient, like the video play button and action buttons.
            RoundIconButton(
                icon = playIcon,
                tint = AgentColors.ink,
                background = AgentColors.actionGradient,
                contentDescription = playDescription,
                onClick = onToggle
            )
        }
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
    activeColor: Color = AgentGlass.accent,
    inactiveColor: Color = AgentColors.ink.copy(alpha = 0.25f)
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

// ─── Composer metrics shared by the text bar and the voice bars ─────────────────

// One set of card and button metrics (Figma 90:120) for AgentInputBar and both voice bars,
// so swapping the text bar for a voice bar is seamless — same 60dp pill, 26dp radius,
// shadow, blur, padding and button size.
internal val ComposerCardShape = RoundedCornerShape(26.dp)
/** The pill's resting height; the text bar grows past it as the message wraps. */
internal val ComposerBarHeight = 60.dp
/** Every circle button on the composer — send, mic, stop, delete, play — is this size. */
internal val ComposerButtonSize = 38.dp
internal val ComposerButtonGap = 9.dp
internal val ComposerContentPadding = PaddingValues(start = 10.dp, end = 15.dp)
/** Outer margin of the pill; the disclaimer line sits in the gap below it. */
internal val ComposerOuterPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 6.dp)
internal val ComposerMutedIconTint = AgentColors.composerMutedIcon
private val VoiceBarWaveHeight = 28.dp

/** Recording border is a touch heavier than the resting hairline so the red reads at a glance. */
private val RecordingBorderWidth = 1.5.dp
private const val RECORDING_PULSE_PERIOD_MS = 700
private const val RECORDING_HALO_PERIOD_MS = 1400
private const val RECORDING_MIC_MAX_SCALE = 1.18f
/** Keeps the ring inside the pill: 19dp × 1.55 ≈ 29.5dp, under the 30dp to the pill edge. */
private const val RECORDING_HALO_MAX_SCALE = 1.55f

/** The composer's drop shadow — one definition for the text bar and the voice bars. */
internal fun Modifier.composerShadow(): Modifier = coloredShadow(
    color = AgentGlass.shadowColor.copy(alpha = 0.55f),
    borderRadius = 22.dp,
    blurRadius = 24.dp,
    offsetY = 10.dp
)

/**
 * The one-line legal note under every composer mode: the assistant's replies are guidance,
 * not an official source. Drawn on the fixed-dark backdrop in the glass palette's secondary
 * text, so it stays readable but quieter than the message itself.
 */
@Composable
internal fun ComposerDisclaimer(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(Res.string.agent_input_disclaimer),
        style = MaterialTheme.typography.labelSmall,
        color = AgentGlass.textSecondary.copy(alpha = 0.75f),
        textAlign = TextAlign.Center,
        maxLines = 2,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.lg, end = Spacing.lg, bottom = Spacing.sm)
    )
}

@Composable
private fun VoiceGlassBar(
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    borderColor: Color = AgentGlass.borderColor,
    borderWidth: Dp = AgentGlass.borderWidth,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(ComposerOuterPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(ComposerBarHeight)
                .composerShadow()
                .agentFrostedGlassCard(ComposerCardShape, hazeState, borderColor, borderWidth)
                .padding(ComposerContentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ComposerButtonGap),
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
    // A circle, like the video bubble's play button, so the two media controls match.
    Box(
        modifier = Modifier
            .size(IconSize.largePlus)
            .clip(CircleShape)
            .background(background)
            .border(AgentGlass.borderWidth, AgentGlass.borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(IconSize.banner))
    }
}
