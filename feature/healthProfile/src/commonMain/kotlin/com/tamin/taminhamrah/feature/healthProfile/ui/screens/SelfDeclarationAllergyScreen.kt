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
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.AllergyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.model.DrugAllergyItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.ui.components.IconBox
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.health_allergy_add
import taminx.feature.healthprofile.generated.resources.health_allergy_add_new_allergy
import taminx.feature.healthprofile.generated.resources.health_allergy_choose_drug
import taminx.feature.healthprofile.generated.resources.health_allergy_choose_your_allergy
import taminx.feature.healthprofile.generated.resources.health_allergy_description
import taminx.feature.healthprofile.generated.resources.health_allergy_final_check
import taminx.feature.healthprofile.generated.resources.health_allergy_no_allergy_registered
import taminx.feature.healthprofile.generated.resources.health_allergy_no_description_for_allergy
import taminx.feature.healthprofile.generated.resources.health_allergy_title
import taminx.feature.healthprofile.generated.resources.health_allergy_unknows
import taminx.feature.healthprofile.generated.resources.health_btn_cancel
import taminx.feature.healthprofile.generated.resources.health_btn_prev_step
import taminx.feature.healthprofile.generated.resources.ic_health_pill

@Composable
fun SelfDeclarationAllergyScreen(
    state: AllergyStepState,
    drugOptions: List<LookupItemPR>,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showBottomsheet by remember { mutableStateOf(false) }

    val unknownDrugText = stringResource(Res.string.health_allergy_unknows)
    val noDescriptionText = stringResource(Res.string.health_allergy_no_description_for_allergy)


    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_allergy_title),
                currentStep = 10,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_allergy_final_check),
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.REVIEW)) },
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

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_pill),
                        backgroundColor = taminColors.greenBg,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        stringResource(Res.string.health_allergy_choose_drug),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                TaminText(
                    text = stringResource(Res.string.health_allergy_description),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = taminColors.textTertiary,
                        lineHeight = 22.sp
                    )
                )

                DashedAddButton(
                    label = stringResource(Res.string.health_allergy_add_new_allergy),
                    onClick = {
                        onIntent(
                            HealthProfileIntent.UpdateAllergy(
                                state.copy(
                                    selectedDrugId = null,
                                    allergyDesc = ""
                                )
                            )
                        )
                        showBottomsheet = true
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (state.allergies.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        TaminText(
                            text = stringResource(Res.string.health_allergy_no_allergy_registered),
                            style = MaterialTheme.typography.bodyMedium,
                            color = taminColors.textMuted
                        )
                    }
                } else {
                    state.allergies.forEachIndexed { idx, allergy ->
                        DynamicItemCard(
                            title = allergy.drugName,
                            description = allergy.allergyComments,
                            onDelete = {
                                onIntent(
                                    HealthProfileIntent.UpdateAllergy(
                                        state.copy(allergies = state.allergies.filterIndexed { i, _ -> i != idx })
                                    )
                                )
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }

    if (showBottomsheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = stringResource(Res.string.health_allergy_choose_drug),
                subtitle = stringResource(Res.string.health_allergy_choose_your_allergy),
                description = "",
                type = BottomSheetType.CUSTOM,
                singleSelection = true,
                submitText = stringResource(Res.string.health_allergy_add),
                cancelText = stringResource(Res.string.health_btn_cancel),
                items = drugOptions.map { drug ->
                    BottomSheetItem(
                        id = drug.id,
                        title = drug.label,
                        isSelected = drug.id == state.selectedDrugId
                    )
                }
            ),
            onDismissRequest = { showBottomsheet = false },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                if (selectedId != null) {
                    val drugOption = drugOptions.find { it.id == selectedId }
                    val allergy = DrugAllergyItemPR(
                        drugId = selectedId,
                        drugName = drugOption?.label
                            ?: unknownDrugText,
                        allergyComments = result.description?.takeIf { it.isNotBlank() }
                            ?: noDescriptionText
                    )
                    onIntent(
                        HealthProfileIntent.UpdateAllergy(
                            state.copy(allergies = state.allergies + allergy)
                        )
                    )
                }
                showBottomsheet = false
            }
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationAllergyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationAllergyScreen(
            state = AllergyStepState(
                allergies = listOf()
            ),
            drugOptions = listOf(),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

