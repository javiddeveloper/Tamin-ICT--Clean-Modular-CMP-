package com.tamin.taminhamrah.ui.contractFlow

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_field_birth_date
import taminx.core.core_ui.contract_field_contract_type
import taminx.core.core_ui.contract_field_full_name
import taminx.core.core_ui.contract_field_national_id
import taminx.core.core_ui.contract_optional_age_max
import taminx.core.core_ui.contract_optional_age_min
import taminx.core.core_ui.contract_optional_check_age
import taminx.core.core_ui.contract_optional_check_age_fallback
import taminx.core.core_ui.contract_optional_check_history
import taminx.core.core_ui.contract_optional_check_no_active
import taminx.core.core_ui.contract_optional_eligibility_ok
import taminx.core.core_ui.contract_optional_info_definition
import taminx.core.core_ui.contract_optional_info_treatment_in_rate
import taminx.core.core_ui.contract_step_reg_banner_1
import taminx.core.core_ui.contract_step_reg_banner_2_student

@Composable
fun ContractRegistrationStepContent(
    info: RegistrationInfoPR,
    insuranceTypeLabel: String,
    eligibility: ContractEligibilityPR? = null,
    genderGateError: String? = null,
    preflightGateError: String? = null,
    isLoading: Boolean = false,
    isOptionalInsurance: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        ContractRegistrationStepShimmerSkeleton(modifier = modifier)
        return
    }

    if (isOptionalInsurance) {
        OptionalRegistrationStepContent(
            info = info,
            insuranceTypeLabel = insuranceTypeLabel,
            eligibility = eligibility,
            genderGateError = genderGateError,
            preflightGateError = preflightGateError,
            modifier = modifier,
        )
    } else {
        SharedRegistrationStepContent(
            info = info,
            insuranceTypeLabel = insuranceTypeLabel,
            eligibility = eligibility,
            genderGateError = genderGateError,
            preflightGateError = preflightGateError,
            modifier = modifier,
        )
    }
}

@Composable
private fun SharedRegistrationStepContent(
    info: RegistrationInfoPR,
    insuranceTypeLabel: String,
    eligibility: ContractEligibilityPR?,
    genderGateError: String?,
    preflightGateError: String?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        RegistrationBannerCard(insuranceId = info.insuranceId)

        if (genderGateError != null || preflightGateError != null || (eligibility != null && !eligibility.isEligible)) {
            val errorMsg = genderGateError
                ?: preflightGateError
                ?: eligibility?.eligibilityMessage(insuranceTypeLabel).orEmpty()
            BannerCard(
                message = errorMsg,
                type = BannerType.Error,
            )
        } else {
            EligibilityBannerCard(
                insuranceTypeLabel = insuranceTypeLabel,
                eligibility = eligibility,
            )
        }

        RegistrationIdentityGrid(
            info = info,
            insuranceTypeLabel = insuranceTypeLabel,
        )
    }
}

@Composable
private fun OptionalRegistrationStepContent(
    info: RegistrationInfoPR,
    insuranceTypeLabel: String,
    eligibility: ContractEligibilityPR?,
    genderGateError: String?,
    preflightGateError: String?,
    modifier: Modifier = Modifier,
) {
    val hasBlockingError = genderGateError != null ||
        preflightGateError != null ||
        (eligibility != null && !eligibility.isEligible)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        OptionalRegistrationIdentityCard(
            info = info,
            insuranceTypeLabel = insuranceTypeLabel,
        )
        OptionalRegistrationEligibilityCard(
            insuranceTypeLabel = insuranceTypeLabel,
            eligibility = eligibility,
            genderGateError = genderGateError,
            preflightGateError = preflightGateError,
            hasBlockingError = hasBlockingError,
            ageYears = PersianDateFormatter.ageYearsFromBirthEpoch(info.dateOfBirthEpoch),
        )
    }
}

@Composable
private fun OptionalRegistrationIdentityCard(
    info: RegistrationInfoPR,
    insuranceTypeLabel: String,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        RegistrationBannerCard(insuranceId = info.insuranceId)
        RegistrationIdentityGrid(
            info = info,
            insuranceTypeLabel = insuranceTypeLabel,
        )
        BannerCard(
            message = stringResource(Res.string.contract_optional_info_definition),
            type = BannerType.Info,
        )
    }
}

@Composable
private fun OptionalRegistrationEligibilityCard(
    insuranceTypeLabel: String,
    eligibility: ContractEligibilityPR?,
    genderGateError: String?,
    preflightGateError: String?,
    hasBlockingError: Boolean,
    ageYears: Int?,
) {
    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (hasBlockingError) {
            val errorMsg = genderGateError
                ?: preflightGateError
                ?: eligibility?.eligibilityMessage(insuranceTypeLabel).orEmpty()
            BannerCard(
                message = errorMsg,
                type = BannerType.Error,
            )
        } else {
            BannerCard(
                message = stringResource(Res.string.contract_optional_eligibility_ok),
                type = BannerType.Success,
            )
            OptionalEligibilityChecklist(ageYears = ageYears)
            BannerCard(
                message = stringResource(Res.string.contract_optional_info_treatment_in_rate),
                type = BannerType.Info,
            )
        }
    }
}

