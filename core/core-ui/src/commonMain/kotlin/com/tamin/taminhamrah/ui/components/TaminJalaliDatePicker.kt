package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits

/** شنبه-first, matching how Persian calendars are read. */
private val WEEKDAY_LABELS = listOf("ش", "ی", "د", "س", "چ", "پ", "ج")

private const val DAYS_PER_WEEK = 7
private val DAY_CELL_SIZE = 40.dp

/**
 * A Jalali date picker in the app's own styling.
 *
 * Deliberately plain: a month to page through and a grid of days, no year carousel or range
 * selection. Callers that need a range show it twice, which keeps this usable anywhere a single
 * date is wanted.
 *
 * [initial] is the date the grid opens on, defaulting to today. [onConfirm] reports the chosen
 * Jalali year/month/day; use [PersianDateFormatter.toEpochMillis] to send it to an endpoint.
 */
@Composable
fun TaminJalaliDatePicker(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int, day: Int) -> Unit,
    initial: Triple<Int, Int, Int> = PersianDateFormatter.today(),
) {
    val colors = LocalTaminColors.current
    var year by remember { mutableStateOf(initial.first) }
    var month by remember { mutableStateOf(initial.second) }
    var day by remember { mutableStateOf(initial.third) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(CornerRadius.card),
            color = colors.bgSurface,
        ) {
            Column(modifier = Modifier.padding(Spacing.lg)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                )

                MonthHeader(
                    year = year,
                    month = month,
                    onPrevious = {
                        if (month == 1) {
                            month = 12
                            year -= 1
                        } else {
                            month -= 1
                        }
                        // The previous month may be shorter, so keep the day inside it.
                        day = day.coerceAtMost(PersianDateFormatter.daysInMonth(year, month))
                    },
                    onNext = {
                        if (month == 12) {
                            month = 1
                            year += 1
                        } else {
                            month += 1
                        }
                        day = day.coerceAtMost(PersianDateFormatter.daysInMonth(year, month))
                    },
                )

                WeekdayRow()

                DayGrid(
                    year = year,
                    month = month,
                    selectedDay = day,
                    onSelect = { day = it },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = "انصراف", color = colors.textSecondary)
                    }
                    TextButton(onClick = { onConfirm(year, month, day) }) {
                        Text(text = "تایید", color = colors.teal)
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthHeader(
    year: Int,
    month: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = "ماه قبل",
            tint = colors.textSecondary,
            modifier = Modifier
                .size(IconSize.medium)
                .clip(CircleShape)
                .clickable(onClick = onPrevious),
        )
        Text(
            text = "${PersianDateFormatter.monthNames[month - 1]} ${year.toString().toPersianDigits()}",
            style = MaterialTheme.typography.titleSmall,
            color = colors.textPrimary,
        )
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            contentDescription = "ماه بعد",
            tint = colors.textSecondary,
            modifier = Modifier
                .size(IconSize.medium)
                .clip(CircleShape)
                .clickable(onClick = onNext),
        )
    }
}

@Composable
private fun WeekdayRow() {
    val colors = LocalTaminColors.current
    Row(modifier = Modifier.fillMaxWidth()) {
        WEEKDAY_LABELS.forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.size(DAY_CELL_SIZE).padding(top = Spacing.sm),
            )
        }
    }
}

@Composable
private fun DayGrid(
    year: Int,
    month: Int,
    selectedDay: Int,
    onSelect: (Int) -> Unit,
) {
    val dayCount = PersianDateFormatter.daysInMonth(year, month)
    val leadingBlanks = PersianDateFormatter.firstWeekdayOfMonth(year, month)
    // Blank cells before the 1st keep each date under the right weekday column.
    val cells = List(leadingBlanks) { null } + (1..dayCount).toList()

    Column {
        cells.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { dayNumber ->
                    DayCell(
                        day = dayNumber,
                        isSelected = dayNumber == selectedDay,
                        onSelect = onSelect,
                    )
                }
                // Pad the final row so it stays aligned with the ones above.
                repeat(DAYS_PER_WEEK - week.size) {
                    Box(modifier = Modifier.size(DAY_CELL_SIZE))
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int?,
    isSelected: Boolean,
    onSelect: (Int) -> Unit,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .size(DAY_CELL_SIZE)
            .padding(Spacing.xxs)
            .clip(CircleShape)
            .background(if (isSelected) colors.teal else colors.bgSurface)
            .then(
                if (day == null) Modifier else Modifier.clickable { onSelect(day) },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (day != null) {
            Text(
                text = day.toString().toPersianDigits(),
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) colors.bgSurface else colors.textPrimary,
            )
        }
    }
}
