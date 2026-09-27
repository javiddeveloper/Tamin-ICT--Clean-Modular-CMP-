package com.tamin.taminhamrah.feature.girlSurvivor.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorIntent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorUiState
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.girl_survivor_address_label
import taminx.core.core_ui.girl_survivor_commitment_body
import taminx.core.core_ui.girl_survivor_commitment_confirm_label
import taminx.core.core_ui.girl_survivor_commitment_title
import taminx.core.core_ui.girl_survivor_deceased_national_id_label
import taminx.core.core_ui.girl_survivor_deceased_pension_id_label
import taminx.core.core_ui.girl_survivor_download_file
import taminx.core.core_ui.girl_survivor_phone_label
import taminx.core.core_ui.girl_survivor_relation_daughter
import taminx.core.core_ui.girl_survivor_zipcode_label
import taminx.core.core_ui.ic_tamin_download

@Composable
fun GirlSurvivorCommitmentStep(
    state: GirlSurvivorUiState,
    onIntent: (GirlSurvivorIntent) -> Unit,
    onDownloadPdf: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val deceasedIdentifier = if (state.usePensionIdMode) {
        state.deceasedPensionId
    } else {
        state.deceasedNationalCode
    }
    val deceasedLabel = if (state.usePensionIdMode) {
        stringResource(Res.string.girl_survivor_deceased_pension_id_label)
    } else {
        stringResource(Res.string.girl_survivor_deceased_national_id_label)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDownloadPdf)
                .padding(vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_download),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(Spacing.lg),
            )
            Text(
                text = stringResource(Res.string.girl_survivor_download_file),
                style = MaterialTheme.typography.labelLarge,
                color = colors.blueText,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.card))
                .background(colors.bgSurface)
                .border(1.dp, colors.blueBorder, RoundedCornerShape(CornerRadius.card))
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Text(
                text = stringResource(Res.string.girl_survivor_commitment_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )

            Text(
                text = stringResource(Res.string.girl_survivor_commitment_body, state.fullName, state.nationalId),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
            )

            HorizontalDivider(color = colors.border.copy(alpha = 0.5f))

            DetailRow(
                label = stringResource(Res.string.girl_survivor_relation_daughter),
                value = stringResource(Res.string.girl_survivor_relation_daughter),
                numeric = false,
            )
            DetailRow(label = deceasedLabel, value = deceasedIdentifier)
            DetailRow(label = stringResource(Res.string.girl_survivor_zipcode_label), value = state.zipCode)
            DetailRow(label = stringResource(Res.string.girl_survivor_phone_label), value = state.phoneNumber)
            DetailRow(
                label = stringResource(Res.string.girl_survivor_address_label),
                value = state.address.ifBlank { "-" },
                numeric = false,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface(CornerRadius.md)
                    .clickable { onIntent(GirlSurvivorIntent.PdfConfirmedChanged(!state.isPdfConfirmed)) }
                    .padding(Spacing.smd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = state.isPdfConfirmed,
                    onClick = { onIntent(GirlSurvivorIntent.PdfConfirmedChanged(!state.isPdfConfirmed)) },
                )
                Text(
                    text = stringResource(Res.string.girl_survivor_commitment_confirm_label),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}
