package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceIntent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceUiState
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.FuneralAllowanceInfoPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.RegisteredFuneralRequestPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.funeral_allowance_account_confirmation
import taminx.core.core_ui.funeral_allowance_account_number
import taminx.core.core_ui.funeral_allowance_applicant_info
import taminx.core.core_ui.funeral_allowance_bank_issue_desc
import taminx.core.core_ui.funeral_allowance_bank_name
import taminx.core.core_ui.funeral_allowance_death_date
import taminx.core.core_ui.funeral_allowance_deceased_info
import taminx.core.core_ui.funeral_allowance_deceased_inquired_info
import taminx.core.core_ui.funeral_allowance_deceased_national_code
import taminx.core.core_ui.funeral_allowance_deceased_national_code_hint
import taminx.core.core_ui.funeral_allowance_dependent_status
import taminx.core.core_ui.funeral_allowance_deposit_account
import taminx.core.core_ui.funeral_allowance_eligibility_banner
import taminx.core.core_ui.funeral_allowance_full_name
import taminx.core.core_ui.funeral_allowance_insurance_number
import taminx.core.core_ui.funeral_allowance_last_branch
import taminx.core.core_ui.funeral_allowance_mobile
import taminx.core.core_ui.funeral_allowance_national_id
import taminx.core.core_ui.funeral_allowance_national_id_length_hint
import taminx.core.core_ui.funeral_allowance_registered_request_title
import taminx.core.core_ui.funeral_allowance_relationship_with_insured
import taminx.core.core_ui.funeral_allowance_request_date
import taminx.core.core_ui.funeral_allowance_request_status
import taminx.core.core_ui.funeral_allowance_select_hint

@Composable
fun Step1ApplicantInfo(
    uiState: FuneralAllowanceUiState,
    onIntent: (FuneralAllowanceIntent) -> Unit,
) {
    val info = uiState.info ?: return
    val isEligible = uiState.deceasedValidation?.isEligible == true

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        ApplicantInfoCard(info)

        if (uiState.showBankAccountIssueFlow) {
            BankAccountIssueSection(
                request = info.registeredRequest,
            )
        } else {
            DeceasedInquirySection(
                uiState = uiState,
                onIntent = onIntent,
            )
            if (isEligible) {
                EligibilitySuccessBanner()
            }
        }
    }
}

