package com.tamin.taminhamrah.feature.contracts.flow.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.contractFlow.isFirstStep
import com.tamin.taminhamrah.contractFlow.isLastStep
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowEvent
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowIntent
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.ContractFlowUiState
import com.tamin.taminhamrah.feature.contracts.flow.ui.contract.isUserInfoStepComplete
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminHeroStepProgress
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.contractFlow.ContractApplicantStepContent
import com.tamin.taminhamrah.ui.contractFlow.ContractFlowScreenShimmerSkeleton
import com.tamin.taminhamrah.ui.contractFlow.ContractRegistrationStepContent
import com.tamin.taminhamrah.ui.contractFlow.ContractRulesBottomSheet
import com.tamin.taminhamrah.ui.contractFlow.ContractTermsStepContent
import com.tamin.taminhamrah.ui.contractFlow.InsurancePremiumStepContent
import com.tamin.taminhamrah.ui.contractFlow.PremiumSalaryStepContent
import com.tamin.taminhamrah.ui.contractFlow.SelectBranchStepContent
import com.tamin.taminhamrah.ui.contractFlow.SubmitContractStepContent
import com.tamin.taminhamrah.ui.contractFlow.UploadImageStepContent
import com.tamin.taminhamrah.ui.contractFlow.UserInfoStepContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_flow_submit_contract
import taminx.core.core_ui.contract_next_step
import taminx.core.core_ui.contract_payment_dialog_dismiss
import taminx.core.core_ui.contract_payment_dialog_message
import taminx.core.core_ui.contract_payment_dialog_pay
import taminx.core.core_ui.contract_payment_dialog_title
import taminx.core.core_ui.error_unknown
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross

@Composable
fun ContractFlowScreen(
    onBack: () -> Unit,
    onShowRules: () -> Unit = {},
    onPaymentRequested: (contractNumber: String, amount: Long) -> Unit = { _, _ -> },
    viewModel: ContractFlowViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val toaster = LocalToaster.current
    var paymentDialog by remember { mutableStateOf<PaymentDialogState?>(null) }
    var showRulesSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
    }

    HandleContractFlowEvents(
        events = viewModel.events,
        onShowMessage = { toaster.error(it) },
        onShowPaymentOption = { contractNumber, amount ->
            paymentDialog = PaymentDialogState(contractNumber, amount)
        },
    )

    paymentDialog?.let { dialog ->
        AlertDialog(
            onDismissRequest = { paymentDialog = null },
            title = { Text(stringResource(Res.string.contract_payment_dialog_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.contract_payment_dialog_message,
                        dialog.contractNumber.toPersianDigits(),
                    ),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPaymentRequested(dialog.contractNumber, dialog.amount)
                        paymentDialog = null
                    },
                ) {
                    Text(stringResource(Res.string.contract_payment_dialog_pay))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { paymentDialog = null }) {
                    Text(stringResource(Res.string.contract_payment_dialog_dismiss))
                }
            },
        )
    }

    ContractFlowScreenContent(
        state = state,
        onBack = onBack,
        onShowRules = {
            showRulesSheet = true
            onShowRules()
        },
        onIntent = viewModel::sendIntent,
    )

    if (showRulesSheet) {
        ContractRulesBottomSheet(
            onDismiss = { showRulesSheet = false },
        )
    }
}

