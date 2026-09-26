package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.ShimmerSize
import com.tamin.taminhamrah.ui.theme.Spacing

private const val DETAIL_ROW_COUNT = 5

/**
 * Loading stand-in for [com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.intro.IllDaysIntroContent]
 * body: request-details card, tip banner, and calculate-estimate row — same rhythm as the loaded UI.
 */
@Composable
internal fun IllDaysIntroSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        RequestDetailsCardSkeleton()
        IntroTipBoxSkeleton()
        CalculateEstimateRowSkeleton()
    }
}

@Composable
private fun RequestDetailsCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.titleWidth)
                .height(ShimmerSize.titleHeight),
        )
        repeat(DETAIL_ROW_COUNT) {
            DetailRowSkeleton()
        }
    }
}

@Composable
private fun DetailRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.labelWidth)
                .height(ShimmerSize.valueHeight),
        )
        ShimmerBlock(
            modifier = Modifier
                .width(ShimmerSize.hintWidth)
                .height(ShimmerSize.valueHeight),
        )
    }
}

@Composable
private fun IntroTipBoxSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.lg)
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        ShimmerBlock(
            modifier = Modifier.size(IconSize.banner),
            cornerRadius = CornerRadius.sm,
        )
        ShimmerBlock(
            modifier = Modifier
                .weight(1f)
                .height(ShimmerSize.infoBodyHeight),
            cornerRadius = CornerRadius.sm,
        )
    }
}

@Composable
private fun CalculateEstimateRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        ShimmerBlock(
            modifier = Modifier.size(IconSize.badge),
            cornerRadius = CornerRadius.md,
        )
        ShimmerBlock(
            modifier = Modifier
                .weight(1f)
                .height(ShimmerSize.valueHeight),
        )
        ShimmerBlock(
            modifier = Modifier.size(IconSize.small),
            cornerRadius = CornerRadius.sm,
        )
    }
}
