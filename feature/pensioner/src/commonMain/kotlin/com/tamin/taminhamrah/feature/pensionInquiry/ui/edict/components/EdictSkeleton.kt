package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

/** Shimmering stand-in for [EdictMainCard] shown while the first load is in flight. */
@Composable
fun EdictSkeletonMainCard(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Spacer(Modifier.height(Spacing.lg))
                // "مبلغ قابل پرداخت ماهانه" label
                ShimmerBlock(modifier = Modifier.width(150.dp).height(14.dp))
                // Big amount number
                ShimmerBlock(modifier = Modifier.width(200.dp).height(36.dp))
                Spacer(Modifier.height(Spacing.xs))
                // Progress bar
                ShimmerBlock(
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    cornerRadius = CornerRadius.full,
                )
                Spacer(Modifier.height(Spacing.xs))
                // Legend rows (4 items)
                repeat(4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        ShimmerBlock(modifier = Modifier.width(110.dp).height(12.dp))
                        ShimmerBlock(modifier = Modifier.width(70.dp).height(12.dp))
                    }
                }
            }
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

/** Shimmering stand-ins for the comparison card and the details section. */
@Composable
fun EdictSkeletonBodyCards(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        // Comparison card skeleton
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.card),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = BorderStroke(1.dp, taminColors.border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Spacer(Modifier.height(Spacing.lg))
                    // Title
                    ShimmerBlock(modifier = Modifier.width(130.dp).height(16.dp))
                    // Two comparison bars
                    repeat(2) {
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                ShimmerBlock(modifier = Modifier.width(60.dp).height(12.dp))
                                ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp))
                            }
                            ShimmerBlock(
                                modifier = Modifier.fillMaxWidth().height(8.dp),
                                cornerRadius = CornerRadius.full,
                            )
                        }
                    }
                    // Description line
                    ShimmerBlock(modifier = Modifier.fillMaxWidth(0.75f).height(12.dp))
                }
                Spacer(Modifier.height(Spacing.lg))
            }
        }

        // Details section skeleton (tab bar + row list)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(CornerRadius.card),
            colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
            border = BorderStroke(1.dp, taminColors.border),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Spacer(Modifier.height(Spacing.lg))
                    // Tab bar
                    ShimmerBlock(
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        cornerRadius = CornerRadius.lg,
                    )
                    Spacer(Modifier.height(Spacing.xs))
                    // Data rows
                    repeat(5) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            ShimmerBlock(modifier = Modifier.width(110.dp).height(14.dp))
                            ShimmerBlock(modifier = Modifier.width(80.dp).height(14.dp))
                        }
                    }
                }
                Spacer(Modifier.height(Spacing.lg))
            }
        }
    }
}
