package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemPR
import com.tamin.taminhamrah.ui.theme.ChartDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius as TaminCornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.calculate_wage_pension_chart_full_year
import taminx.core.core_ui.calculate_wage_pension_chart_months_12
import taminx.core.core_ui.calculate_wage_pension_chart_months_6
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
                        color = colors.greenText,
                        label = stringResource(Res.string.calculate_wage_pension_chart_full_year),
                    )
                    ChartLegendItem(
                        color = colors.orangeText,
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
    val colors = LocalTaminColors.current
    val fullYearLabel = stringResource(Res.string.calculate_wage_pension_chart_months_12)
    val halfYearLabel = stringResource(Res.string.calculate_wage_pension_chart_months_6)
    val fullYearBrush = Brush.verticalGradient(
        colors = listOf(colors.greenText.copy(alpha = 0.55f), colors.greenText),
    )
    val partialYearBrush = Brush.verticalGradient(
        colors = listOf(colors.orangeText.copy(alpha = 0.55f), colors.orangeText),
    )
    val barShape = RoundedCornerShape(
        topStart = ChartDimens.barCorner,
        topEnd = ChartDimens.barCorner,
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(ChartDimens.barChartHeight),
    ) {
        Column(
            modifier = Modifier
                .width(ChartDimens.yAxisLabelWidth)
                .fillMaxHeight()
                .padding(end = Spacing.xs),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = fullYearLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
            Text(
                text = halfYearLabel,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
                modifier = Modifier.padding(bottom = Spacing.xxxl),
            )
            Spacer(modifier = Modifier)
        }

        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val guideColor = colors.outerBorder
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val dashLength = Thickness.border.toPx() * 8f
                    val dash = PathEffect.dashPathEffect(floatArrayOf(dashLength, dashLength))
                    val y12 = size.height * 0.05f
                    val y6 = size.height * 0.5f
                    drawLine(
                        color = guideColor,
                        start = Offset(0f, y12),
                        end = Offset(size.width, y12),
                        strokeWidth = Thickness.border.toPx(),
                        pathEffect = dash,
                    )
                    drawLine(
                        color = guideColor,
                        start = Offset(0f, y6),
                        end = Offset(size.width, y6),
                        strokeWidth = Thickness.border.toPx(),
                        pathEffect = dash,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = ChartDimens.barGap,
                        alignment = Alignment.CenterHorizontally,
                    ),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    items.forEachIndexed { index, item ->
                        val isFull = item.sumYear >= FullYearDays
                        val fraction = (item.sumYear / MaxChartDays).coerceIn(0.08f, 1f)
                        val barBrush = if (isFull) fullYearBrush else partialYearBrush
                        val selected = selectedIndex == index

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(ChartDimens.barWidth)
                                .fillMaxHeight()
                                .clickable { onYearSelected(index) },
                            verticalArrangement = Arrangement.Bottom,
                        ) {
                            Text(
                                text = item.sumYear.toString().toPersianDigits(),
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textMuted,
                                modifier = Modifier.padding(bottom = Spacing.xs),
                            )
                            Box(
                                modifier = Modifier
                                    .width(ChartDimens.barWidth)
                                    .fillMaxHeight(fraction)
                                    .then(
                                        if (selected) {
                                            Modifier.border(
                                                Thickness.medium,
                                                colors.blueText,
                                                barShape,
                                            )
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .background(barBrush, barShape),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    space = ChartDimens.barGap,
                    alignment = Alignment.CenterHorizontally,
                ),
            ) {
                items.forEach { item ->
                    Text(
                        text = item.hisYear.toPersianDigits(),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(ChartDimens.barWidth),
                    )
                }
            }
        }
    }
}
