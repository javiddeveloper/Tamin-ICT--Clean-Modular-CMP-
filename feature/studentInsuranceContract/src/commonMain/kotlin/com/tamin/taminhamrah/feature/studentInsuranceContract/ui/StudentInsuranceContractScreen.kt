package com.tamin.taminhamrah.feature.studentInsuranceContract.ui

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
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractIntent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.contract.StudentInsuranceContractUiState
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractApplicantType
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.ContractEligibilityPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.StudentInsuranceContractStep
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.UserInfoFormPR
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.step.ContractApplicantStepContent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.step.ContractTermsStepContent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.step.SelectBranchStepContent
import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.step.UserInfoStepContent
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun StudentInsuranceContractScreen(
    onBack: () -> Unit,
    onShowRules: () -> Unit = {},
    viewModel: StudentInsuranceContractViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(StudentInsuranceContractIntent.LoadInitialData)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("انعقاد قرارداد بیمه دانشجویی") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت")
                    }
                },
            )
        },
    ) { padding ->
        StudentInsuranceContractContent(
            state = state,
            onNextStep = { viewModel.sendIntent(StudentInsuranceContractIntent.GoToNextStep) },
            onPreviousStep = { viewModel.sendIntent(StudentInsuranceContractIntent.GoToPreviousStep) },
            onRulesConfirmedChange = {
                viewModel.sendIntent(StudentInsuranceContractIntent.SetRulesConfirmed(it))
            },
            onUserInfoChange = {
                viewModel.sendIntent(StudentInsuranceContractIntent.UpdateUserInfo(it))
            },
            onContractApplicantTypeChange = {
                viewModel.sendIntent(StudentInsuranceContractIntent.SetContractApplicantType(it))
            },
            onBranchProvinceSelected = {
                viewModel.sendIntent(StudentInsuranceContractIntent.SelectBranchProvince(it))
            },
            onBranchCitySelected = {
                viewModel.sendIntent(StudentInsuranceContractIntent.SelectBranchCity(it))
            },
            onBranchSelected = {
                viewModel.sendIntent(StudentInsuranceContractIntent.SelectBranch(it))
            },
            onShowRules = onShowRules,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun StudentInsuranceContractContent(
    state: StudentInsuranceContractUiState,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onUserInfoChange: (UserInfoFormPR) -> Unit,
    onContractApplicantTypeChange: (ContractApplicantType) -> Unit,
    onBranchProvinceSelected: (CityOptionPR) -> Unit,
    onBranchCitySelected: (CityOptionPR) -> Unit,
    onBranchSelected: (CityOptionPR) -> Unit,
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
    state: StudentInsuranceContractUiState,
    info: RegistrationInfoPR,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onRulesConfirmedChange: (Boolean) -> Unit,
    onUserInfoChange: (UserInfoFormPR) -> Unit,
    onContractApplicantTypeChange: (ContractApplicantType) -> Unit,
    onBranchProvinceSelected: (CityOptionPR) -> Unit,
    onBranchCitySelected: (CityOptionPR) -> Unit,
    onBranchSelected: (CityOptionPR) -> Unit,
    onShowRules: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        StudentInsuranceContractStep.orderedSteps.forEachIndexed { index, step ->
            val stepState = resolveStepState(step, state.currentStep)
            StepperItem(
                step = step,
                stepState = stepState,
                isLast = index == StudentInsuranceContractStep.orderedSteps.lastIndex,
                content = {
                    when (step) {
                        StudentInsuranceContractStep.STEP_REGISTRATION -> {
                            RegistrationStepContent(info = info)
                        }
                        StudentInsuranceContractStep.STEP_AUTHORIZATION -> {
                            if (state.eligibility != null) {
                                AuthorizationStepContent(eligibility = state.eligibility)
                            } else {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                        StudentInsuranceContractStep.STEP_CONTRACT_TERMS -> {
                            ContractTermsStepContent(
                                info = info,
                                isRulesConfirmed = state.isRulesConfirmed,
                                onRulesConfirmedChange = onRulesConfirmedChange,
                                onShowRules = onShowRules,
                            )
                        }
                        StudentInsuranceContractStep.STEP_USER_INFO -> {
                            UserInfoStepContent(
                                userInfo = state.userInfo,
                                cities = state.cities,
                                isCitiesLoading = state.isCitiesLoading,
                                onCitySelected = { city ->
                                    onUserInfoChange(
                                        state.userInfo.copy(
                                            cityCode = city.code,
                                            cityName = city.name,
                                        ),
                                    )
                                },
                                onAddressChange = { onUserInfoChange(state.userInfo.copy(address = it)) },
                                onZipCodeChange = { onUserInfoChange(state.userInfo.copy(zipCode = it)) },
                                onPhoneNumberChange = { onUserInfoChange(state.userInfo.copy(phoneNumber = it)) },
                            )
                        }
                        StudentInsuranceContractStep.STEP_CONTRACT_APPLICANT -> {
                            ContractApplicantStepContent(
                                selectedType = state.contractApplicantType,
                                onTypeSelected = onContractApplicantTypeChange,
                            )
                        }
                        StudentInsuranceContractStep.STEP_SELECT_BRANCH -> {
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
                        else -> Unit
                    }
                },
                navigation = {
                    if (stepState == StepState.ACTIVE) {
                        StepNavigationButtons(
                            showPrevious = step != StudentInsuranceContractStep.STEP_REGISTRATION,
                            showNext = step != StudentInsuranceContractStep.STEP_SUBMIT_CONTRACT,
                            nextEnabled = state.canGoNext,
                            onNextStep = onNextStep,
                            onPreviousStep = onPreviousStep,
                        )
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
    step: StudentInsuranceContractStep,
    currentStep: StudentInsuranceContractStep,
): StepState = when {
    step.stepIndex < currentStep.stepIndex -> StepState.COMPLETED
    step.stepIndex == currentStep.stepIndex -> StepState.ACTIVE
    else -> StepState.UPCOMING
}

@Composable
private fun StepperItem(
    step: StudentInsuranceContractStep,
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
                stepIndex = step.stepIndex,
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
                text = "${step.stepIndex}. ${step.title}",
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
private fun RegistrationStepContent(info: RegistrationInfoPR) {
    Text(
        text = "متقاضی محترم، نام‌نویسی شما با شماره بیمه تأمین اجتماعی ${info.insuranceId} انجام شده است.",
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
private fun AuthorizationStepContent(eligibility: ContractEligibilityPR) {
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
            text = eligibility.message(),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun StepNavigationButtons(
    showPrevious: Boolean,
    showNext: Boolean,
    nextEnabled: Boolean,
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
