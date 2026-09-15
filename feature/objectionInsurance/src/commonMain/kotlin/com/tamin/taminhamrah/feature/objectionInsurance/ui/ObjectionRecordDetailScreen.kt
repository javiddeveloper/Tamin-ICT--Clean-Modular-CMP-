package com.tamin.taminhamrah.feature.objectionInsurance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionDelta
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionMonthBarPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionMonthRowPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.contract.ObjectionSeasonGroupPR
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildDetailDelta
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildMonthBars
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.buildSeasonGroups
import com.tamin.taminhamrah.feature.objectionInsurance.ui.mapper.registeredMonthValues
import com.tamin.taminhamrah.feature.objectionInsurance.ui.preview.previewHistoryRecord
import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialMonthBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialMonthTop
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.objection_insurance_chart_registered_badge
import taminx.core.core_ui.objection_insurance_chart_title
import taminx.core.core_ui.objection_insurance_delta_added
import taminx.core.core_ui.objection_insurance_delta_none
import taminx.core.core_ui.objection_insurance_delta_reduced
import taminx.core.core_ui.objection_insurance_detail_clear_draft_button
import taminx.core.core_ui.objection_insurance_detail_save_button
import taminx.core.core_ui.objection_insurance_detail_title
import taminx.core.core_ui.objection_insurance_detail_validation_error
import taminx.core.core_ui.objection_insurance_days_count
import taminx.core.core_ui.objection_insurance_legend_added
import taminx.core.core_ui.objection_insurance_legend_full
import taminx.core.core_ui.objection_insurance_legend_partial
import taminx.core.core_ui.objection_insurance_month_not_registered
import taminx.core.core_ui.objection_insurance_table_header_declared
import taminx.core.core_ui.objection_insurance_table_header_month
import taminx.core.core_ui.objection_insurance_table_header_registered

private val ChartHeight = 90.dp
private val BarCorner = RoundedCornerShape(5.dp)
private val TableCorner = RoundedCornerShape(CornerRadius.card)

/**
 * The per-record editor — every month is editable at once (no tap-to-focus), matching the
 * product's own design: a two-tone chart (registered vs. what you're adding) above a season-
 * grouped table of always-visible inputs. A full screen, not a bottom sheet — the design gives it
 * its own hero header with a real back affordance, reached from the year grid or the workshop
 * picker and closed back to the year list, never to the app below it.
 */
@Composable
fun ObjectionRecordDetailScreen(
    record: ObjectionInsuranceHistoryPR,
    seasonGroups: ImmutableList<ObjectionSeasonGroupPR>,
    chartBars: ImmutableList<ObjectionMonthBarPR>,
    delta: ObjectionDelta,
    showValidationError: Boolean,
    hasDraft: Boolean,
    onMonthValueChanged: (Int, String) -> Unit,
    onClearDraftClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val registeredTotal = remember(record) { record.registeredMonthValues().sum() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.objection_insurance_detail_title),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = onBack,
                        bordered = true,
                    )
                },
            ) {
                RecordInfoCard(record)
            }
        },
        bottomBar = {
            DetailFooter(
                showValidationError = showValidationError,
                hasDraft = hasDraft,
                onClearDraftClicked = onClearDraftClicked,
                onSaveClicked = onSaveClicked,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .verticalScroll(rememberScrollState())
                .padding(
                    top = innerPadding.calculateTopPadding() + Spacing.md,
                    bottom = innerPadding.calculateBottomPadding() + Spacing.lg,
                    start = Spacing.page,
                    end = Spacing.page,
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ChartCard(bars = chartBars, registeredTotal = registeredTotal, delta = delta)
            MonthTableCard(seasonGroups = seasonGroups, onValueChanged = onMonthValueChanged)
        }
    }
}

