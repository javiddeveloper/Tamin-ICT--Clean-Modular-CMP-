package com.tamin.taminhamrah.feature.contractFlow.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowIntent
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowUiState
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.ui.contractFlow.ContractApplicantStepContent
import com.tamin.taminhamrah.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR
import com.tamin.taminhamrah.contractFlow.ContractStep
import com.tamin.taminhamrah.ui.contractFlow.ContractTermsStepContent
import com.tamin.taminhamrah.ui.contractFlow.InsurancePremiumStepContent
import com.tamin.taminhamrah.ui.contractFlow.PremiumSalaryStepContent
import com.tamin.taminhamrah.ui.contractFlow.SelectBranchStepContent
import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.ui.contractFlow.SubmitContractStepContent
import com.tamin.taminhamrah.ui.contractFlow.UploadImageStepContent
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.ui.contractFlow.UserInfoStepContent
import com.tamin.taminhamrah.ui.contractFlow.eligibilityMessage
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.contract_flow_birth_date
import taminx.core.core_ui.contract_flow_national_id
import taminx.core.core_ui.contract_flow_registration_message
import taminx.core.core_ui.contract_flow_step_next
import taminx.core.core_ui.contract_flow_step_not_implemented
import taminx.core.core_ui.contract_flow_step_previous
import taminx.core.core_ui.error_unknown

