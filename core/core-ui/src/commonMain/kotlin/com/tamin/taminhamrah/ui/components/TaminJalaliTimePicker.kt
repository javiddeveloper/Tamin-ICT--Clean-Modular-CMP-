package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.time_picker_confirm
import taminx.core.core_ui.time_picker_now

/** The wheel shows this many rows; the middle one is the selection. Must stay odd. Mirrors [TaminJalaliDatePicker]. */
private const val VISIBLE_ROWS = 5

private val ROW_HEIGHT = 44.dp
private val WHEEL_HEIGHT = ROW_HEIGHT * VISIBLE_ROWS

/** Where the top and bottom fades give way to clear glass — one row's worth at each end. */
private const val EDGE_FADE_STOP = 1f / VISIBLE_ROWS

/** Just enough edge to separate the selection band from the panel; the fill carries the selection. */
private const val SELECTION_BORDER_ALPHA = 0.25f

private const val HOURS_IN_DAY = 24
private const val MINUTES_IN_HOUR = 60

private val COLON_WIDTH = 20.dp

/**
 * A time picker matching [TaminJalaliDatePicker]'s look: two snapping wheels for hour and minute,
 * separated by a colon and always read left to right — `12:00`, `14:40`, `00:00` — since a clock
 * time is a numeric value like an amount or a tracking code, not calendar prose.
 *
 * [initial] is the time the wheels open on, defaulting to the current wall-clock time.
 */
@Composable
fun TaminJalaliTimePicker(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initial: Pair<Int, Int> = PersianDateFormatter.now(),
) {
    val colors = LocalTaminColors.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(CornerRadius.sheet),
            color = colors.bgSurface,
        ) {
            JalaliTimePickerContent(
                title = title,
                onDismiss = onDismiss,
                onConfirm = onConfirm,
                initial = initial,
            )
        }
    }
}

/**
 * Same wheels as [TaminJalaliTimePicker], surfaced from the bottom of the screen instead of a
 * centered dialog. Use where the picker is one step in a longer flow and should feel anchored to
 * the field that opened it rather than floating above the page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaminJalaliTimePickerBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initial: Pair<Int, Int> = PersianDateFormatter.now(),
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        JalaliTimePickerContent(
            title = title,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            initial = initial,
            wheelsBackground = colors.bgSurface,
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

/** The header, wheels and action row shared by the dialog and bottom-sheet presentations. */
@Composable
private fun JalaliTimePickerContent(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    initial: Pair<Int, Int>,
    wheelsBackground: Color = LocalTaminColors.current.bgPage,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var hour by remember { mutableIntStateOf(initial.first) }
    var minute by remember { mutableIntStateOf(initial.second) }

    val hours = remember { (0 until HOURS_IN_DAY).map { it.toString().padStart(2, '0') }.toImmutableList() }
    val minutes = remember { (0 until MINUTES_IN_HOUR).map { it.toString().padStart(2, '0') }.toImmutableList() }

    Column(
        modifier = modifier.padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        TimePickerHeader(
            title = title,
            hour = hour,
            minute = minute,
            onNow = {
                val (nowHour, nowMinute) = PersianDateFormatter.now()
                hour = nowHour
                minute = nowMinute
            },
        )

        TimeWheels(
            hours = hours,
            minutes = minutes,
            hourIndex = hour,
            minuteIndex = minute,
            onHourIndex = { hour = it },
            onMinuteIndex = { minute = it },
            background = wheelsBackground,
        )

        // انصراف first so that right-to-left puts it on the right and the wide blue
        // تأیید ساعت on the left, matching the date picker's action row.
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            TaminPrimaryButton(
                text = stringResource(Res.string.time_picker_confirm),
                onClick = { onConfirm(hour, minute) },
                background = SolidColor(colors.blueText),
                modifier = Modifier.weight(2f),
            )
            CancelButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** The caller's wording on one side, the time the wheels currently spell out, and a way back to now. */
@Composable
private fun TimePickerHeader(
    title: String,
    hour: Int,
    minute: Int,
    onNow: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary,
            )
            val headerTimeText = remember(hour, minute) {
                "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
            }
            NumericText(
                text = headerTimeText,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
            )
        }
        Text(
            text = stringResource(Res.string.time_picker_now),
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(colors.blueBg)
                .clickable(onClick = onNow)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )
    }
}

/**
 * The two wheels on their shared panel, forced left-to-right regardless of the surrounding RTL
 * page — a clock reads `hour:minute` left to right the same way an amount or a tracking code does.
 */
@Composable
private fun TimeWheels(
    hours: ImmutableList<String>,
    minutes: ImmutableList<String>,
    hourIndex: Int,
    minuteIndex: Int,
    onHourIndex: (Int) -> Unit,
    onMinuteIndex: (Int) -> Unit,
    background: Color,
) {
    val colors = LocalTaminColors.current
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WHEEL_HEIGHT)
                .clip(RoundedCornerShape(CornerRadius.cardCompact))
                .background(background)
                .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.cardCompact)),
            contentAlignment = Alignment.Center,
        ) {
            // The selection is a place, not a row: it stays put while the numbers move through it.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ROW_HEIGHT)
                    .padding(horizontal = Spacing.sm)
                    .clip(RoundedCornerShape(CornerRadius.chip))
                    .background(colors.blueBg)
                    .border(1.dp, colors.blueText.copy(alpha = SELECTION_BORDER_ALPHA), RoundedCornerShape(CornerRadius.chip)),
            )

            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                WheelColumn(items = hours, selectedIndex = hourIndex, onSelected = onHourIndex, modifier = Modifier.weight(1f))
                Text(
                    text = ":",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.blueText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(COLON_WIDTH),
                )
                WheelColumn(items = minutes, selectedIndex = minuteIndex, onSelected = onMinuteIndex, modifier = Modifier.weight(1f))
            }

            // Fades the rows away from the middle instead of tinting each one, same as the date wheels.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to background,
                            EDGE_FADE_STOP to Color.Transparent,
                            1f - EDGE_FADE_STOP to Color.Transparent,
                            1f to background,
                        ),
                    ),
            )
        }
    }
}
