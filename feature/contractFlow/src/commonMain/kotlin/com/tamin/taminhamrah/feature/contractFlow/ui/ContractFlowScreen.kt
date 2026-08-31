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
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowIntent
import com.tamin.taminhamrah.feature.contractFlow.ui.contract.ContractFlowUiState
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.ProvincePR
import com.tamin.taminhamrah.model.contractFlow.ContractApplicantStepContent
import com.tamin.taminhamrah.model.contractFlow.ContractApplicantType
import com.tamin.taminhamrah.model.contractFlow.ContractEligibilityPR
import com.tamin.taminhamrah.model.contractFlow.ContractStep
import com.tamin.taminhamrah.model.contractFlow.ContractTermsStepContent
import com.tamin.taminhamrah.model.contractFlow.InsurancePremiumStepContent
import com.tamin.taminhamrah.model.contractFlow.PremiumSalaryStepContent
import com.tamin.taminhamrah.model.contractFlow.SelectBranchStepContent
import com.tamin.taminhamrah.model.contractFlow.SpcPremiumRateOptionPR
import com.tamin.taminhamrah.model.contractFlow.SubmitContractStepContent
import com.tamin.taminhamrah.model.contractFlow.UploadImageStepContent
import com.tamin.taminhamrah.model.contractFlow.UserInfoFormPR
import com.tamin.taminhamrah.model.contractFlow.UserInfoStepContent
import com.tamin.taminhamrah.model.contracts.BranchPR
import com.tamin.taminhamrah.model.contracts.FreeJobDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import org.koin.compose.viewmodel.koinViewModel

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
                title = { Text(state.config?.screenTitle ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
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
                    text = state.error ?: "خطای ناشناخته",
                    color = MaterialTheme.colorScheme.error,
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    state.registrationInfo?.let { info ->
                        item {
                            RegistrationHeaderCard(info)
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
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
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = info.fullName, style = MaterialTheme.typography.titleLarge)
            Text(text = "کد ملی: ${info.nationalId}")
            Text(text = "تاریخ تولد: ${info.birthDateFormatted}")
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
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
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
                                    insuranceTypeLabel = config?.insuranceTypeLabel ?: "",
                                )
                            } else {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
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
                                text = "این مرحله هنوز پیاده‌سازی نشده است.",
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
                                agreementContractLabel = config?.agreementContractLabel ?: "",
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
    val activeColor = Color(0xFF2E7D32)
    val upcomingColor = MaterialTheme.colorScheme.outline
    val indicatorColor = when (stepState) {
        StepState.COMPLETED -> activeColor
        StepState.ACTIVE -> activeColor
        StepState.UPCOMING -> upcomingColor
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp),
        ) {
            StepIndicator(
                stepIndex = displayNumber,
                stepState = stepState,
                indicatorColor = indicatorColor,
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (stepState == StepState.ACTIVE) 120.dp else 32.dp)
                        .background(
                            if (stepState == StepState.COMPLETED) activeColor else upcomingColor.copy(alpha = 0.4f),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "$displayNumber. ${step.title}",
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
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
            .size(32.dp)
            .clip(CircleShape)
            .background(
                if (stepState == StepState.UPCOMING) Color.Transparent else indicatorColor,
            )
            .border(
                width = 2.dp,
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
                    modifier = Modifier.size(18.dp),
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "متقاضی محترم، نام‌نویسی شما با شماره بیمه تأمین اجتماعی ${info.insuranceId} انجام شده است.",
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
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = if (eligibility.isEligible) Icons.Default.Check else Icons.Default.Close,
            contentDescription = null,
            tint = if (eligibility.isEligible) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = eligibility.message(insuranceTypeLabel),
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
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showPrevious) {
            OutlinedButton(
                onClick = onPreviousStep,
                modifier = Modifier.weight(1f),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("مرحله قبل")
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
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                } else {
                    Text("مرحله بعد")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }
    }
}
