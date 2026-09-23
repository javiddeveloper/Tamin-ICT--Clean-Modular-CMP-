package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.DarkTaminColors
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.calculate_wage_pension_avg_wage_label
import taminx.core.core_ui.calculate_wage_pension_premium_duration_label
import taminx.core.core_ui.calculate_wage_pension_premium_years_value
import taminx.core.core_ui.unit_rial
import kotlin.math.round

/**
 * Glass stats card that rides the bottom of the hero header — same overlapping treatment as
 * [com.tamin.taminhamrah.ui.components.ValidationStatusCard] on the profile screen.
 */
@Composable
internal fun CalculateWagePensionStatsCard(
    premiumYears: Double,
    averageSalary: Long,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val isDark = colors == DarkTaminColors
    val cardBorderColor = if (isDark) colors.glassBorder else colors.glassBorder

    Box(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowPrimary,
                borderRadius = CornerRadius.iconTile,
                blurRadius = Elevation.xxl,
                offsetY = Spacing.md,
            )
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(colors.bgSurface)
            .border(Thickness.border, cardBorderColor, RoundedCornerShape(CornerRadius.iconTile)),
    ) {
        if (isLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page, vertical = Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatsShimmerCell(modifier = Modifier.weight(1f))
                VerticalDivider(
                    modifier = Modifier.height(Spacing.xxxxxl),
                    thickness = Thickness.border,
                    color = colors.outerBorder,
                )
                StatsShimmerCell(modifier = Modifier.weight(1f))
            }
        } else {
            val premiumText = stringResource(
                Res.string.calculate_wage_pension_premium_years_value,
                formatPremiumYears(premiumYears).toPersianDigits(),
            )
            val averageText =
                "${averageSalary.toPriceFormat().toPersianDigits()} ${stringResource(Res.string.unit_rial)}"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.page, vertical = Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StatCell(
                    label = stringResource(Res.string.calculate_wage_pension_avg_wage_label),
                    value = averageText,
                    modifier = Modifier.weight(1f),
                )
                VerticalDivider(
                    modifier = Modifier.height(Spacing.xxxxxl),
                    thickness = Thickness.border,
                    color = colors.outerBorder,
                )
                StatCell(
                    label = stringResource(Res.string.calculate_wage_pension_premium_duration_label),
                    value = premiumText,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun StatCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.padding(horizontal = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun StatsShimmerCell(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.subtitleWidth)
                .height(ShimmerSize.subtitleHeight),
        )
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.titleWidth)
                .height(ShimmerSize.titleHeight),
        )
    }
}

private fun formatPremiumYears(value: Double): String {
    val rounded = round(value * 100.0) / 100.0
    val asLong = rounded.toLong()
    return if (rounded == asLong.toDouble()) asLong.toString() else rounded.toString()
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionStatsCardPreview() {
    PreviewRtlThemeContent {
        val hazeState = remember { HazeState(initialBlurEnabled = true) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalTaminColors.current.bgPage)
                .hazeSource(state = hazeState)
                .padding(Spacing.page),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                CalculateWagePensionStatsCard(
                    premiumYears = 7.34,
                    averageSalary = 185_000_000L,
                )
                CalculateWagePensionStatsCard(
                    premiumYears = 0.0,
                    averageSalary = 0L,
                    isLoading = true,
                )
            }
        }
    }
}
