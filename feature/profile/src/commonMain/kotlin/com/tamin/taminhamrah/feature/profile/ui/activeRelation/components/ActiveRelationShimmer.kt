package com.tamin.taminhamrah.feature.profile.ui.activeRelation.components

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
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private const val SKELETON_INFO_ROWS = 4
private const val SKELETON_RECIPIENT_ROWS = 6

@Composable
private fun cardShimmerColors(): Pair<Color, Color> {
    val colors = LocalTaminColors.current
    return colors.border to colors.divider
}

@Composable
internal fun ActiveRelationItemCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val (base, highlight) = cardShimmerColors()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.cardCompact),
        colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
        border = BorderStroke(Thickness.border, colors.border),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.lg),
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
                    cornerRadius = CornerRadius.chip,
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
                repeat(SKELETON_INFO_ROWS) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ShimmerBlock(
                            modifier = Modifier
                                .width(ShimmerSize.labelWidth)
                                .height(ShimmerSize.subtitleHeight),
                            colorBase = base,
                            colorHighlight = highlight,
                        )
                        ShimmerBlock(
                            modifier = Modifier
                                .width(ShimmerSize.hintWidth)
                                .height(ShimmerSize.valueHeight),
                            colorBase = base,
                            colorHighlight = highlight,
                        )
                    }
                    if (index < SKELETON_INFO_ROWS - 1) {
                        TaminDivider()
                    }
                }
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
                    modifier = Modifier.size(IconSize.small),
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

@Composable
internal fun RecipientsListShimmer(modifier: Modifier = Modifier) {
    val (base, highlight) = cardShimmerColors()
    Column(modifier = modifier.fillMaxWidth()) {
        repeat(SKELETON_RECIPIENT_ROWS) {
            ShimmerBlock(
                modifier = Modifier
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md)
                    .fillMaxWidth(0.55f)
                    .height(ShimmerSize.valueHeight),
                colorBase = base,
                colorHighlight = highlight,
            )
            TaminDivider(modifier = Modifier.padding(horizontal = Spacing.lg))
        }
    }
}
