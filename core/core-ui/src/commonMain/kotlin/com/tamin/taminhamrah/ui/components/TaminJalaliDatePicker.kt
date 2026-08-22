package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.date_picker_confirm
import taminx.core.core_ui.date_picker_today
import kotlin.math.abs

/** The wheel shows this many rows; the middle one is the selection. Must stay odd. */
private const val VISIBLE_ROWS = 5

private val ROW_HEIGHT = 44.dp
private val WHEEL_HEIGHT = ROW_HEIGHT * VISIBLE_ROWS

/** Where the top and bottom fades give way to clear glass — one row's worth at each end. */
private const val EDGE_FADE_STOP = 1f / VISIBLE_ROWS

/**
 * The span of years the year wheel offers. 1300 is the conventional floor for Jalali pickers in
 * Iranian apps, and a little headroom past today covers forward-dated entries.
 */
private const val FIRST_YEAR = 1300
private const val YEARS_AHEAD = 5

/**
 * A Jalali date picker: three snapping wheels for day, month and year, read right to left in the
 * order the date is written.
 *
 * The selection is the row parked in the middle. Each wheel reports its own settled index through
 * a `snapshotFlow`, so dragging costs no recomposition of the surrounding dialog — only the wheel
 * whose center row changed redraws.
 *
 * [initial] is the date the wheels open on, defaulting to today. [onConfirm] reports the chosen
 * Jalali year/month/day; use [PersianDateFormatter.toEpochMillis] to send it to an endpoint.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaminJalaliDatePicker(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int, day: Int) -> Unit,
    initial: Triple<Int, Int, Int> = PersianDateFormatter.today(),
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        JalaliDatePickerContent(
            title = title,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            initial = initial,
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

/** The header, wheels and action row inside the sheet. */
@Composable
private fun JalaliDatePickerContent(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int, day: Int) -> Unit,
    initial: Triple<Int, Int, Int>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var year by remember { mutableIntStateOf(initial.first) }
    var month by remember { mutableIntStateOf(initial.second) }
    var day by remember { mutableIntStateOf(initial.third) }

    val lastYear = remember { PersianDateFormatter.currentJalaliYear() + YEARS_AHEAD }
    val years = remember(lastYear) {
        (FIRST_YEAR..lastYear).map { it.toString().toPersianDigits() }.toImmutableList()
    }
    val months = remember { PersianDateFormatter.monthNames.toImmutableList() }
    // Rebuilt only when the month's length can actually differ, so spinning the day wheel inside
    // one month never reallocates the list under it.
    val daysInMonth = PersianDateFormatter.daysInMonth(year, month)
    val days = remember(daysInMonth) {
        (1..daysInMonth).map { it.toString().toPersianDigits() }.toImmutableList()
    }

    // A short month cannot hold the day standing on it. Clamped on the way out rather than written
    // back during composition: the wheel reports the row it lands on and corrects `day` itself, and
    // stepping over a short month and back leaves the original day intact.
    val clampedDay = day.coerceAtMost(daysInMonth)

    Column(
        modifier = modifier.padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        PickerHeader(
            title = title,
            year = year,
            month = month,
            day = clampedDay,
            onToday = {
                val today = PersianDateFormatter.today()
                year = today.first
                month = today.second
                day = today.third
            },
        )

        DateWheels(
            days = days,
            months = months,
            years = years,
            dayIndex = clampedDay - 1,
            monthIndex = month - 1,
            yearIndex = year - FIRST_YEAR,
            onDayIndex = { day = it + 1 },
            onMonthIndex = { month = it + 1 },
            onYearIndex = { year = FIRST_YEAR + it },
            background = colors.bgSurface,
        )

        // انصراف first so that right-to-left puts it on the right and the wide blue
        // تأیید تاریخ on the left, as the design has them.
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            TaminPrimaryButton(
                text = stringResource(Res.string.date_picker_confirm),
                onClick = { onConfirm(year, month, clampedDay) },
                // The button's default is the app bar's gradient, which is teal. This
                // dialog is blue throughout, so it takes the same blue as the wheels.
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

/** The caller's wording on one side, the date the wheels currently spell out, and a way back to today. */
@Composable
private fun PickerHeader(
    title: String,
    year: Int,
    month: Int,
    day: Int,
    onToday: () -> Unit,
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
            val headerDateText = remember(day, month, year) {
                buildString {
                    append(day.toString().toPersianDigits())
                    append(' ')
                    append(PersianDateFormatter.monthNames.getOrElse(month - 1) { "" })
                    append(' ')
                    append(year.toString().toPersianDigits())
                }
            }
            Text(
                text = headerDateText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.blueText,
            )
        }
        Text(
            text = stringResource(Res.string.date_picker_today),
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(colors.blueBg)
                .clickable(onClick = onToday)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )
    }
}

/**
 * The three wheels on their shared panel.
 *
 * Day sits first so that in the app's right-to-left layout it lands on the right, and the columns
 * read «۱۱ مرداد ۱۴۰۵» across — the same order the date is written.
 */