@Composable
private fun RecordInfoCard(record: ObjectionInsuranceHistoryPR) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.md)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.onGradient.copy(alpha = 0.1f))
            .padding(vertical = Spacing.smd),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NumericText(
            text = record.year,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.onGradient,
        )
        Box(
            modifier = Modifier
                .padding(vertical = Spacing.xs)
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.onGradient.copy(alpha = 0.18f)),
        )
        TaminText(
            text = record.workshopName,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = colors.onGradient,
        )
        if (record.historyTypeName.isNotBlank()) {
            TaminText(
                text = record.historyTypeName,
                style = MaterialTheme.typography.labelSmall,
                color = colors.onGradient.copy(alpha = 0.75f),
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun ChartCard(bars: ImmutableList<ObjectionMonthBarPR>, registeredTotal: Int, delta: ObjectionDelta) {
    val colors = LocalTaminColors.current
    val fullBrush = remember { Brush.verticalGradient(listOf(TaminHistoryBarFullTop, TaminHistoryBarFullBottom)) }
    val partialBrush = remember {
        Brush.verticalGradient(listOf(TaminHistoryBarPartialMonthTop, TaminHistoryBarPartialMonthBottom))
    }
    val addedBrush = remember { Brush.verticalGradient(listOf(colors.blueBorder, colors.blueText)) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(Brush.verticalGradient(listOf(colors.historyPanelStart, colors.historyPanelEnd)))
            .border(1.dp, colors.historyPanelBorder, RoundedCornerShape(CornerRadius.lg))
            .padding(Spacing.smd),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TaminText(
                text = stringResource(Res.string.objection_insurance_chart_title),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = colors.textPrimary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Pill(
                    text = stringResource(
                        Res.string.objection_insurance_chart_registered_badge,
                        registeredTotal.toString().toPersianDigits(),
                    ),
                    background = colors.bgSurface,
                    contentColor = colors.textTertiary,
                )
                val (deltaText, deltaColor, deltaBg) = when (delta) {
                    ObjectionDelta.None -> Triple(stringResource(Res.string.objection_insurance_delta_none), colors.textTertiary, colors.bgSurface)
                    is ObjectionDelta.Added -> Triple(
                        stringResource(Res.string.objection_insurance_delta_added, delta.days.toString().toPersianDigits()),
                        colors.greenText,
                        colors.greenBg,
                    )
                    is ObjectionDelta.Reduced -> Triple(
                        stringResource(Res.string.objection_insurance_delta_reduced, delta.days.toString().toPersianDigits()),
                        colors.dangerText,
                        colors.dangerBg,
                    )
                }
                Pill(text = deltaText, background = deltaBg, contentColor = deltaColor)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().height(ChartHeight),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            bars.forEach { bar ->
                val recHeight = ChartHeight * bar.recFraction.coerceIn(0f, 1f)
                val extraHeight = ChartHeight * bar.extraFraction.coerceIn(0f, 1f)
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    if (bar.extraFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(extraHeight)
                                .clip(BarCorner)
                                .background(addedBrush),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(recHeight)
                            .clip(BarCorner)
                            .background(if (bar.isFull) fullBrush else partialBrush),
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            (1..MONTHS_IN_YEAR).forEach { month ->
                NumericText(
                    text = month.toString().toPersianDigits(),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp , textAlign = TextAlign.Center),
                    color = colors.textMuted,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Spacing.sm)
                .clip(RoundedCornerShape(CornerRadius.md))
                .background(colors.bgSurface)
                .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            LegendItem(brush = fullBrush, label = stringResource(Res.string.objection_insurance_legend_full))
            LegendItem(brush = partialBrush, label = stringResource(Res.string.objection_insurance_legend_partial))
            LegendItem(brush = addedBrush, label = stringResource(Res.string.objection_insurance_legend_added))
        }
    }
}

@Composable
private fun Pill(text: String, background: Color, contentColor: Color) {
    TaminText(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
        color = contentColor,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

@Composable
private fun LegendItem(brush: Brush, label: String) {
    val colors = LocalTaminColors.current
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Box(modifier = Modifier.size(9.dp).clip(RoundedCornerShape(3.dp)).background(brush))
        TaminText(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.5.sp),
            color = colors.textTertiary,
        )
    }
}

@Composable
private fun MonthTableCard(seasonGroups: ImmutableList<ObjectionSeasonGroupPR>, onValueChanged: (Int, String) -> Unit) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(TableCorner)
            .background(colors.bgSurface)
            .border(1.dp, colors.border, TableCorner),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.bgPage)
                .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
        ) {
            TaminText(
                text = stringResource(Res.string.objection_insurance_table_header_month),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                color = colors.textTertiary,
                modifier = Modifier.weight(1f),
            )
            TaminText(
                text = stringResource(Res.string.objection_insurance_table_header_registered),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                color = colors.textTertiary,
                modifier = Modifier.width(RegisteredColumnWidth),
                textAlign = TextAlign.Center,
            )
            TaminText(
                text = stringResource(Res.string.objection_insurance_table_header_declared),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, fontSize = 9.sp),
                color = colors.blueText,
                modifier = Modifier.width(DeclaredColumnWidth),
                textAlign = TextAlign.Center,
            )
        }

        seasonGroups.forEach { season ->
            TaminText(
                text = season.seasonLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.5.sp),
                color = colors.textMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgPage.copy(alpha = 0.5f))
                    .padding(horizontal = Spacing.smd, vertical = 4.dp),
            )
            season.rows.forEach { row ->
                MonthTableRow(row = row, onValueChanged = { value -> onValueChanged(row.index, value) })
            }
        }
    }
}

