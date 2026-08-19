package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceNavigationBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceTopAppBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.Step5AccidentShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.StyledTextField
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.AccidentStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.occurrence_description_counter
import taminx.core.core_ui.occurrence_field_accident_date
import taminx.core.core_ui.occurrence_field_accident_outcome
import taminx.core.core_ui.occurrence_field_accident_time
import taminx.core.core_ui.occurrence_field_description
import taminx.core.core_ui.occurrence_field_exact_location
import taminx.core.core_ui.occurrence_next_step
import taminx.core.core_ui.occurrence_outcome_death
import taminx.core.core_ui.occurrence_outcome_medical_compensation
import taminx.core.core_ui.occurrence_outcome_medical_rest
import taminx.core.core_ui.occurrence_outcome_none
import taminx.core.core_ui.occurrence_outcome_organ_defect
import taminx.core.core_ui.occurrence_outcome_partial_disability
import taminx.core.core_ui.occurrence_outcome_total_disability
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_select_outcome_hint
import taminx.core.core_ui.occurrence_sheet_outcome_title
import taminx.core.core_ui.occurrence_step5_title

@Composable
internal fun Step5AccidentStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose:() -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val step = uiState.accident
    var showOutcomeSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.occurrence_field_accident_date),
            onDismiss = { showDatePicker = false },
            onConfirm = { year, month, day ->
                val dateStr = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
                onIntent(OccurrenceIntent.UpdateAccident(step.copy(accidentDate = dateStr)))
                showDatePicker = false
            },
        )
    }

    val outcomeOptions = listOf(
        "1" to stringResource(Res.string.occurrence_outcome_death),
        "2" to stringResource(Res.string.occurrence_outcome_total_disability),
        "3" to stringResource(Res.string.occurrence_outcome_partial_disability),
        "4" to stringResource(Res.string.occurrence_outcome_organ_defect),
        "5" to stringResource(Res.string.occurrence_outcome_medical_rest),
        "6" to stringResource(Res.string.occurrence_outcome_medical_compensation),
        "7" to stringResource(Res.string.occurrence_outcome_none),
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OccurrenceTopAppBar(
                title = stringResource(Res.string.occurrence_step5_title),
                onBackClicked = onBack,
                onCloseClicked = onClose,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
            )
        },
        bottomBar = {
            OccurrenceNavigationBar(
                primaryText = stringResource(Res.string.occurrence_next_step),
                primaryEnabled = uiState.isStep5Valid && !uiState.isLoading && !uiState.isSubmitting,
                onPrimaryClick = { onIntent(OccurrenceIntent.GoToNextStep) },
                secondaryText = stringResource(Res.string.occurrence_prev_step),
                onSecondaryClick = onBack,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        OccurrenceErrorWrapper(
            isLoading = uiState.isLoading,
            error = null,
            onRetry = { onIntent(OccurrenceIntent.LoadInitialData) },
            modifier = Modifier.padding(padding),
            shimmerContent = { Step5AccidentShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                StyledTextField(
                    value = step.accidentDate,
                    onValueChange = {},
                    label = stringResource(Res.string.occurrence_field_accident_date),
                    placeholder = "انتخاب کنید",
                    trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                    readOnly = true,
                    onClick = { showDatePicker = true },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.accidentTime,
                    onValueChange = { onIntent(OccurrenceIntent.UpdateAccident(step.copy(accidentTime = it))) },
                    label = stringResource(Res.string.occurrence_field_accident_time),
                    placeholder = "14:30",
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.accidentOutcomeTitle,
                    onValueChange = {},
                    label = stringResource(Res.string.occurrence_field_accident_outcome),
                    placeholder = stringResource(Res.string.occurrence_select_outcome_hint),
                    readOnly = true,
                    onClick = { showOutcomeSheet = true },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.exactLocation,
                    onValueChange = { onIntent(OccurrenceIntent.UpdateAccident(step.copy(exactLocation = it))) },
                    label = stringResource(Res.string.occurrence_field_exact_location),
                    placeholder = "شهر، خیابان، کوچه",
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.description,
                    onValueChange = {
                        if (it.length <= 500) onIntent(OccurrenceIntent.UpdateAccident(step.copy(description = it)))
                    },
                    label = stringResource(Res.string.occurrence_field_description),
                    placeholder = "واقعه را بصورت کامل شرح دهید",
                    singleLine = false,
                )

                if (step.description.isNotEmpty()) {
                    Text(
                        text = stringResource(Res.string.occurrence_description_counter, step.description.length.toString()),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted,
                        modifier = Modifier.padding(top = Spacing.xxs),
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }

    if (showOutcomeSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_outcome_title),
            options = outcomeOptions.map { (id, label) -> OccurrenceSheetOption(id = id, title = label) },
            selectedId = step.accidentOutcomeId,
            onSelect = { option ->
                onIntent(OccurrenceIntent.UpdateAccident(step.copy(accidentOutcomeId = option.id, accidentOutcomeTitle = option.title)))
                showOutcomeSheet = false
            },
            onDismiss = { showOutcomeSheet = false },
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step5AccidentStepPreview() {
    PreviewRtlThemeContent {
        Step5AccidentStep(
            uiState = OccurrenceUiState(
                accident = AccidentStepState(
                    accidentDate = "1402/06/15",
                    accidentTime = "14:30",
                    accidentOutcomeId = "5",
                    accidentOutcomeTitle = "استراحت پزشکی",
                    exactLocation = "سالن تولید، خط مونتاژ شماره ۳",
                    description = "در حین انجام کار با ماشین‌آلات، دست راست در معرض خطر قرار گرفت.",
                ),
            ),
            onIntent = {},
            onBack = {},
            onClose = {}
        )
    }
}
