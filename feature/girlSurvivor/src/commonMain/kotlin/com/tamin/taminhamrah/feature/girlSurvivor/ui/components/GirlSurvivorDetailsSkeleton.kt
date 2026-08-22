package com.tamin.taminhamrah.feature.girlSurvivor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PROFILE_ROW_COUNT = 6

@Composable
fun GirlSurvivorDetailsSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        GirlSurvivorProfileCardSkeleton()

        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp),
            cornerRadius = CornerRadius.sm,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            ShimmerBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                cornerRadius = CornerRadius.sm,
            )
            ShimmerBlock(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                cornerRadius = CornerRadius.sm,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerBlock(modifier = Modifier.width(140.dp).height(16.dp))
            ShimmerBlock(modifier = Modifier.width(48.dp).height(28.dp), cornerRadius = CornerRadius.full)
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            ShimmerBlock(modifier = Modifier.width(160.dp).height(16.dp))
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                cornerRadius = CornerRadius.sm,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            cornerRadius = CornerRadius.md,
        )

        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}

@Composable
private fun GirlSurvivorProfileCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(PROFILE_ROW_COUNT) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ShimmerBlock(modifier = Modifier.width(96.dp).height(14.dp))
                ShimmerBlock(modifier = Modifier.width(120.dp).height(14.dp))
            }
        }
    }
}
