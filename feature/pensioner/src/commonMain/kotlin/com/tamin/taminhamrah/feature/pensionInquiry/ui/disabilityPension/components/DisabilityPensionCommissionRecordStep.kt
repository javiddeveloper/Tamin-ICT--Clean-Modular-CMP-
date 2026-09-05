package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.StatTile
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_commission_objection_no
import taminx.core.core_ui.disability_pension_commission_objection_warning
import taminx.core.core_ui.disability_pension_commission_objection_yes
import taminx.core.core_ui.disability_pension_commission_opinion_title
import taminx.core.core_ui.disability_pension_commission_question
import taminx.core.core_ui.disability_pension_history_objection_link
import taminx.core.core_ui.disability_pension_insurance_record_day
import taminx.core.core_ui.disability_pension_insurance_record_month
import taminx.core.core_ui.disability_pension_insurance_record_title
import taminx.core.core_ui.disability_pension_insurance_record_total_days_label
import taminx.core.core_ui.disability_pension_insurance_record_year
import taminx.core.core_ui.disability_pension_registered_requests_button
import taminx.core.core_ui.disability_pension_view_file_button

@Composable
fun DisabilityPensionCommissionRecordStep(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Text(
                text = stringResource(Res.string.disability_pension_insurance_record_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                StatTile(
                    label = stringResource(Res.string.disability_pension_insurance_record_year),
                    amount = if (state.isInsuranceRecordLoading) null else state.insuranceRecordYears,
                    containerColor = colors.bgPage,
                    contentColor = colors.textPrimary,
                    labelColor = colors.textMuted,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(Res.string.disability_pension_insurance_record_month),
                    amount = if (state.isInsuranceRecordLoading) null else state.insuranceRecordMonths,
                    containerColor = colors.bgPage,
                    contentColor = colors.textPrimary,
                    labelColor = colors.textMuted,
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    label = stringResource(Res.string.disability_pension_insurance_record_day),
                    amount = if (state.isInsuranceRecordLoading) null else state.insuranceRecordDays,
                    containerColor = colors.bgPage,
                    contentColor = colors.textPrimary,
                    labelColor = colors.textMuted,
                    modifier = Modifier.weight(1f),
                )
            }

            StatTile(
                label = stringResource(Res.string.disability_pension_insurance_record_total_days_label),
                amount = if (state.isInsuranceRecordLoading) null else state.insuranceRecordTotalDays,
                containerColor = colors.blueBg,
                contentColor = colors.blueText,
                labelColor = colors.textSecondary,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = stringResource(Res.string.disability_pension_history_objection_link),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textSecondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { onIntent(DisabilityPensionIntent.HistoryObjectionLinkClicked) },
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            Text(
                text = stringResource(Res.string.disability_pension_commission_opinion_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
            Text(
                text = stringResource(Res.string.disability_pension_commission_question),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                CommissionObjectionOption(
                    label = stringResource(Res.string.disability_pension_commission_objection_no),
                    selected = state.hasCommissionObjection == false,
                    selectedContainerColor = colors.blueBg,
                    selectedBorderColor = colors.blueBorder,
                    radioColor = colors.blueText,
                    onClick = { onIntent(DisabilityPensionIntent.CommissionObjectionChanged(false)) },
                    modifier = Modifier.weight(1f),
                )
                CommissionObjectionOption(
                    label = stringResource(Res.string.disability_pension_commission_objection_yes),
                    selected = state.hasCommissionObjection == true,
                    selectedContainerColor = colors.dangerBorder,
                    selectedBorderColor = colors.dangerBorder,
                    radioColor = colors.dangerText,
                    onClick = { onIntent(DisabilityPensionIntent.CommissionObjectionChanged(true)) },
                    modifier = Modifier.weight(1f),
                )
            }

            if (state.hasCommissionObjection == true) {
                BannerCard(
                    message = stringResource(Res.string.disability_pension_commission_objection_warning),
                    type = BannerType.Warning,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                SecondaryActionButton(
                    text = stringResource(Res.string.disability_pension_view_file_button),
                    icon = Icons.Outlined.Description,
                    onClick = { onIntent(DisabilityPensionIntent.ShowMedicalCommissionPdfViewerClicked) },
                    modifier = Modifier.weight(1f),
                )
                SecondaryActionButton(
                    text = stringResource(Res.string.disability_pension_registered_requests_button),
                    icon = Icons.AutoMirrored.Outlined.ListAlt,
                    onClick = { onIntent(DisabilityPensionIntent.ShowRegisteredRequestsClicked) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun CommissionObjectionOption(
    label: String,
    selected: Boolean,
    selectedContainerColor: Color,
    selectedBorderColor: Color,
    radioColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.md)

    Row(
        modifier = modifier
            .clip(shape)
            .background(if (selected) selectedContainerColor else colors.bgSurface, shape)
            .border(1.dp, if (selected) selectedBorderColor else colors.border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.smPlus),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = radioColor),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.md)

    Row(
        modifier = modifier
            .clip(shape)
            .background(colors.bgPage, shape)
            .border(1.dp, colors.border, shape)
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.smd),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textSecondary,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionCommissionRecordStepPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionCommissionRecordStep(
            state = DisabilityPensionUiState(
                insuranceRecordDays = "18",
                insuranceRecordMonths = "3",
                insuranceRecordYears = "14",
                insuranceRecordTotalDays = "5218",
            ),
            onIntent = {},
        )
    }
}
