package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoPR
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.SegmentedInputField
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.amount_unknown
import taminx.core.core_ui.girl_survivor_label_full_name
import taminx.core.core_ui.girl_survivor_label_insurance_id
import taminx.core.core_ui.ic_number
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.inquiry_national_id_label
import taminx.core.core_ui.pension_survivor_deceased_branch
import taminx.core.core_ui.pension_survivor_deceased_death_certificate
import taminx.core.core_ui.pension_survivor_deceased_death_date
import taminx.core.core_ui.pension_survivor_deceased_documents_title
import taminx.core.core_ui.pension_survivor_deceased_id_pages
import taminx.core.core_ui.pension_survivor_soon
import taminx.core.core_ui.verify_label_age
import taminx.core.core_ui.verify_label_age_value
import taminx.core.core_ui.verify_label_national_id
import taminx.core.core_ui.verify_label_relation

@Composable
fun DeceasedStep(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.inquiry_national_id_label),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textMuted,
        )

        SegmentedInputField(
            value = state.deceasedNationalId,
            onValueChange = { onIntent(PensionSurvivorIntent.DeceasedNationalIdChanged(it)) },
            slotCount = NATIONAL_ID_LENGTH,
            leadingIcon = vectorResource(Res.drawable.ic_number),
            showClearButton = true,
        )

        state.deceasedInfo?.let { deceasedInfo ->
            DeceasedInfoCard(info = deceasedInfo)

            // TODO(upload-component): wire shared image upload component from other branch (death cert / ID pages).
            Text(
                text = stringResource(Res.string.pension_survivor_deceased_documents_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PlaceholderUploadCard(
                    title = stringResource(Res.string.pension_survivor_deceased_death_certificate),
                    caption = stringResource(Res.string.pension_survivor_soon),
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                )
                PlaceholderUploadCard(
                    title = stringResource(Res.string.pension_survivor_deceased_id_pages),
                    caption = stringResource(Res.string.pension_survivor_soon),
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                )
            }
        }
    }
}

@Composable
private fun DeceasedInfoCard(
    info: DeceasedInfoPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val rows = deceasedInfoRows(info = info)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.bgSurface,
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .border(
                width = Thickness.border,
                color = colors.border,
                shape = RoundedCornerShape(CornerRadius.card),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        rows.forEachIndexed { index, row ->
            DetailRow(
                label = row.label,
                value = row.value,
                numeric = row.numeric,
            )
            if (index < rows.lastIndex) {
                TaminDivider()
            }
        }
    }
}

@Composable
private fun deceasedInfoRows(info: DeceasedInfoPR): List<DeceasedInfoRow> {
    val fullName = listOfNotNull(
        info.personal?.firstName?.takeIf(String::isNotBlank),
        info.personal?.lastName?.takeIf(String::isNotBlank),
    ).joinToString(" ")

    val ageValue = info.yearsAge
        ?.takeIf(String::isNotBlank)
        ?.let { stringResource(Res.string.verify_label_age_value, it) }
        ?: stringResource(Res.string.amount_unknown)

    return listOf(
        DeceasedInfoRow(
            label = stringResource(Res.string.girl_survivor_label_full_name),
            value = fullName.ifBlank { stringResource(Res.string.amount_unknown) },
            numeric = false,
        ),
        DeceasedInfoRow(
            label = stringResource(Res.string.verify_label_national_id),
            value = info.personal?.nationalId.orUnknown(),
            numeric = true,
        ),
        DeceasedInfoRow(
            label = stringResource(Res.string.girl_survivor_label_insurance_id),
            value = info.insuranceId.orUnknown(),
            numeric = true,
        ),
        DeceasedInfoRow(
            label = stringResource(Res.string.pension_survivor_deceased_death_date),
            value = info.deadDate.orUnknown(),
            numeric = false,
        ),
        DeceasedInfoRow(
            label = stringResource(Res.string.verify_label_age),
            value = ageValue,
            numeric = false,
        ),
        DeceasedInfoRow(
            label = stringResource(Res.string.pension_survivor_deceased_branch),
            value = info.branchName.orUnknown(),
            numeric = false,
        ),
        DeceasedInfoRow(
            label = stringResource(Res.string.verify_label_relation),
            value = info.related.orUnknown(),
            numeric = false,
        ),
    )
}

@Composable
private fun PlaceholderUploadCard(
    title: String,
    caption: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colors.bgSurface,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .border(
                width = Thickness.border,
                color = colors.border,
                shape = RoundedCornerShape(CornerRadius.lg),
            )
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
        )
        Text(
            text = caption,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        LoadingButton(
            text = caption,
            onClick = {},
            enabled = false,
            icon = icon,
            iconPosition = LoadingButtonIconPosition.TRAILING,
        )
    }
}

@Composable
private fun String?.orUnknown(): String {
    return this?.takeIf { it.isNotBlank() } ?: stringResource(Res.string.amount_unknown)
}

private data class DeceasedInfoRow(
    val label: String,
    val value: String,
    val numeric: Boolean,
)

private const val NATIONAL_ID_LENGTH = 10
