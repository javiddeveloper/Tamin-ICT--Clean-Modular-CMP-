package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.FuneralAllowanceViewModel
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceEvent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceIntent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceUiState
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.FuneralAllowanceInfoPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.RegisteredFuneralRequestPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceStep1ShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceStepScaffold
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.NoBankAccountDialog
import com.tamin.taminhamrah.ui.components.SectionLabel
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.funeral_allowance_account_number
import taminx.core.core_ui.funeral_allowance_applicant_info
import taminx.core.core_ui.funeral_allowance_bank_issue_desc
import taminx.core.core_ui.funeral_allowance_bank_name
import taminx.core.core_ui.funeral_allowance_check_eligibility
import taminx.core.core_ui.funeral_allowance_death_date
import taminx.core.core_ui.funeral_allowance_deceased_info
import taminx.core.core_ui.funeral_allowance_deceased_national_code
import taminx.core.core_ui.funeral_allowance_empty
import taminx.core.core_ui.funeral_allowance_full_name
import taminx.core.core_ui.funeral_allowance_insurance_number
import taminx.core.core_ui.funeral_allowance_last_branch
import taminx.core.core_ui.funeral_allowance_mobile
import taminx.core.core_ui.funeral_allowance_registered_request_title
import taminx.core.core_ui.funeral_allowance_request_date
import taminx.core.core_ui.funeral_allowance_request_status
import taminx.core.core_ui.funeral_allowance_title

@Composable
fun FuneralAllowanceRoute(
    viewModel: FuneralAllowanceViewModel,
    onBackClicked: () -> Unit,
    onNavigateToBankAccount: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    var dialog by remember { mutableStateOf<DialogMessage?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(uiState.deceasedValidation?.isEligible) {
        if (uiState.deceasedValidation?.isEligible == true) {
            showSuccessDialog = true
        }
    }

    FuneralAllowanceEvents(
        events = viewModel.events,
        onShowInfo = { dialog = DialogMessage(it, navigateBackOnDismiss = false) },
        onShowSuccess = { },
        onShowError = { toaster.error(it) },
        onNavigateBack = onBackClicked,
        onNavigateToBankAccount = onNavigateToBankAccount,
    )

    FuneralAllowanceScreen(
        uiState = uiState,
        onIntent = viewModel::sendIntent,
        onBack = onBackClicked,
    )

    if (uiState.showNoBankAccountDialog) {
        NoBankAccountDialog(
            onConfirm = { viewModel.sendIntent(FuneralAllowanceIntent.NavigateToBankAccount) },
            onDismiss = { viewModel.sendIntent(FuneralAllowanceIntent.DismissNoBankAccountDialog) },
        )
    }

    dialog?.let { current ->
        com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralMessageDialog(
            title = stringResource(Res.string.funeral_allowance_title),
            message = current.message,
            onDismiss = {
                dialog = null
                if (current.navigateBackOnDismiss) onBackClicked()
            },
        )
    }

    if (showSuccessDialog) {
        com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceEligibilitySuccessDialog(
            onContinue = {
                showSuccessDialog = false
                // TODO: Handle navigation to step 2
            },
            onDismiss = { showSuccessDialog = false }
        )
    }
}

private data class DialogMessage(val message: String, val navigateBackOnDismiss: Boolean)

@Composable
private fun FuneralAllowanceEvents(
    events: Flow<FuneralAllowanceEvent>,
    onShowInfo: (String) -> Unit,
    onShowSuccess: (String) -> Unit,
    onShowError: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToBankAccount: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is FuneralAllowanceEvent.ShowInfoMessage -> onShowInfo(event.message)
            is FuneralAllowanceEvent.ShowSuccessMessage -> onShowSuccess(event.message)
            is FuneralAllowanceEvent.ShowErrorToast -> onShowError(event.message)
            is FuneralAllowanceEvent.NavigateBack -> onNavigateBack()
            is FuneralAllowanceEvent.NavigateToBankAccount -> onNavigateToBankAccount()
        }
    }
}

