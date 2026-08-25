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
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.unit.sp
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

private val ROW_HEIGHT = 48.dp
private val WHEEL_HEIGHT = ROW_HEIGHT * VISIBLE_ROWS

/** Where the top and bottom fades give way to clear glass — one row's worth at each end. */
private const val EDGE_FADE_STOP = 1f / VISIBLE_ROWS

/**
 * The floor of the year wheel — the conventional start for Jalali pickers in Iranian apps. The
 * ceiling is today: every date this picker collects (birthdate, prescription date, a record
 * search range) is a date that has already happened, so a future one is never a valid answer.
 */
private const val FIRST_YEAR = 1300

/**
 * The last month the month wheel offers for [year]: the whole year for any past year, and only up
 * to the current month once the year wheel reaches this one.
 */
internal fun lastSelectableMonth(
    year: Int,
    todayYear: Int,
    todayMonth: Int,
    monthsInYear: Int,
): Int = if (year >= todayYear) todayMonth else monthsInYear

/**
 * The last day the day wheel offers for [year]/[month]: the month's own length, cut back to today
 * once the wheels above it sit on the current year and month.
 */
internal fun lastSelectableDay(
    year: Int,
    month: Int,
    todayYear: Int,
    todayMonth: Int,
    todayDay: Int,
    daysInMonth: Int,
): Int = if (year >= todayYear && month >= todayMonth) todayDay else daysInMonth

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
    val today = remember { PersianDateFormatter.today() }
    val (todayYear, todayMonth, todayDay) = today

    // A caller that hands in a future date still opens on a selectable one.
    var year by remember { mutableIntStateOf(initial.first.coerceAtMost(todayYear)) }
    var month by remember { mutableIntStateOf(initial.second) }
    var day by remember { mutableIntStateOf(initial.third) }

    val years = remember(todayYear) {
        (FIRST_YEAR..todayYear).map { it.toString().toPersianDigits() }.toImmutableList()
    }

    // Each wheel is cut back to today once the wheels above it sit on the current year/month, so a
    // future date cannot be spun to in the first place. Trimming beats validating after the fact:
    // there is nothing to reject and no error to explain.
    val lastMonth = lastSelectableMonth(
        year = year,
        todayYear = todayYear,
        todayMonth = todayMonth,
        monthsInYear = PersianDateFormatter.monthNames.size,
    )
    val clampedMonth = month.coerceAtMost(lastMonth)
    val months = remember(lastMonth) {
        PersianDateFormatter.monthNames.take(lastMonth).toImmutableList()
    }

    val daysInMonth = PersianDateFormatter.daysInMonth(year, clampedMonth)
    val lastDay = lastSelectableDay(
        year = year,
        month = clampedMonth,
        todayYear = todayYear,
        todayMonth = todayMonth,
        todayDay = todayDay,
        daysInMonth = daysInMonth,
    )

    // A short month cannot hold the day standing on it. Clamped on the way out rather than written
    // back during composition: the wheel reports the row it lands on and corrects `day` itself, and
    // stepping over a short month and back leaves the original day intact.
    val clampedDay = day.coerceAtMost(lastDay)
    // Rebuilt only when the month's length can actually differ, so spinning the day wheel inside
    // one month never reallocates the list under it.
    val days = remember(lastDay) {
        (1..lastDay).map { it.toString().toPersianDigits() }.toImmutableList()
    }

    Column(
        modifier = modifier.padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        PickerHeader(
            title = title,
            year = year,
            month = clampedMonth,
            day = clampedDay,
            onToday = {
                year = todayYear
                month = todayMonth
                day = todayDay
            },
        )

        DateWheels(
            days = days,
            months = months,
            years = years,
            dayIndex = clampedDay - 1,
            monthIndex = clampedMonth - 1,
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
                onClick = { onConfirm(year, clampedMonth, clampedDay) },
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
            // Every wheel wraps: the last day rolls into the first, اسفند into فروردین, and the
            // last year back to 1300.
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
 * The list is repeated so the wheel has no ends — spin past the last row and the first follows.
 *
 * Half a wheel of padding above and below lets every entry reach the middle. With that padding the
 * centred row is exactly `firstVisibleItemIndex`, so the selection needs no arithmetic over the
 * scroll offset beyond folding that index back onto the list with `mod`.
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
    val itemCount = items.size
    // A long stretch of repeats with the opening row in the middle, so the wheel can be spun a
    // long way either direction before it could ever run out.
    val anchor = if (itemCount > 0) (CIRCULAR_LOOPS / 2) * itemCount else 0
    val state = rememberLazyListState(
        initialFirstVisibleItemIndex = (anchor + selectedIndex).coerceAtLeast(0),
    )

    val latestOnSelected by rememberUpdatedState(onSelected)
    val latestCount by rememberUpdatedState(itemCount)

    // Reports the row that settled in the middle. Keyed on the state alone rather than on `items`:
    // a month changing length must not restart this and re-report the row already parked in the
    // middle, which under the modulo below would name a different day than the one chosen.
    LaunchedEffect(state) {
        snapshotFlow { state.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                val count = latestCount
                if (count > 0) latestOnSelected(index.mod(count))
            }
    }

    // Follows the caller when the date moves for a reason other than this wheel — «امروز», or a
    // day clamped by a shorter month — and re-anchors after a length change, when the same raw
    // index no longer names the same row.
    LaunchedEffect(selectedIndex, itemCount) {
        if (itemCount == 0) return@LaunchedEffect
        val wanted = selectedIndex.coerceIn(0, itemCount - 1)
        val shown = state.firstVisibleItemIndex.mod(itemCount)
        if (!state.isScrollInProgress && shown != wanted) {
            state.scrollToItem(anchor + wanted)
        }
    }

    // Hoisted out of the row loop: building it per row would allocate a TextStyle for every
    // visible item on every recomposition.
    val rowStyle = MaterialTheme.typography.titleMedium.copy(
        fontSize = WHEEL_TEXT_SIZE,
        lineHeight = WHEEL_TEXT_LINE_HEIGHT,
    )

    LazyColumn(
        state = state,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = ROW_HEIGHT * (VISIBLE_ROWS / 2)),
        flingBehavior = rememberSnapFlingBehavior(state),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(
            count = itemCount * CIRCULAR_LOOPS,
            key = { it },
        ) { index ->
            if (itemCount == 0) return@items
            val label = items[index.mod(itemCount)]
            // Read here rather than in the surrounding wheel: only the rows redraw when the center
            // moves, and only when it crosses a row rather than on every pixel of the drag.
            val isSelected = index == state.firstVisibleItemIndex
            Text(
                text = label,
                style = rowStyle,
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

/** How many times the list repeats in a circular wheel. Large enough never to reach an end. */
private const val CIRCULAR_LOOPS = 401

/** The wheel's own text size, a step up from titleMedium so the date reads at a glance. */
private val WHEEL_TEXT_SIZE = 19.sp
private val WHEEL_TEXT_LINE_HEIGHT = 28.sp
