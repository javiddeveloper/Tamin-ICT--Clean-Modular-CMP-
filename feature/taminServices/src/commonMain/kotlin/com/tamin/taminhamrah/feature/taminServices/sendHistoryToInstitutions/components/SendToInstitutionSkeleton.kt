package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

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
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
internal fun SendToInstitutionSkeleton() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(3) { index ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ShimmerBlock(modifier = Modifier.width(130.dp).height(14.dp))
                ShimmerBlock(modifier = Modifier.width(80.dp).height(12.dp))
            }
            if (index < 2) TaminDivider()
        }
        TaminDivider()
        Spacer(modifier = Modifier.height(Spacing.xs))
        ShimmerBlock(modifier = Modifier.width(90.dp).height(12.dp))
        Spacer(modifier = Modifier.height(Spacing.xs))
        ShimmerBlock(
            modifier = Modifier.width(160.dp).height(28.dp),
            cornerRadius = 50.dp
        )
        Spacer(modifier = Modifier.height(Spacing.sm))
    }
}
