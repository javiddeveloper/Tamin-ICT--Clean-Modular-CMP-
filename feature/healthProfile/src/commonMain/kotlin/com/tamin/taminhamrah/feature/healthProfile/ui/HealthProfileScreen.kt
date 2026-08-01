package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthProfileShimmerSkeleton
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthProfileErrorWrapper
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.ErrorSource
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileEvent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.DrugAllergyItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativePR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthTopAppBar
import com.tamin.taminhamrah.feature.healthProfile.ui.screens.*
import com.tamin.taminhamrah.ui.components.SectionHeaderTitle
import com.tamin.taminhamrah.util.formatDecimal
import org.koin.compose.viewmodel.koinViewModel

private fun SelfDeclarationStep.previousStep(): SelfDeclarationStep? = when (this) {
    SelfDeclarationStep.INTRO -> SelfDeclarationStep.GATE
    SelfDeclarationStep.IDENTITY -> SelfDeclarationStep.INTRO
    SelfDeclarationStep.PERSONAL -> SelfDeclarationStep.IDENTITY
    SelfDeclarationStep.CONTACT -> SelfDeclarationStep.PERSONAL
    SelfDeclarationStep.EMERGENCY -> SelfDeclarationStep.CONTACT
    SelfDeclarationStep.PHYSICAL -> SelfDeclarationStep.EMERGENCY
    SelfDeclarationStep.BLOOD -> SelfDeclarationStep.PHYSICAL
    SelfDeclarationStep.LIFESTYLE -> SelfDeclarationStep.BLOOD
    SelfDeclarationStep.DISEASES -> SelfDeclarationStep.LIFESTYLE
    SelfDeclarationStep.FAMILY -> SelfDeclarationStep.DISEASES
    SelfDeclarationStep.ALLERGY -> SelfDeclarationStep.FAMILY
    SelfDeclarationStep.REVIEW -> SelfDeclarationStep.ALLERGY
    else -> null
}

@Composable
fun HealthProfileScreen(
    viewModel: HealthProfileViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
    nationalCode: String
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(nationalCode) {
        viewModel.sendIntent(HealthProfileIntent.LoadHealthProfile(nationalCode))
    }

    HandleHealthProfileEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked
    )

    HealthProfileMainContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked
    )
}

@Composable
fun HandleHealthProfileEvents(
    events: Flow<HealthProfileEvent>,
    onBackClicked: () -> Unit
) {
    events.collectWithLifecycleAware(key = onBackClicked) { event ->
        when (event) {
            HealthProfileEvent.NavigateBack -> {
                onBackClicked()
            }

            else -> {
                // todo
            }
        }
    }
}

