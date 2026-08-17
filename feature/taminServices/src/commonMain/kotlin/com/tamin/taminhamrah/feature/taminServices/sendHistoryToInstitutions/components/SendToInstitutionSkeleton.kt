package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
internal fun SendToInstitutionSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Selection Row Shimmer
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            cornerRadius = 18.dp
        )

        Spacer(modifier = Modifier.height(Spacing.lg))

        // Disclaimer Shimmer
        repeat(3) {
            ShimmerBlock(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .padding(horizontal = 8.dp),
                cornerRadius = 4.dp
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
        }

        Spacer(modifier = Modifier.height(Spacing.xl))

        // Button Shimmer
        ShimmerBlock(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            cornerRadius = 12.dp
        )
    }
}
