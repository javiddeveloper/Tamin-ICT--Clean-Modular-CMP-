package com.tamin.taminhamrah.feature.objectionInsurance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

private const val GRID_COLUMNS = 3
private const val PLACEHOLDER_CARDS = 6

/**
 * Loading stand-in for [ObjectionYearGrid] — same 3-column square cards so the real grid
 * lands without a jump.
 */
@Composable
fun ObjectionYearGridSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        repeat(PLACEHOLDER_CARDS / GRID_COLUMNS) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                repeat(GRID_COLUMNS) {
                    YearCardSkeleton(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun YearCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(CornerRadius.xlg))
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.xlg))
            .padding(Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterVertically),
    ) {
        ShimmerBlock(
            modifier = Modifier.size(IconSize.xxlarge),
            cornerRadius = CornerRadius.full,
        )
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.chipWidth)
                .height(ShimmerSize.subtitleHeight),
        )
    }
}
