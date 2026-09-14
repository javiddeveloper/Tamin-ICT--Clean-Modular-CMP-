package com.tamin.taminhamrah.feature.contractaffair.ui.premiumPayment.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

/**
 * Stand-in for the پرداخت حق بیمه body while «آخرین پرداخت» is loading — mirrors the real layout
 * (info card · جداکننده · دورهٔ پرداخت stepper · محاسبهٔ حق بیمه button) so the screen does not jump
 * when the data arrives.
 */
@Composable
internal fun ContractPremiumPaymentInitSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            cornerRadius = CornerRadius.lg,
        )
        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(96.dp),
            cornerRadius = CornerRadius.lg,
        )

        TaminDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(modifier = Modifier.width(44.dp).height(44.dp), cornerRadius = CornerRadius.md)
            ShimmerBlock(modifier = Modifier.weight(1f).height(32.dp), cornerRadius = CornerRadius.sm)
            ShimmerBlock(modifier = Modifier.width(44.dp).height(44.dp), cornerRadius = CornerRadius.md)
        }

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            cornerRadius = CornerRadius.lg,
        )
    }
}

/**
 * Stand-in for [ContractDebitResultCard] while «محاسبهٔ حق بیمه» is running — same surface, six
 * label/value reading rows and a CTA row, so the result appears in place without a layout jump.
 */
@Composable
internal fun ContractDebitResultCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        repeat(6) { index ->
            if (index > 0) TaminDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(modifier = Modifier.width(120.dp).height(16.dp), cornerRadius = CornerRadius.sm)
                ShimmerBlock(modifier = Modifier.width(96.dp).height(16.dp), cornerRadius = CornerRadius.sm)
            }
        }

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(44.dp).padding(top = Spacing.sm),
            cornerRadius = CornerRadius.lg,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractPremiumPaymentInitSkeletonPreviewLight() {
    PreviewRtlThemeContent {
        ContractPremiumPaymentInitSkeleton()
    }
}

@PreviewRtlTheme
@Composable
private fun ContractDebitResultCardSkeletonPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractDebitResultCardSkeleton(modifier = Modifier.padding(Spacing.page))
    }
}
