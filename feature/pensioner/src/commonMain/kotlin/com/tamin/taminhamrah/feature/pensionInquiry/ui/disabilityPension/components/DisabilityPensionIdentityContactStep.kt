package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.AddressError
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.LandlinePhoneError
import com.tamin.taminhamrah.ui.components.TaminCheckbox
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_dependents_collapse
import taminx.core.core_ui.disability_pension_dependents_show_details
import taminx.core.core_ui.disability_pension_identity_address_error_blank
import taminx.core.core_ui.disability_pension_identity_address_error_invalid
import taminx.core.core_ui.disability_pension_identity_address_error_length
import taminx.core.core_ui.disability_pension_identity_address_label
import taminx.core.core_ui.disability_pension_identity_field_placeholder
import taminx.core.core_ui.disability_pension_identity_age
import taminx.core.core_ui.disability_pension_identity_age_years
import taminx.core.core_ui.disability_pension_identity_confirm_error
import taminx.core.core_ui.disability_pension_identity_confirm_label
import taminx.core.core_ui.disability_pension_identity_full_name
import taminx.core.core_ui.disability_pension_identity_insurance_id
import taminx.core.core_ui.disability_pension_identity_issue_place
import taminx.core.core_ui.disability_pension_identity_mobile
import taminx.core.core_ui.disability_pension_identity_phone_error_blank
import taminx.core.core_ui.disability_pension_identity_phone_error_length
import taminx.core.core_ui.disability_pension_identity_phone_error_prefix
import taminx.core.core_ui.disability_pension_identity_phone_label
import taminx.core.core_ui.identity_field_birth_date
import taminx.core.core_ui.identity_field_father_name
import taminx.core.core_ui.identity_field_gender
import taminx.core.core_ui.identity_field_id_number
import taminx.core.core_ui.identity_field_national_code
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.model.personal.DisabilityPersonalInfoPR
import com.tamin.taminhamrah.model.personal.DisabilityPersonalPR

@Composable
fun DisabilityPensionIdentityContactStep(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val info = state.identityInfo?.personal

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (state.identityInfo == null) {
            DisabilityPensionInfoGridSkeleton()
        } else {
            IdentityInfoGrid(
                tiles = listOf(
                    stringResource(Res.string.disability_pension_identity_full_name) to
                        listOfNotNull(info?.firstName, info?.lastName).joinToString(" ").ifBlank { "-" },
                    stringResource(Res.string.disability_pension_identity_insurance_id) to
                        state.identityInfo.insuranceId.ifBlank { "-" },
                    stringResource(Res.string.identity_field_national_code) to
                        (info?.nationalId?.ifBlank { "-" } ?: "-"),
                    stringResource(Res.string.disability_pension_identity_mobile) to
                        state.identityInfo.mobileNumber.ifBlank { "-" },
                ),
            )

            if (state.isIdentityDetailsExpanded) {
                val ageLabel = state.identityAgeYears.ifBlank { null }
                    ?.let { stringResource(Res.string.disability_pension_identity_age_years, it) }
                    ?: "-"

                IdentityInfoGrid(
                    tiles = listOf(
                        stringResource(Res.string.identity_field_father_name) to
                            (info?.fatherName?.ifBlank { "-" } ?: "-"),
                        stringResource(Res.string.identity_field_id_number) to
                            (info?.idCardNumber?.ifBlank { "-" } ?: "-"),
                        stringResource(Res.string.identity_field_gender) to
                            (info?.genderDesc?.ifBlank { "-" } ?: "-"),
                        stringResource(Res.string.identity_field_birth_date) to
                            (info?.dateOfBirth?.ifBlank { "-" } ?: "-"),
                        stringResource(Res.string.disability_pension_identity_age) to ageLabel,
                        stringResource(Res.string.disability_pension_identity_issue_place) to
                            (info?.cityOfIssue?.ifBlank { "-" } ?: "-"),
                    ),
                )
            }

            Text(
                text = stringResource(
                    if (state.isIdentityDetailsExpanded) {
                        Res.string.disability_pension_dependents_collapse
                    } else {
                        Res.string.disability_pension_dependents_show_details
                    },
                ),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIntent(DisabilityPensionIntent.ToggleIdentityDetails) },
            )
        }

        val fieldPlaceholder = stringResource(Res.string.disability_pension_identity_field_placeholder)

        TaminStyledTextField(
            value = state.landlinePhone,
            onValueChange = { onIntent(DisabilityPensionIntent.LandlinePhoneChanged(it)) },
            label = stringResource(Res.string.disability_pension_identity_phone_label),
            placeholder = fieldPlaceholder,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isRequired = true,
            isValid = if (state.landlinePhoneError != null) false else null,
            errorText = state.landlinePhoneError?.let {
                stringResource(
                    when (it) {
                        LandlinePhoneError.Blank -> Res.string.disability_pension_identity_phone_error_blank
                        LandlinePhoneError.InvalidPrefix -> Res.string.disability_pension_identity_phone_error_prefix
                        LandlinePhoneError.InvalidLength -> Res.string.disability_pension_identity_phone_error_length
                    },
                )
            },
        )

        TaminStyledTextField(
            value = state.address,
            onValueChange = { onIntent(DisabilityPensionIntent.AddressChanged(it)) },
            label = stringResource(Res.string.disability_pension_identity_address_label),
            placeholder = fieldPlaceholder,
            isRequired = true,
            isValid = if (state.addressError != null) false else null,
            errorText = state.addressError?.let {
                stringResource(
                    when (it) {
                        AddressError.Blank -> Res.string.disability_pension_identity_address_error_blank
                        AddressError.TooShort -> Res.string.disability_pension_identity_address_error_length
                        AddressError.InvalidCharacters -> Res.string.disability_pension_identity_address_error_invalid
                    },
                )
            },
        )

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onIntent(DisabilityPensionIntent.IdentityConfirmedChanged(!state.isIdentityConfirmed)) },
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminCheckbox(checked = state.isIdentityConfirmed)
                Text(
                    text = stringResource(Res.string.disability_pension_identity_confirm_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    modifier = Modifier.weight(1f),
                )
            }

            if (state.showIdentityConfirmationError) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
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
                        text = stringResource(Res.string.disability_pension_identity_confirm_error),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.dangerText,
                    )
                }
            }
        }
    }
}

