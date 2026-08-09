package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import com.tamin.taminhamrah.feature.healthProfile.ui.components.CardsListShimmerSkeleton
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthIrritateNavigationBar
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthTopAppBar
import com.tamin.taminhamrah.feature.healthProfile.ui.components.InfoBanner
import com.tamin.taminhamrah.feature.healthProfile.ui.components.InteractiveChoiceChips
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthProfileErrorWrapper
import com.tamin.taminhamrah.feature.healthProfile.ui.components.SegmentedControl
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.findGroup
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationDiseasesScreen(
    state: DiseasesStepState,
    illnessGroups: List<IllnessGroupPR>,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    isLoading: Boolean = false,
    error: String? = null
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val optionsYesNo = listOf(
        stringResource(Res.string.health_option_yes),
        stringResource(Res.string.health_option_no)
    )

    // Filter groups via Enum type mapping
    val riskFactorGroup = illnessGroups.findGroup(BottomSheetType.RISK_FACTOR)
    val chronicGroup = illnessGroups.findGroup(BottomSheetType.ILLNESS_HISTORY)
    val mentalGroup = illnessGroups.findGroup(BottomSheetType.MENTAL)
    val cancerGroup = illnessGroups.findGroup(BottomSheetType.CANCER)

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_diseases_title),
                currentStep = 8,
                totalSteps = 10,
                onBackClicked = onBackClicked,
                onCloseClicked = onCloseClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.FAMILY)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        HealthProfileErrorWrapper(
            isLoading = isLoading,
            error = error,
            onRetry = { onIntent(HealthProfileIntent.RetryStep) },
            modifier = Modifier.padding(paddingValues),
            shimmerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(taminColors.bgPage)
                ) {
                    CardsListShimmerSkeleton()
                }
            }
        ) {
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
                    message = stringResource(Res.string.health_confidential_notice),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

            // Group 1: Risk Factors
            riskFactorGroup?.let { group ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_high_risk),
                        backgroundColor = LocalTaminColors.current.greenText.copy(alpha = 0.2f),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        text = group.groupTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                InteractiveChoiceChips(
                    options = group.illnesses.map { it.label },
                    selectedIndices = group.illnesses.mapIndexedNotNull { index, item ->
                        if (state.riskFactorIds.contains(item.id)) index else null
                    }.toSet(),
                    onSelectionChanged = { indices ->
                        val selectedIds = indices.map { group.illnesses[it].id }.toSet()
                        onIntent(
                            HealthProfileIntent.UpdateDiseaseSelections(
                                BottomSheetType.RISK_FACTOR,
                                selectedIds
                            )
                        )
                    }
                )
            }

            // Group 2: Chronic Illness
            chronicGroup?.let { group ->
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
                            text = group.groupTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )
                    }
                    SegmentedControl(
                        options = optionsYesNo,
                        selectedIndex = if (state.hasChronicDisease == true) 0 else 1,
                        onOptionSelected = { idx ->
                            onIntent(HealthProfileIntent.SetDiseaseAnswer(BottomSheetType.ILLNESS_HISTORY, idx == 0))
                        }
                    )
                }

                if (state.hasChronicDisease == true && state.chronicDiseaseIds.isNotEmpty()) {
                    val selectedItems =
                        group.illnesses.filter { state.chronicDiseaseIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIntent(
                                    HealthProfileIntent.OpenDiseaseBottomSheet(
                                        BottomSheetType.ILLNESS_HISTORY
                                    )
                                )
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Edit button chip
                        Box(
                            modifier = Modifier
                                .background(taminColors.blueBg, androidx.compose.foundation.shape.RoundedCornerShape(100.dp))
                                .border(
                                    1.dp,
                                    taminColors.blueText.copy(alpha = 0.3f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                androidx.compose.material3.Icon(
                                    painter = painterResource(Res.drawable.ic_family_edit),
                                    contentDescription = null,
                                    tint = taminColors.blueText,
                                    modifier = Modifier.size(14.dp)
                                )
                                TaminText(
                                    text = "ویرایش",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = taminColors.blueText
                                    )
                                )
                            }
                        }
                        selectedItems.forEach { item ->
                            CustomChip(
                                text = item.label,
                                containerColor = taminColors.dangerBg,
                                textColor = taminColors.dangerText,
                                border = BorderStroke(
                                    1.dp,
                                    taminColors.dangerText.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }

            // Group 3: Mental Illness
            mentalGroup?.let { group ->
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
                            text = group.groupTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )
                    }

                    SegmentedControl(
                        options = optionsYesNo,
                        selectedIndex = if (state.hasMentalIllness == true) 0 else 1,
                        onOptionSelected = { idx ->
                            onIntent(HealthProfileIntent.SetDiseaseAnswer(BottomSheetType.MENTAL, idx == 0))
                        }
                    )
                }

                if (state.hasMentalIllness == true && state.mentalIllnessIds.isNotEmpty()) {
                    val selectedItems =
                        group.illnesses.filter { state.mentalIllnessIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIntent(
                                    HealthProfileIntent.OpenDiseaseBottomSheet(
                                        BottomSheetType.MENTAL
                                    )
                                )
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Edit button chip
                        Box(
                            modifier = Modifier
                                .background(taminColors.blueBg, androidx.compose.foundation.shape.RoundedCornerShape(100.dp))
                                .border(
                                    1.dp,
                                    taminColors.blueText.copy(alpha = 0.3f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                androidx.compose.material3.Icon(
                                    painter = org.jetbrains.compose.resources.painterResource(taminx.feature.healthprofile.generated.resources.Res.drawable.ic_family_edit),
                                    contentDescription = null,
                                    tint = taminColors.blueText,
                                    modifier = Modifier.size(14.dp)
                                )
                                TaminText(
                                    text = "ویرایش",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = taminColors.blueText
                                    )
                                )
                            }
                        }
                        selectedItems.forEach { item ->
                            CustomChip(
                                text = item.label,
                                containerColor = taminColors.dangerBg,
                                textColor = taminColors.dangerText,
                                border = BorderStroke(
                                    1.dp,
                                    taminColors.dangerText.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }

            // Group 4: Cancer
            cancerGroup?.let { group ->
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
                            text = group.groupTitle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary
                        )
                    }

                    SegmentedControl(
                        options = optionsYesNo,
                        selectedIndex = if (state.hasCancer == true) 0 else 1,
                        onOptionSelected = { idx ->
                            onIntent(HealthProfileIntent.SetDiseaseAnswer(BottomSheetType.CANCER, idx == 0))
                        }
                    )
                }

                if (state.hasCancer == true && state.cancerIds.isNotEmpty()) {
                    val selectedItems = group.illnesses.filter { state.cancerIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIntent(
                                    HealthProfileIntent.OpenDiseaseBottomSheet(
                                        BottomSheetType.CANCER
                                    )
                                )
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(taminColors.blueBg, androidx.compose.foundation.shape.RoundedCornerShape(100.dp))
                                .border(
                                    1.dp,
                                    taminColors.blueText.copy(alpha = 0.3f),
                                    androidx.compose.foundation.shape.RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                androidx.compose.material3.Icon(
                                    painter = painterResource(Res.drawable.ic_family_edit),
                                    contentDescription = null,
                                    tint = taminColors.blueText,
                                    modifier = Modifier.size(14.dp)
                                )
                                TaminText(
                                    text = "ویرایش",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = taminColors.blueText
                                    )
                                )
                            }
                        }
                        selectedItems.forEach { item ->
                            CustomChip(
                                text = item.label,
                                containerColor = taminColors.dangerBg,
                                textColor = taminColors.dangerText,
                                border = BorderStroke(
                                    1.dp,
                                    taminColors.dangerText.copy(alpha = 0.3f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }

    // Dynamic Bottom Sheet Renderer based on ViewModel activeBottomSheet State
    state.activeBottomSheet?.let { type ->
        val config = state.buildBottomSheetConfig(type, illnessGroups)
        if (config != null) {
            HealthBottomSheet(
                config = config,
                onDismissRequest = { onIntent(HealthProfileIntent.CloseDiseaseBottomSheet) },
                onSubmit = { result ->
                    onIntent(
                        HealthProfileIntent.UpdateDiseaseSelections(
                            type,
                            result.selectedItemIds.toSet()
                        )
                    )
                    onIntent(HealthProfileIntent.CloseDiseaseBottomSheet)
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
        LookupItemPR(3, "چربی خون یا کلسترول بالا")
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
