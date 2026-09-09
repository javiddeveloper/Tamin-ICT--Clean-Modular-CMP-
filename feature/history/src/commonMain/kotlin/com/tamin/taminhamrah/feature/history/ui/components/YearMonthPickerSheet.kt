package com.tamin.taminhamrah.feature.history.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.tamin.taminhamrah.feature.history.ui.HistoryDimens
import com.tamin.taminhamrah.feature.history.ui.model.YearPickerRowPR
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminSearchField
import com.tamin.taminhamrah.ui.theme.Duration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonEnd
import com.tamin.taminhamrah.ui.theme.TaminHistoryButtonStart
import com.tamin.taminhamrah.ui.theme.TaminHistoryPickedRowSub
import com.tamin.taminhamrah.ui.theme.TaminHistoryZeroText
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.history_picker_all_years
import taminx.core.core_ui.history_picker_empty
import taminx.core.core_ui.history_picker_hint
import taminx.core.core_ui.history_picker_month
import taminx.core.core_ui.history_picker_month_optional
import taminx.core.core_ui.history_picker_search
import taminx.core.core_ui.history_picker_title
import taminx.core.core_ui.history_picker_whole_year
import taminx.core.core_ui.history_picker_year
import taminx.core.core_ui.history_picker_year_order

/**
 * «انتخاب سال و ماه» — reaching any year of a long career without scrolling the chip strip to it.
 *
 * The strip is the fast path for the last few years; this is the one for the other thirty. Nothing
 * here changes the page until [onApply]: a person lands on a year, reads its months, and may change
 * their mind, and applying on each tap would reload the chart under them three times on the way to
 * the month they actually wanted. The staged pick lives in the state rather than here so that a
 * rotation mid-choice does not throw it away.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearMonthPickerSheet(
    query: String,
    yearRows: ImmutableList<YearPickerRowPR>,
    monthRows: ImmutableList<YearPickerRowPR>,
    wholeYearSelected: Boolean,
    applyLabel: String,
    applyEnabled: Boolean,
    yearRange: String,
    onQueryChange: (String) -> Unit,
    onYearClick: (String) -> Unit,
    onMonthClick: (Int?) -> Unit,
    onApply: () -> Unit,
    onAllYears: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    // Same rule the year sheet uses: run the hide to its end, then report, or the sheet is taken
    // out from under its own animation and vanishes on the spot.
    val dismiss: () -> Unit = {
        scope.launch { sheetState.hide() }
            .invokeOnCompletion { if (!sheetState.isVisible) onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = dismiss,
        sheetState = sheetState,
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(
            topStart = HistoryDimens.sheetCorner,
            topEnd = HistoryDimens.sheetCorner,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HistoryDimens.sheetPaddingH)
                .padding(bottom = HistoryDimens.sheetPaddingBottom)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(HistoryDimens.pickerColumnGap),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.history_picker_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.textPrimary,
                )
                Text(
                    text = stringResource(Res.string.history_picker_hint),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }

            // The shared field, wearing the sheet's ground instead of the hero's glass.
            TaminSearchField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = stringResource(Res.string.history_picker_search),
                searchIcon = Icons.Outlined.Search,
                containerColor = colors.bgSurface,
                borderColor = colors.border,
                contentColor = colors.textPrimary,
                placeholderColor = colors.textMuted,
                iconColor = colors.textMuted,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                keyboardType = KeyboardType.Number,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(HistoryDimens.pickerColumnGap),
            ) {
                // The design gives the year column the wider share: its rows carry a four-digit
                // number and a day count, the month rows a short name.
                PickerColumn(
                    title = stringResource(Res.string.history_picker_year),
                    caption = stringResource(Res.string.history_picker_year_order),
                    modifier = Modifier.weight(YEAR_COLUMN_WEIGHT),
                ) {
                    if (yearRows.isEmpty()) {
                        Text(
                            text = stringResource(Res.string.history_picker_empty),
                            style = MaterialTheme.typography.labelMedium,
                            color = colors.textMuted,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = HistoryDimens.pickerEmptyPaddingV),
                        )
                    } else {
                        PickerRowList(
                            rows = yearRows,
                            height = HistoryDimens.pickerYearListHeight,
                            numericLabel = true,
                            onClick = onYearClick,
                        )
                    }
                }

                PickerColumn(
                    title = stringResource(Res.string.history_picker_month),
                    caption = stringResource(Res.string.history_picker_month_optional),
                    modifier = Modifier.weight(MONTH_COLUMN_WEIGHT),
                ) {
                    // «کل سال» is the column's default answer, not one of the twelve — it sits
                    // above them, and is only offered once a year is staged for it to mean.
                    PickerRow(
                        label = stringResource(Res.string.history_picker_whole_year),
                        caption = null,
                        selected = wholeYearSelected,
                        enabled = monthRows.isNotEmpty(),
                        numericLabel = false,
                        centered = true,
                        onClick = { onMonthClick(null) },
                    )
                    Box(modifier = Modifier.height(HistoryDimens.pickerRowGap))
                    PickerRowList(
                        rows = monthRows,
                        height = HistoryDimens.pickerMonthListHeight,
                        numericLabel = false,
                        onClick = { id -> id.toIntOrNull()?.let(onMonthClick) },
                    )
                }
            }

            ApplyButton(label = applyLabel, enabled = applyEnabled, onClick = onApply)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(HistoryDimens.pickerAllYearsCorner))
                    .background(colors.bgSurface)
                    .border(
                        HistoryDimens.hairline,
                        colors.border,
                        RoundedCornerShape(HistoryDimens.pickerAllYearsCorner),
                    )
                    .clickable(onClick = onAllYears)
                    .padding(
                        horizontal = HistoryDimens.pickerAllYearsPaddingH,
                        vertical = HistoryDimens.pickerAllYearsPaddingV,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.history_picker_all_years),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.textPrimary,
                )
                NumericText(
                    text = yearRange,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textMuted,
                )
            }
        }
    }
}

/** One of the two columns: a heading, a note, and whatever the column lists. */
@Composable
private fun PickerColumn(
    title: String,
    caption: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(HistoryDimens.pickerCardCorner)
    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.bgSurface)
            .border(HistoryDimens.hairline, colors.border, shape)
            .padding(
                start = HistoryDimens.pickerCardPaddingH,
                end = HistoryDimens.pickerCardPaddingH,
                top = HistoryDimens.pickerCardPaddingTop,
                bottom = HistoryDimens.pickerCardPaddingBottom,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = HistoryDimens.pickerHeaderPaddingH)
                .padding(bottom = HistoryDimens.pickerHeaderPaddingBottom),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.ExtraBold,
                color = colors.textPrimary,
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
        content()
    }
}