@Composable
fun ContractFlowScreen(
    onBack: () -> Unit,
    onShowRules: () -> Unit = {},
    viewModel: ContractFlowViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(ContractFlowIntent.LoadInitialData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    state.config?.screenTitleRes?.let { titleRes ->
                        Text(stringResource(titleRes))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back_content_description),
                        )
                    }
                },
            )
        },
    ) { padding ->
        ContractFlowContent(
            state = state,
            onNextStep = { viewModel.sendIntent(ContractFlowIntent.GoToNextStep) },
            onPreviousStep = { viewModel.sendIntent(ContractFlowIntent.GoToPreviousStep) },
            onRulesConfirmedChange = {
                viewModel.sendIntent(ContractFlowIntent.SetRulesConfirmed(it))
            },
            onUserInfoChange = {
                viewModel.sendIntent(ContractFlowIntent.UpdateUserInfo(it))
            },
            onContractApplicantTypeChange = {
                viewModel.sendIntent(ContractFlowIntent.SetContractApplicantType(it))
            },
            onBranchProvinceSelected = {
                viewModel.sendIntent(ContractFlowIntent.SelectBranchProvince(it))
            },
            onBranchCitySelected = {
                viewModel.sendIntent(ContractFlowIntent.SelectBranchCity(it))
            },
            onBranchSelected = {
                viewModel.sendIntent(ContractFlowIntent.SelectBranch(it))
            },
            onPremiumRateSelected = {
                viewModel.sendIntent(ContractFlowIntent.SelectPremiumRate(it))
            },
            onFreeJobSelected = {
                viewModel.sendIntent(ContractFlowIntent.SelectFreeJob(it))
            },
            onMonthlyPremiumChange = {
                viewModel.sendIntent(ContractFlowIntent.SelectMonthlyPremium(it))
            },
            onCalculateMonthlyPremium = {
                viewModel.sendIntent(ContractFlowIntent.CalculateMonthlyPremium)
            },
            onAgreementConfirmedChange = {
                viewModel.sendIntent(ContractFlowIntent.SetAgreementConfirmed(it))
            },
            onSubmitContract = {
                viewModel.sendIntent(ContractFlowIntent.SubmitContract)
            },
            onDocumentDescriptionChange = {
                viewModel.sendIntent(ContractFlowIntent.UpdateDocumentDescription(it))
            },
            onImagePicked = { fileName, bytes ->
                viewModel.sendIntent(ContractFlowIntent.UploadPickedImage(fileName, bytes))
            },
            onClearDocument = {
                viewModel.sendIntent(ContractFlowIntent.ClearUploadedDocument)
            },
            onShowRules = onShowRules,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun ContractFlowContent(
    state: ContractFlowUiState,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onUserInfoChange: (UserInfoFormPR) -> Unit,
    onContractApplicantTypeChange: (ContractApplicantType) -> Unit,
    onBranchProvinceSelected: (ProvincePR) -> Unit,
    onBranchCitySelected: (CityPR) -> Unit,
    onBranchSelected: (BranchPR) -> Unit,
    onPremiumRateSelected: (SpcPremiumRateOptionPR) -> Unit,
    onFreeJobSelected: (FreeJobDN) -> Unit,
    onMonthlyPremiumChange: (Long) -> Unit,
    onCalculateMonthlyPremium: () -> Unit,
    onAgreementConfirmedChange: (Boolean) -> Unit,
    onSubmitContract: () -> Unit,
    onDocumentDescriptionChange: (String) -> Unit,
    onImagePicked: (fileName: String, bytes: ByteArray) -> Unit,
    onClearDocument: () -> Unit,
    onShowRules: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading && state.registrationInfo == null -> {
                CircularProgressIndicator()
            }

            state.error != null && state.registrationInfo == null -> {
                Text(
                    text = state.error ?: stringResource(Res.string.error_unknown),
                    color = MaterialTheme.colorScheme.error,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.none),
                ) {
                    state.registrationInfo?.let { info ->
                        item {
                            RegistrationHeaderCard(info)
                        }
                        item {
                            Spacer(modifier = Modifier.height(Spacing.lg))
                        }
                        item {
                            ContractStepper(
                                state = state,
                                info = info,
                                onNextStep = onNextStep,
                                onPreviousStep = onPreviousStep,
                                onRulesConfirmedChange = onRulesConfirmedChange,
                                onUserInfoChange = onUserInfoChange,
                                onContractApplicantTypeChange = onContractApplicantTypeChange,
                                onBranchProvinceSelected = onBranchProvinceSelected,
                                onBranchCitySelected = onBranchCitySelected,
                                onBranchSelected = onBranchSelected,
                                onPremiumRateSelected = onPremiumRateSelected,
                                onFreeJobSelected = onFreeJobSelected,
                                onMonthlyPremiumChange = onMonthlyPremiumChange,
                                onCalculateMonthlyPremium = onCalculateMonthlyPremium,
                                onAgreementConfirmedChange = onAgreementConfirmedChange,
                                onSubmitContract = onSubmitContract,
                                onDocumentDescriptionChange = onDocumentDescriptionChange,
                                onImagePicked = onImagePicked,
                                onClearDocument = onClearDocument,
                                onShowRules = onShowRules,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RegistrationHeaderCard(info: RegistrationInfoPR) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg), verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(text = info.fullName, style = MaterialTheme.typography.titleLarge)
            Text(text = stringResource(Res.string.contract_flow_national_id, info.nationalId))
            Text(text = stringResource(Res.string.contract_flow_birth_date, info.birthDateFormatted))
        }
    }
}

@Composable
private fun ContractStepper(
    state: ContractFlowUiState,
    info: RegistrationInfoPR,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onUserInfoChange: (UserInfoFormPR) -> Unit,
    onContractApplicantTypeChange: (ContractApplicantType) -> Unit,
    onBranchProvinceSelected: (ProvincePR) -> Unit,
    onBranchCitySelected: (CityPR) -> Unit,
    onBranchSelected: (BranchPR) -> Unit,
    onPremiumRateSelected: (SpcPremiumRateOptionPR) -> Unit,
    onFreeJobSelected: (FreeJobDN) -> Unit,
    onMonthlyPremiumChange: (Long) -> Unit,
    onCalculateMonthlyPremium: () -> Unit,
    onAgreementConfirmedChange: (Boolean) -> Unit,
    onSubmitContract: () -> Unit,
    onDocumentDescriptionChange: (String) -> Unit,
    onImagePicked: (fileName: String, bytes: ByteArray) -> Unit,
    onClearDocument: () -> Unit,
    onShowRules: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.none)) {
        val config = state.config
        val steps = config?.steps ?: emptyList()
        steps.forEachIndexed { index, step ->
            val stepState = resolveStepState(step, state.currentStep, steps)
            val displayNumber = config?.displayNumber(step) ?: 0
            StepperItem(
                step = step,
                displayNumber = displayNumber,
                stepState = stepState,
                isLast = index == steps.lastIndex,
                content = {
                    when (step) {
                        ContractStep.STEP_REGISTRATION -> {
                            RegistrationStepContent(
                                info = info,
                                genderGateError = state.genderGateError,
                            )
                        }
                        ContractStep.STEP_AUTHORIZATION -> {
                            if (state.eligibility != null) {
                                AuthorizationStepContent(
                                    eligibility = state.eligibility,
                                    insuranceTypeLabel = config?.insuranceTypeLabelRes?.let { stringResource(it) }.orEmpty(),
                                )
                            } else {
                                CircularProgressIndicator(modifier = Modifier.size(IconSize.medium))
                            }
                        }
                        ContractStep.STEP_CONTRACT_TERMS -> {
                            ContractTermsStepContent(
                                info = info,
                                isRulesConfirmed = state.isRulesConfirmed,
                                onRulesConfirmedChange = onRulesConfirmedChange,
                                onShowRules = onShowRules,
                            )
                        }
                        ContractStep.STEP_USER_INFO -> {
                            UserInfoStepContent(
                                userInfo = state.userInfo,
                                cities = state.cities,
                                isCitiesLoading = state.isCitiesLoading,
                                onCitySelected = { city ->
                                    onUserInfoChange(
                                        state.userInfo.copy(
                                            cityCode = city.cityCode,
                                            cityName = city.cityName,
                                        ),
                                    )
                                },
                                onAddressChange = { onUserInfoChange(state.userInfo.copy(address = it)) },
                                onZipCodeChange = { onUserInfoChange(state.userInfo.copy(zipCode = it)) },
                                onPhoneNumberChange = { onUserInfoChange(state.userInfo.copy(phoneNumber = it)) },
                            )
                        }
                        ContractStep.STEP_CONTRACT_APPLICANT -> {
                            ContractApplicantStepContent(
                                selectedType = state.contractApplicantType,
                                onTypeSelected = onContractApplicantTypeChange,
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
                                onProvinceSelected = onBranchProvinceSelected,
                                onCitySelected = onBranchCitySelected,
                                onBranchSelected = onBranchSelected,
                            )
                        }
                        ContractStep.STEP_UPLOAD_IMAGE -> {
                            UploadImageStepContent(
                                description = state.documentDescription,
                                previewBytes = state.documentPreviewBytes,
                                uploadedDocuments = state.uploadedDocuments,
                                isUploading = state.isUploadingDocument,
                                uploadError = state.uploadDocumentError,
                                onDescriptionChange = onDocumentDescriptionChange,
                                onImagePicked = onImagePicked,
                                onClearDocument = onClearDocument,
                            )
                        }
                        ContractStep.STEP_TREATMENT_SUPPORT -> {
                            Text(
                                text = stringResource(Res.string.contract_flow_step_not_implemented),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                        ContractStep.STEP_INSURANCE_PREMIUM -> {
                            InsurancePremiumStepContent(
                                premiumRates = state.premiumRates,
                                selectedCode = state.selectedPremiumRateCode,
                                isLoading = state.isPremiumRatesLoading,
                                onRateSelected = onPremiumRateSelected,
                                showFreeJobSelector = config?.requiresFreeJob == true,
                                freeJobs = state.freeJobs,
                                selectedFreeJobCode = state.selectedFreeJobCode,
                                selectedFreeJobName = state.selectedFreeJobName,
                                isFreeJobsLoading = state.isFreeJobsLoading,
                                onFreeJobSelected = onFreeJobSelected,
                            )
                        }
                        ContractStep.STEP_SALARY -> {
                            PremiumSalaryStepContent(
                                premiumRange = state.premiumRange,
                                selectedPremium = state.selectedMonthlyPremium,
                                calculatedMonthlySalary = state.calculatedMonthlySalary,
                                isLoading = state.isPremiumRangeLoading,
                                isCalculating = state.isCalculatingPremium,
                                onPremiumChange = onMonthlyPremiumChange,
                                onCalculate = onCalculateMonthlyPremium,
                                showPremiumSlider = !state.hidePremiumSlider &&
                                    (config?.usesFreelancePremiumRange == true || config?.isOptionalInsurance == true),
                            )
                        }
                        ContractStep.STEP_SUBMIT_CONTRACT -> {
                            SubmitContractStepContent(
                                registrationInfo = info,
                                selectedPremiumRateDescription = state.premiumRates
                                    .firstOrNull { it.code == state.selectedPremiumRateCode }
                                    ?.description,
                                calculatedMonthlySalary = state.calculatedMonthlySalary,
                                agreementContractLabel = config?.agreementContractLabelRes?.let { stringResource(it) }.orEmpty(),
                                isAgreementConfirmed = state.isAgreementConfirmed,
                                isSubmitting = state.isSubmittingContract,
                                submittedContract = state.submittedContract,
                                onAgreementConfirmedChange = onAgreementConfirmedChange,
                                onSubmit = onSubmitContract,
                                canSubmit = true,
                            )
                        }
                    }
                },
                navigation = {
                    if (stepState == StepState.ACTIVE) {
                        if (step == ContractStep.STEP_SUBMIT_CONTRACT) {
                            StepNavigationButtons(
                                showPrevious = true,
                                showNext = false,
                                nextEnabled = false,
                                onNextStep = onNextStep,
                                onPreviousStep = onPreviousStep,
                            )
                        } else {
                            StepNavigationButtons(
                                showPrevious = !(config?.isFirstStep(step) ?: true),
                                showNext = !(config?.isLastStep(step) ?: true),
                                nextEnabled = state.canGoNext,
                                nextLoading = state.isSavingContact &&
                                    step == ContractStep.STEP_USER_INFO,
                                onNextStep = onNextStep,
                                onPreviousStep = onPreviousStep,
                            )
                        }
                    }
                },
            )
        }
    }
}

private enum class StepState {
    COMPLETED,
    ACTIVE,
    UPCOMING,
}

private fun resolveStepState(
    step: ContractStep,
    currentStep: ContractStep,
    steps: List<ContractStep>,
): StepState {
    val stepPosition = steps.indexOf(step)
    val currentPosition = steps.indexOf(currentStep)
    return when {
        stepPosition < currentPosition -> StepState.COMPLETED
        stepPosition == currentPosition -> StepState.ACTIVE
        else -> StepState.UPCOMING
    }
}

@Composable
private fun StepperItem(
    step: ContractStep,
    displayNumber: Int,
    stepState: StepState,
    isLast: Boolean,
    content: @Composable () -> Unit,
    navigation: @Composable () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val activeColor = taminColors.greenText
    val upcomingColor = MaterialTheme.colorScheme.outline
    val indicatorColor = when (stepState) {
        StepState.COMPLETED -> activeColor
        StepState.ACTIVE -> activeColor
        StepState.UPCOMING -> upcomingColor
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(Spacing.xxxl),
        ) {
            StepIndicator(
                stepIndex = displayNumber,
                stepState = stepState,
                indicatorColor = indicatorColor,
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(Thickness.medium)
                        .height(if (stepState == StepState.ACTIVE) Spacing.xxxxxxl + Spacing.xxxxl else Spacing.xxl)
                        .background(
                            if (stepState == StepState.COMPLETED) activeColor else upcomingColor.copy(alpha = 0.4f),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.width(Spacing.md))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Text(
                text = "$displayNumber. ${stringResource(step.titleRes)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (stepState == StepState.ACTIVE) FontWeight.Bold else FontWeight.Normal,
                color = when (stepState) {
                    StepState.UPCOMING -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else -> MaterialTheme.colorScheme.onSurface
                },
            )

            if (stepState == StepState.ACTIVE) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = Elevation.xxs),
                ) {
                    Column(
                        modifier = Modifier.padding(Spacing.lg),
                        verticalArrangement = Arrangement.spacedBy(Spacing.md),
                    ) {
                        content()
                        navigation()
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(
    stepIndex: Int,
    stepState: StepState,
    indicatorColor: Color,
) {
    Box(
        modifier = Modifier
            .size(IconSize.badge)
            .clip(CircleShape)
            .background(
                if (stepState == StepState.UPCOMING) Color.Transparent else indicatorColor,
            )
            .border(
                width = Thickness.medium,
                color = indicatorColor,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        when (stepState) {
            StepState.COMPLETED -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                )
            }
            StepState.ACTIVE -> {
                Text(
                    text = stepIndex.toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
            StepState.UPCOMING -> {
                Text(
                    text = stepIndex.toString(),
                    color = indicatorColor,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun RegistrationStepContent(
    info: RegistrationInfoPR,
    genderGateError: String? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(Res.string.contract_flow_registration_message, info.insuranceId),
            style = MaterialTheme.typography.bodyMedium,
        )
        genderGateError?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun AuthorizationStepContent(
    eligibility: ContractEligibilityPR,
    insuranceTypeLabel: String,
) {
    val taminColors = LocalTaminColors.current
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            imageVector = if (eligibility.isEligible) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (eligibility.isEligible) taminColors.greenText else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(IconSize.medium),
        )
        Text(
            text = eligibility.eligibilityMessage(insuranceTypeLabel),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun StepNavigationButtons(
    showPrevious: Boolean,
    showNext: Boolean,
    nextEnabled: Boolean,
    nextLoading: Boolean = false,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        if (showPrevious) {
            OutlinedButton(
                onClick = onPreviousStep,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                )
                Spacer(modifier = Modifier.width(Spacing.xs))
                Text(stringResource(Res.string.contract_flow_step_previous))
            }
        }
        if (showNext) {
            Button(
                onClick = onNextStep,
                enabled = nextEnabled,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                if (nextLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                        strokeWidth = ButtonDimens.loadingIndicatorStroke,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text(stringResource(Res.string.contract_flow_step_next))
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDimens.loadingIndicatorSize),
                    )
                }
            }
        }
    }
}