@Composable
private fun MonthTableRow(row: ObjectionMonthRowPR, onValueChanged: (String) -> Unit) {
    val colors = LocalTaminColors.current
    val dividerColor = colors.divider
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = 0.6.dp.toPx()
                drawLine(
                    color = dividerColor,
                    start = Offset(0f, size.height - stroke / 2),
                    end = Offset(size.width, size.height - stroke / 2),
                    strokeWidth = stroke,
                )
            }
            .padding(horizontal = Spacing.smd, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = row.label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        NumericText(
            text = if (row.isRegistered) {
                stringResource(Res.string.objection_insurance_days_count, row.registeredDays.toString().toPersianDigits())
            } else {
                stringResource(Res.string.objection_insurance_month_not_registered)
            },
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp, textAlign = TextAlign.Center),
            color = if (row.isRegistered) colors.textSecondary else colors.textMuted,
            modifier = Modifier.width(RegisteredColumnWidth),
        )
        Box(modifier = Modifier.width(DeclaredColumnWidth), contentAlignment = Alignment.Center) {
            MonthValueInput(value = row.value, onValueChange = onValueChanged)
        }
    }
}

@Composable
private fun MonthValueInput(value: String, onValueChange: (String) -> Unit) {
    val colors = LocalTaminColors.current
    val filled = value.isNotBlank()
    val borderColor = if (filled) colors.blueText else colors.border
    val background = if (filled) colors.blueBg else colors.bgPage

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(
            color = colors.blueText,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
        ),
        cursorBrush = Brush.verticalGradient(listOf(colors.blueText, colors.blueText)),
        modifier = Modifier
            .width(InputWidth)
            .height(InputHeight)
            .clip(RoundedCornerShape(CornerRadius.sm))
            .background(background)
            .border(1.4.dp, borderColor, RoundedCornerShape(CornerRadius.sm)),
        decorationBox = { innerTextField ->
            Box(modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp), contentAlignment = Alignment.Center) {
                if (value.isEmpty()) {
                    TaminText(text = "—", style = MaterialTheme.typography.labelMedium, color = colors.textMuted)
                }
                innerTextField()
            }
        },
    )
}

@Composable
private fun DetailFooter(
    showValidationError: Boolean,
    hasDraft: Boolean,
    onClearDraftClicked: () -> Unit,
    onSaveClicked: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.bgSurface)
            .padding(horizontal = Spacing.page, vertical = Spacing.sm)
            .navigationBarsPadding()
            .imePadding(),
    ) {
        if (showValidationError) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = Spacing.sm)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(colors.dangerBg)
                    .padding(Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = colors.dangerText,
                    modifier = Modifier.size(16.dp),
                )
                TaminText(
                    text = stringResource(Res.string.objection_insurance_detail_validation_error),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.dangerText,
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            if (hasDraft) {
                TaminOutlinedButton(
                    text = stringResource(Res.string.objection_insurance_detail_clear_draft_button),
                    onClick = onClearDraftClicked,
                    modifier = Modifier.weight(1f),
                )
            }
            TaminFilledButton(
                text = stringResource(Res.string.objection_insurance_detail_save_button),
                onClick = onSaveClicked,
                modifier = Modifier.weight(1f),
                background = colors.buttonGradient,
            )
        }
    }
}

private const val MONTHS_IN_YEAR = 12
private val RegisteredColumnWidth = 74.dp
private val DeclaredColumnWidth = 66.dp
private val InputWidth = 62.dp
private val InputHeight = 36.dp

@PreviewRtlTheme
@Composable
private fun ObjectionRecordDetailPreview() {
    val record = previewHistoryRecord(year = "1403", monthDays = List(12) { 20 })
    val draft = mapOf(0 to "28", 1 to "25")
    PreviewRtlThemeContent {
        ObjectionRecordDetailScreen(
            record = record,
            seasonGroups = buildSeasonGroups(record, draft),
            chartBars = buildMonthBars(record, draft),
            delta = buildDetailDelta(record, draft),
            showValidationError = false,
            hasDraft = true,
            onMonthValueChanged = { _, _ -> },
            onClearDraftClicked = {},
            onSaveClicked = {},
            onBack = {},
        )
    }
}
