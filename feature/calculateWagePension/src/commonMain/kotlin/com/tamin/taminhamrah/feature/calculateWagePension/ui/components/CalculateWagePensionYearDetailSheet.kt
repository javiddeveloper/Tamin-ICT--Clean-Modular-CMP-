package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.model.calculateWagePension.WagePensionChartItemPR
import com.tamin.taminhamrah.feature.calculateWagePension.ui.CalculateWagePensionPreviewData
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.ChartDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_close
import taminx.core.core_ui.calculate_wage_pension_chart_full_year
import taminx.core.core_ui.calculate_wage_pension_chart_partial_year
import taminx.core.core_ui.calculate_wage_pension_history_days
import taminx.core.core_ui.calculate_wage_pension_month_leap
import taminx.core.core_ui.calculate_wage_pension_season_autumn
import taminx.core.core_ui.calculate_wage_pension_season_spring
import taminx.core.core_ui.calculate_wage_pension_season_summer
import taminx.core.core_ui.calculate_wage_pension_season_winter
import taminx.core.core_ui.calculate_wage_pension_year_detail_title
import taminx.core.core_ui.jalali_months

private const val FullYearDays = 365
private const val EsfandIndex = 11
private const val JalaliEsfand = 12

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CalculateWagePensionYearDetailSheet(
    item: WagePensionChartItemPR,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val dismissSheet: () -> Unit = {
        scope.launch {
            sheetState.hide()
        }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bgSurface,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        YearDetailContent(
            item = item,
            onDismiss = dismissSheet,
            modifier = Modifier.navigationBarsPadding(),
        )
    }
}

@Composable
internal fun YearDetailContent(
    item: WagePensionChartItemPR,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val monthNames = stringArrayResource(Res.array.jalali_months)
    val year = item.hisYear.filter { it.isDigit() }.toIntOrNull()
    val isLeap = year != null && PersianDateFormatter.daysInMonth(year, JalaliEsfand) == 30
    val isFullYear = item.sumYear >= FullYearDays
    val seasons = listOf(
        Season(
            title = stringResource(Res.string.calculate_wage_pension_season_spring),
            tint = colors.greenText,
            monthStart = 0,
        ),
        Season(
            title = stringResource(Res.string.calculate_wage_pension_season_summer),
            tint = colors.orangeText,
            monthStart = 3,
        ),
        Season(
            title = stringResource(Res.string.calculate_wage_pension_season_autumn),
            tint = colors.fuchsiaBlue,
            monthStart = 6,
        ),
        Season(
            title = stringResource(Res.string.calculate_wage_pension_season_winter),
            tint = colors.blueText,
            monthStart = 9,
        ),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page)
            .padding(bottom = Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = stringResource(
                    Res.string.calculate_wage_pension_year_detail_title,
                    item.hisYear.toPersianDigits(),
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            YearStatusChip(
                text = stringResource(
                    if (isFullYear) Res.string.calculate_wage_pension_chart_full_year
                    else Res.string.calculate_wage_pension_chart_partial_year,
                ),
                containerColor = if (isFullYear) colors.greenBg else colors.orangeBg,
                contentColor = if (isFullYear) colors.greenText else colors.orangeText,
                borderColor = if (isFullYear) colors.greenBg else colors.orangeBg,
            )
            YearStatusChip(
                text = stringResource(
                    Res.string.calculate_wage_pension_history_days,
                    item.sumYear.toString().toPersianDigits(),
                ),
                containerColor = colors.bgSurface,
                contentColor = colors.textSecondary,
                borderColor = colors.border,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            seasons.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    row.forEach { season ->
                        SeasonCard(
                            season = season,
                            monthNames = monthNames,
                            days = item.months,
                            isLeap = isLeap,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        TaminFilledButton(
            text = stringResource(Res.string.action_close),
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun YearStatusChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Medium,
        color = contentColor,
        modifier = Modifier
            .border(Thickness.border, borderColor, RoundedCornerShape(CornerRadius.max))
            .background(containerColor, RoundedCornerShape(CornerRadius.max))
            .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
    )
}

@Composable
private fun SeasonCard(
    season: Season,
    monthNames: List<String>,
    days: List<Int>,
    isLeap: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.listRow))
            .background(colors.bgSurface, RoundedCornerShape(CornerRadius.listRow))
            .padding(Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Text(
                text = season.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
            )
            Box(
                modifier = Modifier
                    .size(ChartDimens.legendDot)
                    .background(season.tint, CircleShape),
            )
        }

        repeat(3) { offset ->
            val monthIndex = season.monthStart + offset
            val name = monthNames.getOrNull(monthIndex).orEmpty()
            val label = if (isLeap && monthIndex == EsfandIndex) {
                stringResource(Res.string.calculate_wage_pension_month_leap, name)
            } else {
                name
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(
                        Res.string.calculate_wage_pension_history_days,
                        (days.getOrNull(monthIndex) ?: 0).toString().toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    color = colors.textPrimary,
                )
            }
        }
    }
}

private data class Season(
    val title: String,
    val tint: Color,
    val monthStart: Int,
)

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionYearDetailContentPreview() {
    PreviewRtlThemeContent {
        YearDetailContent(
            item = CalculateWagePensionPreviewData.chartItems[2],
            onDismiss = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionYearDetailPartialPreview() {
    PreviewRtlThemeContent {
        YearDetailContent(
            item = CalculateWagePensionPreviewData.chartItems.first(),
            onDismiss = {},
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
