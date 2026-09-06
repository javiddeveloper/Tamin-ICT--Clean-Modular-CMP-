package com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.components

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.employerOnlineServices.ui.model.EmployerAgreementSearch
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.employer_online_services_filter_clear_chip
import taminx.core.core_ui.employer_online_services_filter_label
import taminx.core.core_ui.employer_online_services_search_branch_label
import taminx.core.core_ui.employer_online_services_search_workshop_label

/**
 * The "فیلتر …" summary row shown above the list while a client-side filter is active — a copy of
 * `inspection`'s `InspectionFilterChipRow`. Tapping حذف clears the filter.
 */
@Composable
internal fun EmployerOnlineServicesFilterChipRow(
    criteria: EmployerAgreementSearch,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val branchLabel = stringResource(Res.string.employer_online_services_search_branch_label)
    val workshopLabel = stringResource(Res.string.employer_online_services_search_workshop_label)

    val summary = remember(criteria) {
        listOfNotNull(
            criteria.branchCode.takeIf { it.isNotBlank() }?.let { "$branchLabel $it" },
            criteria.workshopCode.takeIf { it.isNotBlank() }?.let { "$workshopLabel $it" },
        ).joinToString(" · ")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.employer_online_services_filter_label),
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )

        Text(
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
                contentDescription = stringResource(Res.string.employer_online_services_filter_clear_chip),
                tint = colors.textSecondary,
                modifier = Modifier.padding(start = 1.dp).size(14.dp),
            )
            Text(
                text = stringResource(Res.string.employer_online_services_filter_clear_chip),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesFilterChipRowPreviewLight() {
    PreviewRtlThemeContent {
        EmployerOnlineServicesFilterChipRow(
            criteria = EmployerAgreementSearch(branchCode = "1202", workshopCode = "0081631829"),
            onClear = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun EmployerOnlineServicesFilterChipRowPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        EmployerOnlineServicesFilterChipRow(
            criteria = EmployerAgreementSearch(branchCode = "1202", workshopCode = "0081631829"),
            onClear = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
