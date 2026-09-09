package com.tamin.taminhamrah.feature.contractaffair.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_search_empty_subtitle
import taminx.core.core_ui.contract_affairs_search_empty_title

/**
 * Shown in place of the contract list when جستجوی قرارداد is active but nothing matched — a dashed
 * card, same as `employerOnlineServices`' `EmployerOnlineServicesSearchEmptyState`. The plain
 * "no contracts at all" case still uses the shared `EmptyStateMessage`.
 */
@Composable
internal fun ContractSearchEmptyState(
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
        TaminText(
            text = stringResource(Res.string.contract_affairs_search_empty_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
            textAlign = TextAlign.Center,
        )
        TaminText(
            text = stringResource(Res.string.contract_affairs_search_empty_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSearchEmptyStatePreviewLight() {
    PreviewRtlThemeContent {
        ContractSearchEmptyState(modifier = Modifier.padding(Spacing.lg))
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSearchEmptyStatePreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractSearchEmptyState(modifier = Modifier.padding(Spacing.lg))
    }
}
