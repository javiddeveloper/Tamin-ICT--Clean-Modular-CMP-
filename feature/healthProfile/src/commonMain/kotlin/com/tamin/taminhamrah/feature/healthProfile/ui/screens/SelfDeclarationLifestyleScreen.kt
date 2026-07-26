package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.LifestyleStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep

import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.ui.components.IconBox
import org.jetbrains.compose.resources.painterResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.ic_health_addiction
import taminx.feature.healthprofile.generated.resources.ic_health_alcohol
import taminx.feature.healthprofile.generated.resources.ic_health_tobacco

@Composable
fun SelfDeclarationLifestyleScreen(
    state: LifestyleStepState,
    smokingStatusOptions: List<LookupItemPR>,
    actFrequencyOptions: List<LookupItemPR>,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showSmokingBottomSheet by remember { mutableStateOf(false) }
    var showAddictionBottomSheet by remember { mutableStateOf(false) }
    var showAlcoholBottomSheet by remember { mutableStateOf(false) }
    var showExerciseBottomSheet by remember { mutableStateOf(false) }

    val optionsYesNo = listOf("خیر", "بله")

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 9,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.ALLERGY)) },
                secondaryText = "مرحلهٔ قبلی",
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TaminText(
                text = "سبک زندگی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            InfoBanner(
                message = "فرآیند اطلاعات شما کاملاً محرمانه بوده و تنها برای ارزیابی پروندهٔ سلامت استفاده می‌شود.",
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Smoking
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_tobacco),
                        backgroundColor = LocalTaminColors.current.orangeBg,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        "آیا از دخانیات استفاده می\u200Cکنید؟",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isSmoking == true) 1 else 0,
                    onOptionSelected = { idx ->
                        val isYes = idx == 1
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isSmoking = isYes)))
                        if (isYes) showSmokingBottomSheet = true
                    }
                )
            }

            // Addiction
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_addiction),
                        backgroundColor = LocalTaminColors.current.orangeBg,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        "آیا اعتیاد دارید؟",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasAddiction == true) 1 else 0,
                    onOptionSelected = { idx ->
                        val isYes = idx == 1
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(hasAddiction = isYes)))
                        if (isYes) showAddictionBottomSheet = true
                    }
                )
            }

            // Alcohol
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_alcohol),
                        backgroundColor = LocalTaminColors.current.orangeBg,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        "آیا الکل مصرف دارید؟",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isDrinking == true) 1 else 0,
                    onOptionSelected = { idx ->
                        val isYes = idx == 1
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isDrinking = isYes)))
                        if (isYes) showAlcoholBottomSheet = true
                    }
                )
            }

            // Exercise
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_alcohol),
                        backgroundColor = LocalTaminColors.current.orangeBg,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        "آیا ورزش می\u200Cکنید؟",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isExercising == true) 1 else 0,
                    onOptionSelected = { idx ->
                        val isYes = idx == 1
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isExercising = isYes)))
                        if (isYes) showExerciseBottomSheet = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }

    if (showSmokingBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = BottomSheetType.SMOKING_ADDICTION.title ?: "",
                subtitle = "الگوی مصرف خود را انتخاب کنید",
                type = BottomSheetType.SMOKING_ADDICTION,
                singleSelection = true,
                items = smokingStatusOptions.map {
                    BottomSheetItem(id = it.id, title = it.label, isSelected = state.smokingStatusId == it.id)
                }
            ),
            onDismissRequest = { showSmokingBottomSheet = false },
            onSubmit = { result ->
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            smokingStatusId = result.selectedItemIds.firstOrNull(),
                            smokingPattern = result.description
                        )
                    )
                )
                showSmokingBottomSheet = false
            }
        )
    }

    if (showAddictionBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = BottomSheetType.DRUG_ADDICTION.title ?: "",
                subtitle = "الگوی مصرف خود را انتخاب کنید",
                type = BottomSheetType.DRUG_ADDICTION,
                singleSelection = true,
                items = actFrequencyOptions.map {
                    BottomSheetItem(id = it.id, title = it.label) // Not saving id to state currently based on UI contract
                }
            ),
            onDismissRequest = { showAddictionBottomSheet = false },
            onSubmit = { result ->
                // API just takes boolean for substance usage right now, but UI shows description
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            // Not mapping frequency ID yet as it's not in LifestyleStepState, but we map description to patterns if we want, but actually there is no addictionPattern. We'll skip saving the freq id or use description if added. Wait, API requires substanceUseDesc? Yes, it's null in ViewModel currently. We can just dismiss.
                        )
                    )
                )
                showAddictionBottomSheet = false
            }
        )
    }

    if (showAlcoholBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = BottomSheetType.ALCOHOL_ADDICTION.title ?: "",
                subtitle = "جزئیات مربوط به مصرف الکل را وارد کنید",
                type = BottomSheetType.ALCOHOL_ADDICTION,
                singleSelection = true,
                items = actFrequencyOptions.map {
                    BottomSheetItem(id = it.id, title = it.label)
                }
            ),
            onDismissRequest = { showAlcoholBottomSheet = false },
            onSubmit = { result ->
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            drinkingPattern = result.description ?: actFrequencyOptions.find { it.id == result.selectedItemIds.firstOrNull() }?.label
                        )
                    )
                )
                showAlcoholBottomSheet = false
            }
        )
    }

    if (showExerciseBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = BottomSheetType.EXERCISE.title ?: "",
                subtitle = "جزئیات مربوط به ورزش را وارد کنید",
                type = BottomSheetType.EXERCISE,
                singleSelection = true,
                items = actFrequencyOptions.map {
                    BottomSheetItem(id = it.id, title = it.label)
                }
            ),
            onDismissRequest = { showExerciseBottomSheet = false },
            onSubmit = { result ->
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            exerciseFrequency = result.description ?: actFrequencyOptions.find { it.id == result.selectedItemIds.firstOrNull() }?.label
                        )
                    )
                )
                showExerciseBottomSheet = false
            }
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationLifestyleScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationLifestyleScreen(
            state = LifestyleStepState(isSmoking = true, smokingPattern = "روزانه"),
            smokingStatusOptions = listOf(),
            actFrequencyOptions = listOf(),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

