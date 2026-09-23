package com.tamin.taminhamrah.feature.taminServices.constructionInsurance.ui.components

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
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.branch_code
import taminx.core.core_ui.construction_insurance_filter_clear
import taminx.core.core_ui.construction_insurance_filter_label
import taminx.core.core_ui.file_number
import taminx.core.core_ui.label_request_number
import taminx.core.core_ui.workshop_number

@Composable
fun ConstructionSearchFilterChipRow(
    fileNoQuery: String,
    reqNoQuery: String,
    workshopIdQuery: String,
    branchCodeQuery: String,
    onClear: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val fileNoLabel = stringResource(Res.string.file_number)
    val reqNoLabel = stringResource(Res.string.label_request_number)
    val workshopIdLabel = stringResource(Res.string.workshop_number)
    val branchCodeLabel = stringResource(Res.string.branch_code)

    val summary = remember(fileNoQuery, reqNoQuery, workshopIdQuery, branchCodeQuery) {
        listOfNotNull(
            fileNoQuery.takeIf { it.isNotBlank() }?.let { "$fileNoLabel $it" },
            reqNoQuery.takeIf { it.isNotBlank() }?.let { "$reqNoLabel $it" },
            workshopIdQuery.takeIf { it.isNotBlank() }?.let { "$workshopIdLabel $it" },
            branchCodeQuery.takeIf { it.isNotBlank() }?.let { "$branchCodeLabel $it" },
        ).joinToString(" · ")
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, alignment = Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.construction_insurance_filter_label),
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
                contentDescription = stringResource(Res.string.construction_insurance_filter_clear),
                tint = colors.textSecondary,
                modifier = Modifier.padding(start = 1.dp).size(14.dp),
            )
            Text(
                text = stringResource(Res.string.construction_insurance_filter_clear),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchFilterChipRowPreviewLight() {
    PreviewRtlThemeContent {
        ConstructionSearchFilterChipRow(
            fileNoQuery = "4479890882",
            reqNoQuery = "",
            workshopIdQuery = "9028222442",
            branchCodeQuery = "",
            onClear = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ConstructionSearchFilterChipRowPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        ConstructionSearchFilterChipRow(
            fileNoQuery = "4479890882",
            reqNoQuery = "123456789",
            workshopIdQuery = "9028222442",
            branchCodeQuery = "6400",
            onClear = {},
            modifier = Modifier.padding(Spacing.lg),
        )
    }
}
