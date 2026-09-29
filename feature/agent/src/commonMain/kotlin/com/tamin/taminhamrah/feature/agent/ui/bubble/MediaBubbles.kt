package com.tamin.taminhamrah.feature.agent.ui.bubble

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.agent.audio.MediaPlaybackCoordinator
import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.ui.AgentColors
import com.tamin.taminhamrah.feature.agent.ui.AgentGlass
import com.tamin.taminhamrah.feature.agent.ui.agentGlassCard
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.koin.compose.koinInject
import kotlin.math.roundToInt

private val MEDIA_WIDTH = 240.dp
private val IMAGE_HEIGHT = 160.dp
private val VIDEO_HEIGHT = 140.dp
private val VIDEO_PROGRESS_HEIGHT = 3.dp

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
                color = AgentGlass.textSecondary
            )
        }
    }
}

/**
 * An image attachment with an optional caption underneath. The frame is a glass tile so the
 * loading and failed states read as part of the reply, not as a grey block.
 */
@Composable
fun ImageBubble(
    content: ChatBubbleContent.Image,
    modifier: Modifier = Modifier
) {
    val frameShape = RoundedCornerShape(com.tamin.taminhamrah.ui.theme.CornerRadius.lg)
    Column(modifier = modifier.width(MEDIA_WIDTH)) {
        SubcomposeAsyncImage(
            model = content.source,
            contentDescription = content.caption,
            contentScale = ContentScale.Crop,
            loading = {
                Box(modifier = Modifier.fillMaxWidth().height(IMAGE_HEIGHT).background(AgentGlass.tileFill))
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxWidth().height(IMAGE_HEIGHT).background(AgentGlass.tileFill),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        Icon(
                            Icons.Outlined.BrokenImage,
                            contentDescription = null,
                            tint = AgentGlass.textSecondary,
                            modifier = Modifier.size(IconSize.medium)
                        )
                        Text(
                            text = "تصویر بارگذاری نشد",
                            style = MaterialTheme.typography.labelSmall,
                            color = AgentGlass.textSecondary
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(IMAGE_HEIGHT)
                .clip(frameShape)
                .border(AgentGlass.borderWidth, AgentGlass.borderColor, frameShape)
        )
        content.caption?.takeIf { it.isNotBlank() }?.let { caption ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = AgentGlass.textSecondary
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
    val owner = remember(content.source) { MediaPlaybackCoordinator.videoOwner(content.source) }
    val activeOwner by coordinator.activeOwner.collectAsState()
    var isPlayingInline by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    // Inline playback starts silent: a bubble that shouts audio the moment it scrolls
    // into view is hostile. Fullscreen opts back in.
    var isMuted by remember { mutableStateOf(true) }
    // Inline transport is the bubble's own: a tap pauses/resumes, and a thin progress bar
    // sits on the frame's bottom edge. The native controller is off — its strip does not
    // fit a 140dp frame (it drew a stray time bar above a black band).
    var isPausedByUser by remember { mutableStateOf(false) }
    var positionMs by remember { mutableStateOf(0L) }
    var durationMs by remember { mutableStateOf(content.durationMs ?: 0L) }
    // A finished clip offers a replay. The player cannot be resumed once it has ended, so
    // replay bumps this key, which drops the old player and starts a fresh one from the top.
    var hasEnded by remember { mutableStateOf(false) }
    var playbackKey by remember { mutableStateOf(0) }
    val replay: () -> Unit = {
        hasEnded = false
        isPausedByUser = false
        positionMs = 0L
        playbackKey++
    }
    val frameShape = RoundedCornerShape(com.tamin.taminhamrah.ui.theme.CornerRadius.lg)

    Column(modifier = modifier.width(MEDIA_WIDTH)) {
        // The frame is a glass tile: a poster-less clip shows the tile, not a grey block.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(VIDEO_HEIGHT)
                .clip(frameShape)
                .background(AgentGlass.tileFill)
                .border(AgentGlass.borderWidth, AgentGlass.borderColor, frameShape),
            contentAlignment = Alignment.Center
        ) {
            if (isPlayingInline) {
                key(playbackKey) {
                    VideoPlayer(
                        url = content.source,
                        autoPlay = true,
                        muted = isMuted,
                        // A muted clip is not competing for the ear, so it only has to yield
                        // once the user turns its sound on.
                        paused = isPausedByUser || (!isMuted && activeOwner != owner),
                        showControls = false,
                        onPlayingChanged = { playing ->
                            if (playing && !isMuted) coordinator.claim(owner)
                            else if (!playing) coordinator.release(owner)
                        },
                        onProgress = { position, duration ->
                            positionMs = position
                            if (duration > 0) durationMs = duration
                        },
                        onEnded = { hasEnded = true },
                        modifier = Modifier.matchParentSize()
                    )
                }
                // Above the player, so the tap lands here and not in the platform view.
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            if (hasEnded) replay() else isPausedByUser = !isPausedByUser
                        }
                )
                when {
                    hasEnded -> MediaPlayButton(
                        icon = Icons.Default.Replay,
                        contentDescription = "پخش دوباره",
                        onClick = replay
                    )
                    isPausedByUser -> MediaPlayButton(
                        icon = Icons.Default.PlayArrow,
                        contentDescription = "ادامه پخش",
                        onClick = { isPausedByUser = false }
                    )
                }
                if (durationMs > 0) {
                    Text(
                        text = formatDuration((durationMs - positionMs).coerceAtLeast(0L)),
                        style = MaterialTheme.typography.labelSmall,
                        color = AgentColors.ink,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 8.dp, vertical = 8.dp + VIDEO_PROGRESS_HEIGHT)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                VideoProgressBar(
                    progress = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f,
                    modifier = Modifier.align(Alignment.BottomCenter)
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
                MediaPlayButton(
                    icon = Icons.Default.PlayArrow,
                    contentDescription = "پخش ویدیو",
                    onClick = { isPlayingInline = true }
                )
                content.durationMs?.let { duration ->
                    Text(
                        text = formatDuration(duration),
                        style = MaterialTheme.typography.labelSmall,
                        color = AgentColors.ink,
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
                color = AgentGlass.textSecondary
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

/** The centered play control on a clip: the same gradient circle as the voice play button. */
@Composable
private fun MediaPlayButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(AgentColors.actionGradient)
            .border(AgentGlass.borderWidth, AgentGlass.borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = AgentColors.ink)
    }
}

/**
 * The inline clip's progress, flush with the frame's bottom edge so it reads as part of the
 * frame, not as a control floating in the picture.
 */
@Composable
private fun VideoProgressBar(progress: Float, modifier: Modifier = Modifier) {
    // Playback runs left to right whatever the layout direction, like every media scrubber.
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(VIDEO_PROGRESS_HEIGHT)
                .background(AgentColors.ink.copy(alpha = 0.25f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(VIDEO_PROGRESS_HEIGHT)
                    .background(AgentGlass.accent)
            )
        }
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
            tint = AgentColors.ink,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * A chart drawn from plain data, so it renders identically whether it just arrived or
 * was restored from a saved conversation. It sits on the reply's glass card, like a table,
 * and draws in the [AgentGlass] palette — the theme's surface is white in light mode.
 */
@Composable
fun ChartBubble(
    content: ChatBubbleContent.Chart,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .agentGlassCard(RoundedCornerShape(com.tamin.taminhamrah.ui.theme.CornerRadius.xl))
            .padding(Spacing.md)
    ) {
        content.title?.takeIf { it.isNotBlank() }?.let { title ->
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = AgentGlass.textPrimary
            )
            Spacer(Modifier.height(Spacing.smPlus))
        }

        val values = content.series.firstOrNull()?.values.orEmpty()
        if (values.isEmpty()) {
            Text(
                text = "داده‌ای برای نمایش نمودار نیست.",
                style = MaterialTheme.typography.bodySmall,
                color = AgentGlass.textSecondary
            )
            return@Column
        }

        val labels = content.labels.take(values.size)
        when (content.kind) {
            ChartKind.BAR -> {
                BarChart(values)
                ChartAxisLabels(labels)
            }
            ChartKind.LINE -> {
                LineChart(values)
                ChartAxisLabels(labels)
            }
            ChartKind.PIE -> PieChart(values, labels, content.valueUnit)
        }

        content.valueUnit?.takeIf { it.isNotBlank() && content.kind != ChartKind.PIE }?.let { unit ->
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = "واحد: $unit",
                style = MaterialTheme.typography.labelSmall,
                color = AgentGlass.textSecondary
            )
        }
    }
}

/** One label under each bar/point, in plot order (left to right, whatever the layout direction). */
@Composable
private fun ChartAxisLabels(labels: List<String>) {
    if (labels.isEmpty()) return
    Spacer(Modifier.height(Spacing.sm))
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = AgentGlass.textSecondary,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Bars fade from the accent at the top into the card, with a hairline baseline. */
@Composable
private fun BarChart(values: List<Double>) {
    val max = values.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
    val accent = AgentGlass.accent
    val baseline = AgentGlass.borderColor
    Canvas(modifier = Modifier.fillMaxWidth().height(CHART_HEIGHT)) {
        val gap = Spacing.sm.toPx()
        val barWidth = ((size.width - gap * (values.size - 1)) / values.size).coerceAtLeast(1f)
        values.forEachIndexed { index, value ->
            val barHeight = (value / max * size.height).toFloat().coerceAtLeast(2f)
            val top = size.height - barHeight
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(accent, accent.copy(alpha = 0.45f)),
                    startY = top,
                    endY = size.height,
                ),
                topLeft = Offset(index * (barWidth + gap), top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(BAR_CORNER.toPx(), BAR_CORNER.toPx())
            )
        }
        drawLine(
            color = baseline,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = Thickness.border.toPx()
        )
    }
}

/** A line with a soft area beneath it and a dot on every point. */
@Composable
private fun LineChart(values: List<Double>) {
    val max = values.maxOrNull()?.takeIf { it > 0.0 } ?: 1.0
    val accent = AgentGlass.accent
    val baseline = AgentGlass.borderColor
    Canvas(modifier = Modifier.fillMaxWidth().height(CHART_HEIGHT)) {
        // Points are inset so the end dots are not clipped by the canvas edge.
        val inset = LINE_DOT_RADIUS.toPx()
        val plotWidth = size.width - inset * 2
        val plotHeight = size.height - inset * 2
        val points = values.mapIndexed { index, value ->
            val x = if (values.size == 1) inset + plotWidth / 2 else inset + index * (plotWidth / (values.size - 1))
            Offset(x, inset + plotHeight - (value / max * plotHeight).toFloat())
        }
        drawLine(
            color = baseline,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = Thickness.border.toPx()
        )
        if (points.size >= 2) {
            val area = Path().apply {
                moveTo(points.first().x, size.height)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, size.height)
                close()
            }
            drawPath(
                path = area,
                brush = Brush.verticalGradient(listOf(accent.copy(alpha = 0.35f), accent.copy(alpha = 0f)))
            )
            val line = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path = line, color = accent, style = Stroke(width = LINE_WIDTH.toPx(), cap = StrokeCap.Round))
        }
        points.forEach { point ->
            drawCircle(color = accent, radius = LINE_DOT_RADIUS.toPx(), center = point)
            drawCircle(color = AgentGlass.shadowColor, radius = LINE_DOT_RADIUS.toPx() * 0.45f, center = point)
        }
    }
}

/**
 * A donut with a legend: one slice per value in the [AgentGlass.chartPalette], each legend row
 * carrying its label, value and share. Non-positive values are skipped rather than drawn.
 */
@Composable
private fun PieChart(values: List<Double>, labels: List<String>, unit: String?) {
    val slices = values.mapIndexedNotNull { index, value ->
        if (value > 0.0) Triple(labels.getOrNull(index) ?: "", value, AgentGlass.chartPalette[index % AgentGlass.chartPalette.size]) else null
    }
    val total = slices.sumOf { it.second }
    if (slices.isEmpty() || total <= 0.0) {
        Text(
            text = "داده‌ای برای نمایش نمودار نیست.",
            style = MaterialTheme.typography.bodySmall,
            color = AgentGlass.textSecondary
        )
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Canvas(modifier = Modifier.size(PIE_SIZE)) {
            val stroke = PIE_RING_WIDTH.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = Offset((size.width - diameter) / 2, (size.height - diameter) / 2)
            var startAngle = -90f
            slices.forEach { (_, value, color) ->
                val sweep = (value / total * 360.0).toFloat()
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = (sweep - PIE_SLICE_GAP_DEG).coerceAtLeast(0.5f),
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(diameter, diameter),
                    style = Stroke(width = stroke, cap = StrokeCap.Butt)
                )
                startAngle += sweep
            }
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            slices.forEach { (label, value, color) ->
                val share = (value / total * 100).roundToInt()
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Box(modifier = Modifier.size(LEGEND_DOT).clip(CircleShape).background(color))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = AgentGlass.textPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${formatChartValue(value)}${unit?.takeIf { it.isNotBlank() }?.let { " $it" } ?: ""} · $share٪".toPersianDigits(),
                        style = MaterialTheme.typography.labelSmall,
                        color = AgentGlass.textSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** Whole numbers without a trailing `.0`, otherwise one decimal. */
private fun formatChartValue(value: Double): String {
    val rounded = (value * 10).roundToInt() / 10.0
    return if (rounded == rounded.toLong().toDouble()) rounded.toLong().toString() else rounded.toString()
}

private val CHART_HEIGHT = 120.dp
private val BAR_CORNER = 4.dp
private val LINE_WIDTH = 2.5.dp
private val LINE_DOT_RADIUS = 4.dp
private val PIE_SIZE = 108.dp
private val PIE_RING_WIDTH = 18.dp
private val LEGEND_DOT = 8.dp
private const val PIE_SLICE_GAP_DEG = 2f

private fun formatDuration(ms: Long): String {
    val totalSeconds = (ms / 1000).toInt()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${if (seconds < 10) "0$seconds" else "$seconds"}"
}
