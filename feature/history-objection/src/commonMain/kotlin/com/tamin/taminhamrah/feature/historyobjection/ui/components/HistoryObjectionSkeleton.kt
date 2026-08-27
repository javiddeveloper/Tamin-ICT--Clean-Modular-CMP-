package com.tamin.taminhamrah.feature.historyobjection.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

private const val PLACEHOLDER_CARDS = 2
private val ActionButtonHeight = 50.dp

/**
 * What the list shows while it loads: [PLACEHOLDER_CARDS] cards shaped like
 * [com.tamin.taminhamrah.feature.historyobjection.ui.NotExistRequestCard] — same header, divider
 * rows and action-button row — so the real cards land where the placeholders already were.
 */
@Composable
fun HistoryObjectionListSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        repeat(PLACEHOLDER_CARDS) {
            NotExistRequestCardSkeleton()
        }
    }
}

@Composable
private fun NotExistRequestCardSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column {
                ShimmerBlock(modifier = Modifier.width(100.dp).height(16.dp))
                Spacer(modifier = Modifier.height(Spacing.xxs))
                ShimmerBlock(modifier = Modifier.width(140.dp).height(12.dp))
            }
            ShimmerBlock(modifier = Modifier.width(70.dp).height(22.dp), cornerRadius = CornerRadius.full)
        }
        Spacer(modifier = Modifier.height(Spacing.md))
        TaminDivider()
        repeat(4) {
            DetailRowSkeleton(modifier = Modifier.padding(vertical = Spacing.xs))
            TaminDivider()
        }
        Spacer(modifier = Modifier.height(Spacing.sm))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerBlock(modifier = Modifier.weight(1f).height(ActionButtonHeight))
            ShimmerBlock(modifier = Modifier.weight(1f).height(ActionButtonHeight))
        }
    }
}

@Composable
private fun DetailRowSkeleton(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp))
        ShimmerBlock(modifier = Modifier.width(90.dp).height(12.dp))
    }
}
