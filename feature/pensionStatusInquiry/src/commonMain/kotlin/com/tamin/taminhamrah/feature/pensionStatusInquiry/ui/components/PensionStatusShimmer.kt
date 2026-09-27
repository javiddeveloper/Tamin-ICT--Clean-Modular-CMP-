package com.tamin.taminhamrah.feature.pensionStatusInquiry.ui.components

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
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private const val PLACEHOLDER_CARDS = 3

@Composable
private fun cardShimmerColors(): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return colors.border to colors.divider
}

@Composable
internal fun PensionStatusListSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(PLACEHOLDER_CARDS) {
            PensionStatusCardSkeleton()
        }
    }
}

@Composable
internal fun PensionStatusCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (base, highlight) = cardShimmerColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none),
        border = BorderStroke(Thickness.border, colors.border),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.chipWidth)
                        .height(ShimmerSize.titleHeight),
                    colorBase = base,
                    colorHighlight = highlight,
                )
                ShimmerBlock(
                    modifier = Modifier
                        .width(ShimmerSize.badgeWidth)
                        .height(ShimmerSize.badgeHeight),
                    cornerRadius = CornerRadius.max,
                    colorBase = base,
                    colorHighlight = highlight,
                )
            }

            TaminDivider()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    ShimmerBlock(
                        modifier = Modifier.size(IconSize.largePlus),
                        cornerRadius = CornerRadius.max,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                        ShimmerBlock(
                            modifier = Modifier
                                .width(ShimmerSize.titleWidth)
                                .height(ShimmerSize.titleHeight),
                            colorBase = base,
                            colorHighlight = highlight,
                        )
                        ShimmerBlock(
                            modifier = Modifier
                                .width(ShimmerSize.subtitleWidth)
                                .height(ShimmerSize.subtitleHeight),
                            colorBase = base,
                            colorHighlight = highlight,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(ShimmerSize.copyRowHeight),
                        cornerRadius = CornerRadius.lg,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(ShimmerSize.copyRowHeight),
                        cornerRadius = CornerRadius.lg,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                }

                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ShimmerSize.copyRowHeight),
                    cornerRadius = CornerRadius.lg,
                    colorBase = base,
                    colorHighlight = highlight,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                ) {
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(ShimmerSize.copyRowHeight),
                        cornerRadius = CornerRadius.lg,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                    ShimmerBlock(
                        modifier = Modifier
                            .weight(1f)
                            .height(ShimmerSize.copyRowHeight),
                        cornerRadius = CornerRadius.lg,
                        colorBase = base,
                        colorHighlight = highlight,
                    )
                }

                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ShimmerSize.copyRowHeight),
                    cornerRadius = CornerRadius.lg,
                    colorBase = base,
                    colorHighlight = highlight,
                )
            }

            TaminDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                ShimmerBlock(
                    modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
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
        }
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusCardSkeleton() {
    PreviewRtlThemeContent {
        PensionStatusCardSkeleton(modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPensionStatusListSkeleton() {
    PreviewRtlThemeContent {
        PensionStatusListSkeleton(modifier = Modifier.padding(Spacing.page))
    }
}