@Composable
fun ContractFlowScreenContent(
    state: ContractFlowUiState,
    onBack: () -> Unit,
    onShowRules: () -> Unit,
    onIntent: (ContractFlowIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading && state.registrationInfo == null) {
        ContractFlowScreenShimmerSkeleton(modifier = modifier)
        return
    }

    val steps = state.config?.steps ?: emptyList()
    val currentStepIndex = (steps.indexOf(state.currentStep) + 1).coerceAtLeast(1)
    val totalSteps = steps.size.coerceAtLeast(1)
    val screenTitle = state.config?.screenTitleRes?.let { stringResource(it) }.orEmpty()
    val stepTitle = stringResource(state.currentStep.titleRes)
    val stepSubtitle = stringResource(state.currentStep.descRes)

    val handleNavigateBack: () -> Unit = {
        if (steps.isFirstStep(state.currentStep)) {
            onBack()
        } else {
            onIntent(ContractFlowIntent.GoToPreviousStep)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = screenTitle,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = handleNavigateBack,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
            ) {
                TaminHeroStepProgress(
                    stepTitle = stepTitle,
                    stepSubtitle = stepSubtitle,
                    currentStep = currentStepIndex,
                    totalSteps = totalSteps,
                    modifier = Modifier.padding(top = Spacing.md),
                )
            }
        },
        bottomBar = {
            ContractFlowBottomBar(
                state = state,
                steps = steps,
                onNextStep = {
                    if (steps.isLastStep(state.currentStep)) {
                        onIntent(ContractFlowIntent.SubmitContract)
                    } else {
                        onIntent(ContractFlowIntent.GoToNextStep)
                    }
                },
                onPreviousStep = {
                    onIntent(ContractFlowIntent.GoToPreviousStep)
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            when {
                state.error != null && state.registrationInfo == null -> {
                    Text(
                        text = state.error ?: stringResource(Res.string.error_unknown),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(Spacing.lg),
                    )
                }

                state.registrationInfo != null -> {
                    val info = state.registrationInfo!!
                    AnimatedContent(
                        targetState = state.currentStep,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = Spacing.sm),
                        transitionSpec = {
                            val forward = targetState.ordinal > initialState.ordinal
                            if (forward) {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                            } else {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            }
                        },
                        label = "contractFlowStep",
                    ) { step ->
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = Spacing.page,
                                end = Spacing.page,
                                top = Spacing.md,
                                bottom = Spacing.xl,
                            ),
                            verticalArrangement = Arrangement.spacedBy(Spacing.md),
                        ) {
                            item {
                                when (step) {
                                    ContractStep.STEP_REGISTRATION -> {
                                        ContractRegistrationStepContent(
                                            info = info,
                                            insuranceTypeLabel = state.config?.insuranceTypeLabelRes?.let { stringResource(it) }.orEmpty(),
                                            eligibility = state.eligibility,
                                            genderGateError = state.genderGateError,
                                            preflightGateError = state.preflightGateError,
                                        )
                                    }

                                    ContractStep.STEP_CONTRACT_TERMS -> {
                                        ContractTermsStepContent(
                                            info = info,
                                            isRulesConfirmed = state.isRulesConfirmed,
                                            onRulesConfirmedChange = {
                                                onIntent(ContractFlowIntent.SetRulesConfirmed(it))
                                            },
                                            onShowRules = onShowRules,
                                        )
                                    }

                                    ContractStep.STEP_USER_INFO -> {
                                        UserInfoStepContent(
                                            userInfo = state.userInfo,
                                            cities = state.cities,
                                            isCitiesLoading = state.isCitiesLoading,
                                            onCitySelected = { city ->
                                                onIntent(
                                                    ContractFlowIntent.UpdateUserInfo(
                                                        state.userInfo.copy(
                                                            cityCode = city.cityCode,
                                                            cityName = city.cityName,
                                                        ),
                                                    ),
                                                )
                                            },
                                            onAddressChange = {
                                                onIntent(ContractFlowIntent.UpdateUserInfo(state.userInfo.copy(address = it)))
                                            },
                                            onZipCodeChange = {
                                                onIntent(ContractFlowIntent.UpdateUserInfo(state.userInfo.copy(zipCode = it)))
                                            },
                                            onPhoneNumberChange = {
                                                onIntent(ContractFlowIntent.UpdateUserInfo(state.userInfo.copy(phoneNumber = it)))
                                            },
                                        )
                                    }

                                    ContractStep.STEP_CONTRACT_APPLICANT -> {
                                        ContractApplicantStepContent(
                                            selectedType = state.contractApplicantType,
                                            onTypeSelected = {
                                                onIntent(ContractFlowIntent.SetContractApplicantType(it))
                                            },
                                            guardianForm = state.guardianForm,
                                            onGuardianNationalIdChange = {
                                                onIntent(ContractFlowIntent.UpdateGuardianForm(state.guardianForm.copy(nationalId = it)))
                                            },
                                            onGuardianLetterNumberChange = {
                                                onIntent(ContractFlowIntent.UpdateGuardianForm(state.guardianForm.copy(letterNumber = it)))
                                            },
                                            onGuardianFullNameChange = {
                                                onIntent(ContractFlowIntent.UpdateGuardianForm(state.guardianForm.copy(fullName = it)))
                                            },
                                            onGuardianLetterDateChange = { formatted, epoch ->
                                                onIntent(
                                                    ContractFlowIntent.UpdateGuardianForm(
                                                        state.guardianForm.copy(
                                                            letterDateFormatted = formatted,
                                                            letterDateEpoch = epoch,
                                                        ),
                                                    ),
                                                )
                                            },
                                            onGuardianImagePicked = { fileName, bytes ->
                                                onIntent(ContractFlowIntent.UploadGuardianImage(fileName, bytes))
                                            },
                                            onClearGuardianDocument = {
                                                onIntent(ContractFlowIntent.ClearGuardianDocument)
                                            },
                                        )
                                    }

                                    ContractStep.STEP_SELECT_BRANCH -> {
                                        SelectBranchStepContent(
                                            branchSelection = state.branchSelection,
                                            provinces = state.provinces,
                                            cities = state.branchCities,
                                            branches = state.branches,
                                            isProvincesLoading = state.isProvincesLoading,
                                            isCitiesLoading = state.isBranchCitiesLoading,
                                            isBranchesLoading = state.isBranchesLoading,
                                            onProvinceSelected = {
                                                onIntent(ContractFlowIntent.SelectBranchProvince(it))
                                            },
                                            onCitySelected = {
                                                onIntent(ContractFlowIntent.SelectBranchCity(it))
                                            },
                                            onBranchSelected = {
                                                onIntent(ContractFlowIntent.SelectBranch(it))
                                            },
                                        )
                                    }

                                    ContractStep.STEP_UPLOAD_IMAGE -> {
                                        UploadImageStepContent(
                                            description = state.documentDescription,
                                            previewBytes = state.documentPreviewBytes,
                                            uploadedDocuments = state.uploadedDocuments,
                                            isUploading = state.isUploadingDocument,
                                            uploadError = state.uploadDocumentError,
                                            onDescriptionChange = {
                                                onIntent(ContractFlowIntent.UpdateDocumentDescription(it))
                                            },
                                            onImagePicked = { fileName, bytes ->
                                                onIntent(ContractFlowIntent.UploadPickedImage(fileName, bytes))
                                            },
                                            onClearDocument = {
                                                onIntent(ContractFlowIntent.ClearUploadedDocument)
                                            },
                                        )
                                    }

                                    ContractStep.STEP_INSURANCE_PREMIUM -> {
                                        InsurancePremiumStepContent(
                                            premiumRates = state.premiumRates,
                                            selectedCode = state.lockedPremiumRateCode ?: state.selectedPremiumRateCode,
                                            isLoading = state.isPremiumRatesLoading,
                                            isRateSelectionEnabled = state.lockedPremiumRateCode == null,
                                            onRateSelected = {
                                                onIntent(ContractFlowIntent.SelectPremiumRate(it))
                                            },
                                            showFreeJobSelector = state.config?.requiresFreeJob == true,
                                            freeJobs = state.freeJobs,
                                            selectedFreeJobCode = state.selectedFreeJobCode,
                                            selectedFreeJobName = state.selectedFreeJobName,
                                            isFreeJobsLoading = state.isFreeJobsLoading,
                                            onFreeJobSelected = {
                                                onIntent(ContractFlowIntent.SelectFreeJob(it))
                                            },
                                        )
                                    }

                                    ContractStep.STEP_SALARY -> {
                                        PremiumSalaryStepContent(
                                            premiumRange = state.premiumRange,
                                            selectedPremium = state.selectedMonthlyPremium,
                                            calculatedMonthlySalary = state.calculatedMonthlySalary,
                                            isLoading = state.isPremiumRangeLoading,
                                            isCalculating = state.isCalculatingPremium,
                                            onPremiumChange = {
                                                onIntent(ContractFlowIntent.SelectMonthlyPremium(it))
                                            },
                                            onCalculate = {
                                                onIntent(ContractFlowIntent.CalculateMonthlyPremium)
                                            },
                                            showPremiumSlider = !state.hidePremiumSlider &&
                                                (state.config?.usesFreelancePremiumRange == true || state.config?.isOptionalInsurance == true),
                                        )
                                    }

                                    ContractStep.STEP_SUBMIT_CONTRACT -> {
                                        val effectiveRateCode = state.lockedPremiumRateCode ?: state.selectedPremiumRateCode
                                        SubmitContractStepContent(
                                            registrationInfo = info,
                                            selectedPremiumRateDescription = state.premiumRates
                                                .firstOrNull { it.code == effectiveRateCode }
                                                ?.description,
                                            calculatedMonthlySalary = state.calculatedMonthlySalary,
                                            agreementContractLabel = state.config?.agreementContractLabelRes?.let { stringResource(it) }.orEmpty(),
                                            isAgreementConfirmed = state.isAgreementConfirmed,
                                            isSubmitting = state.isSubmittingContract,
                                            submittedContract = state.submittedContract,
                                            onAgreementConfirmedChange = {
                                                onIntent(ContractFlowIntent.SetAgreementConfirmed(it))
                                            },
                                            onSubmit = {
                                                onIntent(ContractFlowIntent.SubmitContract)
                                            },
                                            canSubmit = true,
                                        )
                                    }

                                    else -> Unit
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContractFlowBottomBar(
    state: ContractFlowUiState,
    steps: List<ContractStep>,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
) {
    val isFirst = steps.isFirstStep(state.currentStep)
    val isLast = steps.isLastStep(state.currentStep)
    val nextEnabled = isStepValid(state)

    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        if (isFirst) {
            LoadingButton(
                text = stringResource(Res.string.contract_next_step),
                onClick = onNextStep,
                enabled = nextEnabled,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth(),
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconPosition = LoadingButtonIconPosition.TRAILING,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LoadingButton(
                    text = if (isLast) {
                        stringResource(Res.string.contract_flow_submit_contract)
                    } else {
                        stringResource(Res.string.contract_next_step)
                    },
                    onClick = onNextStep,
                    enabled = nextEnabled,
                    isLoading = if (isLast) state.isSubmittingContract else state.isLoading,
                    modifier = Modifier.weight(1f),
                    icon = if (!isLast) vectorResource(Res.drawable.ic_tamin_chevron_forward) else null,
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )

                SquareIconButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    onClick = onPreviousStep,
                )
            }
        }
    }
}

private fun isStepValid(state: ContractFlowUiState): Boolean {
    if (state.isLoading) return false
    return when (state.currentStep) {
        ContractStep.STEP_REGISTRATION -> {
            state.registrationInfo != null &&
                state.genderGateError == null &&
                state.preflightGateError == null &&
                (state.eligibility == null || state.eligibility.isEligible)
        }
        ContractStep.STEP_AUTHORIZATION -> {
            state.eligibility != null && state.eligibility.isEligible
        }
        ContractStep.STEP_CONTRACT_TERMS -> {
            state.isRulesConfirmed
        }
        ContractStep.STEP_USER_INFO -> {
            isUserInfoStepComplete(state.userInfo) && !state.isSavingContact
        }
        ContractStep.STEP_CONTRACT_APPLICANT -> {
            state.contractApplicantType == ContractApplicantType.PERSONAL ||
                (state.contractApplicantType == ContractApplicantType.GUARDIAN && state.guardianForm.isValid)
        }
        ContractStep.STEP_SELECT_BRANCH -> {
            state.branchSelection.isValid
        }
        ContractStep.STEP_UPLOAD_IMAGE -> {
            !state.isUploadingDocument
        }
        ContractStep.STEP_TREATMENT_SUPPORT -> {
            true
        }
        ContractStep.STEP_INSURANCE_PREMIUM -> {
            val hasPremiumRate = state.selectedPremiumRateCode != null || state.lockedPremiumRateCode != null
            val hasFreeJob = !(state.config?.requiresFreeJob ?: false) || state.selectedFreeJobCode != null
            hasPremiumRate && hasFreeJob
        }
        ContractStep.STEP_SALARY -> {
            state.isPremiumCalculated && !state.isCalculatingPremium
        }
        ContractStep.STEP_SUBMIT_CONTRACT -> {
            state.isAgreementConfirmed && !state.isSubmittingContract
        }
    }
}

@Composable
private fun HandleContractFlowEvents(
    events: Flow<ContractFlowEvent>,
    onShowMessage: (String) -> Unit,
    onShowPaymentOption: (contractNumber: String, amount: Long) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ContractFlowEvent.ShowMessage -> onShowMessage(event.message)
            is ContractFlowEvent.ShowPaymentOption -> onShowPaymentOption(event.contractNumber, event.amount)
        }
    }
}

private data class PaymentDialogState(
    val contractNumber: String,
    val amount: Long,
)

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentLoadingPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = true,
                registrationInfo = null,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep1Preview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = com.tamin.taminhamrah.model.contracts.RegistrationInfoPR(
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
                config = com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig(),
                currentStep = ContractStep.STEP_REGISTRATION,
                eligibility = com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR(
                    statusCode = 1,
                    isEligible = true,
                    reason = com.tamin.taminhamrah.contractFlow.ContractEligibilityReason.AGE_UNDER_FIFTY,
                ),
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep2Preview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = com.tamin.taminhamrah.model.contracts.RegistrationInfoPR(
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
                config = com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig(),
                currentStep = ContractStep.STEP_CONTRACT_TERMS,
                isRulesConfirmed = true,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}
