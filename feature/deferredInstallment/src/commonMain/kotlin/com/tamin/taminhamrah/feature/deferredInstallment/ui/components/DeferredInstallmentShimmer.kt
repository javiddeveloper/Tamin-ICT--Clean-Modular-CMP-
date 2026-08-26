package com.tamin.taminhamrah.feature.deferredInstallment.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
internal fun DeferredInstallmentStepOneShimmer(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        ShimmerBlock(
            modifier = Modifier
                .width(160.dp)
                .height(24.dp),
            cornerRadius = 4.dp,
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            cornerRadius = CornerRadius.lg,
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            cornerRadius = CornerRadius.lg,
        )
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            cornerRadius = CornerRadius.lg,
        )
    }
}

@Composable
internal fun DeferredInstallmentBankListShimmer(
    itemCount: Int = 4,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        repeat(itemCount) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                cornerRadius = CornerRadius.lg,
            )
        }
    }
}