@Composable
fun HealthProfileMainContent(
    state: HealthProfileUiState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val selfDecState = state.selfDeclaration
    val currentStep = selfDecState.currentStep
    val combinedLoading = state.isLoading || selfDecState.isLoading

    val navigateBack = remember(currentStep, onBackClicked, onIntent) {
        {
            val previousStep = currentStep.previousStep()
            if (previousStep != null) {
                onIntent(HealthProfileIntent.ChangeStep(previousStep))
            } else {
                onBackClicked()
            }
        }
    }

    BackHandler(onBack = navigateBack)

    val errors = state.errors

    fun getError(vararg allowedSources: ErrorSource): String? {
        val matchingSource = allowedSources.firstOrNull { it in errors }
        return errors[matchingSource] ?: errors[ErrorSource.PATIENT_GENERAL]
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            when (currentStep) {
                SelfDeclarationStep.GATE -> {
                    SelfDeclarationGateScreen(
                        onIntent = onIntent,
                        onBackClicked = onBackClicked
                    )
                }
                SelfDeclarationStep.INTRO -> {
                    SelfDeclarationIntroScreen(
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.IDENTITY -> {
                    SelfDeclarationIdentityScreen(
                        state = selfDecState.identity,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.PERSONAL -> {
                    SelfDeclarationPersonalScreen(
                        state = selfDecState.personal,
                        maritalStatusOptions = state.maritalStatusOptions,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.MARITAL_STATUS),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.CONTACT -> {
                    SelfDeclarationContactScreen(
                        state = selfDecState.contact,
                        provinceOptions = state.provinceOptions,
                        cityOptions = state.cityOptions,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.PROVINCES, ErrorSource.CITIES),
                        isProvincesLoading = state.isProvincesLoading,
                        isCitiesLoading = state.isCitiesLoading,
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.EMERGENCY -> {
                    SelfDeclarationEmergencyScreen(
                        state = selfDecState.emergency,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.PHYSICAL -> {
                    SelfDeclarationPhysicalScreen(
                        state = selfDecState.physical,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.BLOOD -> {
                    SelfDeclarationBloodScreen(
                        state = selfDecState.bloodGroup,
                        bloodGroupOptions = state.bloodGroupOptions,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.BLOOD_GROUPS),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.LIFESTYLE -> {
                    SelfDeclarationLifestyleScreen(
                        state = selfDecState.lifestyle,
                        smokingStatusOptions = state.smokingStatusOptions,
                        actFrequencyOptions = state.actFrequencyOptions,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.SMOKING_STATUS, ErrorSource.ACT_FREQUENCIES),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.DISEASES -> {
                    SelfDeclarationDiseasesScreen(
                        state = selfDecState.diseases,
                        illnessGroups = state.illnessGroups,
                        isLoading = combinedLoading,
                        onIntent = onIntent,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.ILLNESS_GROUPS),
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.FAMILY -> {
                    SelfDeclarationFamilyScreen(
                        state = selfDecState.family,
                        illnessGroups = state.illnessGroups,
                        isLoading = combinedLoading,
                        onIntent = onIntent,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.ILLNESS_GROUPS),
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.ALLERGY -> {
                    SelfDeclarationAllergyScreen(
                        state = selfDecState.allergy,
                        drugOptions = state.drugOptions,
                        isLoading = combinedLoading,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.DRUGS),
                        onIntent = onIntent,
                        onBackClicked = navigateBack
                    )
                }
                SelfDeclarationStep.REVIEW -> {
                    SelfDeclarationReviewScreen(
                        state = state,
                        isLoading = combinedLoading,
                        onIntent = onIntent,
                        error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.ILLNESS_GROUPS, ErrorSource.DRUGS),
                        onBackClicked = navigateBack
                    )
                }

                SelfDeclarationStep.SUCCESS -> {
                    val onFinish = remember(onIntent, onBackClicked) {
                        { enterProfile: Boolean ->
                            if (enterProfile) {
                                onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.COMPLETED))
                            } else {
                                onBackClicked()
                            }
                        }
                    }
                    SelfDeclarationSuccessScreen(onFinish = onFinish)
                }
                else -> {
                    Scaffold(
                        topBar = {
                            HealthTopAppBar(
                                title = "پروندهٔ سلامت",
                                onBackClicked = onBackClicked
                            )
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                        ) {
                            HealthProfileErrorWrapper(
                                isLoading = state.isLoading,
                                error = getError(ErrorSource.PATIENT_GENERAL, ErrorSource.PATIENT_LIFESTYLE, ErrorSource.PATIENT_ALLERGIES),
                                onRetry = { onIntent(HealthProfileIntent.RetryStep) },
                                shimmerContent = { HealthProfileShimmerSkeleton() }
                            ) {
                                HealthProfileContent(
                                    generalInfo = state.generalInfo,
                                    lifestyleInfo = state.lifestyleInfo,
                                    drugAllergies = state.drugAllergies
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun HealthProfileContent(
    generalInfo: PatientGeneralPR?,
    lifestyleInfo: PatientSelfDeclarativePR?,
    drugAllergies: List<DrugAllergyItemPR>
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. General Profile Card
        generalInfo?.let { general ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeaderTitle(title = "اطلاعات عمومی پرونده سلامت")
                    Spacer(modifier = Modifier.height(12.dp))

                    val name = "${general.patientName} ${general.patientFamily}".trim()
                    ProfileRowItem(label = "نام بیمار:", value = name.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "کد ملی:", value = general.patientNatCode.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "نام پدر:", value = general.patientFather.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "سن:", value = "${general.patientAge.ifEmpty { "نامشخص" }} سال")
                    ProfileRowItem(label = "تاریخ تولد:", value = general.patientBirthDate.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "جنسیت:", value = general.patientGender.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "شماره همراه:", value = general.patientMobile.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "آدرس بیمار:", value = general.patientAddress.ifEmpty { "نامشخص" })
                }
            }

            // Health Metrics Card (BMI, Height, Weight, Blood Type)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MetricItem(
                        label = "گروه خونی",
                        value = general.patientBloodGroup.ifEmpty { "نامشخص" },
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "قد (سانتی‌متر)",
                        value = general.patientHeight.let { "${it.toInt()}" },
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "وزن (کیلوگرم)",
                        value = general.patientWeight.let { "${it.toInt()}" },
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "شاخص توده بدنی (BMI)",
                        value = general.patientBMI.formatDecimal(1) ?: "نامشخص",
                        modifier = Modifier.weight(1f)

                    )
                }
            }
        }

        // 2. Lifestyle/Self-Declarative Info Card
        lifestyleInfo?.let { lifestyle ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeaderTitle(title = "سبک زندگی و خود اظهاری")
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileRowItem(label = "مصرف سیگار / دخانیات:", value = lifestyle.smokingStatusLabel.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "توضیحات دخانیات:", value = lifestyle.smokingDesc.ifEmpty { "ندارد" })
                    ProfileRowItem(label = "مصرف الکل:", value = lifestyle.alcoholUsageLabel.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "توضیحات الکل:", value = lifestyle.alcoholDesc.ifEmpty { "ندارد" })
                    ProfileRowItem(label = "فراوانی ورزش:", value = lifestyle.exerciseFreqLabel.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "توضیحات فعالیت ورزشی:", value = lifestyle.exerciseDesc.ifEmpty { "ندارد" })
                    ProfileRowItem(label = "سوء مصرف مواد:", value = lifestyle.substanceUsageLabel.ifEmpty { "نامشخص" })
                }
            }
        }

        // 3. Drug Allergies Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SectionHeaderTitle(title = "حساسیت‌های دارویی")
                Spacer(modifier = Modifier.height(12.dp))

                if (drugAllergies.isEmpty()) {
                    TaminText(
                        text = "هیچ حساسیت دارویی ثبت نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    drugAllergies.forEach { allergy ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                TaminText(
                                    text = allergy.drugName ?: "داروی نامشخص",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                TaminText(
                                    text = allergy.allergyComments ?: "فاقد توضیحات",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Emergency Contact Details Card
        generalInfo?.let { general ->
            if (general.emergencyName.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeaderTitle(title = "اطلاعات تماس اضطراری")
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileRowItem(label = "نام مخاطب اضطراری:", value = "${general.emergencyName} ${general.emergencyFamily}".trim())
                        ProfileRowItem(label = "نسبت خانوادگی:", value = general.emergencyRelation.ifEmpty { "نامشخص" })
                        ProfileRowItem(label = "تلفن همراه اضطراری:", value = general.emergencyMobile.ifEmpty { "نامشخص" })
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRowItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun MetricItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TaminText(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        TaminText(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}
