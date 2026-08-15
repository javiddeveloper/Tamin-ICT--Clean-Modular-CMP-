package com.tamin.taminhamrah.feature.userRequest.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.userRequest.ui.contract.RequestStatusTab
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing

import org.jetbrains.compose.resources.stringResource

@Composable
fun UserRequestStatusTabs(
    selectedTab: RequestStatusTab,
    counts: Map<RequestStatusTab, Int>,
    onTabSelected: (RequestStatusTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RequestStatusTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            val count = counts[tab] ?: 0

            val bg = if (isSelected) Color(0xFFEFF6FF) else LocalTaminColors.current.bgSurface
            val textCol = if (isSelected) Color(0xFF1F4FA3) else LocalTaminColors.current.textSecondary
            val borderCol = if (isSelected) Color(0xFFBFDBFE) else LocalTaminColors.current.divider

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(bg)
                    .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = Spacing.sm),
                contentAlignment = Alignment.Center
            ) {
                TaminText(
                    text = "${stringResource(tab.labelRes)} $count",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = textCol
                )
            }
        }
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun UserRequestStatusTabsPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        UserRequestStatusTabs(
            selectedTab = RequestStatusTab.ALL,
            counts = mapOf(
                RequestStatusTab.ALL to 13,
                RequestStatusTab.IN_PROGRESS to 5,
                RequestStatusTab.ACTION_REQUIRED to 3,
                RequestStatusTab.COMPLETED to 5
            ),
            onTabSelected = {}
        )
    }
}

