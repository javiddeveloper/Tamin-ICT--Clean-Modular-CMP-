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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.tamin.taminhamrah.contractFlow.isEditableFromSummary
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
import com.tamin.taminhamrah.ui.components.TaminText
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
import com.tamin.taminhamrah.ui.contractFlow.ContractSubmitResult
import com.tamin.taminhamrah.ui.contractFlow.ContractSubmitResultDialog
import com.tamin.taminhamrah.ui.contractFlow.ContractSummaryRowPR
import com.tamin.taminhamrah.ui.contractFlow.ContractTermsStepContent
import com.tamin.taminhamrah.ui.contractFlow.InsurancePremiumStepContent
import com.tamin.taminhamrah.ui.contractFlow.JobTitleStepContent
import com.tamin.taminhamrah.ui.contractFlow.PremiumSalaryStepContent
import com.tamin.taminhamrah.ui.contractFlow.SelectBranchStepContent
import com.tamin.taminhamrah.ui.contractFlow.SubmitContractStepContent
import com.tamin.taminhamrah.ui.contractFlow.TreatmentSupportStepContent
import com.tamin.taminhamrah.ui.contractFlow.UploadImageStepContent
import com.tamin.taminhamrah.ui.contractFlow.UserInfoStepContent
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.TaminOnAccentInkSoft
import com.tamin.taminhamrah.ui.toPriceFormat
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_flow_submit_contract
import taminx.core.core_ui.contract_hero_step_job_title
import taminx.core.core_ui.contract_next_step
import taminx.core.core_ui.contract_save_edit
import taminx.core.core_ui.contract_step_contract_applicant
import taminx.core.core_ui.contract_step_treatment_support
import taminx.core.core_ui.contract_step_user_info
import taminx.core.core_ui.contract_summary_applicant_guardian
import taminx.core.core_ui.contract_summary_applicant_personal
import taminx.core.core_ui.contract_summary_branch
import taminx.core.core_ui.contract_summary_document_value
import taminx.core.core_ui.contract_summary_documents
import taminx.core.core_ui.contract_summary_premium
import taminx.core.core_ui.contract_summary_premium_value
import taminx.core.core_ui.contract_summary_treatment_with
import taminx.core.core_ui.contract_summary_treatment_without
import taminx.core.core_ui.error_unknown
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.no_items_found

