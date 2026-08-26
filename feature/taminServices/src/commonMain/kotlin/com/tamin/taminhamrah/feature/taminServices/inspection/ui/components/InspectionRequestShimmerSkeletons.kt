package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
private fun ShimmerFormField(modifier: Modifier = Modifier, fieldHeight: Dp = 50.dp) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp), cornerRadius = 4.dp)
        ShimmerBlock(modifier = Modifier.fillMaxWidth().height(fieldHeight), cornerRadius = 13.dp)
    }
}

/** Page-level shimmer skeleton for the request wizard's Step 1 (identity/contact). */
@Composable
internal fun IdentityContactShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerBlock(modifier = Modifier.width(160.dp).height(16.dp), cornerRadius = 4.dp)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }

        ShimmerFormField()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }
    }
}

/** Page-level shimmer skeleton for the request wizard's Step 2 (workshop info). */
@Composable
internal fun WorkshopInfoShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Spacer(modifier = Modifier.height(Spacing.md))

        ShimmerBlock(modifier = Modifier.width(160.dp).height(16.dp), cornerRadius = 4.dp)

        ShimmerFormField()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }

        ShimmerFormField()

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
            ShimmerFormField(modifier = Modifier.weight(1f))
            ShimmerFormField(modifier = Modifier.weight(1f))
        }

        ShimmerFormField()
    }
}
