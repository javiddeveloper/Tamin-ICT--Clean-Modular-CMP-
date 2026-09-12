package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.TaminStaticCheckbox
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_commitment_label
import taminx.core.core_ui.disability_pension_commitment_text
import taminx.core.core_ui.disability_pension_info_notice
import taminx.core.core_ui.disability_pension_show_rules
import taminx.core.core_ui.disability_pension_terms_validation_error

@Composable
fun DisabilityPensionTermsStep(
    state: DisabilityPensionUiState,
    onTermsAcceptedChange: (Boolean) -> Unit,
    onShowRules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        BannerCard(
            message = stringResource(Res.string.disability_pension_info_notice),
            type = BannerType.Info,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface(CornerRadius.chip)
                .background(colors.blueBg)
                .clickable(onClick = onShowRules)
                .padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.InsertDriveFile,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(IconSize.small),
            )
            Text(
                text = stringResource(Res.string.disability_pension_show_rules),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textSecondary,
            )
        }

        Column() {
            Text(
                text = stringResource(Res.string.disability_pension_commitment_label),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.sm)
                    .clickable { onTermsAcceptedChange(!state.isTermsAccepted) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.Top,
            ) {
                TaminStaticCheckbox(checked = state.isTermsAccepted)
                Text(
                    text = stringResource(
                        Res.string.disability_pension_commitment_text,
                        state.applicantGenderTitle,
                        state.applicantFullName,
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }

            if (state.showTermsValidationError) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.xs),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = colors.dangerText,
                        modifier = Modifier.size(IconSize.small),
                    )
                    Text(
                        text = stringResource(Res.string.disability_pension_terms_validation_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.dangerText,
                    )
                }
            }
        }
    }
}