@Composable
private fun DateWheels(
    days: ImmutableList<String>,
    months: ImmutableList<String>,
    years: ImmutableList<String>,
    dayIndex: Int,
    monthIndex: Int,
    yearIndex: Int,
    onDayIndex: (Int) -> Unit,
    onMonthIndex: (Int) -> Unit,
    onYearIndex: (Int) -> Unit,
    background: Color,
) {
    val colors = LocalTaminColors.current
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
                // The pale blue band from the design, edged in the same blue rather than in ink —
                // the selection should sit quietly under the numbers, not box them in.
                .background(colors.blueBg)
                .border(1.dp, colors.blueText.copy(alpha = SELECTION_BORDER_ALPHA), RoundedCornerShape(CornerRadius.chip)),
        )

        Row(modifier = Modifier.fillMaxSize()) {
            WheelColumn(items = days, selectedIndex = dayIndex, onSelected = onDayIndex, modifier = Modifier.weight(1f))
            WheelColumn(items = months, selectedIndex = monthIndex, onSelected = onMonthIndex, modifier = Modifier.weight(1f))
            WheelColumn(items = years, selectedIndex = yearIndex, onSelected = onYearIndex, modifier = Modifier.weight(1f))
        }

        // Fades the rows away from the middle instead of tinting each one: a static overlay costs
        // one draw, where per-row alpha would have to read the scroll offset on every frame.
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

/**
 * One wheel.
 *
 * Half a wheel of padding above and below lets the first and last entries reach the middle. With
 * that padding the centred row is exactly `firstVisibleItemIndex`, so the selection needs no
 * arithmetic over the scroll offset.
 */
@Suppress("FrequentlyChangingValue")
@Composable
private fun WheelColumn(
    items: ImmutableList<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val state = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceAtLeast(0))

    // Reports the row that settled in the middle. Read through a snapshotFlow rather than during
    // composition, so a spin never recomposes the dialog.
    LaunchedEffect(state, items) {
        snapshotFlow { state.firstVisibleItemIndex }.distinctUntilChanged().collect(onSelected)
    }

    // Follows the caller when the date moves for a reason other than this wheel — «امروز», or a
    // day clamped by a shorter month.
    LaunchedEffect(selectedIndex) {
        if (!state.isScrollInProgress && state.firstVisibleItemIndex != selectedIndex) {
            state.scrollToItem(selectedIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0)))
        }
    }

    LazyColumn(
        state = state,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = ROW_HEIGHT * (VISIBLE_ROWS / 2)),
        flingBehavior = rememberSnapFlingBehavior(state),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        itemsIndexed(items, key = { index, _ -> index }) { index, label ->
            // Read here rather than in the surrounding wheel: only the rows redraw when the center
            // moves, and only when it crosses a row rather than on every pixel of the drag.
            val isSelected = index == state.firstVisibleItemIndex
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) colors.blueText else colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ROW_HEIGHT)
                    // Rows shrink and fade the further they sit from the middle, so the column
                    // reads as a cylinder turning rather than a flat list. The offsets are read
                    // inside the layer block, which runs at draw time: a spin costs no
                    // recomposition, and the value updates every pixel of the drag rather than
                    // only when the center crosses a row.
                    .graphicsLayer {
                        val layout = state.layoutInfo
                        val item = layout.visibleItemsInfo.firstOrNull { it.index == index }
                            ?: return@graphicsLayer
                        val viewportCenter =
                            (layout.viewportStartOffset + layout.viewportEndOffset) / 2f
                        val itemCenter = item.offset + item.size / 2f
                        val rowsFromCenter =
                            abs(itemCenter - viewportCenter) / item.size.coerceAtLeast(1)
                        // Half the wheel is the furthest a row can be before it leaves the panel.
                        val distance = (rowsFromCenter / (VISIBLE_ROWS / 2f)).coerceIn(0f, 1f)
                        scaleX = lerp(1f, WHEEL_MIN_SCALE, distance)
                        scaleY = scaleX
                        alpha = lerp(1f, WHEEL_MIN_ALPHA, distance)
                    }
                    .padding(top = ROW_TEXT_TOP_PADDING),
            )
        }
    }
}

/** The bordered outline beside the primary action. */
@Composable
private fun CancelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .height(CANCEL_BUTTON_HEIGHT)
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .border(1.dp, colors.border, RoundedCornerShape(CornerRadius.iconTile))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = colors.textSecondary,
        )
    }
}


/** Just enough edge to separate the band from the panel; the fill carries the selection. */
private const val SELECTION_BORDER_ALPHA = 0.25f

/** Nudges the glyph off the row's top edge so it sits optically centred. */
private val ROW_TEXT_TOP_PADDING = 10.dp
private val CANCEL_BUTTON_HEIGHT = 52.dp

/**
 * How far a row shrinks and fades once it reaches the edge of the wheel. Tuned so the row either
 * side of the selection is still clearly readable, while the outermost pair recedes close to the
 * panel and reads as edge rather than content.
 */
private const val WHEEL_MIN_SCALE = 0.55f
private const val WHEEL_MIN_ALPHA = 0.12f