@Composable
fun ContractFlowScreen(
    onBack: () -> Unit,
    onShowRules: () -> Unit = {},
    onPaymentRequested: (contractNumber: String, amount: Long) -> Unit = { _, _ -> },
    viewModel: ContractFlowViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val toaster = LocalToaster.current
    var submitResult by remember { mutableStateOf<ContractSubmitResult?>(null) }
    var showRulesSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
    }

    HandleContractFlowEvents(
        events = viewModel.events,
        onShowMessage = { toaster.error(it) },
        onShowSubmitSuccess = { contractNumber, contractDate, amount, canPayOnline ->
            submitResult = ContractSubmitResult.Success(
                contractNumber = contractNumber,
                contractDate = contractDate,
                amount = amount,
                canPayOnline = canPayOnline,
            )
        },
        onShowSubmitFailure = { message ->
            submitResult = ContractSubmitResult.Failure(message = message)
        },
    )

    submitResult?.let { result ->
        ContractSubmitResultDialog(
            result = result,
            onDismiss = { submitResult = null },
            onPay = { contractNumber, amount ->
                onPaymentRequested(contractNumber, amount)
            },
            onRetry = {
                viewModel.sendIntent(ContractFlowIntent.SubmitContract)
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
        when {
            state.isEditMode -> onIntent(ContractFlowIntent.GoToPreviousStep)
            steps.isFirstStep(state.currentStep) -> onBack()
            else -> onIntent(ContractFlowIntent.GoToPreviousStep)
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
                if (!state.isEditMode) {
                    TaminHeroStepProgress(
                        stepTitle = stepTitle,
                        stepSubtitle = stepSubtitle,
                        currentStep = currentStepIndex,
                        totalSteps = totalSteps,
                        modifier = Modifier.padding(top = Spacing.md),
                    )
                } else {
                    TaminText(
                        text = stepTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TaminOnAccentInkSoft,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = Spacing.md),
                    )
                }
            }
        },
        bottomBar = {
            ContractFlowBottomBar(
                state = state,
                steps = steps,
                onNextStep = {
                    when {
                        state.isEditMode -> onIntent(ContractFlowIntent.SaveEdit)
                        steps.isLastStep(state.currentStep) -> onIntent(ContractFlowIntent.SubmitContract)
                        else -> onIntent(ContractFlowIntent.GoToNextStep)
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

                                    ContractStep.STEP_JOB_TITLE -> {
                                        JobTitleStepContent(
                                            freeJobs = state.freeJobs,
                                            selectedFreeJobCode = state.selectedFreeJobCode,
                                            selectedFreeJobName = state.selectedFreeJobName,
                                            isFreeJobsLoading = state.isFreeJobsLoading,
                                            onFreeJobSelected = {
                                                onIntent(ContractFlowIntent.SelectFreeJob(it))
                                            },
                                        )
                                    }

                                    ContractStep.STEP_TREATMENT_SUPPORT -> {
                                        TreatmentSupportStepContent(
                                            treatmentSupportCode = state.treatmentSupportCode,
                                            isCommitmentConfirmed = state.isTreatmentCommitmentConfirmed,
                                            forceTreatmentSupport = state.forceTreatmentSupport,
                                            dependents = state.dependents,
                                            isDependentsLoading = state.isDependentsLoading,
                                            hasLoadedDependents = state.hasLoadedDependents,
                                            dependentsError = state.dependentsError,
                                            onSelectWithSupport = {
                                                onIntent(ContractFlowIntent.SelectTreatmentSupport(true))
                                            },
                                            onSelectWithoutSupport = {
                                                onIntent(ContractFlowIntent.SelectTreatmentSupport(false))
                                            },
                                            onCommitmentChanged = {
                                                onIntent(ContractFlowIntent.SetTreatmentCommitment(it))
                                            },
                                            onViewDependents = {
                                                onIntent(ContractFlowIntent.LoadDependents)
                                            },
                                            withSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITH,
                                            withoutSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITHOUT,
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
                                            premiumRange = state.premiumRange,
                                            selectedPremium = state.selectedMonthlyPremium,
                                            calculatedMonthlySalary = state.calculatedMonthlySalary,
                                            isPremiumRangeLoading = state.isPremiumRangeLoading,
                                            isCalculating = state.isCalculatingPremium,
                                            isPremiumCalculated = state.isPremiumCalculated,
                                            showPremiumSlider = !state.hidePremiumSlider &&
                                                (state.config?.usesFreelancePremiumRange == true || state.config?.isOptionalInsurance == true),
                                            onPremiumChange = {
                                                onIntent(ContractFlowIntent.SelectMonthlyPremium(it))
                                            },
                                            onCalculate = {
                                                onIntent(ContractFlowIntent.CalculateMonthlyPremium)
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
                                            summaryRows = buildContractSummaryRows(state),
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
                                            onEditStep = { step ->
                                                onIntent(ContractFlowIntent.EditStep(step))
                                            },
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
    val isEditMode = state.isEditMode

    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when {
            isEditMode -> {
                LoadingButton(
                    text = stringResource(Res.string.contract_save_edit),
                    onClick = onNextStep,
                    enabled = nextEnabled,
                    isLoading = state.isSavingContact || state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_check),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }
            isFirst -> {
                LoadingButton(
                    text = stringResource(Res.string.contract_next_step),
                    onClick = onNextStep,
                    enabled = nextEnabled,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }
            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = onPreviousStep,
                    )
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


                }
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
        ContractStep.STEP_JOB_TITLE -> {
            state.selectedFreeJobCode != null
        }
        ContractStep.STEP_TREATMENT_SUPPORT -> {
            state.treatmentSupportCode == ContractFlowUiState.TREATMENT_SUPPORT_WITHOUT ||
                (
                    state.treatmentSupportCode == ContractFlowUiState.TREATMENT_SUPPORT_WITH &&
                        state.isTreatmentCommitmentConfirmed
                    )
        }
        ContractStep.STEP_INSURANCE_PREMIUM -> {
            val hasPremiumRate = state.selectedPremiumRateCode != null || state.lockedPremiumRateCode != null
            val hasFreeJob = !(state.config?.requiresFreeJob ?: false) || state.selectedFreeJobCode != null
            val usesCombinedPremiumStep = state.config?.steps?.none { it == ContractStep.STEP_SALARY } == true
            val needsCalculation = usesCombinedPremiumStep &&
                !state.hidePremiumSlider &&
                (state.config?.usesFreelancePremiumRange == true || state.config?.isOptionalInsurance == true)
            val calculationComplete = !needsCalculation || (state.isPremiumCalculated && !state.isCalculatingPremium)
            hasPremiumRate && hasFreeJob && calculationComplete
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
private fun buildContractSummaryRows(state: ContractFlowUiState): List<ContractSummaryRowPR> {
    val steps = state.config?.steps.orEmpty()
    return steps
        .filter { it.isEditableFromSummary() }
        .mapNotNull { step ->
            val title = summaryTitleFor(step) ?: return@mapNotNull null
            val value = summaryValueFor(state, step)
            ContractSummaryRowPR(step = step, title = title, value = value)
        }
}

@Composable
private fun summaryTitleFor(step: ContractStep): String? = when (step) {
    ContractStep.STEP_USER_INFO -> stringResource(Res.string.contract_step_user_info)
    ContractStep.STEP_CONTRACT_APPLICANT -> stringResource(Res.string.contract_step_contract_applicant)
    ContractStep.STEP_SELECT_BRANCH -> stringResource(Res.string.contract_summary_branch)
    ContractStep.STEP_UPLOAD_IMAGE -> stringResource(Res.string.contract_summary_documents)
    ContractStep.STEP_JOB_TITLE -> stringResource(Res.string.contract_hero_step_job_title)
    ContractStep.STEP_TREATMENT_SUPPORT -> stringResource(Res.string.contract_step_treatment_support)
    ContractStep.STEP_INSURANCE_PREMIUM,
    ContractStep.STEP_SALARY,
    -> stringResource(Res.string.contract_summary_premium)
    else -> null
}

@Composable
private fun summaryValueFor(state: ContractFlowUiState, step: ContractStep): String = when (step) {
    ContractStep.STEP_USER_INFO -> state.userInfo.cityName.ifBlank { state.branchSelection.cityName }
    ContractStep.STEP_CONTRACT_APPLICANT -> when (state.contractApplicantType) {
        ContractApplicantType.PERSONAL -> stringResource(Res.string.contract_summary_applicant_personal)
        ContractApplicantType.GUARDIAN -> stringResource(Res.string.contract_summary_applicant_guardian)
    }
    ContractStep.STEP_SELECT_BRANCH -> state.branchSelection.branchName
    ContractStep.STEP_UPLOAD_IMAGE ->
        if (state.uploadedDocuments.isEmpty()) {
            stringResource(Res.string.no_items_found)
        } else {
            state.documentDescription
        }
    ContractStep.STEP_JOB_TITLE -> state.selectedFreeJobName.orEmpty()
    ContractStep.STEP_TREATMENT_SUPPORT ->
        if (state.treatmentSupportCode == ContractFlowUiState.TREATMENT_SUPPORT_WITH) {
            stringResource(Res.string.contract_summary_treatment_with)
        } else {
            stringResource(Res.string.contract_summary_treatment_without)
        }
    ContractStep.STEP_INSURANCE_PREMIUM,
    ContractStep.STEP_SALARY,
    -> {
        val amount = state.selectedMonthlyPremium ?: state.calculatedMonthlySalary
        if (amount != null) {
            stringResource(
                Res.string.contract_summary_premium_value,
                amount.toPriceFormat().toPersianDigits(),
            )
        } else {
            ""
        }
    }
    else -> ""
}

@Composable
private fun HandleContractFlowEvents(
    events: Flow<ContractFlowEvent>,
    onShowMessage: (String) -> Unit,
    onShowSubmitSuccess: (
        contractNumber: String,
        contractDate: String,
        amount: Long,
        canPayOnline: Boolean,
    ) -> Unit,
    onShowSubmitFailure: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ContractFlowEvent.ShowMessage -> onShowMessage(event.message)
            is ContractFlowEvent.ShowSubmitSuccess -> onShowSubmitSuccess(
                event.contractNumber,
                event.contractDate,
                event.amount,
                event.canPayOnline,
            )
            is ContractFlowEvent.ShowSubmitFailure -> onShowSubmitFailure(event.message)
        }
    }
}

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

private val MockRegistrationInfo = com.tamin.taminhamrah.model.contracts.RegistrationInfoPR(
    fullName = "علی محمدی",
    nationalId = "0012345678",
    birthDateFormatted = "1375/04/15",
    insuranceId = "12345678",
    genderCode = "01",
    address = "مشهد، بلوار وکیل‌آباد، نبش وکیل‌آباد ۵۲",
    zipCode = "9187654321",
    phoneNumber = "05832245678",
    mobileNumber = "09143018372",
    hasMobile = true,
)

private val MockConfig = com.tamin.taminhamrah.feature.contracts.flow.config.StudentContractFlowConfig()

private val MockUserInfo = com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR(
    cityCode = "021",
    cityName = "مشهد",
    address = "مشهد، بلوار وکیل‌آباد، نبش وکیل‌آباد ۵۲",
    zipCode = "9187654321",
    phoneNumber = "05832245678",
    mobileNumber = "09143018372",
)

private val MockGuardianForm = com.tamin.taminhamrah.model.contractFlow.GuardianFormPR(
    nationalId = "0012345678",
    letterNumber = "1234567890",
    fullName = "رضا نادری",
    letterDateFormatted = "1405/07/11",
    documentGuid = "sample-guid",
)

private val MockBranchSelection = com.tamin.taminhamrah.model.contractFlow.BranchSelectionFormPR(
    provinceCode = "021",
    provinceName = "تهران",
    cityCode = "021",
    cityName = "تهران",
    branchCode = "001",
    branchName = "شعبه ۱ تهران (شهدای هفتم تیر)",
)

private val MockProvinces = listOf(
    com.tamin.taminhamrah.model.common.ProvincePR(provinceCode = "021", provinceName = "تهران"),
)

private val MockCities = listOf(
    com.tamin.taminhamrah.model.common.CityPR(cityCode = "021", cityName = "تهران", provinceCode = "021"),
    com.tamin.taminhamrah.model.common.CityPR(cityCode = "051", cityName = "مشهد", provinceCode = "051"),
)

private val MockBranches = listOf(
    com.tamin.taminhamrah.model.contracts.BranchPR(code = "001", name = "شعبه ۱ تهران (شهدای هفتم تیر)"),
)

private val MockPremiumRates = listOf(
    com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR(
        code = "1",
        description = "۱۲ درصد — شامل بازنشستگی و فوت بعد از بازنشستگی",
        insurancePercent = "12",
    ),
    com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR(
        code = "2",
        description = "۱۴ درصد — شامل بازنشستگی و فوت قبل و بعد از بازنشستگی",
        insurancePercent = "14",
    ),
    com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR(
        code = "3",
        description = "۱۸ درصد — شامل بازنشستگی، فوت و ازکارافتادگی",
        insurancePercent = "18",
    ),
)

private val MockPremiumRange = com.tamin.taminhamrah.model.contractFlow.FreelancePremiumRangePR(
    lowPremium = 104_400_000L,
    highPremium = 216_578_072L,
    paymentTabayi = 1_000_000L,
    history = 12,
)

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
private fun ContractFlowScreenContentStep1RegistrationPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
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
private fun ContractFlowScreenContentStep2TermsPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_CONTRACT_TERMS,
                isRulesConfirmed = true,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep3UserInfoPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_USER_INFO,
                userInfo = MockUserInfo,
                cities = MockCities,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep4ApplicantPersonalPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_CONTRACT_APPLICANT,
                contractApplicantType = ContractApplicantType.PERSONAL,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep4ApplicantGuardianPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_CONTRACT_APPLICANT,
                contractApplicantType = ContractApplicantType.GUARDIAN,
                guardianForm = MockGuardianForm,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep5SelectBranchPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_SELECT_BRANCH,
                branchSelection = MockBranchSelection,
                provinces = MockProvinces,
                branchCities = MockCities,
                branches = MockBranches,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep6UploadImagePreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_UPLOAD_IMAGE,
                documentDescription = "تصویر کارت دانشجویی",
                uploadedDocuments = listOf(
                    com.tamin.taminhamrah.model.contractFlow.UploadImagePR(
                        imageId = "doc-1",
                        fileName = "student_card.jpg",
                        description = "تصویر کارت دانشجویی",
                    ),
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
private fun ContractFlowScreenContentStep7TreatmentSupportPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_TREATMENT_SUPPORT,
                treatmentSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITH,
                isTreatmentCommitmentConfirmed = false,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep8InsurancePremiumPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_INSURANCE_PREMIUM,
                premiumRates = MockPremiumRates,
                selectedPremiumRateCode = "2",
                premiumRange = MockPremiumRange,
                selectedMonthlyPremium = 104_400_000L,
                treatmentSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITH,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep8InsurancePremiumCalculatedPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_INSURANCE_PREMIUM,
                premiumRates = MockPremiumRates,
                selectedPremiumRateCode = "2",
                premiumRange = MockPremiumRange,
                selectedMonthlyPremium = 26_516_000L,
                calculatedMonthlySalary = 189_400_000L,
                isPremiumCalculated = true,
                treatmentSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITH,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep8InsurancePremiumShimmerPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_INSURANCE_PREMIUM,
                isPremiumRatesLoading = true,
                treatmentSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITH,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep9SalaryPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_SALARY,
                premiumRange = MockPremiumRange,
                selectedMonthlyPremium = 14_000_000L,
                calculatedMonthlySalary = 71_661_840L,
                isPremiumCalculated = true,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentStep10SubmitContractPreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_SUBMIT_CONTRACT,
                userInfo = MockUserInfo,
                contractApplicantType = ContractApplicantType.PERSONAL,
                branchSelection = MockBranchSelection.copy(branchName = "شعبه چناران"),
                documentDescription = "مدرک",
                uploadedDocuments = listOf(
                    com.tamin.taminhamrah.model.contractFlow.UploadImagePR(
                        imageId = "1",
                        fileName = "doc.jpg",
                        description = "مدرک",
                    ),
                ),
                treatmentSupportCode = ContractFlowUiState.TREATMENT_SUPPORT_WITH,
                isTreatmentCommitmentConfirmed = true,
                selectedMonthlyPremium = 22_596_000L,
                calculatedMonthlySalary = 71_661_840L,
                selectedPremiumRateCode = "3",
                premiumRates = listOf(
                    com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR(
                        code = "3",
                        description = "نرخ ۱۴ درصد (بازنشستگی و فوت قبل و بعد از بازنشستگی)",
                        insurancePercent = "14",
                    ),
                ),
                isAgreementConfirmed = false,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractFlowScreenContentEditModePreview() {
    PreviewRtlThemeContent {
        ContractFlowScreenContent(
            state = ContractFlowUiState(
                isLoading = false,
                registrationInfo = MockRegistrationInfo,
                config = MockConfig,
                currentStep = ContractStep.STEP_USER_INFO,
                isEditMode = true,
                userInfo = MockUserInfo,
            ),
            onBack = {},
            onShowRules = {},
            onIntent = {},
        )
    }
}
