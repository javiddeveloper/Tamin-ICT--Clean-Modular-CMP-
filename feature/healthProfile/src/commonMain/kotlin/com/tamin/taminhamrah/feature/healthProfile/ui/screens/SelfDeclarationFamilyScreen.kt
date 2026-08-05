package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthProfileErrorWrapper
import com.tamin.taminhamrah.ui.components.InteractiveChoiceChips
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheet
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetConfig
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetItem
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
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
    onCloseClicked: (() -> Unit)? = null,
    isLoading: Boolean = false,
    error: String? = null
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showCancerSheet by remember { mutableStateOf(false) }

    val optionsYesNo = listOf(
        stringResource(Res.string.health_option_yes),
        stringResource(Res.string.health_option_no),

    )

    // Lookup family groups via Enum mapping (forFamily = true)
    val familyDiseasesGroup =
        illnessGroups.findGroup(TaminBottomSheetType.FAMILY_DISEASES, forFamily = true)
    val familyCancerGroup = illnessGroups.findGroup(TaminBottomSheetType.FAMILY_CANCER, forFamily = true)

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
                onCloseClicked = onCloseClicked,
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
                    message = "فرآیند اطلاعات شما کاملاً محرمانه بوده و تنها برای ارزیابی پروندهٔ سلامت استفاده می‌شود.",
                    modifier = Modifier.padding(bottom = 6.dp)
                )

            // Group 5: Family Diseases / Risk Factors
            familyDiseasesGroup?.let { group ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_family_high_risk),
                        backgroundColor = LocalTaminColors.current.teal.copy(alpha = 0.15f),
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


            // Group 6: Family Cancer
            familyCancerGroup?.let { group ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(
                            painter = painterResource(Res.drawable.ic_family_cancer),
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
                        selectedIndex = if (state.familyHasCancer == true) 0 else 1,
                        onOptionSelected = { idx ->
                            val isYes = idx == 0
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

                if (state.familyHasCancer == true && state.familyCancerIds.isNotEmpty()) {
                    val selectedItems =
                        group.illnesses.filter { state.familyCancerIds.contains(it.id) }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCancerSheet = true },
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
                            if (selectedItems.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
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
                        }
                        // Individual selected cancer chips
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

    if (showCancerSheet) {
        val bottomSheetItems = familyCancerGroup?.illnesses?.map { option ->
            TaminBottomSheetItem(
                id = option.id,
                title = option.label,
                isSelected = state.familyCancerIds.contains(option.id)
            )
        } ?: emptyList()

        TaminBottomSheet(
            config = TaminBottomSheetConfig(
                title = familyCancerGroup?.groupTitle ?: stringResource(Res.string.health_family_cancer_bs_title),
                subtitle = stringResource(Res.string.health_family_cancer_bs_subtitle),
                type = TaminBottomSheetType.FAMILY_CANCER,
                singleSelection = false,
                items = bottomSheetItems
            ),
            onDismissRequest = {
                if (state.familyCancerIds.isEmpty()) {
                    onIntent(HealthProfileIntent.UpdateFamily(state.copy(familyHasCancer = false)))
                }
                showCancerSheet = false
            },
            onSubmit = { result ->
                val selectedIds = result.selectedItemIds.toSet()
                onIntent(
                    HealthProfileIntent.UpdateFamily(
                        state.copy(
                            familyCancerIds = selectedIds,
                            familyHasCancer = selectedIds.isNotEmpty()
                        )
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