/**
 * A column's rows, capped at [height] and scrolling inside it.
 *
 * Lazy and keyed: a long career is forty years and six of them are ever on screen, and a keyed row
 * survives a keystroke in the search field without being recycled onto a different year.
 */
@Composable
private fun PickerRowList(
    rows: ImmutableList<YearPickerRowPR>,
    height: Dp,
    numericLabel: Boolean,
    onClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.height(height),
        verticalArrangement = Arrangement.spacedBy(HistoryDimens.pickerRowGap),
    ) {
        items(rows, key = { it.id }) { row ->
            PickerRow(
                label = row.label,
                caption = row.caption,
                selected = row.selected,
                enabled = row.enabled,
                numericLabel = numericLabel,
                centered = false,
                // Captures the id, not the row: a String is the same value across recompositions,
                // so the row keeps its skippability while the list around it re-folds.
                onClick = remember(row.id, onClick) { { onClick(row.id) } },
            )
        }
    }
}

/**
 * One pickable row, in the design's three states.
 *
 * The selection crossfades rather than snapping, and every part of it is held as State and read in
 * a draw or color lambda — these rows are tapped repeatedly while a person hunts for a year, and a
 * selection that recomposed the list would recompose every row in it.
 */
@Composable
private fun PickerRow(
    label: String,
    caption: String?,
    selected: Boolean,
    enabled: Boolean,
    numericLabel: Boolean,
    centered: Boolean,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(HistoryDimens.pickerRowCorner) }
    val pickedBrush = remember {
        Brush.linearGradient(listOf(TaminHistoryButtonStart, TaminHistoryButtonEnd))
    }

    val selection = animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(Duration.fast),
        label = "pickerSelection",
    )
    val ground = colors.bgPage
    val border = animateColorAsState(
        targetValue = when {
            selected -> TaminHistoryButtonEnd
            enabled -> colors.border
            else -> Color.Transparent
        },
        animationSpec = tween(Duration.fast),
        label = "pickerBorder",
    )
    val labelColor = animateColorAsState(
        targetValue = when {
            selected -> Color.White
            enabled -> colors.textPrimary
            else -> TaminHistoryZeroText
        },
        animationSpec = tween(Duration.fast),
        label = "pickerLabel",
    )
    val captionColor = animateColorAsState(
        targetValue = when {
            selected -> TaminHistoryPickedRowSub
            enabled -> colors.blueText
            else -> TaminHistoryZeroText
        },
        animationSpec = tween(Duration.fast),
        label = "pickerCaption",
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBehind {
                // A row with nothing recorded has no ground at all — the design leaves it bare so
                // the gaps read as gaps rather than as another kind of button.
                if (enabled) drawRect(ground)
                drawRect(brush = pickedBrush, alpha = selection.value)
            }
            .border(HistoryDimens.hairline, border.value, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(
                horizontal = HistoryDimens.pickerRowPaddingH,
                vertical = HistoryDimens.pickerRowPaddingV,
            ),
        horizontalArrangement = if (centered) Arrangement.Center else Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val labelStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        if (numericLabel) {
            NumericText(text = label, style = labelStyle, color = labelColor.value)
        } else {
            Text(text = label, style = labelStyle, color = labelColor.value, maxLines = 1)
        }
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                color = captionColor.value,
                maxLines = 1,
            )
        }
    }
}

/**
 * The one control that changes the page.
 *
 * Its label says what it will do — «نمایش کل سال ۱۴۰۲», «نمایش مرداد ۱۴۰۲» — rather than «تأیید»,
 * because the sheet stages a choice silently and this is the only place it is stated back.
 */
@Composable
private fun ApplyButton(label: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = LocalTaminColors.current
    val shape = remember { RoundedCornerShape(HistoryDimens.pickerApplyCorner) }
    val readyBrush = remember {
        Brush.linearGradient(listOf(TaminHistoryButtonStart, TaminHistoryButtonEnd))
    }
    val ready = animateFloatAsState(
        targetValue = if (enabled) 1f else 0f,
        animationSpec = tween(Duration.normal),
        label = "applyReady",
    )
    val labelColor = animateColorAsState(
        targetValue = if (enabled) Color.White else colors.textMuted,
        animationSpec = tween(Duration.normal),
        label = "applyLabel",
    )
    val idle = colors.bgPage

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .drawBehind {
                drawRect(idle)
                drawRect(brush = readyBrush, alpha = ready.value)
            }
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = HistoryDimens.pickerApplyPaddingV),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.ExtraBold,
            color = labelColor.value,
        )
    }
}

/** The design's `1.02fr 1fr` split between the year and month columns. */
private const val YEAR_COLUMN_WEIGHT = 1.02f
private const val MONTH_COLUMN_WEIGHT = 1f
