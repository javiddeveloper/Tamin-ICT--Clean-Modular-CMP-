package com.tamin.taminhamrah.feature.girlSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PROFILE_ROW_COUNT = 6

@Composable
fun GirlSurvivorDetailsSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        GirlSurvivorProfileCardSkeleton()

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            ShimmerBlock(modifier = Modifier.width(48.dp).height(14.dp))
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                cornerRadius = CornerRadius.lg,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ShimmerBlock(modifier = Modifier.width(64.dp).height(14.dp))
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    cornerRadius = CornerRadius.lg,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                ShimmerBlock(modifier = Modifier.width(72.dp).height(14.dp))
                ShimmerBlock(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    cornerRadius = CornerRadius.lg,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .dashedOutline(colors.hawkesBlue, CornerRadius.card, 1.dp)
                .padding(horizontal = Spacing.md, vertical = Spacing.smd),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ShimmerBlock(modifier = Modifier.width(180.dp).height(16.dp))
            ShimmerBlock(modifier = Modifier.width(48.dp).height(28.dp), cornerRadius = CornerRadius.full)
        }

        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            ShimmerBlock(modifier = Modifier.width(100.dp).height(14.dp))
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                cornerRadius = CornerRadius.lg,
            )
        }

        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}

@Composable
private fun GirlSurvivorProfileCardSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.bgSurface)
            .border(1.dp, colors.hawkesBlue, shape)
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
