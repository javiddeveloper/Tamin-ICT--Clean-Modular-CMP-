package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePickerBottomSheet
import com.tamin.taminhamrah.ui.components.TaminJalaliTimePickerBottomSheet
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
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
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val step = uiState.accident

    if (uiState.dialogs.showAccidentDatePicker) {
        TaminJalaliDatePickerBottomSheet(
            title = stringResource(Res.string.occurrence_field_accident_date),
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentDatePicker = false))) },
            onConfirm = { year, month, day ->
                onIntent(
                    OccurrenceIntent.UpdateAccident(
                        step.copy(
                            accidentDate = PersianDateFormatter.format(year, month, day),
                            accidentDateTimestamp = PersianDateFormatter.toEpochMillis(year, month, day),
                        )
                    )
                )
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentDatePicker = false)))
            },
        )
    }

    if (uiState.dialogs.showAccidentTimePicker) {
        TaminJalaliTimePickerBottomSheet(
            title = stringResource(Res.string.occurrence_field_accident_time),
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentTimePicker = false))) },
            onConfirm = { hour, minute ->
                val timeStr =
                    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                onIntent(OccurrenceIntent.UpdateAccident(step.copy(accidentTime = timeStr)))
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentTimePicker = false)))
            },
        )
    }

    val outcomeOptions = listOf(
        "0" to stringResource(Res.string.occurrence_outcome_death),
        "1" to stringResource(Res.string.occurrence_outcome_total_disability),
        "2" to stringResource(Res.string.occurrence_outcome_partial_disability),
        "3" to stringResource(Res.string.occurrence_outcome_organ_defect),
        "4" to stringResource(Res.string.occurrence_outcome_medical_rest),
        "5" to stringResource(Res.string.occurrence_outcome_medical_compensation),
        "6" to stringResource(Res.string.occurrence_outcome_none),
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
                    onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentDatePicker = true))) },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    StyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.accidentTime,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_accident_time),
                        placeholder = "14:30",
                        trailingIcon = Icons.Default.AccessTime,
                        readOnly = true,
                        onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentTimePicker = true))) },
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    StyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.accidentOutcomeTitle,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_accident_outcome),
                        placeholder = stringResource(Res.string.occurrence_select_outcome_hint),
                        readOnly = true,
                        onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentOutcomeSheet = true))) },
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.exactLocation,
                    onValueChange = {
                        onIntent(
                            OccurrenceIntent.UpdateAccident(
                                step.copy(
                                    exactLocation = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_exact_location),
                    placeholder = "شهر، خیابان، کوچه",
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.description,
                    onValueChange = {
                        if (it.length <= 500) onIntent(
                            OccurrenceIntent.UpdateAccident(
                                step.copy(
                                    description = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_description),
                    placeholder = "واقعه را بصورت کامل شرح دهید",
                    singleLine = false,
                )

                if (step.description.isNotEmpty()) {
                    Text(
                        text = stringResource(
                            Res.string.occurrence_description_counter,
                            step.description.length.toString()
                        ),
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

    if (uiState.dialogs.showAccidentOutcomeSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_outcome_title),
            options = outcomeOptions.map { (id, label) ->
                OccurrenceSheetOption(
                    id = id,
                    title = label
                )
            },
            selectedId = step.accidentOutcomeId,
            onSelect = { option ->
                onIntent(
                    OccurrenceIntent.UpdateAccident(
                        step.copy(
                            accidentOutcomeId = option.id,
                            accidentOutcomeTitle = option.title
                        )
                    )
                )
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentOutcomeSheet = false)))
            },
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showAccidentOutcomeSheet = false))) },
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
                currentStep = OccurrenceStep.ACCIDENT_DETAILS,
                accident = AccidentStepState(
                    accidentDate = "1402/06/15",
                    accidentTime = "14:30",
                    accidentOutcomeId = "4",
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