@Composable
private fun IdentityInfoGrid(
    tiles: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        tiles.chunked(2).forEach { rowTiles ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            ) {
                rowTiles.forEach { (label, value) ->
                    IdentityInfoTile(label = label, value = value, modifier = Modifier.weight(1f))
                }
                if (rowTiles.size == 1) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun IdentityInfoTile(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = modifier
            .background(colors.bgPage, RoundedCornerShape(CornerRadius.lg))
            .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.xxs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionIdentityContactStepPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionIdentityContactStep(
            state = DisabilityPensionUiState(
                identityInfo = DisabilityPersonalInfoPR(
                    branch = "1",
                    branchName = "شعبه یک",
                    confirmed = true,
                    insuranceId = "12345678",
                    mobileNumber = "09123456789",
                    personal = DisabilityPersonalPR(
                        firstName = "علی",
                        lastName = "علوی",
                        nationalId = "0012345678",
                        fatherName = "محمد",
                        idCardNumber = "123",
                        cityOfIssue = "تهران",
                        dateOfBirth = "1370/01/01",
                        genderDesc = "مرد"
                    ),
                    provinceName = "تهران",
                    work = null,
                    yearsAge = "33",
                    monthsAge = "5",
                    daysAge = "10",
                    strAge = "33 سال"
                ),
                identityAgeYears = "33",
                landlinePhone = "02188888888",
                address = "تهران، خیابان آزادی، پلاک ۱"
            ),
            onIntent = {}
        )
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionIdentityContactStepExpandedPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionIdentityContactStep(
            state = DisabilityPensionUiState(
                isIdentityDetailsExpanded = true,
                identityInfo = DisabilityPersonalInfoPR(
                    branch = "1",
                    branchName = "شعبه یک",
                    confirmed = true,
                    insuranceId = "12345678",
                    mobileNumber = "09123456789",
                    personal = DisabilityPersonalPR(
                        firstName = "علی",
                        lastName = "علوی",
                        nationalId = "0012345678",
                        fatherName = "محمد",
                        idCardNumber = "123",
                        cityOfIssue = "تهران",
                        dateOfBirth = "1370/01/01",
                        genderDesc = "مرد"
                    ),
                    provinceName = "تهران",
                    work = null,
                    yearsAge = "33",
                    monthsAge = "5",
                    daysAge = "10",
                    strAge = "33 سال"
                ),
                identityAgeYears = "33",
                landlinePhone = "02188888888",
                address = "تهران، خیابان آزادی، پلاک ۱"
            ),
            onIntent = {}
        )
    }
}
