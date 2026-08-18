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
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.userRequest.ui.contract.RequestStatusTab
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness

import org.jetbrains.compose.resources.stringResource

@Composable
fun UserRequestStatusTabs(
    selectedTab: RequestStatusTab,
    counts: Map<RequestStatusTab, Int>,
    onTabSelected: (RequestStatusTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val colorScheme = MaterialTheme.colorScheme

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

            val bg = if (isSelected) taminColors.chipBg else taminColors.bgSurface
            val textCol = if (isSelected) colorScheme.primary else taminColors.textSecondary
            val borderCol = if (isSelected) taminColors.hawkesBlue else taminColors.divider

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(CornerRadius.xl))
                    .background(bg)
                    .border(Thickness.border, borderCol, RoundedCornerShape(CornerRadius.xl))
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

