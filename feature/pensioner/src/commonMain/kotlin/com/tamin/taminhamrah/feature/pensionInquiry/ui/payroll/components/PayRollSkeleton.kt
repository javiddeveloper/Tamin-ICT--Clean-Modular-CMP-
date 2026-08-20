package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

/**
 * Explicit, higher-contrast pair for skeleton bars sitting directly on a [LocalTaminColors]
 * `bgSurface` card. [ShimmerBlock]'s own default (surfaceVariant → surface, i.e. divider →
 * bgSurface) is built for content on the page background — on a white card those two colors are
 * nearly identical and the shimmer all but disappears.
 */
@Composable
private fun payRollShimmerColors(): Pair<Color, Color> {
    val taminColors = LocalTaminColors.current
    return taminColors.border to taminColors.divider
}

/** Shimmering stand-in for [PayRollMainCard] shown while the first load is in flight. */
@Composable
fun PayRollSkeletonMainCard(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val (base, highlight) = payRollShimmerColors()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(CornerRadius.card),
        colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Spacer(Modifier.height(Spacing.lg))
                ShimmerBlock(modifier = Modifier.width(150.dp).height(14.dp), colorBase = base, colorHighlight = highlight)
                ShimmerBlock(modifier = Modifier.width(200.dp).height(36.dp), colorBase = base, colorHighlight = highlight)
                Spacer(Modifier.height(Spacing.xs))
                ShimmerBlock(
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    cornerRadius = CornerRadius.full,
                    colorBase = base,
                    colorHighlight = highlight,
                )
                Spacer(Modifier.height(Spacing.xs))
                repeat(2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        ShimmerBlock(modifier = Modifier.width(110.dp).height(12.dp), colorBase = base, colorHighlight = highlight)
                        ShimmerBlock(modifier = Modifier.width(70.dp).height(12.dp), colorBase = base, colorHighlight = highlight)
                    }
                }
            }
            Spacer(Modifier.height(Spacing.lg))
        }
    }
}

/** Shimmering stand-ins for the payments/deductions breakdown sections. */
@Composable
fun PayRollSkeletonBodyCards(modifier: Modifier = Modifier) {
    val taminColors = LocalTaminColors.current
    val (base, highlight) = payRollShimmerColors()
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.lg)) {
        repeat(2) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(CornerRadius.card),
                colors = CardDefaults.cardColors(containerColor = taminColors.bgSurface),
                border = BorderStroke(1.dp, taminColors.border),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        Spacer(Modifier.height(Spacing.lg))
                        ShimmerBlock(modifier = Modifier.width(130.dp).height(16.dp), colorBase = base, colorHighlight = highlight)
                        repeat(4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                ShimmerBlock(modifier = Modifier.width(110.dp).height(14.dp), colorBase = base, colorHighlight = highlight)
                                ShimmerBlock(modifier = Modifier.width(80.dp).height(14.dp), colorBase = base, colorHighlight = highlight)
                            }
                        }
                    }
                    Spacer(Modifier.height(Spacing.lg))
                }
            }
        }
    }
}
