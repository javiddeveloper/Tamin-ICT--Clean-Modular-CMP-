package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator
import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.koin.compose.koinInject

/**
 * Renderers for the media and data-view bubble families.
 *
 * They live apart from `AgentScreen` so adding an answer type stays a local change:
 * declare the type in `ChatBubbleContent`, drop its composable here, and wire one
 * branch in the renderer.
 */

/** Titled block: a header line above the body — the shape most service answers take. */
@Composable
fun RichTextBubble(
    content: ChatBubbleContent.RichText,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = content.header,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = contentColor
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = content.body,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor
        )
        content.footnote?.takeIf { it.isNotBlank() }?.let { note ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = note,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted
            )
        }
    }
}

/** An image attachment with an optional caption underneath. */
@Composable
fun ImageBubble(
    content: ChatBubbleContent.Image,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    Column(modifier = modifier.width(240.dp)) {
        SubcomposeAsyncImage(
            model = content.source,
            contentDescription = content.caption,
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                )
            },
            error = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "تصویر بارگذاری نشد",
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textMuted
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        content.caption?.takeIf { it.isNotBlank() }?.let { caption ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted
            )
        }
    }
}

/**
 * A video attachment.
 *
 * Starts as a poster frame so the list stays cheap to scroll — a player per bubble would
 * hold a decoder open for every clip in the conversation. Tapping play swaps in the real
 * player inline; the expand button opens the same clip fullscreen.
 */
@Composable
fun VideoBubble(
    content: ChatBubbleContent.Video,
    modifier: Modifier = Modifier,
    coordinator: MediaPlaybackCoordinator = koinInject()
) {
    val taminColors = LocalTaminColors.current
    val owner = remember(content.source) { MediaPlaybackCoordinator.videoOwner(content.source) }
    val activeOwner by coordinator.activeOwner.collectAsState()
    var isPlayingInline by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    // Inline playback starts silent: a bubble that shouts audio the moment it scrolls
    // into view is hostile. Fullscreen opts back in.
    var isMuted by remember { mutableStateOf(true) }

    Column(modifier = modifier.width(240.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            if (isPlayingInline) {
                VideoPlayer(
                    url = content.source,
                    autoPlay = true,
                    muted = isMuted,
                    // A muted clip is not competing for the ear, so it only has to yield
                    // once the user turns its sound on.
                    paused = !isMuted && activeOwner != owner,
                    onPlayingChanged = { playing ->
                        if (playing && !isMuted) coordinator.claim(owner)
                        else if (!playing) coordinator.release(owner)
                    },
                    modifier = Modifier.matchParentSize()
                )
            } else {
                content.thumbnailUrl?.let { thumb ->
                    SubcomposeAsyncImage(
                        model = thumb,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { isPlayingInline = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "پخش ویدیو",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                content.durationMs?.let { duration ->
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OverlayIconButton(
                    icon = Icons.Default.Fullscreen,
                    contentDescription = "تمام‌صفحه",
                    onClick = { isFullscreen = true }
                )
                if (isPlayingInline) {
                    OverlayIconButton(
                        icon = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff
                               else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = if (isMuted) "پخش صدا" else "قطع صدا",
                        onClick = {
                            isMuted = !isMuted
                            if (isMuted) coordinator.release(owner) else coordinator.claim(owner)
                        }
                    )
                }
            }
        }

        content.caption?.takeIf { it.isNotBlank() }?.let { caption ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted
            )
        }
    }

    if (isFullscreen) {
        FullscreenVideoDialog(
            url = content.source,
            onDismiss = { isFullscreen = false }
        )
    }
}

/** Small translucent control drawn over a video frame. */
@Composable
private fun OverlayIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * A chart drawn from plain data, so it renders identically whether it just arrived or
 * was restored from a saved conversation.
 */
@Composable
fun ChartBubble(
    content: ChatBubbleContent.Chart,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val accent = MaterialTheme.colorScheme.primary

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(taminColors.bgSurface)
            .padding(12.dp)
    ) {
        content.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary
            )
            Spacer(Modifier.height(10.dp))
        }

        val values = content.series.firstOrNull()?.values.orEmpty()
        if (values.isEmpty()) {
            Text(
                text = "داده‌ای برای نمایش نمودار نیست.",
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textMuted
            )
            return@Column
        }

        when (content.kind) {
            ChartKind.BAR, ChartKind.PIE -> BarChart(values, accent)
            ChartKind.LINE -> LineChart(values, accent)
        }

        if (content.labels.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                content.labels.take(values.size).forEach { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textMuted,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        content.valueUnit?.takeIf { it.isNotBlank() }?.let { unit ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = "واحد: $unit",
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textMuted
            )
        }
    }
}

@Composable
private fun BarChart(values: List<Double>, color: Color) {
    val max = values.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        val gap = 6.dp.toPx()
        val barWidth = ((size.width - gap * (values.size - 1)) / values.size).coerceAtLeast(1f)
        values.forEachIndexed { index, value ->
            val barHeight = (value / max * size.height).toFloat().coerceAtLeast(2f)
            drawRoundRect(
                color = color,
                topLeft = Offset(index * (barWidth + gap), size.height - barHeight),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}

@Composable
private fun LineChart(values: List<Double>, color: Color) {
    val max = values.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
    Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        if (values.size < 2) return@Canvas
        val step = size.width / (values.size - 1)
        var previous = Offset(0f, size.height - (values[0] / max * size.height).toFloat())
        values.drop(1).forEachIndexed { index, value ->
            val point = Offset(
                (index + 1) * step,
                size.height - (value / max * size.height).toFloat()
            )
            drawLine(color = color, start = previous, end = point, strokeWidth = 3f)
            previous = point
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
}
