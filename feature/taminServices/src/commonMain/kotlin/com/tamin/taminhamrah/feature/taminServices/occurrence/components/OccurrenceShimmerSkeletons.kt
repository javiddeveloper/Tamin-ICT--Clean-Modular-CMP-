package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
private fun ShimmerFormField(modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp), cornerRadius = 4.dp)
        ShimmerBlock(modifier = Modifier.fillMaxWidth().height(50.dp), cornerRadius = 13.dp)
    }
}

/**
 * Page-level shimmer skeleton for Step 2 (workshop info), shown only while the step's
 * initial data is loading. The workshop-name lookup triggered from selecting a workshop
 * uses its own inline shimmer (see Step2WorkshopStep) instead of this full-page skeleton.
 */
@Composable
fun Step2WorkshopShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerFormField()

        ShimmerBlock(
            modifier = Modifier.fillMaxWidth().height(48.dp),
            cornerRadius = 13.dp,
        )

        ShimmerFormField()
        ShimmerFormField()
        ShimmerFormField()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }
    }
}
