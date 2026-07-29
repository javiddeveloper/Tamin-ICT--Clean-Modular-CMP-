package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LifeStyleStatus
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.LifestyleStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.model.SmokingStatus
import com.tamin.taminhamrah.util.Logger
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.CustomChip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

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

    val optionsYesNo = listOf(
        stringResource(Res.string.health_option_yes),
        stringResource(Res.string.health_option_no)
    )

    LaunchedEffect(Unit) {
        Logger.d(
            "LifestyleScreen",
            """
        Initial State Loaded:
        isSmoking: ${state.isSmoking}, smokingStatusId: ${state.smokingStatusId}, smokingPattern: ${state.smokingPattern}
        hasAddiction: ${state.hasAddiction}, substanceStatusId: ${state.substanceStatusId}, substancePattern: ${state.substancePattern}
        isDrinking: ${state.isDrinking}, drinkingStatusId: ${state.drinkingStatusId}, drinkingPattern: ${state.drinkingPattern}
        isExercising: ${state.isExercising}, exerciseStatusId: ${state.exerciseStatusId}, exerciseFrequency: ${state.exerciseFrequency}
        """.trimIndent()
        )
    }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_lifestyle_title),
                currentStep = 7,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.DISEASES)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
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
                message = stringResource(Res.string.health_confidential_notice),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // ── Smoking ──────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_tobacco),
                        backgroundColor = taminColors.warning.copy(alpha = 0.13f),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        text = stringResource(Res.string.health_lifestyle_smoking_question),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isSmoking == true) 0 else 1,
                    onOptionSelected = { idx ->
                        val isYes = idx == 0
                        onIntent(
                            HealthProfileIntent.UpdateLifestyle(
                                state.copy(
                                    isSmoking = isYes,
                                    smokingStatusId = if (isYes) state.smokingStatusId else null,
                                    smokingPattern = if (isYes) state.smokingPattern else null
                                )
                            )
                        )
                        if (isYes) showSmokingBottomSheet = true
                    }
                )

                if (state.isSmoking == true && state.smokingPattern != null) {
                    CustomChip(
                        text = state.smokingPattern,
                        containerColor = taminColors.warning.copy(alpha = 0.13f),
                        textColor = taminColors.textPrimary,
                        modifier = Modifier
                            .clickable { showSmokingBottomSheet = true }
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_addiction),
                        backgroundColor = taminColors.dangerText.copy(alpha = 0.13f),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        text = stringResource(Res.string.health_lifestyle_addiction_question),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasAddiction == true) 0 else 1,
                    onOptionSelected = { idx ->
                        val isYes = idx == 0
                        onIntent(
                            HealthProfileIntent.UpdateLifestyle(
                                state.copy(
                                    hasAddiction = isYes,
                                    substanceStatusId = if (isYes) state.substanceStatusId else null,
                                    substancePattern = if (isYes) state.substancePattern else null
                                )
                            )
                        )
                        if (isYes) showAddictionBottomSheet = true
                    }
                )

                if (state.hasAddiction == true && state.substancePattern != null) {
                    CustomChip(
                        text = state.substancePattern,
                        containerColor = taminColors.dangerText.copy(alpha = 0.13f),
                        textColor = taminColors.textPrimary,
                        modifier = Modifier
                            .clickable { showAddictionBottomSheet = true }
                    )
                }

            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_alcohol),
                        backgroundColor = taminColors.fuchsiaBlue.copy(alpha = 0.13f),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        text = stringResource(Res.string.health_lifestyle_alcohol_question),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isDrinking == true) 0 else 1,
                    onOptionSelected = { idx ->
                        val isYes = idx == 0
                        onIntent(
                            HealthProfileIntent.UpdateLifestyle(
                                state.copy(
                                    isDrinking = isYes,
                                    drinkingStatusId = if (isYes) state.drinkingStatusId else null,
                                    drinkingPattern = if (isYes) state.drinkingPattern else null,
                                )
                            )
                        )
                        if (isYes) showAlcoholBottomSheet = true
                    }
                )

                if (state.isDrinking == true && state.drinkingPattern != null) {
                    CustomChip(
                        text = state.drinkingPattern,
                        containerColor = taminColors.fuchsiaBlue.copy(alpha = 0.13f),
                        textColor = taminColors.textPrimary,
                        modifier = Modifier
                            .clickable { showAlcoholBottomSheet = true }
                    )
                }

            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBox(
                        painter = painterResource(Res.drawable.ic_health_exercise),
                        backgroundColor = taminColors.greenText.copy(alpha = 0.13f),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TaminText(
                        text = stringResource(Res.string.health_lifestyle_exercise_question),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = taminColors.textPrimary
                    )
                }

                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isExercising == true) 0 else 1,
                    onOptionSelected = { idx ->
                        val isYes = idx == 0
                        onIntent(
                            HealthProfileIntent.UpdateLifestyle(
                                state.copy(
                                    isExercising = isYes,
                                    exerciseStatusId = if (isYes) state.exerciseStatusId else null,
                                    exerciseFrequency = if (isYes) state.exerciseFrequency else null,
                                )
                            )
                        )
                        if (isYes) showExerciseBottomSheet = true
                    }
                )

                if (state.isExercising == true && state.exerciseFrequency != null) {
                    CustomChip(
                        text = state.exerciseFrequency,
                        containerColor = taminColors.greenText.copy(alpha = 0.13f),
                        textColor = taminColors.textPrimary,
                        modifier = Modifier
                            .clickable { showExerciseBottomSheet = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }

    if (showSmokingBottomSheet) {
        HealthBottomSheet(
            config = BottomSheetConfig(
                title = BottomSheetType.SMOKING_ADDICTION.title ?: "",
                subtitle = stringResource(Res.string.health_lifestyle_smoking_bs_subtitle),
                type = BottomSheetType.SMOKING_ADDICTION,
                singleSelection = true,
                items = smokingStatusOptions.map {
                    BottomSheetItem(
                        id = it.id,
                        title = it.label,
                        isSelected = state.smokingStatusId == it.id
                    )
                }
            ),
            onDismissRequest = {
                if (state.smokingStatusId == null) {
                    onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isSmoking = false)))
                }
                showSmokingBottomSheet = false
            },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            isSmoking = selectedId != SmokingStatus.NEVER_CONSUMED.id,
                            smokingStatusId = selectedId,
                            smokingPattern = result.description
                                ?: smokingStatusOptions.find { it.id == result.selectedItemIds.firstOrNull() }?.label
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
                subtitle = stringResource(Res.string.health_lifestyle_addiction_bs_subtitle),
                type = BottomSheetType.DRUG_ADDICTION,
                singleSelection = true,
                items = actFrequencyOptions.map {
                    BottomSheetItem(
                        id = it.id,
                        title = it.label,
                        isSelected = state.substanceStatusId == it.id
                    )
                }
            ),
            onDismissRequest = {
                if (state.substanceStatusId == null) {
                    onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(hasAddiction = false)))
                }
                showAddictionBottomSheet = false
            },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            hasAddiction = LifeStyleStatus.fromStyleId(selectedId) != LifeStyleStatus.NEVER,
                            substanceStatusId = selectedId,
                            substancePattern = result.description
                                ?: actFrequencyOptions.find { it.id == selectedId }?.label
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
                subtitle = stringResource(Res.string.health_lifestyle_alcohol_bs_subtitle),
                type = BottomSheetType.ALCOHOL_ADDICTION,
                singleSelection = true,
                items = LifeStyleStatus.entries.map {
                    BottomSheetItem(
                        id = it.id,
                        title = it.title,
                        isSelected = state.drinkingStatusId == it.id
                    )
                }
            ),
            onDismissRequest = {
                if (state.drinkingStatusId == null) {
                    onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isDrinking = false)))
                }
                showAlcoholBottomSheet = false
            },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            isDrinking = LifeStyleStatus.fromStyleId(selectedId) != LifeStyleStatus.NEVER,
                            drinkingStatusId = selectedId,
                            drinkingPattern = result.description
                                ?: LifeStyleStatus.fromStyleId(selectedId)?.title
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
                subtitle = stringResource(Res.string.health_lifestyle_exercise_bs_subtitle),
                type = BottomSheetType.EXERCISE,
                singleSelection = true,
                items = LifeStyleStatus.entries.map {
                    BottomSheetItem(
                        id = it.id,
                        title = it.title,
                        isSelected = state.exerciseStatusId == it.id
                    )
                }
            ),
            onDismissRequest = {
                if (state.exerciseStatusId == null) {
                    onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isExercising = false)))
                }
                showExerciseBottomSheet = false
            },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                onIntent(
                    HealthProfileIntent.UpdateLifestyle(
                        state.copy(
                            isExercising = LifeStyleStatus.fromStyleId(selectedId) != LifeStyleStatus.NEVER,
                            exerciseStatusId = selectedId,
                            exerciseFrequency = result.description
                                ?: LifeStyleStatus.fromStyleId(selectedId)?.title
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
