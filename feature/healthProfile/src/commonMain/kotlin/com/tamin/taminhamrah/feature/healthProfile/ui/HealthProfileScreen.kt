package com.tamin.taminhamrah.feature.healthProfile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.SectionHeader
import com.tamin.taminhamrah.ui.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthProfileScreen(
    viewModel: HealthProfileViewModel = koinViewModel(),
    selfDecViewModel: SelfDeclarationViewModel = koinViewModel(),
    onBackClicked: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selfDecState by selfDecViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.sendIntent(HealthProfileIntent.LoadHealthProfile)
    }

    viewModel.events.collectWithLifecycleAware { event ->
        // Handle events if any
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ProvideTextStyle(value = MaterialTheme.typography.bodyMedium) {
            when (selfDecState.currentStep) {
                SelfDeclarationStep.GATE -> {
                    SelfDeclarationGateScreen(
                        onBackClicked = onBackClicked,
                        onStartClicked = {
                            selfDecViewModel.sendIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.INTRO))
                        }
                    )
                }
                SelfDeclarationStep.INTRO -> {
                    SelfDeclarationIntroScreen(
                        onBackClicked = {
                            selfDecViewModel.sendIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.GATE))
                        },
                        onNextClicked = {
                            // In the first step, click Next to display the mock profile data screen
                            selfDecViewModel.sendIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.IDENTITY))
                        }
                    )
                }
                else -> {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "پروفایل سلامت",
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        )
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        selfDecViewModel.sendIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.INTRO))
                                    }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "بازگشت"
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (!uiState.error.isNullOrEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = uiState.error ?: "",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { viewModel.sendIntent(HealthProfileIntent.LoadHealthProfile) }) {
                                        Text("تلاش مجدد")
                                    }
                                }
                            } else {
                                HealthProfileContent(state = uiState)
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
                    SectionHeader(title = "اطلاعات عمومی پرونده سلامت", showDivider = false)
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
                        value = general.patientBMI?.let { String.format("%.1f", it) } ?: "نامشخص",
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
                    SectionHeader(title = "سبک زندگی و خود اظهاری", showDivider = false)
                    Spacer(modifier = Modifier.height(12.dp))

                    ProfileRowItem(label = "مصرف سیگار / دخانیات:", value = lifestyle.smokingStatusTitle ?: "نامشخص")
                    ProfileRowItem(label = "توضیحات دخانیات:", value = lifestyle.smokingDesc ?: "ندارد")
                    ProfileRowItem(label = "مصرف الکل:", value = lifestyle.alcoholUsageTitle ?: "نامشخص")
                    ProfileRowItem(label = "توضیحات الکل:", value = lifestyle.alcoholDesc ?: "ندارد")
                    ProfileRowItem(label = "فراوانی ورزش:", value = lifestyle.exerciseFreqTitle ?: "نامشخص")
                    ProfileRowItem(label = "توضیحات فعالیت ورزشی:", value = lifestyle.exerciseDesc ?: "ندارد")
                    ProfileRowItem(label = "سوء مصرف مواد:", value = lifestyle.substanceUsageTitle ?: "نامشخص")
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
                SectionHeader(title = "حساسیت‌های دارویی", showDivider = false)
                Spacer(modifier = Modifier.height(12.dp))

                if (state.drugAllergies.isEmpty()) {
                    Text(
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
                                Text(
                                    text = allergy.drugName ?: "داروی نامشخص",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
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
                        SectionHeader(title = "اطلاعات تماس اضطراری", showDivider = false)
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
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
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
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
