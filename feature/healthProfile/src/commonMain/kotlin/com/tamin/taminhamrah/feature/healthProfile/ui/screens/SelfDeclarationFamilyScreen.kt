package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.findGroup
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.FamilyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.IllnessGroupPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationFamilyScreen(
    state: FamilyStepState,
    illnessGroups: List<IllnessGroupPR> = emptyList(),
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    isLoading: Boolean = false
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showCancerSheet by remember { mutableStateOf(false) }

    val optionsYesNo = listOf(
        stringResource(Res.string.health_option_no),
        stringResource(Res.string.health_option_yes)
    )

    // Lookup family groups via Enum mapping (forFamily = true)
    val familyDiseasesGroup = illnessGroups.findGroup(BottomSheetType.FAMILY_DISEASES, forFamily = true)
    val familyCancerGroup   = illnessGroups.findGroup(BottomSheetType.FAMILY_CANCER, forFamily = true)

    // Selected cancer names for display when fallback textfield is shown
    val selectedCancerNames = remember(familyCancerGroup, state.familyCancerIds) {
        familyCancerGroup?.illnesses
            ?.filter { state.familyCancerIds.contains(it.id) }
            ?.joinToString("، ") { it.label }
            .orEmpty()
    }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 9,
                totalSteps = 10,
                onBackClicked = onBackClicked,
                title = stringResource(Res.string.health_family_title)
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.ALLERGY)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(taminColors.bgPage)
            ) {
                CardsListShimmerSkeleton()
            }
        } else {
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

            // Group 5: Family Diseases / Risk Factors
            familyDiseasesGroup?.let { group ->
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
                        if (state.familyDiseaseIds.contains(item.id)) index else null
                    }.toSet(),
                    onSelectionChanged = { indices ->
                        val selectedIds = indices.map { group.illnesses[it].id }.toSet()
                        onIntent(HealthProfileIntent.UpdateFamily(state.copy(familyDiseaseIds = selectedIds)))
                    }
                )
            }

            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

            // Group 6: Family Cancer
            familyCancerGroup?.let { group ->
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
                        selectedIndex = if (state.familyHasCancer == true) 1 else 0,
                        onOptionSelected = { idx ->
                            val isYes = idx == 1
                            onIntent(
                                HealthProfileIntent.UpdateFamily(
                                    state.copy(
                                        familyHasCancer = isYes,
                                        familyCancerIds = if (!isYes) emptySet() else state.familyCancerIds
                                    )
                                )
                            )
                            if (isYes) {
                                showCancerSheet = true
                            }
                        }
                    )
                }

                if (state.familyHasCancer == true) {
                    if (state.familyCancerIds.isNotEmpty()) {
                        val selectedItems = group.illnesses.filter { state.familyCancerIds.contains(it.id) }
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCancerSheet = true },
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            selectedItems.forEach { item ->
                                CustomChip(
                                    text = item.label,
                                    containerColor = LocalTaminColors.current.dangerText.copy(alpha = 0.13f),
                                    textColor = taminColors.textPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }

    if (showCancerSheet) {
        val bottomSheetItems = familyCancerGroup?.illnesses?.map { option ->
            BottomSheetItem(
                id = option.id,
                title = option.label,
                isSelected = state.familyCancerIds.contains(option.id)
            )
        } ?: emptyList()

        HealthBottomSheet(
            config = BottomSheetConfig(
                title = familyCancerGroup?.groupTitle ?: stringResource(Res.string.health_family_cancer_bs_title),
                subtitle = stringResource(Res.string.health_family_cancer_bs_subtitle),
                type = BottomSheetType.FAMILY_CANCER,
                singleSelection = false,
                items = bottomSheetItems
            ),
            onDismissRequest = { showCancerSheet = false },
            onSubmit = { result ->
                onIntent(
                    HealthProfileIntent.UpdateFamily(
                        state.copy(familyCancerIds = result.selectedItemIds.toSet())
                    )
                )
                showCancerSheet = false
            }
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationFamilyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationFamilyScreen(
            state = FamilyStepState(familyHasCancer = true, familyCancerIds = setOf(1)),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
