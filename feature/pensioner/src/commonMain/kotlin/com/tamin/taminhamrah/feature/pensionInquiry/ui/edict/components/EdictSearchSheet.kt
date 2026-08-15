package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.tamin.taminhamrah.ui.components.IconPosition
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.edict_search_change
import taminx.core.core_ui.edict_search_clear
import taminx.core.core_ui.edict_search_continue
import taminx.core.core_ui.edict_search_edict_note
import taminx.core.core_ui.edict_search_select_month
import taminx.core.core_ui.edict_search_selected_year
import taminx.core.core_ui.edict_search_title
import taminx.core.core_ui.edict_search_year_edicts

private val WHEEL_ROW_HEIGHT = 40.dp
private const val WHEEL_VISIBLE_ROWS = 3
private val WHEEL_HEIGHT = WHEEL_ROW_HEIGHT * WHEEL_VISIBLE_ROWS
private const val EDGE_FADE_STOP = 1f / WHEEL_VISIBLE_ROWS
private const val SELECTION_BORDER_ALPHA = 0.3f
private val FIRST_YEAR = 1350

// Only فروردین edicts are shown. مرداد may be added in a future iteration.
private val MONTH_CODES = listOf("01")

private enum class EdictSearchStep { Year, Month }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EdictSearchSheet(
    searchYear: String,
    searchMonth: String,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onApply: () -> Unit,
    onDismiss: () -> Unit,
    onClear: () -> Unit = {},
) {
    val taminColors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by remember { mutableStateOf(EdictSearchStep.Year) }

    // Auto-select فروردین whenever the year changes (only one month is ever available).
    LaunchedEffect(searchYear) {
        if (searchMonth != MONTH_CODES.first()) onMonthChanged(MONTH_CODES.first())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = taminColors.bgSurface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg)
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            // Header row: title (right/start in RTL) + clear button (left/end in RTL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = stringResource(Res.string.edict_search_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.textPrimary,
                )
                TaminText(
                    text = stringResource(Res.string.edict_search_clear),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = taminColors.blueText,
                    modifier = Modifier
                        .clip(RoundedCornerShape(CornerRadius.full))
                        .clickable(onClick = onClear)
                        .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                )
            }

            when (step) {
                EdictSearchStep.Year -> {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        YearWheelPicker(
                            modifier = Modifier.fillMaxWidth(),
                            selectedYear = searchYear,
                            onYearChanged = onYearChanged,
                        )
                    }
                    TaminFilledButton(
                        text = stringResource(Res.string.edict_search_continue),
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        iconPosition = IconPosition.End,
                        onClick = { step = EdictSearchStep.Month },
                        modifier = Modifier.fillMaxWidth(),
                        height = 52.dp,
                        shape = RoundedCornerShape(CornerRadius.md),
                    )
                }

                EdictSearchStep.Month -> {
                    SelectedYearCard(
                        year = searchYear,
                        onChangeYear = { step = EdictSearchStep.Year },
                    )

                    TaminText(
                        text = stringResource(Res.string.edict_search_year_edicts),
                        style = MaterialTheme.typography.labelMedium,
                        color = taminColors.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val monthName = remember { PersianDateFormatter.monthNames[0] }
                    val yearPersian = remember(searchYear) { searchYear }
                    EdictMonthOptionCard(
                        title = "$monthName $yearPersian",
                        subtitle = "حکم افزایش سالیانهٔ $yearPersian",
                        isSelected = searchMonth == MONTH_CODES.first(),
                        onClick = {
                            val newMonth =
                                if (searchMonth == MONTH_CODES.first()) "" else MONTH_CODES.first()
                            onMonthChanged(newMonth)
                        },
                    )

                    TaminText(
                        text = stringResource(Res.string.edict_search_edict_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textSecondary,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    val isMonthSelected = searchMonth.isNotEmpty()
                    val buttonText = if (isMonthSelected) {
                        "مشاهدهٔ حکم $monthName $yearPersian"
                    } else {
                        stringResource(Res.string.edict_search_select_month)
                    }
                    TaminFilledButton(
                        text = buttonText,
                        onClick = onApply,
                        modifier = Modifier.fillMaxWidth(),
                        height = 52.dp,
                        shape = RoundedCornerShape(CornerRadius.md),
                        enabled = isMonthSelected,
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedYearCard(
    year: String,
    onChangeYear: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.cardCompact))
            .border(1.dp, taminColors.border, RoundedCornerShape(CornerRadius.cardCompact))
            .background(taminColors.bgPage)
            .padding(Spacing.md),
    ) {
        // Right side (RTL start): label + year value
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            TaminText(
                text = stringResource(Res.string.edict_search_selected_year),
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textSecondary,
            )
            TaminText(
                text = year,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = taminColors.textPrimary,
            )
        }
        // Left side (RTL end): change button
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(CornerRadius.full))
                .clickable(onClick = onChangeYear)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = taminColors.blueText,
                modifier = Modifier.size(14.dp),
            )
            TaminText(
                text = stringResource(Res.string.edict_search_change),
                style = MaterialTheme.typography.labelMedium,
                color = taminColors.blueText,
            )
        }
    }
}

@Composable
private fun EdictMonthOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val borderColor = if (isSelected) taminColors.blueText else taminColors.border
    val bgColor = if (isSelected) taminColors.blueBg else taminColors.bgPage
    val titleColor = if (isSelected) taminColors.blueText else taminColors.textPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.cardCompact))
            .border(1.dp, borderColor, RoundedCornerShape(CornerRadius.cardCompact))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Start (right in RTL): title + subtitle
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            TaminText(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = titleColor,
            )
            TaminText(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = taminColors.textSecondary,
            )
        }
        // End (left in RTL): checkmark or placeholder
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = taminColors.blueText,
                modifier = Modifier.size(22.dp),
            )
        } else {
            Spacer(Modifier.size(22.dp))
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
    val years = remember(lastYear) {
        (FIRST_YEAR..lastYear).map { it.toString() }
    }
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
                .height(WHEEL_ROW_HEIGHT)
                .padding(horizontal = Spacing.sm)
                .clip(RoundedCornerShape(CornerRadius.chip))
                .border(
                    1.dp,
                    taminColors.border,
                    RoundedCornerShape(CornerRadius.chip),
                ),
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
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) taminColors.blueText else taminColors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .height(WHEEL_ROW_HEIGHT)
                        .background(
                            color = if (isSelected) taminColors.chipBg else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            if (isSelected) taminColors.blueText else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(top = 10.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to taminColors.bgPage,
                        EDGE_FADE_STOP to Color.Transparent,
                        1f - EDGE_FADE_STOP to Color.Transparent,
                        1f to taminColors.bgPage,
                    ),
                ),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EdictSearchSheetYearStepPreview() {
    PreviewRtlThemeContent {
        EdictSearchSheet(
            searchYear = "1402",
            searchMonth = "01",
            onYearChanged = {},
            onMonthChanged = {},
            onApply = {},
            onDismiss = {},
        )
    }
}
