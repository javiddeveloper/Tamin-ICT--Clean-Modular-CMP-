package com.tamin.taminhamrah.feature.healthProfile.ui

import com.tamin.taminhamrah.ui.components.TaminText

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileEvent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
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
    nationalCode : String
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(nationalCode) {
        viewModel.sendIntent(HealthProfileIntent.LoadHealthProfile(nationalCode))
    }

    HandleHealthProfileEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked
    )

    HealthProfileMainContent(
        state = uiState,
        selfDecState = uiState.selfDeclaration,
        onIntent = viewModel::sendIntent,
        onSelfDecIntent = viewModel::sendIntent,
        onBackClicked = onBackClicked
    )
}

@Composable
fun HandleHealthProfileEvents(
    events: Flow<HealthProfileEvent>,
    onBackClicked: () -> Unit
) {
    val scope = rememberCoroutineScope()
    events.collectWithLifecycleAware { event ->
        when (event) {
            HealthProfileEvent.NavigateBack -> {
                scope.launch { onBackClicked() }
            }

            else -> {
                //todo
            }
        }
    }
}

@Composable
fun HealthProfileMainContent(
    state: HealthProfileUiState,
    selfDecState: SelfDeclarationUiState,
    onIntent: (HealthProfileIntent) -> Unit,
    onSelfDecIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val previousStep = selfDecState.currentStep.previousStep()
    BackHandler {
        if (previousStep != null) {
            onSelfDecIntent(HealthProfileIntent.ChangeStep(previousStep))
        } else {
            onBackClicked()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            when (selfDecState.currentStep) {
                SelfDeclarationStep.GATE -> {
                    SelfDeclarationGateScreen(
                        onIntent = onSelfDecIntent,
                        onBackClicked = onBackClicked
                    )
                }
                SelfDeclarationStep.INTRO -> {
                    SelfDeclarationIntroScreen(
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.GATE))
                        }
                    )
                }
                SelfDeclarationStep.IDENTITY -> {
                    SelfDeclarationIdentityScreen(
                        state = selfDecState.identity,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.INTRO))
                        }
                    )
                }
                SelfDeclarationStep.PERSONAL -> {
                    SelfDeclarationPersonalScreen(
                        state = selfDecState.personal,
                        maritalStatusOptions = state.maritalStatusOptions,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.IDENTITY))
                        }
                    )
                }
                SelfDeclarationStep.CONTACT -> {
                    SelfDeclarationContactScreen(
                        state = selfDecState.contact,
                        provinceOptions = state.provinceOptions,
                        cityOptions = state.cityOptions,
                        isProvincesLoading = state.isProvincesLoading,
                        isCitiesLoading = state.isCitiesLoading,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.PERSONAL))
                        }
                    )
                }
                SelfDeclarationStep.EMERGENCY -> {
                    SelfDeclarationEmergencyScreen(
                        state = selfDecState.emergency,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.CONTACT))
                        }
                    )
                }
                SelfDeclarationStep.PHYSICAL -> {
                    SelfDeclarationPhysicalScreen(
                        state = selfDecState.physical,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.EMERGENCY))
                        }
                    )
                }
                SelfDeclarationStep.BLOOD -> {
                    SelfDeclarationBloodScreen(
                        state = selfDecState.bloodGroup,
                        bloodGroupOptions = state.bloodGroupOptions,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.PHYSICAL))
                        }
                    )
                }
                SelfDeclarationStep.LIFESTYLE -> {
                    SelfDeclarationLifestyleScreen(
                        state = state.selfDeclaration.lifestyle,
                        smokingStatusOptions = state.smokingStatusOptions,
                        actFrequencyOptions = state.actFrequencyOptions,
                        onIntent = onIntent,
                        onBackClicked = {
                            onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.BLOOD))
                        }
                    )
                }
                SelfDeclarationStep.DISEASES -> {
                    SelfDeclarationDiseasesScreen(
                        state = selfDecState.diseases,
                        illnessGroups = state.illnessGroups,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE))
                        }
                    )
                }
                SelfDeclarationStep.FAMILY -> {
                    SelfDeclarationFamilyScreen(
                        state = selfDecState.family,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.DISEASES))
                        }
                    )
                }
                SelfDeclarationStep.ALLERGY -> {
                    SelfDeclarationAllergyScreen(
                        state = selfDecState.allergy,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.FAMILY))
                        }
                    )
                }
                SelfDeclarationStep.REVIEW -> {
                    SelfDeclarationReviewScreen(
                        state = selfDecState,
                        onIntent = onSelfDecIntent,
                        onBackClicked = {
                            onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.ALLERGY))
                        }
                    )
                }

                SelfDeclarationStep.SUCCESS -> {
                    SelfDeclarationSuccessScreen(
                        onFinish = { enterProfile ->
                            if (enterProfile) {
                                onSelfDecIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.COMPLETED))
                            } else {
                                onBackClicked()
                            }
                        }
                    )
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
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (!state.error.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    TaminText(
                                        text = state.error ?: "",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { onIntent(HealthProfileIntent.LoadHealthProfile()) }) {
                                        TaminText("تلاش مجدد")
                                    }
                                }
                            } else {
                                HealthProfileContent(state = state)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HealthProfileContent(state: HealthProfileUiState) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. General Profile Card
        state.generalInfo?.let { general ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeaderTitle(title = "اطلاعات عمومی پرونده سلامت",)
                    Spacer(modifier = Modifier.height(12.dp))

                    val name = "${general.patientName ?: ""} ${general.patientFamily ?: ""}".trim()
                    ProfileRowItem(label = "نام بیمار:", value = name.ifEmpty { "نامشخص" })
                    ProfileRowItem(label = "کد ملی:", value = general.patientNatCode ?: "نامشخص")
                    ProfileRowItem(label = "نام پدر:", value = general.patientFather ?: "نامشخص")
                    ProfileRowItem(label = "سن:", value = "${general.patientAge ?: "نامشخص"} سال")
                    ProfileRowItem(label = "تاریخ تولد:", value = general.patientBirthDate ?: "نامشخص")
                    ProfileRowItem(label = "جنسیت:", value = general.patientGender ?: "نامشخص")
                    ProfileRowItem(label = "شماره همراه:", value = general.patientMobile ?: "نامشخص")
                    ProfileRowItem(label = "آدرس بیمار:", value = general.patientAddress ?: "نامشخص")
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
                        value = general.patientBloodGroup ?: "نامشخص",
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "قد (سانتی‌متر)",
                        value = general.patientHeight?.let { "${it.toInt()}" } ?: "نامشخص",
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "وزن (کیلوگرم)",
                        value = general.patientWeight?.let { "${it.toInt()}" } ?: "نامشخص",
                        modifier = Modifier.weight(1f)
                    )
                    MetricItem(
                        label = "شاخص توده بدنی (BMI)",
                        value = general.patientBMI.formatDecimal(1) ?: "نامشخص"     ,
                        modifier = Modifier.weight(1f)

                    )
                }
            }
        }

        // 2. Lifestyle/Self-Declarative Info Card
        state.lifestyleInfo?.let { lifestyle ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionHeaderTitle(title = "سبک زندگی و خود اظهاری",)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileRowItem(label = "مصرف سیگار / دخانیات:", value = lifestyle.smokingStatus.toString() ?: "نامشخص")
                    ProfileRowItem(label = "توضیحات دخانیات:", value = lifestyle.smokingDesc ?: "ندارد")
                    ProfileRowItem(label = "مصرف الکل:", value = lifestyle.alcoholUsage.toString() ?: "نامشخص")
                    ProfileRowItem(label = "توضیحات الکل:", value = lifestyle.alcoholDesc ?: "ندارد")
                    ProfileRowItem(label = "فراوانی ورزش:", value = lifestyle.exerciseFreq.toString() ?: "نامشخص")
                    ProfileRowItem(label = "توضیحات فعالیت ورزشی:", value = lifestyle.exerciseDesc ?: "ندارد")
                    ProfileRowItem(label = "سوء مصرف مواد:", value = lifestyle.substanceUsage.toString() ?: "نامشخص")
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
                SectionHeaderTitle(title = "حساسیت‌های دارویی",)
                Spacer(modifier = Modifier.height(12.dp))

                if (state.drugAllergies.isEmpty()) {
                    TaminText(
                        text = "هیچ حساسیت دارویی ثبت نشده است.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    state.drugAllergies.forEach { allergy ->
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
        state.generalInfo?.let { general ->
            if (!general.emergencyName.isNullOrEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeaderTitle(title = "اطلاعات تماس اضطراری",)
                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileRowItem(label = "نام مخاطب اضطراری:", value = "${general.emergencyName} ${general.emergencyFamily ?: ""}".trim())
                        ProfileRowItem(label = "نسبت خانوادگی:", value = general.emergencyRelation ?: "نامشخص")
                        ProfileRowItem(label = "تلفن همراه اضطراری:", value = general.emergencyMobile ?: "نامشخص")
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
