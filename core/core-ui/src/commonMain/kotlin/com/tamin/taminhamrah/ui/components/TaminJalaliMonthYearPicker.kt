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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.date_picker_confirm
import taminx.core.core_ui.date_picker_today

/**
 * A month+year-only sibling of [TaminJalaliDatePicker], for pickers — payroll period search,
 * statement ranges — that never need a specific day. Reuses [WheelColumn]/[CancelButton] and the
 * wheel-panel constants from the day picker so both spin and settle identically; only the day
 * wheel and its clamping are dropped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaminJalaliMonthYearPickerBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit,
    initial: Pair<Int, Int> = PersianDateFormatter.today().let { (year, month, _) -> year to month },
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        MonthYearPickerContent(
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
private fun MonthYearPickerContent(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (year: Int, month: Int) -> Unit,
    initial: Pair<Int, Int>,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val (todayYear, todayMonth, _) = remember { PersianDateFormatter.today() }

    // A caller that hands in a future year still opens on a selectable one.
    var year by remember { mutableIntStateOf(initial.first.coerceAtMost(todayYear)) }
    var month by remember { mutableIntStateOf(initial.second) }

    val years = remember(todayYear) {
        (FIRST_YEAR..todayYear).map { it.toString().toPersianDigits() }.toImmutableList()
    }

    // Cut back to the current month once the year wheel reaches this year, so a future month
    // cannot be spun to in the first place — same trim-not-validate approach as the day picker.
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

    Column(
        modifier = modifier.padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        MonthYearPickerHeader(
            title = title,
            year = year,
            month = clampedMonth,
            onToday = {
                year = todayYear
                month = todayMonth
            },
        )

        MonthYearWheels(
            months = months,
            years = years,
            monthIndex = clampedMonth - 1,
            yearIndex = year - FIRST_YEAR,
            onMonthIndex = { month = it + 1 },
            onYearIndex = { year = FIRST_YEAR + it },
            background = colors.bgSurface,
        )

        // انصراف first so that right-to-left puts it on the right and the wide blue
        // تأیید تاریخ on the left, as the design has them.
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            TaminPrimaryButton(
                text = stringResource(Res.string.date_picker_confirm),
                onClick = { onConfirm(year, clampedMonth) },
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

/** The caller's wording on one side, the month/year the wheels currently spell out, and a way back to today. */
@Composable
private fun MonthYearPickerHeader(
    title: String,
    year: Int,
    month: Int,
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
            val headerDateText = remember(month, year) {
                buildString {
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
 * The two wheels on their shared panel.
 *
 * Month sits first so that in the app's right-to-left layout it lands on the right, and the
 * columns read «مرداد ۱۴۰۵» across — the same order the month/year is written.
 */
@Composable
private fun MonthYearWheels(
    months: ImmutableList<String>,
    years: ImmutableList<String>,
    monthIndex: Int,
    yearIndex: Int,
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
                .background(colors.blueBg)
                .border(1.dp, colors.blueText.copy(alpha = SELECTION_BORDER_ALPHA), RoundedCornerShape(CornerRadius.chip)),
        )

        Row(modifier = Modifier.fillMaxSize()) {
            // Both wheels wrap: اسفند rolls into فروردین, and the last year back to 1300.
            WheelColumn(items = months, selectedIndex = monthIndex, onSelected = onMonthIndex, modifier = Modifier.weight(1f))
            WheelColumn(items = years, selectedIndex = yearIndex, onSelected = onYearIndex, modifier = Modifier.weight(1f))
        }

        // Fades the rows away from the middle instead of tinting each one — same approach as the
        // day picker's wheel panel.
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

@PreviewRtlTheme
@Composable
private fun TaminJalaliMonthYearPickerBottomSheetPreview() {
    PreviewRtlThemeContent {
        MonthYearPickerContent(
            title = "انتخاب ماه و سال",
            onDismiss = {},
            onConfirm = { _, _ -> },
            initial = PersianDateFormatter.today().let { (year, month, _) -> year to month },
        )
    }
}