@Composable
internal fun FuneralAllowanceScreen(
    uiState: FuneralAllowanceUiState,
    onIntent: (FuneralAllowanceIntent) -> Unit,
    onBack: () -> Unit,
) {
    val isEligible = uiState.deceasedValidation?.isEligible == true
    val isStep1 = uiState.currentStep == com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep.APPLICANT_INFO

    com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceStepScaffold(
        title = stringResource(Res.string.funeral_allowance_title),
        stepNumber = if (isStep1) 1 else 2,
        totalSteps = 3,
        onBackClicked = {
            if (isStep1) onBack() else onIntent(FuneralAllowanceIntent.GoToPreviousStep)
        },
        primaryText = if (isStep1) {
            if (isEligible) "ادامه درخواست" else stringResource(Res.string.funeral_allowance_check_eligibility)
        } else {
            "مرحله بعدی"
        },
        primaryEnabled = if (isStep1) {
            if (isEligible) true else uiState.deceasedNationalCode.length == 10 && !uiState.isValidatingDeceased
        } else {
            uiState.canSubmitRequest
        },
        isPrimaryLoading = uiState.isValidatingDeceased || uiState.isSubmitting,
        onPrimaryClick = {
            if (isStep1) {
                if (isEligible) {
                    onIntent(FuneralAllowanceIntent.GoToNextStep)
                } else {
                    onIntent(FuneralAllowanceIntent.ValidateDeceased)
                }
            } else {
                // Next step action for step 2 (presumably submit or step 3 preview)
                onIntent(FuneralAllowanceIntent.SubmitRequest)
            }
        },
        secondaryText = if (isStep1) "انصراف" else "مرحله قبلی",
        onSecondaryClick = {
            if (isStep1) onBack() else onIntent(FuneralAllowanceIntent.GoToPreviousStep)
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            val info = uiState.info
            when {
                info == null && uiState.isLoading -> {
                    com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceStep1ShimmerSkeleton()
                }
                info == null -> TaminEmptyState(
                    message = stringResource(Res.string.funeral_allowance_empty),
                    modifier = Modifier.fillMaxSize(),
                )

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.lg),
                ) {
                    when (uiState.currentStep) {
                        com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep.APPLICANT_INFO -> {
                            com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.Step1ApplicantInfo(
                                uiState = uiState,
                                onIntent = onIntent,
                            )
                        }
                        com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep.DECEASED_AND_BANK_INFO -> {
                            if (uiState.showBankAccountIssueFlow) {
                                // Normally shouldn't reach step 2 if there's an issue, but just in case
                                com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.Step1ApplicantInfo(uiState, onIntent)
                            } else {
                                com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.Step2DeceasedAndBankInfo(
                                    uiState = uiState,
                                    onIntent = onIntent,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (uiState.showBankAccountBottomSheet) {
        com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceBankAccountBottomSheet(
            bankAccounts = uiState.bankAccounts,
            selectedAccount = uiState.selectedBankAccount,
            onSelect = { onIntent(FuneralAllowanceIntent.SelectBankAccount(it)) },
            onDismiss = { onIntent(FuneralAllowanceIntent.ShowBankAccountBottomSheet(false)) }
        )
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
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = taminColors.greenText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        TaminText(
            text = "شرایط برخورداری از کمک‌هزینه مراسم ترحیم احراز شد. برای ادامه، اطلاعات متوفی و حساب واریز را تأیید کنید.",
            color = taminColors.greenText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f)
        )
    }
}

// ─── Previews ─────────────────────────────────────────────────────────────────

private val PreviewInfo = FuneralAllowanceInfoPR(
    fullName = "رضا دریکوند",
    firstName = "رضا",
    lastName = "دریکوند",
    insuranceNumber = "0053185242",
    bankAccount = "9611690067",
    bankName = "ملت",
    mobileNumber = "09166798149",
    branchName = "یک تهران",
    branchCode = "1101",
    nationalCode = "0012345678",
    deceasedNationalId = "",
    requestHelpType = "07",
    hasBankAccountIssue = false,
    registeredRequest = null,
)

@PreviewRtlTheme
@Composable
private fun FuneralAllowanceScreenPreview() {
    PreviewRtlThemeContent {
        FuneralAllowanceScreen(
            uiState = FuneralAllowanceUiState(info = PreviewInfo),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FuneralAllowanceScreenBankIssuePreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        FuneralAllowanceScreen(
            uiState = FuneralAllowanceUiState(
                info = PreviewInfo.copy(
                    hasBankAccountIssue = true,
                    registeredRequest = RegisteredFuneralRequestPR(
                        requestId = 42L,
                        deceasedNationalId = "0021234567",
                        deathDate = "۱۴۰۴/۰۳/۱۲",
                        requestDate = "۱۴۰۴/۰۳/۱۵",
                        statusName = "در انتظار تأیید شعبه",
                    ),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
