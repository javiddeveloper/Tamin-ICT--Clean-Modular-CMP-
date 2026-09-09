package com.tamin.taminhamrah.feature.contractaffair.ui.paymentCalcDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PLACEHOLDER_CARDS = 3

/**
 * Shown in place of the جزئیات برگ پرداخت list on first load — a summary-card stand-in plus a few
 * month-card stand-ins mirroring [PaymentCalcDetailRow], so the list appears without a layout jump.
 */
@Composable
internal fun ContractPaymentCalcDetailSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                start = Spacing.page,
                end = Spacing.page,
                top = Spacing.lg,
                bottom = Spacing.xxl,
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            cornerRadius = CornerRadius.lg,
        )
        repeat(PLACEHOLDER_CARDS) {
            MonthCardSkeleton()
        }
    }
}

@Composable
private fun MonthCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ShimmerBlock(modifier = Modifier.size(48.dp), cornerRadius = CornerRadius.xl)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ShimmerBlock(modifier = Modifier.width(120.dp).height(18.dp), cornerRadius = CornerRadius.sm)
                ShimmerBlock(modifier = Modifier.width(72.dp).height(14.dp), cornerRadius = CornerRadius.sm)
            }
        }

        repeat(2) { index ->
            if (index > 0) TaminDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(modifier = Modifier.width(80.dp).height(16.dp), cornerRadius = CornerRadius.sm)
                ShimmerBlock(modifier = Modifier.width(110.dp).height(16.dp), cornerRadius = CornerRadius.sm)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ShimmerBlock(modifier = Modifier.weight(1f).height(52.dp), cornerRadius = CornerRadius.md)
            ShimmerBlock(modifier = Modifier.weight(1f).height(52.dp), cornerRadius = CornerRadius.md)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentCalcDetailSkeletonPreviewLight() {
    PreviewRtlThemeContent {
        ContractPaymentCalcDetailSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentCalcDetailSkeletonPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractPaymentCalcDetailSkeleton()
    }
}
