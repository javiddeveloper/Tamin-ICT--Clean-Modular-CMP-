package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components

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
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
private fun ShimmerFormField(modifier: Modifier = Modifier, fieldHeight: Dp = 50.dp) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp), cornerRadius = 4.dp)
        ShimmerBlock(modifier = Modifier.fillMaxWidth().height(fieldHeight), cornerRadius = 13.dp)
    }
}

@Composable
private fun ShimmerDetailRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ShimmerBlock(modifier = Modifier.width(100.dp).height(14.dp), cornerRadius = 4.dp)
        ShimmerBlock(modifier = Modifier.width(120.dp).height(14.dp), cornerRadius = 4.dp)
    }
}

@Composable
fun FuneralAllowanceStep1ShimmerSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        // Applicant Info Card Shimmer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            ShimmerBlock(modifier = Modifier.width(140.dp).height(16.dp), cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(Spacing.xs))
            ShimmerDetailRow()
            ShimmerDetailRow()
            ShimmerDetailRow()
            ShimmerDetailRow()
            ShimmerDetailRow()
            ShimmerDetailRow()
        }

        // Deceased Info Card Shimmer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            ShimmerBlock(modifier = Modifier.width(140.dp).height(16.dp), cornerRadius = 4.dp)
            Spacer(modifier = Modifier.height(Spacing.xs))
            ShimmerFormField()
            ShimmerBlock(modifier = Modifier.fillMaxWidth().height(32.dp), cornerRadius = 4.dp)
        }
    }
}
