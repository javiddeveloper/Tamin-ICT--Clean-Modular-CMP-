package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.beneficiaries.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.coloredShadow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PLACEHOLDER_CARDS = 4

@Composable
fun BeneficiariesSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(PLACEHOLDER_CARDS) {
            BeneficiaryCardSkeleton()
        }
    }
}

/** Mirrors `BeneficiaryCard`'s layout: avatar + name/code beside the role pill, then the mobile strip. */
@Composable
private fun BeneficiaryCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .coloredShadow(
                color = colors.shadowSubtle,
                borderRadius = CornerRadius.card,
                blurRadius = 20.dp,
                offsetY = 8.dp,
            )
            .taminSurface(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(
                    modifier = Modifier.size(IconSize.xlarge),
                    cornerRadius = IconSize.xlarge / 2,
                )
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                    ShimmerBlock(
                        modifier = Modifier.width(100.dp).height(16.dp),
                        cornerRadius = CornerRadius.sm,
                    )
                    ShimmerBlock(
                        modifier = Modifier.width(70.dp).height(12.dp),
                        cornerRadius = CornerRadius.sm,
                    )
                }
            }
            ShimmerBlock(
                modifier = Modifier.width(56.dp).height(24.dp),
                cornerRadius = CornerRadius.chip,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    colors.bgPage,
                    RoundedCornerShape(bottomStart = CornerRadius.card, bottomEnd = CornerRadius.card),
                )
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(
                modifier = Modifier.width(90.dp).height(14.dp),
                cornerRadius = CornerRadius.sm,
            )
            ShimmerBlock(
                modifier = Modifier.width(110.dp).height(16.dp),
                cornerRadius = CornerRadius.sm,
            )
        }
    }
}
