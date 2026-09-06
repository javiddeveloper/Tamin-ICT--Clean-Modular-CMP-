package com.tamin.taminhamrah.feature.contracts.ui.affairs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.contracts.ui.affairs.contract.ContractSearchFilter
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_affairs_filter_clear
import taminx.core.core_ui.contract_affairs_filter_label
import taminx.core.core_ui.contract_affairs_filter_number
import taminx.core.core_ui.contract_affairs_search_type_all
import taminx.core.core_ui.contract_affairs_search_type_fraction
import taminx.core.core_ui.contract_affairs_search_type_freelance
import taminx.core.core_ui.contract_affairs_search_type_optional

/**
 * The "فیلتر: …" summary row shown above the list while جستجوی قرارداد is active — same shape as
 * `employerOnlineServices`' `EmployerOnlineServicesFilterChipRow`. Tapping حذف فیلتر clears it.
 */
@Composable
internal fun ContractSearchFilterChipRow(
    contractNumber: String,
    filter: ContractSearchFilter,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val numberPrefix = stringResource(Res.string.contract_affairs_filter_number)
    val filterLabel = contractSearchFilterLabel(filter)

    val summary = buildList {
        contractNumber.takeIf { it.isNotBlank() }?.let { add("$numberPrefix $it") }
        if (filter != ContractSearchFilter.ALL) add(filterLabel)
    }.joinToString(" · ")

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = stringResource(Res.string.contract_affairs_filter_label),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )

        TaminText(
            text = summary,
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f, fill = false)
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(colors.chipBg)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(color = colors.bgSurface, shape = RoundedCornerShape(CornerRadius.full))
                .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.full))
                .clickable(onClick = onClear)
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.contract_affairs_filter_clear),
                tint = colors.textSecondary,
                modifier = Modifier.padding(start = 1.dp).size(14.dp),
            )
            TaminText(
                text = stringResource(Res.string.contract_affairs_filter_clear),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
        }
    }
}

@Composable
private fun contractSearchFilterLabel(filter: ContractSearchFilter): String = stringResource(
    when (filter) {
        ContractSearchFilter.ALL -> Res.string.contract_affairs_search_type_all
        ContractSearchFilter.FREELANCE -> Res.string.contract_affairs_search_type_freelance
        ContractSearchFilter.OPTIONAL -> Res.string.contract_affairs_search_type_optional
        ContractSearchFilter.FRACTION -> Res.string.contract_affairs_search_type_fraction
    },
)

@PreviewRtlTheme
@Composable
private fun ContractSearchFilterChipRowPreviewLight() {
    PreviewRtlThemeContent {
        ContractSearchFilterChipRow(
            contractNumber = "4832222686",
            filter = ContractSearchFilter.OPTIONAL,
            onClear = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSearchFilterChipRowPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ContractSearchFilterChipRow(
            contractNumber = "",
            filter = ContractSearchFilter.FREELANCE,
            onClear = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
