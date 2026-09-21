package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

/** Shimmering stand-in for [IdentityInfoGrid]/[WorkshopInfoGrid]'s 2-per-row tile layout. */
@Composable
fun DisabilityPensionInfoGridSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                repeat(2) {
                    InfoTileSkeleton(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun InfoTileSkeleton(modifier: Modifier = Modifier) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        ShimmerBlock(modifier = Modifier.width(56.dp).height(10.dp))
        ShimmerBlock(modifier = Modifier.width(84.dp).height(14.dp))
    }
}

/** Shimmering stand-in for a handful of [DependentCard]-shaped rows while dependents load. */
@Composable
fun DisabilityPensionDependentsListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 3,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        repeat(itemCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.bgPage)
                    .padding(horizontal = Spacing.smd, vertical = Spacing.smd),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ShimmerBlock(modifier = Modifier.width(120.dp).height(16.dp))
                ShimmerBlock(modifier = Modifier.width(64.dp).height(24.dp), cornerRadius = CornerRadius.full)
            }
        }
    }
}

/** Shimmering stand-in for a couple of [RegisteredRequestItem]-shaped cards while the list loads. */
@Composable
fun DisabilityPensionRegisteredRequestsListSkeleton(
    modifier: Modifier = Modifier,
    itemCount: Int = 2,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.smd),
    ) {
        repeat(itemCount) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface(cornerRadius = Spacing.lg)
                    .padding(Spacing.smd),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ShimmerBlock(modifier = Modifier.width(110.dp).height(14.dp))
                    ShimmerBlock(modifier = Modifier.width(70.dp).height(12.dp))
                }
                ShimmerBlock(modifier = Modifier.fillMaxWidth(0.85f).height(12.dp))
            }
        }
    }
}