@Composable
private fun OptionalEligibilityChecklist(ageYears: Int?) {
    val ageMin = stringResource(Res.string.contract_optional_age_min)
    val ageMax = stringResource(Res.string.contract_optional_age_max)
    val ageCheck = if (ageYears != null) {
        stringResource(
            Res.string.contract_optional_check_age,
            ageYears.toString().toPersianDigits(),
            ageMin,
            ageMax,
        )
    } else {
        stringResource(
            Res.string.contract_optional_check_age_fallback,
            ageMin,
            ageMax,
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        OptionalEligibilityCheckRow(text = ageCheck)
        OptionalEligibilityCheckRow(
            text = stringResource(Res.string.contract_optional_check_no_active),
        )
        OptionalEligibilityCheckRow(
            text = stringResource(Res.string.contract_optional_check_history),
        )
    }
}

@Composable
private fun OptionalEligibilityCheckRow(text: String) {
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
                .background(colors.greenBg)
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
private fun RegistrationIdentityGrid(
    info: RegistrationInfoPR,
    insuranceTypeLabel: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DetailInfoTile(
                label = stringResource(Res.string.contract_field_full_name),
                value = info.fullName,
                numeric = false,
                modifier = Modifier.weight(1f),
            )
            DetailInfoTile(
                label = stringResource(Res.string.contract_field_national_id),
                value = info.nationalId,
                numeric = true,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            DetailInfoTile(
                label = stringResource(Res.string.contract_field_birth_date),
                value = info.birthDateFormatted,
                numeric = false,
                modifier = Modifier.weight(1f),
            )
            DetailInfoTile(
                label = stringResource(Res.string.contract_field_contract_type),
                value = insuranceTypeLabel,
                numeric = false,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun RegistrationBannerCard(
    insuranceId: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val bannerShape = RoundedCornerShape(CornerRadius.card)

    val text = buildAnnotatedString {
        val template = stringResource(Res.string.contract_step_reg_banner_1, "%1\$s")
        val marker = "%1\$s"
        val markerIndex = template.indexOf(marker)

        if (markerIndex == -1) {
            append(template)
        } else {
            append(template.substring(0, markerIndex))
            withStyle(
                SpanStyle(
                    color = colors.greenText,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                append(insuranceId.toPersianDigits())
            }
            append(template.substring(markerIndex + marker.length))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(bannerShape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenText.copy(alpha = 0.25f), bannerShape)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@Composable
private fun EligibilityBannerCard(
    insuranceTypeLabel: String,
    eligibility: ContractEligibilityPR?,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val bannerShape = RoundedCornerShape(CornerRadius.card)

    val text = buildAnnotatedString {
        val template = stringResource(Res.string.contract_step_reg_banner_2_student, "%1\$s")
        val marker = "%1\$s"
        val markerIndex = template.indexOf(marker)

        if (markerIndex == -1) {
            append(template)
        } else {
            append(template.substring(0, markerIndex))
            withStyle(
                SpanStyle(
                    color = colors.greenText,
                    fontWeight = FontWeight.Bold,
                ),
            ) {
                append(insuranceTypeLabel)
            }
            append(template.substring(markerIndex + marker.length))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(bannerShape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenText.copy(alpha = 0.25f), bannerShape)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = colors.greenText,
            modifier = Modifier.size(IconSize.small),
        )
    }
}

@Composable
private fun DetailInfoTile(
    label: String,
    value: String,
    numeric: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)

    Column(
        modifier = modifier
            .clip(shape)
            .background(colors.bgPage)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = colors.textMuted,
        )
        if (numeric) {
            NumericText(
                text = value.toPersianDigits(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
            )
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun ContractRegistrationStepContentPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractRegistrationStepContent(
            info = RegistrationInfoPR(
                fullName = "علی محمدی",
                nationalId = "0012345678",
                birthDateFormatted = "1375/04/15",
                insuranceId = "12345678",
                genderCode = "01",
                address = "تهران",
                zipCode = "1234567890",
                phoneNumber = "02166001234",
                mobileNumber = "09121234567",
                hasMobile = true,
            ),
            insuranceTypeLabel = "بیمه دانشجویی",
            eligibility = ContractEligibilityPR(
                statusCode = 1,
                isEligible = true,
                reason = com.tamin.taminhamrah.contractFlow.ContractEligibilityReason.AGE_UNDER_FIFTY,
            ),
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun OptionalContractRegistrationStepContentPreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractRegistrationStepContent(
            info = RegistrationInfoPR(
                fullName = "رضا دریکوند",
                nationalId = "4060434061",
                birthDateFormatted = "1362/04/12",
                insuranceId = "0019273648",
                genderCode = "01",
                address = "تهران",
                zipCode = "1234567890",
                phoneNumber = "02166001234",
                mobileNumber = "09121234567",
                hasMobile = true,
                dateOfBirthEpoch = 427_939_200_000L,
            ),
            insuranceTypeLabel = "بیمه اختیاری",
            eligibility = ContractEligibilityPR(
                statusCode = 2,
                isEligible = true,
                reason = com.tamin.taminhamrah.contractFlow.ContractEligibilityReason.AGE_UNDER_FIFTY,
            ),
            isOptionalInsurance = true,
        )
    }
}

@com.tamin.taminhamrah.ui.PreviewRtlTheme
@Composable
private fun ContractRegistrationStepContentIneligiblePreview() {
    com.tamin.taminhamrah.ui.PreviewRtlThemeContent {
        ContractRegistrationStepContent(
            info = RegistrationInfoPR(
                fullName = "سارا احمدی",
                nationalId = "0012345679",
                birthDateFormatted = "1340/01/01",
                insuranceId = "87654321",
                genderCode = "02",
                address = "اصفهان",
                zipCode = "1234567890",
                phoneNumber = "03131234567",
                mobileNumber = "09131234567",
                hasMobile = true,
            ),
            insuranceTypeLabel = "بیمه دانشجویی",
            eligibility = ContractEligibilityPR(
                statusCode = 5,
                isEligible = false,
                reason = com.tamin.taminhamrah.contractFlow.ContractEligibilityReason.AGE_HISTORY_NOT_MET,
            ),
        )
    }
}
