package com.tamin.taminhamrah.feature.fractionContract.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.contractFlow.ContractEligibilityReason
import com.tamin.taminhamrah.feature.fractionContract.ui.preview.FractionContractPreviewData
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.fraction_contract_check_age
import taminx.core.core_ui.fraction_contract_check_no_active
import taminx.core.core_ui.fraction_contract_check_primary_insured
import taminx.core.core_ui.fraction_contract_eligible_message
import taminx.core.core_ui.fraction_contract_eligibility_section_subtitle
import taminx.core.core_ui.fraction_contract_eligibility_section_title
import taminx.core.core_ui.fraction_contract_ineligible_message
import taminx.core.core_ui.fraction_contract_label_branch
import taminx.core.core_ui.fraction_contract_label_contract_type
import taminx.core.core_ui.fraction_contract_label_insurance_id
import taminx.core.core_ui.fraction_contract_title
import taminx.core.core_ui.ic_error
import taminx.core.core_ui.ic_success

@Composable
internal fun FractionEligibilityStep(
    eligibility: FractionEligibilityPR?,
    insuranceId: String,
    isEligible: Boolean,
    birthEpoch: Long,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.cardCompact)
    val reason = eligibilityReasonText(eligibility)
    val ageYears = ageYears(eligibility, birthEpoch)
    val statusColor = if (isEligible) colors.greenText else colors.dangerText
    val statusBg = if (isEligible) colors.greenBg else colors.dangerBg

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.fraction_contract_eligibility_section_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.fraction_contract_eligibility_section_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(colors.bgSurface)
                .border(Thickness.border, colors.border, cardShape)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            DetailRow(
                label = stringResource(Res.string.fraction_contract_label_insurance_id),
                value = insuranceId,
                numeric = true,
            )
            DetailRow(
                label = stringResource(Res.string.fraction_contract_label_branch),
                value = eligibility?.branchAddress.orEmpty(),
                numeric = false,
            )
            DetailRow(
                label = stringResource(Res.string.fraction_contract_label_contract_type),
                value = stringResource(Res.string.fraction_contract_title),
                numeric = false,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(statusBg)
                .border(Thickness.border, statusColor.copy(alpha = 0.25f), cardShape)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.Top,
            ) {
                Icon(
                    imageVector = vectorResource(
                        if (isEligible) Res.drawable.ic_success else Res.drawable.ic_error,
                    ),
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(IconSize.banner),
                )
                Text(
                    text = if (isEligible) {
                        stringResource(Res.string.fraction_contract_eligible_message, reason)
                    } else {
                        stringResource(Res.string.fraction_contract_ineligible_message, reason)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor,
                    modifier = Modifier.weight(1f),
                )

            }

            if (isEligible) {
                EligibilityCheckRow(
                    text = stringResource(
                        Res.string.fraction_contract_check_age,
                        (ageYears?.toString() ?: "—").toPersianDigits(),
                    ),
                )
                EligibilityCheckRow(
                    text = stringResource(Res.string.fraction_contract_check_no_active),
                )
                EligibilityCheckRow(
                    text = stringResource(Res.string.fraction_contract_check_primary_insured),
                )
            }
        }
    }
}

@Composable
private fun EligibilityCheckRow(text: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier
                .size(IconSize.small)
                .clip(CircleShape)
                .background(colors.bgSurface)
                .padding(Spacing.xxs),
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun eligibilityReasonText(eligibility: FractionEligibilityPR?): String {
    val status = eligibility?.eligibilityStatus ?: -1
    return when (status) {
        1 -> stringResource(ContractEligibilityReason.MIN_TEN_YEARS_HISTORY.textRes)
        2 -> stringResource(ContractEligibilityReason.AGE_UNDER_FIFTY.textRes)
        3 -> {
            val age = eligibility?.newAge.orEmpty()
            val year = age.take(2).ifBlank { "—" }
            val month = age.drop(2).take(2).ifBlank { "—" }
            val day = age.drop(4).take(2).ifBlank { "—" }
            val ageFormatted = listOf(year, month, day).joinToString(" / ") { it.toPersianDigits() }
            stringResource(
                ContractEligibilityReason.HISTORY_AND_AGE_DYNAMIC.textRes,
                (eligibility?.history ?: 0).toString().toPersianDigits(),
                ageFormatted,
            )
        }
        4 -> stringResource(ContractEligibilityReason.MAX_TWO_FREELANCE_CONTRACTS.textRes)
        else -> stringResource(ContractEligibilityReason.AGE_HISTORY_NOT_MET.textRes)
    }
}

private fun ageYears(eligibility: FractionEligibilityPR?, birthEpoch: Long): Int? {
    val fromNewAge = eligibility?.newAge?.take(2)?.toIntOrNull()
    if (fromNewAge != null) return fromNewAge
    return PersianDateFormatter.ageYearsFromBirthEpoch(birthEpoch.takeIf { it > 0L })
}

@PreviewRtlTheme
@Composable
private fun FractionEligibilityStepEligiblePreview() {
    PreviewRtlThemeContent {
        FractionEligibilityStep(
            eligibility = FractionContractPreviewData.eligibleEligibility,
            insuranceId = FractionContractPreviewData.registrationInfo.insuranceId,
            isEligible = true,
            birthEpoch = FractionContractPreviewData.registrationInfo.dateOfBirthEpoch ?: 0L,
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionEligibilityStepIneligiblePreview() {
    PreviewRtlThemeContent {
        FractionEligibilityStep(
            eligibility = FractionContractPreviewData.ineligibleEligibility,
            insuranceId = FractionContractPreviewData.registrationInfo.insuranceId,
            isEligible = false,
            birthEpoch = FractionContractPreviewData.registrationInfo.dateOfBirthEpoch ?: 0L,
            modifier = Modifier.padding(Spacing.page),
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionEligibilityStepEligibleDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        FractionEligibilityStep(
            eligibility = FractionContractPreviewData.eligibleEligibility,
            insuranceId = FractionContractPreviewData.registrationInfo.insuranceId,
            isEligible = true,
            birthEpoch = FractionContractPreviewData.registrationInfo.dateOfBirthEpoch ?: 0L,
            modifier = Modifier.padding(Spacing.page),
        )
    }
}
