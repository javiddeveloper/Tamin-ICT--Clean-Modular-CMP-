package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.calculateWagePension.ui.CalculateWagePensionPreviewData
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BarChartItem
import com.tamin.taminhamrah.ui.components.ChartScrollBehavior
import com.tamin.taminhamrah.ui.components.TaminBarChart
import com.tamin.taminhamrah.ui.components.rememberChartGridLines
import com.tamin.taminhamrah.ui.theme.ChartDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius as TaminCornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullSelectedBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullSelectedTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarFullTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialSelectedBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialSelectedTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialYearBottom
import com.tamin.taminhamrah.ui.theme.TaminHistoryBarPartialYearTop
import com.tamin.taminhamrah.ui.theme.TaminHistoryZeroText
import com.tamin.taminhamrah.ui.theme.TaminLightTextSecondary
import com.tamin.taminhamrah.ui.theme.TaminNavy700
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.calculate_wage_pension_chart_full_year
import taminx.core.core_ui.calculate_wage_pension_chart_partial_year
import taminx.core.core_ui.calculate_wage_pension_chart_tap_hint
import taminx.core.core_ui.calculate_wage_pension_history_days
import taminx.core.core_ui.calculate_wage_pension_history_days_unit
import taminx.core.core_ui.calculate_wage_pension_history_months
import taminx.core.core_ui.calculate_wage_pension_history_title
import taminx.core.core_ui.calculate_wage_pension_history_years
import taminx.core.core_ui.ic_history

private const val FullYearDays = 365
private const val MaxChartDays = 365f

@Composable
internal fun CalculateWagePensionHistoryCard(
    years: Int,
    months: Int,
    days: Int,
    totalDays: Int,
    chartItems: ImmutableList<WagePensionChartItemPR>,
    selectedIndex: Int?,
    onYearSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TaminCornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                // RTL: icon + title on the start (right), total-days chip on the end (left).
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_history),
                    contentDescription = null,
                    tint = colors.blueText,
                    modifier = Modifier.size(IconSize.medium),
                )
                Text(
                    text = stringResource(Res.string.calculate_wage_pension_history_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .background(colors.blueBg, RoundedCornerShape(TaminCornerRadius.max))
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                ) {
                    Text(
                        text = stringResource(
                            Res.string.calculate_wage_pension_history_days,
                            totalDays.toString().toPersianDigits(),
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.blueText,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                HistoryDurationChip(
                    text = stringResource(
                        Res.string.calculate_wage_pension_history_years,
                        years.toString().toPersianDigits(),
                    ),
                    modifier = Modifier.weight(1f),
                )
                HistoryDurationChip(
                    text = stringResource(
                        Res.string.calculate_wage_pension_history_months,
                        months.toString().toPersianDigits(),
                    ),
                    modifier = Modifier.weight(1f),
                )
                HistoryDurationChip(
                    text = stringResource(
                        Res.string.calculate_wage_pension_history_days_unit,
                        days.toString().toPersianDigits(),
                    ),
                    modifier = Modifier.weight(1f),
                )
            }

            if (chartItems.isNotEmpty()) {
                WagePensionBarChart(
                    items = chartItems,
                    selectedIndex = selectedIndex,
                    onYearSelected = onYearSelected,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    // RTL: legend on the start (right), tap hint on the end (left).
                    ChartLegendItem(
                        color = TaminHistoryBarFullBottom,
                        label = stringResource(Res.string.calculate_wage_pension_chart_full_year),
                    )
                    ChartLegendItem(
                        color = TaminHistoryBarPartialYearBottom,
                        label = stringResource(Res.string.calculate_wage_pension_chart_partial_year),
                    )
                    Text(
                        text = stringResource(Res.string.calculate_wage_pension_chart_tap_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryDurationChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Box(
        modifier = modifier
            .background(colors.blueBg, RoundedCornerShape(TaminCornerRadius.lg))
            .padding(vertical = Spacing.md),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ChartLegendItem(
    color: androidx.compose.ui.graphics.Color,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = LocalTaminColors.current.textMuted,
        )
        Box(
            modifier = Modifier
                .size(ChartDimens.legendDot)
                .background(color, CircleShape),
        )
    }
}

@Composable
private fun WagePensionBarChart(
    items: ImmutableList<WagePensionChartItemPR>,
    selectedIndex: Int?,
    onYearSelected: (Int) -> Unit,
) {
    val bars = remember(items, selectedIndex) {
        items.mapIndexed { index, item ->
            val isFull = item.sumYear >= FullYearDays
            val selected = selectedIndex == index
            BarChartItem(
                id = index.toString(),
                label = item.hisYear.toPersianDigits(),
                fraction = (item.sumYear / MaxChartDays).coerceIn(0f, 1f),
                fillTop = when {
                    selected -> if (isFull) TaminHistoryBarFullSelectedTop else TaminHistoryBarPartialSelectedTop
                    isFull -> TaminHistoryBarFullTop
                    else -> TaminHistoryBarPartialYearTop
                },
                fillBottom = when {
                    selected -> if (isFull) TaminHistoryBarFullSelectedBottom else TaminHistoryBarPartialSelectedBottom
                    isFull -> TaminHistoryBarFullBottom
                    else -> TaminHistoryBarPartialYearBottom
                },
                labelColor = when {
                    selected -> TaminNavy700
                    item.sumYear > 0 -> TaminLightTextSecondary
                    else -> TaminHistoryZeroText
                },
                pill = item.sumYear.toString().toPersianDigits().takeIf { selected && item.sumYear > 0 },
                labelBold = selected,
                enabled = item.sumYear > 0,
                isSelected = selected,
            )
        }.toImmutableList()
    }

    TaminBarChart(
        bars = bars,
        onBarClick = { id -> id.toIntOrNull()?.let(onYearSelected) },
        scrollBehavior = ChartScrollBehavior.Adaptive,
        animationKey = items.size,
        gridLines = rememberChartGridLines(),
    )
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionHistoryCardPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionHistoryCard(
            years = 7,
            months = 4,
            days = 0,
            totalDays = 2675,
            chartItems = CalculateWagePensionPreviewData.chartItems,
            selectedIndex = null,
            onYearSelected = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionHistoryCardDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        CalculateWagePensionHistoryCard(
            years = 2,
            months = 0,
            days = 326,
            totalDays = 1056,
            chartItems = CalculateWagePensionPreviewData.chartItems,
            selectedIndex = 2,
            onYearSelected = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
