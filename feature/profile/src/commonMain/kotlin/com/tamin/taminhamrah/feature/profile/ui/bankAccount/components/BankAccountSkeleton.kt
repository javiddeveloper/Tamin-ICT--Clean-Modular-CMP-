package com.tamin.taminhamrah.feature.profile.ui.bankAccount.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer

private const val PLACEHOLDER_CARDS = 3

private val CardHeight = 200.dp
private val CardCorner = 22.dp

/**
 * What the list shows while it loads: the cards that are coming, in outline.
 *
 * Shaped like [BankAccountCard] on purpose — same height, same corner, same badge and row
 * positions — so the real cards land where the placeholders already were instead of the page
 * jumping when they arrive.
 */
@Composable
fun BankAccountListSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(Spacing.page),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        repeat(PLACEHOLDER_CARDS) {
            BankAccountCardSkeleton()
        }
    }
}

@Composable
private fun BankAccountCardSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CardHeight)
            .clip(RoundedCornerShape(CardCorner))
            .shimmer(),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ShimmerBlock(modifier = Modifier.size(40.dp), cornerRadius = 11.dp)
                    Spacer(Modifier.width(10.dp))
                    ShimmerBlock(modifier = Modifier.width(110.dp).height(16.dp))
                }
                ShimmerBlock(modifier = Modifier.width(64.dp).height(12.dp))
            }
            Spacer(Modifier.height(10.dp))
            ShimmerBlock(modifier = Modifier.width(84.dp).height(18.dp), cornerRadius = 50.dp)

            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                ShimmerBlock(modifier = Modifier.width(190.dp).height(22.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                repeat(2) {
                    Column {
                        ShimmerBlock(modifier = Modifier.width(44.dp).height(10.dp))
                        Spacer(Modifier.height(4.dp))
                        ShimmerBlock(modifier = Modifier.width(70.dp).height(14.dp))
                    }
                }
            }
        }
    }
}
