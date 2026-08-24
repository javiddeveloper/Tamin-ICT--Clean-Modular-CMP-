package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_search_empty_subtitle
import taminx.core.core_ui.inspection_search_empty_title

/**
 * Shown when the API list has inspections but the local search criteria matches none of them —
 * a dashed-outline card rather than [com.tamin.taminhamrah.ui.components.EmptyStateMessage]'s
 * icon-tile treatment, distinguishing "no results for this filter" from "no inspections at all".
 */
@Composable
internal fun InspectionSearchEmptyState(
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.card))
            .background(color = colors.bgSurface)
            .dashedOutline(colors.border, CornerRadius.card, Thickness.border)
            .padding(vertical = Spacing.xxxl, horizontal = Spacing.xlg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = stringResource(Res.string.inspection_search_empty_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(Res.string.inspection_search_empty_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionSearchEmptyStatePreviewLight() {
    PreviewRtlThemeContent {
        InspectionSearchEmptyState(modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionSearchEmptyStatePreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        InspectionSearchEmptyState(modifier = Modifier.padding(Spacing.lg))
    }
}
