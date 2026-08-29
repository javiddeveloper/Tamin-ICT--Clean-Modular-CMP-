package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.girl_survivor_download_form
import taminx.core.core_ui.ic_tamin_download
import taminx.core.core_ui.pension_survivor_commitment_body
import taminx.core.core_ui.pension_survivor_commitment_confirm_label
import taminx.core.core_ui.pension_survivor_commitment_title
import taminx.core.core_ui.pension_survivor_final_request_unavailable

@Composable
fun FinalStep(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
    onDownloadPdf: () -> Unit,
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
        if (state.requestId == null) {
            BannerCard(
                message = stringResource(Res.string.pension_survivor_final_request_unavailable),
            )
        }

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
                modifier = Modifier.size(IconSize.small),
            )
            Text(
                text = stringResource(Res.string.girl_survivor_download_form),
                style = MaterialTheme.typography.labelLarge,
                color = colors.blueText,
            )
        }

        Column(
            modifier = Modifier
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
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Text(
                text = stringResource(Res.string.pension_survivor_commitment_title),
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(
                    Res.string.pension_survivor_commitment_body,
                    state.applicantFullName.ifBlank { "..." },
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textMuted,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface(CornerRadius.md)
                    .clickable {
                        onIntent(
                            PensionSurvivorIntent.PdfConfirmedChanged(!state.isPdfConfirmed),
                        )
                    }
                    .padding(Spacing.smd),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = state.isPdfConfirmed,
                    onClick = {
                        onIntent(
                            PensionSurvivorIntent.PdfConfirmedChanged(!state.isPdfConfirmed),
                        )
                    },
                )
                Text(
                    text = stringResource(Res.string.pension_survivor_commitment_confirm_label),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.xl))
    }
}
