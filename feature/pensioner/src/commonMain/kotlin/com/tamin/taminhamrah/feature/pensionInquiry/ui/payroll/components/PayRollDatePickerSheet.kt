package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.payroll_date_picker_confirm
import taminx.core.core_ui.payroll_date_picker_note
import taminx.core.core_ui.payroll_date_picker_title

private val WHEEL_ROW_HEIGHT = 40.dp
private const val WHEEL_VISIBLE_ROWS = 5
private val WHEEL_HEIGHT = WHEEL_ROW_HEIGHT * WHEEL_VISIBLE_ROWS
private const val EDGE_FADE_STOP = 1f / WHEEL_VISIBLE_ROWS
private val FIRST_YEAR = 1350

/** "انتخاب ماه و سال" — a year wheel beside a selectable 12-month list, unlike Edict's
 * فروردین-only picker: payroll data is available for every month. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayRollDatePickerSheet(
    searchYear: String,
    searchMonth: String,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            TaminText(
                text = stringResource(Res.string.payroll_date_picker_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(CornerRadius.cardCompact))
                        .background(color = taminColors.bgSurface)
                        .border(1.dp, taminColors.border, RoundedCornerShape(CornerRadius.cardCompact))
                        .padding(vertical = Spacing.lg),
                    contentAlignment = Alignment.Center,
                ) {
                    YearWheelPicker(
                        modifier = Modifier.fillMaxWidth(),
                        selectedYear = searchYear,
                        onYearChanged = onYearChanged,
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(CornerRadius.cardCompact))
                        .background(color = taminColors.bgSurface)
                        .border(1.dp, taminColors.border, RoundedCornerShape(CornerRadius.cardCompact)),
                ) {
                    MonthList(
                        selectedMonth = searchMonth,
                        onMonthChanged = onMonthChanged,
                    )
                }
            }

            TaminText(
                text = stringResource(Res.string.payroll_date_picker_note),
                style = MaterialTheme.typography.bodySmall,
                color = taminColors.textSecondary,
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
            )

            TaminFilledButton(
                text = stringResource(Res.string.payroll_date_picker_confirm),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                enabled = searchYear.isNotEmpty() && searchMonth.isNotEmpty(),
            )
        }
    }
}

@Composable
private fun MonthList(
    selectedMonth: String,
    onMonthChanged: (String) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val months = PersianDateFormatter.monthNames

    LazyColumn(
        modifier = Modifier.height(WHEEL_HEIGHT),
        contentPadding = PaddingValues(vertical = Spacing.xs),
    ) {
        itemsIndexed(months) { index, name ->
            val code = (index + 1).toString().padStart(2, '0')
            val isSelected = code == selectedMonth
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onMonthChanged(code) }
                    .background(if (isSelected) taminColors.chipBg else Color.Transparent)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.Center,
            ) {
                TaminText(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) taminColors.blueText else taminColors.textPrimary,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Suppress("FrequentlyChangingValue")
@Composable
private fun YearWheelPicker(
    selectedYear: String,
    onYearChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val lastYear = remember { PersianDateFormatter.currentJalaliYear() }
    val years = remember(lastYear) { (FIRST_YEAR..lastYear).map { it.toString() } }
    val initialIndex = remember(selectedYear) {
        val idx = years.indexOfFirst { it == selectedYear }
        if (idx >= 0) idx else (years.size - 1).coerceAtLeast(0)
    }
    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    LaunchedEffect(state, years) {
        snapshotFlow { state.firstVisibleItemIndex }
            .distinctUntilChanged()
            .filter { it in years.indices }
            .collect { idx ->
                val persianYear = years[idx]
                val asciiYear = buildString {
                    persianYear.forEach { c ->
                        append(if (c in '۰'..'۹') '0' + (c - '۰') else c)
                    }
                }
                onYearChanged(asciiYear)
            }
    }

    Box(
        modifier = modifier
            .height(WHEEL_HEIGHT)
            .clip(RoundedCornerShape(CornerRadius.cardCompact)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(WHEEL_ROW_HEIGHT)
                .padding(horizontal = Spacing.sm)
                .clip(RoundedCornerShape(CornerRadius.chip))
                .background(color = taminColors.chipBg)
                .border(1.dp, taminColors.blueText, RoundedCornerShape(CornerRadius.chip)),
        )

        LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = WHEEL_ROW_HEIGHT * (WHEEL_VISIBLE_ROWS / 2)),
            flingBehavior = rememberSnapFlingBehavior(state),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            itemsIndexed(years, key = { index, _ -> index }) { index, label ->
                val isSelected = index == state.firstVisibleItemIndex
                TaminText(
                    text = label.toPersianDigits(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) taminColors.textPrimary else taminColors.textMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(WHEEL_ROW_HEIGHT)
                        .padding(top = 10.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to taminColors.bgSurface,
                        EDGE_FADE_STOP to Color.Transparent,
                        1f - EDGE_FADE_STOP to Color.Transparent,
                        1f to taminColors.bgSurface,
                    ),
                ),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollDatePickerSheetPreview() {
    PreviewRtlThemeContent {
        PayRollDatePickerSheet(
            searchYear = "1404",
            searchMonth = "05",
            onYearChanged = {},
            onMonthChanged = {},
            onConfirm = {},
            onDismiss = {},
        )
    }
}
