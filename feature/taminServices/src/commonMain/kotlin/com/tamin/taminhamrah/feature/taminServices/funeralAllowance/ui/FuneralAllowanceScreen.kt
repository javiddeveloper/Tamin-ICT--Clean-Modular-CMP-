package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.FuneralAllowanceViewModel
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceEvent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceIntent
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceUiState
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.FuneralAllowanceInfoPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.RegisteredFuneralRequestPR
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceEligibilitySuccessDialog
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceRequestSubmittedDialog
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceStep1ShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralAllowanceStepScaffold
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.FuneralMessageDialog
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.Step1ApplicantInfo
import com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components.Step2DeceasedInfo
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.NoBankAccountDialog
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.funeral_allowance_cancel
import taminx.core.core_ui.funeral_allowance_check_eligibility
import taminx.core.core_ui.funeral_allowance_continue_request
import taminx.core.core_ui.funeral_allowance_empty
import taminx.core.core_ui.funeral_allowance_exit_confirmation_confirm
import taminx.core.core_ui.funeral_allowance_exit_confirmation_desc
import taminx.core.core_ui.funeral_allowance_exit_confirmation_dismiss
import taminx.core.core_ui.funeral_allowance_exit_confirmation_title
import taminx.core.core_ui.funeral_allowance_previous_step
import taminx.core.core_ui.funeral_allowance_submit_request
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

    /** Backend success text for the terminal "request submitted" modal; null hides it. */
    var submittedMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.deceasedValidation?.isEligible) {
        if (uiState.deceasedValidation?.isEligible == true) {
            showSuccessDialog = true
        }
    }

    FuneralAllowanceEvents(
        events = viewModel.events,
        onShowInfo = { dialog = DialogMessage(it, navigateBackOnDismiss = false) },
        onShowSuccess = { submittedMessage = it },
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
        FuneralMessageDialog(
            title = stringResource(Res.string.funeral_allowance_title),
            message = current.message,
            onDismiss = {
                dialog = null
                if (current.navigateBackOnDismiss) onBackClicked()
            },
        )
    }

    if (showSuccessDialog) {
        FuneralAllowanceEligibilitySuccessDialog(
            onContinue = {
                showSuccessDialog = false
                viewModel.sendIntent(FuneralAllowanceIntent.GoToNextStep)
            },
            onDismiss = { showSuccessDialog = false }
        )
    }

    submittedMessage?.let { message ->
        FuneralAllowanceRequestSubmittedDialog(
            message = message,
            onBackToServices = {
                submittedMessage = null
                onBackClicked()
            },
        )
    }
}

private data class DialogMessage(val message: String, val navigateBackOnDismiss: Boolean)

/**
 * Steps on which "close" must warn before leaving — the user has entered / confirmed data that
 * isn't persisted. Step 1 (applicant info) carries no user input, so closing there just leaves.
 * Mirrors the occurrence flow's exit guard.
 */
private val STEPS_REQUIRING_EXIT_CONFIRMATION = setOf(
    FuneralAllowanceStep.DECEASED_INFO,
)

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
    val isStep1 = uiState.currentStep == FuneralAllowanceStep.APPLICANT_INFO

    var showExitConfirmation by remember { mutableStateOf(false) }

    // "Close" leaves the whole flow; on a data-entry step it asks first. "Back" (top-bar arrow
    // and system back) steps backwards — the ViewModel emits NavigateBack once past step 1.
    val onExitRequested = remember(uiState.currentStep, onBack) {
        {
            if (uiState.currentStep in STEPS_REQUIRING_EXIT_CONFIRMATION) {
                showExitConfirmation = true
            } else {
                onBack()
            }
        }
    }

    BackHandler(onBack = { onIntent(FuneralAllowanceIntent.GoToPreviousStep) })

    if (showExitConfirmation) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.funeral_allowance_exit_confirmation_title),
            description = stringResource(Res.string.funeral_allowance_exit_confirmation_desc),
            icon = Icons.AutoMirrored.Outlined.HelpOutline,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.funeral_allowance_exit_confirmation_confirm),
                    onClick = { showExitConfirmation = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.funeral_allowance_exit_confirmation_dismiss),
                    onClick = {
                        showExitConfirmation = false
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
            },
            onDismissRequest = { showExitConfirmation = false },
        )
    }

    FuneralAllowanceStepScaffold(
        title = stringResource(Res.string.funeral_allowance_title),
        currentStep = uiState.currentStep,
        onBackClicked = { onIntent(FuneralAllowanceIntent.GoToPreviousStep) },
        // Step 1 has no unsaved input, so its back arrow already leaves — no separate close there.
        onCloseClicked = if (isStep1) null else onExitRequested,
        primaryText = if (isStep1) {
            if (isEligible) stringResource(Res.string.funeral_allowance_continue_request)
            else stringResource(Res.string.funeral_allowance_check_eligibility)
        } else {
            stringResource(Res.string.funeral_allowance_submit_request)
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
                // Step 2 is the last step — its primary action submits the request.
                onIntent(FuneralAllowanceIntent.SubmitRequest)
            }
        },
        secondaryText = if (isStep1) {
            stringResource(Res.string.funeral_allowance_cancel)
        } else {
            stringResource(Res.string.funeral_allowance_previous_step)
        },
        onSecondaryClick = {
            if (isStep1) onExitRequested() else onIntent(FuneralAllowanceIntent.GoToPreviousStep)
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
                    FuneralAllowanceStep1ShimmerSkeleton()
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
                        FuneralAllowanceStep.APPLICANT_INFO -> {
                           Step1ApplicantInfo(
                                uiState = uiState,
                                onIntent = onIntent,
                            )
                        }

                        FuneralAllowanceStep.DECEASED_INFO -> {
                            if (uiState.showBankAccountIssueFlow) {
                                // Normally shouldn't reach step 2 if there's an issue, but just in case
                                Step1ApplicantInfo(
                                    uiState,
                                    onIntent
                                )
                            } else {
                                Step2DeceasedInfo(
                                    uiState = uiState,
                                )
                            }
                        }
                    }
                }
            }
        }
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

private val PreviewStep2State = FuneralAllowanceUiState(
    currentStep = com.tamin.taminhamrah.feature.taminServices.funeralAllowance.contract.FuneralAllowanceStep.DECEASED_INFO,
    info = PreviewInfo,
    deceasedNationalCode = "0039073041",
    deceasedValidation = com.tamin.taminhamrah.feature.taminServices.funeralAllowance.model.DeceasedValidationPR(
        deceasedFullName = "علي اكبر شيخ عباسي",
        relationship = "همسر",
        isEligible = true,
        message = "دارای شرایط می‌باشید",
    ),
)

@PreviewRtlTheme
@Composable
private fun FuneralAllowanceScreenStep2Preview() {
    PreviewRtlThemeContent {
        FuneralAllowanceScreen(
            uiState = PreviewStep2State,
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FuneralAllowanceScreenStep2PreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        FuneralAllowanceScreen(
            uiState = PreviewStep2State,
            onIntent = {},
            onBack = {},
        )
    }
}
