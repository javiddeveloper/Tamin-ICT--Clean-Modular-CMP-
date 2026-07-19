package com.tamin.taminhamrah.ui.components.taminServices

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun TabSelector(
    selectedTab: TaminServicesTab,
    onTabSelected: (TaminServicesTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
            .padding(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val tabs = listOf(TaminServicesTab.INSURED, TaminServicesTab.PENSIONER, TaminServicesTab.EMPLOYER)
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            val itemModifier = if (isSelected) {
                Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surface)
            } else {
                Modifier
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .then(itemModifier)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onTabSelected(tab)
                    }
                    .padding(vertical = Spacing.md),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    ),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                )
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ProfileScreenPreview() {
    PreviewRtlThemeContent {
        TabSelector(
            selectedTab = TaminServicesTab.INSURED,
            onTabSelected = {}
        )
    }
}
