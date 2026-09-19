package com.tamin.taminhamrah.feature.contractaffair.ui.paymentHistory.components

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
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PLACEHOLDER_CARDS = 3

/**
 * Shown in place of the سوابق پرداخت list on first load — a summary-card stand-in plus a few
 * payment-card stand-ins mirroring [PaymentHistoryItemCard], so the list appears without a jump.
 */
@Composable
internal fun ContractPaymentHistorySkeleton(modifier: Modifier = Modifier) {
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
            modifier = Modifier.fillMaxWidth().height(72.dp),
            cornerRadius = CornerRadius.chip,
        )
        repeat(PLACEHOLDER_CARDS) {
            PaymentCardSkeleton()
        }
    }
}

@Composable
private fun PaymentCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 26.dp,
                offsetY = 10.dp,
            )
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.lg, bottom = Spacing.sm),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            ShimmerBlock(modifier = Modifier.size(IconSize.large), cornerRadius = CornerRadius.avatarTile)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ShimmerBlock(modifier = Modifier.width(120.dp).height(18.dp), cornerRadius = CornerRadius.sm)
                ShimmerBlock(modifier = Modifier.width(72.dp).height(20.dp), cornerRadius = CornerRadius.chip)
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ShimmerBlock(modifier = Modifier.width(56.dp).height(12.dp), cornerRadius = CornerRadius.sm)
                ShimmerBlock(modifier = Modifier.width(80.dp).height(16.dp), cornerRadius = CornerRadius.sm)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBlock(modifier = Modifier.width(100.dp).height(16.dp), cornerRadius = CornerRadius.sm)
            ShimmerBlock(modifier = Modifier.weight(1f).height(1.dp), cornerRadius = CornerRadius.sm)
            ShimmerBlock(modifier = Modifier.width(140.dp).height(28.dp), cornerRadius = CornerRadius.md)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xlg)
                .padding(top = Spacing.md, bottom = Spacing.lg),
            horizontalArrangement = Arrangement.End,
        ) {
            ShimmerBlock(modifier = Modifier.width(110.dp).height(28.dp), cornerRadius = CornerRadius.md)
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistorySkeletonPreviewLight() {
    PreviewRtlThemeContent {
        ContractPaymentHistorySkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPaymentHistorySkeletonPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractPaymentHistorySkeleton()
    }
}