@Composable
fun Step2DeceasedAndBankInfo(
    uiState: FuneralAllowanceUiState,
    onIntent: (FuneralAllowanceIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val deceasedValidation = uiState.deceasedValidation

    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        // Deceased Inquired Info Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface()
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            TaminText(
                text = stringResource(Res.string.funeral_allowance_deceased_inquired_info),
                style = MaterialTheme.typography.labelLarge.copy(color = taminColors.textPrimary)
            )
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_full_name),
                value = deceasedValidation?.deceasedFullName?.ifBlank { "—" } ?: "—",
                numeric = false,
            )
            HorizontalDivider(thickness = Thickness.border, color = taminColors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_national_id),
                value = uiState.deceasedNationalCode.ifBlank { "—" },
            )
            HorizontalDivider(thickness = Thickness.border, color = taminColors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_relationship_with_insured),
                value = deceasedValidation?.relationship?.ifBlank { "—" } ?: "—",
                numeric = false,
            )
            HorizontalDivider(thickness = Thickness.border, color = taminColors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_death_date),
                value = deceasedValidation?.deathDate?.ifBlank { "—" } ?: "—",
            )
            HorizontalDivider(thickness = Thickness.border, color = taminColors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_dependent_status),
                value = deceasedValidation?.dependentStatus?.ifBlank { "—" } ?: "—",
                numeric = false,
            )
        }

        // Bank Account Selection
        val selected = uiState.selectedBankAccount
        val bankName =
            selected?.bank?.label?.let { stringResource(it) } ?: selected?.bankNameFallback ?: ""
        val accountType = selected?.accountType?.label?.let { stringResource(it) }
            ?: selected?.accountTypeNameFallback ?: ""
        val title = if (accountType.isNotBlank()) "$bankName - $accountType" else bankName
        val displayValue = if (selected != null) {
            if (title.isNotBlank()) "$title - ${selected.accountNumber}" else selected.accountNumber
        } else {
            ""
        }

        TaminStyledTextField(
            value = displayValue,
            onValueChange = {},
            label = stringResource(Res.string.funeral_allowance_deposit_account),
            placeholder = stringResource(Res.string.funeral_allowance_select_hint),
            leadingIcon = Icons.Outlined.Home,
            trailingIcon = Icons.Default.KeyboardArrowDown,
            readOnly = true,
            isRequired = true,
            onClick = { onIntent(FuneralAllowanceIntent.ShowBankAccountBottomSheet(true)) }
        )

        // Confirmation Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, taminColors.border, RoundedCornerShape(12.dp))
                .background(taminColors.bgSurface)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }) {
                    onIntent(
                        FuneralAllowanceIntent.ToggleAccountConfirmation(!uiState.isAccountConfirmed)
                    )
                }
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = uiState.isAccountConfirmed,
                onCheckedChange = { onIntent(FuneralAllowanceIntent.ToggleAccountConfirmation(it)) },
                colors = CheckboxDefaults.colors(
                    checkedColor = taminColors.blueText,
                    uncheckedColor = taminColors.border
                )
            )
            Spacer(modifier = Modifier.width(Spacing.xs))
            TaminText(
                modifier = Modifier.padding(top = 13.dp),
                text = stringResource(Res.string.funeral_allowance_account_confirmation),
                color = taminColors.textSecondary,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
fun EligibilitySuccessBanner() {
    val taminColors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(taminColors.greenBg)
            .border(1.dp, taminColors.greenBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = taminColors.greenText,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        TaminText(
            textAlign = TextAlign.Justify,
            text = stringResource(Res.string.funeral_allowance_eligibility_banner),
            color = taminColors.greenText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ApplicantInfoCard(info: FuneralAllowanceInfoPR) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        val colors = LocalTaminColors.current
        TaminText(
            text = stringResource(Res.string.funeral_allowance_applicant_info),
            style = MaterialTheme.typography.labelLarge.copy(color = colors.textPrimary)
        )
        DetailRow(
            label = stringResource(Res.string.funeral_allowance_full_name),
            value = info.fullName.ifBlank { "—" },
            numeric = false,
        )
        HorizontalDivider(thickness = Thickness.border, color = colors.divider)
        DetailRow(
            label = stringResource(Res.string.funeral_allowance_insurance_number),
            value = info.insuranceNumber.ifBlank { "—" },
        )
        HorizontalDivider(thickness = Thickness.border, color = colors.divider)
        DetailRow(
            label = stringResource(Res.string.funeral_allowance_account_number),
            value = info.bankAccount.ifBlank { "—" },
        )
        HorizontalDivider(thickness = Thickness.border, color = colors.divider)
        DetailRow(
            label = stringResource(Res.string.funeral_allowance_bank_name),
            value = info.bankName.ifBlank { "—" },
            numeric = false,
        )
        HorizontalDivider(thickness = Thickness.border, color = colors.divider)
        DetailRow(
            label = stringResource(Res.string.funeral_allowance_mobile),
            value = info.mobileNumber.ifBlank { "—" },
        )
        HorizontalDivider(thickness = Thickness.border, color = colors.divider)
        DetailRow(
            label = stringResource(Res.string.funeral_allowance_last_branch),
            value = info.branchName.ifBlank { "—" },
            numeric = false,
        )
    }
}

@Composable
private fun DeceasedInquirySection(
    uiState: FuneralAllowanceUiState,
    onIntent: (FuneralAllowanceIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val isEligible = uiState.deceasedValidation?.isEligible == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        TaminText(
            text = stringResource(Res.string.funeral_allowance_deceased_info),
            style = MaterialTheme.typography.labelLarge.copy(color = taminColors.textPrimary)
        )

        TaminStyledTextField(
            value = uiState.deceasedNationalCode,
            onValueChange = { onIntent(FuneralAllowanceIntent.DeceasedNationalCodeChanged(it)) },
            label = stringResource(Res.string.funeral_allowance_deceased_national_code) + " *",
            placeholder = stringResource(Res.string.funeral_allowance_national_id_length_hint),
            isValid = if (uiState.deceasedNationalCodeError != null) false else null,
            errorText = uiState.deceasedNationalCodeError,
            readOnly = isEligible,
        )

        TaminText(
            text = stringResource(Res.string.funeral_allowance_deceased_national_code_hint),
            color = taminColors.textSecondary,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun BankAccountIssueSection(
    request: RegisteredFuneralRequestPR?,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        SectionLabel(text = stringResource(Res.string.funeral_allowance_registered_request_title))
        TaminText(
            text = stringResource(Res.string.funeral_allowance_bank_issue_desc),
            color = colors.textSecondary,
        )
        if (request != null) {
            TaminDivider()
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_deceased_national_code),
                value = request.deceasedNationalId.ifBlank { "—" },
            )
            HorizontalDivider(thickness = Thickness.border, color = colors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_death_date),
                value = request.deathDate.ifBlank { "—" },
            )
            HorizontalDivider(thickness = Thickness.border, color = colors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_request_date),
                value = request.requestDate.ifBlank { "—" },
            )
            HorizontalDivider(thickness = Thickness.border, color = colors.divider)
            DetailRow(
                label = stringResource(Res.string.funeral_allowance_request_status),
                value = request.statusName.ifBlank { "—" },
                numeric = false,
            )
        }
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

@PreviewRtlTheme
@Composable
private fun Step1Preview() {
    PreviewRtlThemeContent {
        Column(modifier = Modifier.background(LocalTaminColors.current.bgPage).padding(16.dp)) {
            Step1ApplicantInfo(
                uiState = FuneralAllowanceUiState(
                    info = FuneralAllowanceInfoPR(
                        fullName = "نام متقاضی",
                        firstName = "تست",
                        lastName = "تست",
                        insuranceNumber = "12345678",
                        bankAccount = "1234567890",
                        bankName = "بانک تست",
                        mobileNumber = "09123456789",
                        branchName = "شعبه تست",
                        branchCode = "123",
                        nationalCode = "1234567890",
                        deceasedNationalId = "",
                        requestHelpType = "",
                        hasBankAccountIssue = false,
                        registeredRequest = null
                    )
                ),
                onIntent = {}
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun Step2Preview() {
    PreviewRtlThemeContent {
        Column(modifier = Modifier.background(LocalTaminColors.current.bgPage).padding(16.dp)) {
            Step2DeceasedAndBankInfo(
                uiState = FuneralAllowanceUiState(
                    deceasedNationalCode = "0039073041",
                    deceasedValidation = com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.DeceasedValidationPR(
                        deceasedFullName = "علي اكبر شيخ عباسي",
                        relationship = "همسر",
                        isEligible = true,
                        message = "دارای شرایط می باشید",
                        dependentStatus = "مستمري بگير بازنشسته",
                        deathDate = "1405/01/10"
                    ),
                    selectedBankAccount = com.tamin.taminhamrah.model.bankAccount.BankAccountPR(
                        id = 1,
                        bank = null,
                        bankNameFallback = "بانک ملت",
                        accountType = null,
                        accountTypeNameFallback = "کوتاه‌مدت",
                        accountNumber = "1234567890",
                        startDate = null,
                        endDate = null,
                        isActive = true
                    ),
                    isAccountConfirmed = true
                ),
                onIntent = {}
            )
        }
    }
}
