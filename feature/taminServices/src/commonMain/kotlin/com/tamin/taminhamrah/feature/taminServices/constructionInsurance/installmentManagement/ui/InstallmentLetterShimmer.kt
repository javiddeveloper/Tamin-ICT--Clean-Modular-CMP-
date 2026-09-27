package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.installmentManagement.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PLACEHOLDER_CARDS = 3
private const val PLACEHOLDER_ROWS = 3

@Composable
fun InstallmentLetterSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(PLACEHOLDER_CARDS) {
            InstallmentLetterCardSkeleton()
        }
    }
}

@Composable
private fun InstallmentLetterCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ShimmerBlock(
                modifier = Modifier.width(96.dp).height(18.dp),
                cornerRadius = CornerRadius.sm,
            )
            ShimmerBlock(
                modifier = Modifier.width(64.dp).height(22.dp),
                cornerRadius = CornerRadius.chip,
            )
        }

        TaminDivider()

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            repeat(PLACEHOLDER_ROWS) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ShimmerBlock(
                        modifier = Modifier.width(72.dp).height(14.dp),
                        cornerRadius = CornerRadius.sm,
                    )
                    ShimmerBlock(
                        modifier = Modifier.width(100.dp).height(16.dp),
                        cornerRadius = CornerRadius.sm,
                    )
                }
            }
        }

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(40.dp),
            cornerRadius = CornerRadius.md,
        )
    }
}
