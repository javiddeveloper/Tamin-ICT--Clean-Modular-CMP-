package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthIrritateNavigationBar
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthTopAppBar
import com.tamin.taminhamrah.feature.healthProfile.ui.components.InfoBanner
import com.tamin.taminhamrah.feature.healthProfile.ui.components.InteractiveChoiceChips
import com.tamin.taminhamrah.feature.healthProfile.ui.components.SegmentedControl
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.DiseasesStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.IllnessGroupPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.util.Logger
import org.jetbrains.compose.resources.painterResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.ic_health_cancer
import taminx.feature.healthprofile.generated.resources.ic_health_disease
import taminx.feature.healthprofile.generated.resources.ic_health_high_risk
import taminx.feature.healthprofile.generated.resources.ic_health_mental

@Composable
fun SelfDeclarationDiseasesScreen(
    state: DiseasesStepState,
    illnessGroups: List<IllnessGroupPR>,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val optionsYesNo = listOf("خیر", "بله")

    // Filter the groups for this screen (forFamily == false)
    val group1 = illnessGroups.find { it.groupId == 1 && !it.forFamily }
    val group2 = illnessGroups.find { it.groupId == 2 && !it.forFamily }
    val group3 = illnessGroups.find { it.groupId == 3 && !it.forFamily }
    val group4 = illnessGroups.find { it.groupId == 4 && !it.forFamily }

    // Bottom sheet visibility states
    var showGroup2Sheet by remember { mutableStateOf(false) }
    var showGroup3Sheet by remember { mutableStateOf(false) }
    var showGroup4Sheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Logger.d(
            "DiseasesScreen",
            """
        Initial State Loaded:
        riskFactorIds: ${state.riskFactorIds}
        hasChronicDisease: ${state.hasChronicDisease}, chronicDiseaseIds: ${state.chronicDiseaseIds}
        hasMentalIllness: ${state.hasMentalIllness}, mentalIllnessIds: ${state.mentalIllnessIds}
        hasCancer: ${state.hasCancer}, cancerIds: ${state.cancerIds}
        """.trimIndent()
        )
    }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = "سوالات سلامت",
                currentStep = 8,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.FAMILY)) },
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

            InfoBanner(
                message = "فرآیند اطلاعات شما کاملاً محرمانه بوده و تنها برای ارزیابی پروندهٔ سلامت استفاده می‌شود.",
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Group 1: Risk Factors
            if (group1 != null) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_high_risk),
                        backgroundColor = LocalTaminColors.current.greenText.copy(alpha = 0.2f),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        text = "آیا سابقهٔ بالا بودن هر یک از موارد زیر را دارید؟",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                InteractiveChoiceChips(
                    options = group1.illnesses.map { it.label },
                    selectedIndices = group1.illnesses.mapIndexedNotNull { index, item ->
                        if (state.riskFactorIds.contains(item.id)) index else null
                    }.toSet(),
                    onSelectionChanged = { indices ->
                        val selectedIds = indices.map { group1.illnesses[it].id }.toSet()
                        onIntent(HealthProfileIntent.UpdateDiseases(state.copy(riskFactorIds = selectedIds)))
                    }
                )
            }

            // Group 2: Chronic illness
            if (group2 != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(
                            painter = painterResource(Res.drawable.ic_health_disease),
                            backgroundColor = LocalTaminColors.current.hawkesBlue.copy(alpha = 0.6f),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TaminText(
                            "آیا سابقه ابتلا به بیماری دارید؟",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )

                    }
                    SegmentedControl(
                        options = optionsYesNo,
                        selectedIndex = if (state.hasChronicDisease == true) 1 else 0,
                        onOptionSelected = { idx ->
                            val isYes = idx == 1
                            onIntent(
                                HealthProfileIntent.UpdateDiseases(
                                    state.copy(
                                        hasChronicDisease = isYes,
                                        chronicDiseaseIds = if (isYes) state.chronicDiseaseIds else emptySet()
                                    )
                                )
                            )
                            if (isYes) showGroup2Sheet = true
                        }
                    )
                }

                if (state.hasChronicDisease == true && state.chronicDiseaseIds.isNotEmpty()) {
                    val selectedItems =
                        group2.illnesses.filter { state.chronicDiseaseIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGroup2Sheet = true },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedItems.forEach { item ->
                            CustomChip(
                                text = item.label,
                                containerColor = LocalTaminColors.current.hawkesBlue.copy(alpha = 0.6f),
                                textColor = taminColors.textPrimary
                            )
                        }
                    }
                }
            }

            // Group 3: Mental Illness
            if (group3 != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(
                            painter = painterResource(Res.drawable.ic_health_mental),
                            backgroundColor = LocalTaminColors.current.fuchsiaBlue.copy(alpha = 0.13f),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TaminText(
                            "آیا بیماری اعصاب و روان دارید؟",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )
                    }

                    SegmentedControl(
                        options = optionsYesNo,
                        selectedIndex = if (state.hasMentalIllness == true) 1 else 0,
                        onOptionSelected = { idx ->
                            val isYes = idx == 1
                            onIntent(
                                HealthProfileIntent.UpdateDiseases(
                                    state.copy(
                                        hasMentalIllness = isYes,
                                        mentalIllnessIds = if (isYes) state.mentalIllnessIds else emptySet()
                                    )
                                )
                            )
                            if (isYes) showGroup3Sheet = true
                        }
                    )
                }

                if (state.hasMentalIllness == true && state.mentalIllnessIds.isNotEmpty()) {
                    val selectedItems =
                        group3.illnesses.filter { state.mentalIllnessIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGroup3Sheet = true },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedItems.forEach { item ->
                            CustomChip(
                                text = item.label,
                                containerColor = taminColors.fuchsiaBlue.copy(alpha = 0.13f),
                                textColor = taminColors.textPrimary
                            )
                        }
                    }
                }
            }

            // Group 4: Cancer
            if (group4 != null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(
                            painter = painterResource(Res.drawable.ic_health_cancer),
                            backgroundColor = LocalTaminColors.current.dangerText.copy(alpha = 0.13f),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        TaminText(
                            "آیا سابقه ابتلا به سرطان دارید؟",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )
                    }

                    SegmentedControl(
                        options = optionsYesNo,
                        selectedIndex = if (state.hasCancer == true) 1 else 0,
                        onOptionSelected = { idx ->
                            val isYes = idx == 1
                            onIntent(
                                HealthProfileIntent.UpdateDiseases(
                                    state.copy(
                                        hasCancer = isYes,
                                        cancerIds = if (isYes) state.cancerIds else emptySet()
                                    )
                                )
                            )
                            if (isYes) showGroup4Sheet = true
                        }
                    )
                }

                if (state.hasCancer == true && state.cancerIds.isNotEmpty()) {
                    val selectedItems = group4.illnesses.filter { state.cancerIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showGroup4Sheet = true },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedItems.forEach { item ->
                            CustomChip(
                                text = item.label,
                                containerColor = taminColors.dangerText.copy(alpha = 0.13f),
                                textColor = taminColors.textPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }

    // Bottom Sheets
    group2?.let { g ->
        if (showGroup2Sheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = g.groupTitle,
                    subtitle = "نوع بیماری خود را انتخاب کنید:",
                    type = BottomSheetType.fromGroupId(g.groupId) ?: BottomSheetType.CUSTOM,
                    singleSelection = false,
                    items = g.illnesses.map {
                        BottomSheetItem(
                            id = it.id,
                            title = it.label,
                            isSelected = state.chronicDiseaseIds.contains(it.id)
                        )
                    }
                ),
                onDismissRequest = {
                    if (state.chronicDiseaseIds.isEmpty()) {
                        onIntent(HealthProfileIntent.UpdateDiseases(state.copy(hasChronicDisease = false)))
                    }
                    showGroup2Sheet = false
                },
                onSubmit = { result ->
                    onIntent(HealthProfileIntent.UpdateDiseases(state.copy(chronicDiseaseIds = result.selectedItemIds.toSet())))
                    showGroup2Sheet = false
                }
            )
        }
    }

    group3?.let { g ->
        if (showGroup3Sheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = g.groupTitle,
                    subtitle = "نوع عارضه را انتخاب کنید:",
                    type = BottomSheetType.fromGroupId(g.groupId) ?: BottomSheetType.CUSTOM,
                    singleSelection = false,
                    items = g.illnesses.map {
                        BottomSheetItem(
                            id = it.id,
                            title = it.label,
                            isSelected = state.mentalIllnessIds.contains(it.id)
                        )
                    }
                ),
                onDismissRequest = {
                    if (state.mentalIllnessIds.isEmpty()) {
                        onIntent(HealthProfileIntent.UpdateDiseases(state.copy(hasMentalIllness = false)))
                    }
                    showGroup3Sheet = false
                },
                onSubmit = { result ->
                    onIntent(HealthProfileIntent.UpdateDiseases(state.copy(mentalIllnessIds = result.selectedItemIds.toSet())))
                    showGroup3Sheet = false
                }
            )
        }
    }

    group4?.let { g ->
        if (showGroup4Sheet) {
            HealthBottomSheet(
                config = BottomSheetConfig(
                    title = g.groupTitle,
                    subtitle = "نوع سرطان را انتخاب کنید:",
                    type = BottomSheetType.fromGroupId(g.groupId) ?: BottomSheetType.CUSTOM,
                    singleSelection = false,
                    items = g.illnesses.map {
                        BottomSheetItem(
                            id = it.id,
                            title = it.label,
                            isSelected = state.cancerIds.contains(it.id)
                        )
                    }
                ),
                onDismissRequest = {
                    if (state.cancerIds.isEmpty()) {
                        onIntent(HealthProfileIntent.UpdateDiseases(state.copy(hasCancer = false)))
                    }
                    showGroup4Sheet = false
                },
                onSubmit = { result ->
                    onIntent(HealthProfileIntent.UpdateDiseases(state.copy(cancerIds = result.selectedItemIds.toSet())))
                    showGroup4Sheet = false
                }
            )
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationDiseasesScreenPreview() {
    val mockIllnesses = listOf(
        LookupItemPR(1, "قند خون بالا"),
        LookupItemPR(2, "فشار خون بالا"),
        LookupItemPR(
            3,
            "چربی خون یا کلسترول بالا"
        )
    )

    val mockGroups = listOf(
        IllnessGroupPR(
            groupId = 1,
            groupTitle = "سابقه (رسیک فاکتور)",
            forFamily = false,
            illnesses = mockIllnesses
        ),
        IllnessGroupPR(
            groupId = 2,
            groupTitle = "سابقه ابتلا به بیماری",
            forFamily = false,
            illnesses = mockIllnesses
        ),
        IllnessGroupPR(
            groupId = 3,
            groupTitle = "بیماری اعصاب و روان",
            forFamily = false,
            illnesses = mockIllnesses
        ),
        IllnessGroupPR(
            groupId = 4,
            groupTitle = "سابقه ابتلا به سرطان",
            forFamily = false,
            illnesses = mockIllnesses
        )
    )

    PreviewRtlThemeContent {
        SelfDeclarationDiseasesScreen(
            state = DiseasesStepState(
                riskFactorIds = setOf(1),
                hasChronicDisease = true,
                chronicDiseaseIds = setOf(2)
            ),
            illnessGroups = mockGroups,
            onIntent = {},
            onBackClicked = {}
        )
    }
}
