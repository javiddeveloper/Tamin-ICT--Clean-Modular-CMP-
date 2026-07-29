package com.tamin.taminhamrah.feature.healthProfile.ui.components

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.TaminHamrahShapes
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

/**
 * A highly interactive, premium Ruler Picker component.
 * Allows users to choose a value (like height or weight) by dragging the ruler
 * or using increment/decrement buttons.
 *
 * @param value The current value (nullable for optional state).
 * @param onValueChange Callback when the value changes.
 * @param range The valid range of integers.
 * @param unit The unit label (e.g. "سانتی‌متر" or "کیلوگرم").
 * @param accentColor The color of the main pointer and active buttons.
 */
@Composable
fun RulerPicker(
    modifier: Modifier = Modifier,
    value: Int?,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    unit: String,
    defaultPoint: Int = range.first + (range.last - range.first) / 2,
    accentColor: Color = LocalTaminColors.current.blueText,
    shape: Shape = RoundedCornerShape(20.dp)
) {
    val taminColors = LocalTaminColors.current
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    val coroutineScope = rememberCoroutineScope()

    // Ticks configuration
    val tickSpacingPx = 24f // spacing between ticks in pixels
    val totalTicks = range.last - range.first

    val activeValue = value ?: defaultPoint

    // We maintain a float offset representing the scroll position
    val scrollOffset = remember { Animatable((activeValue - range.first) * tickSpacingPx) }

    // Synchronize external value changes
    LaunchedEffect(value) {
        val targetOffset = (activeValue - range.first) * tickSpacingPx
        if (scrollOffset.value.roundToInt() != targetOffset.roundToInt()) {
            scrollOffset.animateTo(targetOffset)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(taminColors.bgSurface, shape)
            .border(1.dp, taminColors.border, shape)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Value and Unit display
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            // Decrement Button
            IconButton(
                onClick = {
                    val prevVal = if (value == null) defaultPoint else (value - 1).coerceAtLeast(range.first)
                    onValueChange(prevVal)
                },
                modifier = Modifier
                    .size(38.dp)
                    .background(taminColors.divider, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(Res.string.health_ruler_decrease),
                    tint = taminColors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Current Value Display
            TaminText(
                text = value?.toString() ?: stringResource(Res.string.health_physical_unselected_value),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (value != null) taminColors.textPrimary else taminColors.textMuted,
                lineHeight = 40.sp,
                modifier = Modifier.alignByBaseline()
            )

            Spacer(modifier = Modifier.width(6.dp))

            TaminText(
                text = unit,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textMuted
                ),
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .alignByBaseline()
            )

            Spacer(modifier = Modifier.width(20.dp))

            // Increment Button
            IconButton(
                onClick = {
                    val nextVal = if (value == null) defaultPoint else (value + 1).coerceAtMost(range.last)
                    onValueChange(nextVal)
                },
                modifier = Modifier
                    .size(38.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.health_ruler_increase),
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Ruler view area
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        coroutineScope.launch {
                            val dragDelta = if (isRtl) delta else -delta
                            val newOffset = (scrollOffset.value - dragDelta)
                                .coerceIn(0f, totalTicks * tickSpacingPx)
                            scrollOffset.snapTo(newOffset)
                            val newValue = range.first + (newOffset / tickSpacingPx).roundToInt()
                            onValueChange(newValue.coerceIn(range))
                        }
                    },
                    onDragStopped = {
                        val nearestTick = (scrollOffset.value / tickSpacingPx).roundToInt()
                        val targetOffset = nearestTick * tickSpacingPx
                        scrollOffset.animateTo(targetOffset)
                        onValueChange((range.first + nearestTick).coerceIn(range))
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            val viewWidthPx = with(density) { maxWidth.toPx() }
            val midXPx = viewWidthPx / 2f
            val bufferPx = with(density) { 48.dp.toPx() }
            val centerVal = scrollOffset.value
            val centerTickIndex = centerVal / tickSpacingPx

            // 1. Draw Ruler Ticks & Center Active Pointer Line
            Canvas(modifier = Modifier.fillMaxSize()) {
                val viewWidth = size.width
                val midX = viewWidth / 2f

                val rangeSpan = (midX + bufferPx) / tickSpacingPx
                val startTickIndex = (centerTickIndex - rangeSpan).toInt().coerceAtLeast(0)
                val endTickIndex = (centerTickIndex + rangeSpan).toInt().coerceAtMost(totalTicks)

                for (i in startTickIndex..endTickIndex) {
                    val tickValue = range.first + i
                    val deltaIndex = i - centerTickIndex
                    val offsetPx = deltaIndex * tickSpacingPx
                    val tickX = if (isRtl) midX - offsetPx else midX + offsetPx

                    val isMajor = tickValue % 10 == 0
                    val isHalf = tickValue % 5 == 0 && !isMajor

                    val tickHeight = when {
                        isMajor -> 26.dp.toPx()
                        isHalf -> 18.dp.toPx()
                        else -> 14.dp.toPx()
                    }

                    val tickThickness = if (isMajor) 2.dp.toPx() else 1.5.dp.toPx()
                    val tickColor = if (isMajor) taminColors.textSecondary.copy(alpha = 0.6f) else taminColors.border

                    // Draw tick mark line (hanging down from top)
                    drawLine(
                        color = tickColor,
                        start = Offset(tickX, 0f),
                        end = Offset(tickX, tickHeight),
                        strokeWidth = tickThickness,
                        cap = StrokeCap.Round
                    )
                }

                // Sleek central active indicator pointer line (matching Figma 2.5dp width x 32dp height)
                drawLine(
                    color = accentColor,
                    start = Offset(midX, 0f),
                    end = Offset(midX, 32.dp.toPx()),
                    strokeWidth = 2.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 2. Draw Major Labels
            val rangeSpan = (midXPx + bufferPx) / tickSpacingPx
            val startTickIndex = (centerTickIndex - rangeSpan).toInt().coerceAtLeast(0)
            val endTickIndex = (centerTickIndex + rangeSpan).toInt().coerceAtMost(totalTicks)

            Box(modifier = Modifier.fillMaxSize()) {
                for (i in startTickIndex..endTickIndex) {
                    val tickValue = range.first + i
                    if (tickValue % 10 == 0) {
                        val deltaIndex = i - centerTickIndex
                        val offsetPx = if (isRtl) -(deltaIndex * tickSpacingPx) else (deltaIndex * tickSpacingPx)
                        val offsetDp = with(density) { offsetPx.toDp() }
                        val isCurrent = tickValue == value

                        TaminText(
                            text = tickValue.toString(),
                            fontSize = 11.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCurrent) accentColor else taminColors.textMuted,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .absoluteOffset(x = offsetDp, y = 38.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // 3. Draw Side Fade Gradients OVER both Ticks and Text
            Canvas(modifier = Modifier.fillMaxSize()) {
                val viewWidth = size.width
                val viewHeight = size.height
                val fadeWidthPx = 54.dp.toPx()

                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(taminColors.bgSurface, Color.Transparent),
                        startX = 0f,
                        endX = fadeWidthPx
                    ),
                    topLeft = Offset(0f, 0f),
                    size = Size(fadeWidthPx, viewHeight)
                )
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, taminColors.bgSurface),
                        startX = viewWidth - fadeWidthPx,
                        endX = viewWidth
                    ),
                    topLeft = Offset(viewWidth - fadeWidthPx, 0f),
                    size = Size(fadeWidthPx, viewHeight)
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun RulerPickerPreview() {
    PreviewRtlThemeContent {
        var height by remember { mutableStateOf(175) }
        var weight by remember { mutableStateOf(70) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TaminText("Height Picker (Accent Blue)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            RulerPicker(
                value = height,
                onValueChange = { height = it },
                range = 120..220,
                unit = "سانتی‌متر"
            )

            Spacer(modifier = Modifier.height(16.dp))

            TaminText("Weight Picker (Accent Teal)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            RulerPicker(
                value = weight,
                onValueChange = { weight = it },
                range = 40..150,
                unit = "کیلوگرم",
            )
        }
    }
}
