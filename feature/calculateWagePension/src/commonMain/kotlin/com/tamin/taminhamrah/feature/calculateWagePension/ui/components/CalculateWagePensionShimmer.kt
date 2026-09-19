package com.tamin.taminhamrah.feature.calculateWagePension.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.ChartDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private const val SkeletonBarCount = 6

@Composable
private fun cardShimmerColors(): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return colors.border to colors.divider
}

@Composable
internal fun CalculateWagePensionBodyShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        WorkshopSwitchCardShimmer()
        HistoryCardShimmer()
        DisclaimerBannerShimmer()
    }
}

@Composable
private fun WorkshopSwitchCardShimmer(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (base, highlight) = cardShimmerColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        border = BorderStroke(Thickness.border, colors.border),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ShimmerBlock(
                modifier = Modifier.size(IconSize.badge),
                cornerRadius = CornerRadius.md,
                colorBase = base,
                colorHighlight = highlight,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(ShimmerSize.titleHeight),
                    colorBase = base,
                    colorHighlight = highlight,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(ShimmerSize.subtitleHeight),
                    colorBase = base,
                    colorHighlight = highlight,
                )
            }
            ShimmerBlock(
                modifier = Modifier
                    .width(ShimmerSize.badgeWidth)
                    .height(ShimmerSize.badgeHeight),
                cornerRadius = CornerRadius.max,
                colorBase = base,
                colorHighlight = highlight,
            )
        }
    }
}

@Composable
private fun HistoryCardShimmer(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (base, highlight) = cardShimmerColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        border = BorderStroke(Thickness.border, colors.border),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.size(IconSize.medium),
                        cornerRadius = CornerRadius.md,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ShimmerSize.titleWidth)
                            .height(ShimmerSize.titleHeight),
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                }
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.chipWidth)
                        .height(ShimmerSize.badgeHeight),
                    cornerRadius = CornerRadius.max,
                    colorBase = base,
                    colorHighlight = highlight,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                repeat(3) {
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(ShimmerSize.copyRowHeight),
                        cornerRadius = CornerRadius.lg,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ChartDimens.barChartHeight),
                horizontalArrangement = Arrangement.spacedBy(
                    space = ChartDimens.barGap,
                    alignment = Alignment.CenterHorizontally,
                ),
                verticalAlignment = Alignment.Bottom,
            ) {
                repeat(SkeletonBarCount) { index ->
                    val fraction = when (index % 3) {
                        0 -> 0.45f
                        1 -> 0.85f
                        else -> 0.65f
                    }
                    ShimmerBlock(
                        modifier = Modifier
                            .width(ChartDimens.barWidth)
                            .height(ChartDimens.barChartHeight * fraction),
                        cornerRadius = ChartDimens.barCorner,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.hintWidth)
                        .height(ShimmerSize.subtitleHeight),
                    colorBase = base,
                    colorHighlight = highlight,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.labelWidth)
                        .height(ShimmerSize.subtitleHeight),
                    colorBase = base,
                    colorHighlight = highlight,
                )
            }
        }
    }
}

@Composable
private fun DisclaimerBannerShimmer(modifier: Modifier = Modifier) {
    val (base, highlight) = cardShimmerColors()
    ShimmerBlock(
        modifier = modifier
            .fillMaxWidth()
            .height(ShimmerSize.bannerHeight),
        cornerRadius = CornerRadius.listRow,
        colorBase = base,
        colorHighlight = highlight,
    )
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionBodyShimmerPreview() {
    PreviewRtlThemeContent {
        CalculateWagePensionBodyShimmer(
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CalculateWagePensionBodyShimmerDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        CalculateWagePensionBodyShimmer(
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
