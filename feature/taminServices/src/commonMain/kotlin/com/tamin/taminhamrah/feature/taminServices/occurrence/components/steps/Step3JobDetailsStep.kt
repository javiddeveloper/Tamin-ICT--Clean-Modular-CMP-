package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.PersonInfoCard
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.PersonInfoGridItem
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.Step3JobDetailsShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.ErrorSource
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.JobDetailsStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.MaritalStatus
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePickerBottomSheet
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.components.topbars.TaminStepTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toGenderLabel
import com.tamin.taminhamrah.util.PersianDateFormatter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.occurrence_field_employment_date
import taminx.core.core_ui.occurrence_field_gender
import taminx.core.core_ui.occurrence_field_insurance_type
import taminx.core.core_ui.occurrence_field_job_title
import taminx.core.core_ui.occurrence_field_marital_status
import taminx.core.core_ui.occurrence_field_name
import taminx.core.core_ui.occurrence_field_nationality
import taminx.core.core_ui.occurrence_field_work_location
import taminx.core.core_ui.occurrence_next_step
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_select_marital
import taminx.core.core_ui.occurrence_sheet_marital_title
import taminx.core.core_ui.occurrence_step3_readonly_hint
import taminx.core.core_ui.occurrence_step3_title

@Composable
internal fun Step3JobDetailsStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose:() -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val taminColors = LocalTaminColors.current
    val step = uiState.jobDetails

    if (uiState.dialogs.showEmploymentDatePicker) {
        TaminJalaliDatePickerBottomSheet(
            title = stringResource(Res.string.occurrence_field_employment_date),
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showEmploymentDatePicker = false))) },
            onConfirm = { year, month, day ->
                onIntent(
                    OccurrenceIntent.UpdateJobDetails(
                        step.copy(
                            employmentDate = PersianDateFormatter.format(year, month, day),
                            employmentDateTimestamp = PersianDateFormatter.toEpochMillis(year, month, day),
                        )
                    )
                )
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showEmploymentDatePicker = false)))
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminStepTopAppBar(
                title = stringResource(Res.string.occurrence_step3_title),
                onBackClicked = onBack,
                onCloseClicked = onClose,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
            )
        },
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.occurrence_next_step),
                primaryEnabled = uiState.isStep3Valid && !uiState.isLoading && !uiState.isSubmitting,
                onPrimaryClick = { onIntent(OccurrenceIntent.GoToNextStep) },
                secondaryText = stringResource(Res.string.occurrence_prev_step),
                onSecondaryClick = onBack,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        OccurrenceErrorWrapper(
            isLoading = uiState.isLoading,
            error = error,
            onRetry = { onIntent(OccurrenceIntent.RetrySource(ErrorSource.INSURED_RELATION)) },
            modifier = Modifier.padding(padding),
            shimmerContent = { Step3JobDetailsShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                InfoBanner(message = stringResource(Res.string.occurrence_step3_readonly_hint))

                Spacer(Modifier.height(Spacing.md))

                PersonInfoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_field_name),
                            value = step.fullName,
                        )

                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_field_nationality),
                            value = step.nationality,
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_field_insurance_type),
                            value = step.insuranceType,
                        )

                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_field_gender),
                            value = if (step.gender.isBlank()) {
                                "-"
                            } else {
                                stringResource(step.gender.toGenderLabel())
                            },
                        )
                    }
                }

                TaminDivider()

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    TaminStyledTextField(
                        leadingIcon = Icons.Default.KeyboardArrowDown,
                        modifier = Modifier.weight(1f),
                        value = MaritalStatus.fromCode(step.maritalStatus)?.displayName.orEmpty(),
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_marital_status),
                        placeholder = stringResource(Res.string.occurrence_select_marital),
                        readOnly = true,
                        onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showMaritalSheet = true))) },
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.employmentDate,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_employment_date),
                        placeholder = "انتخاب تاریخ",
                        trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                        readOnly = true,
                        onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showEmploymentDatePicker = true))) },
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                TaminTextArea(
                    value = step.jobTitle,
                    onValueChange = { onIntent(OccurrenceIntent.UpdateJobDetails(step.copy(jobTitle = it))) },
                    label = stringResource(Res.string.occurrence_field_job_title),
                    placeholder = "",
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                TaminTextArea(
                    value = step.workLocation,
                    onValueChange = {
                        onIntent(
                            OccurrenceIntent.UpdateJobDetails(
                                step.copy(
                                    workLocation = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_work_location),
                    placeholder = "",
                )

                Spacer(modifier = Modifier.height(Spacing.lg))
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }

    if (uiState.dialogs.showMaritalSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_marital_title),
            options = MaritalStatus.entries.map { OccurrenceSheetOption(id = it.code, title = it.displayName) },
            selectedId = step.maritalStatus,
            onSelect = { option ->
                onIntent(OccurrenceIntent.UpdateJobDetails(step.copy(maritalStatus = option.id)))
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showMaritalSheet = false)))
            },
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showMaritalSheet = false))) },
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step3JobDetailsStepPreview() {
    PreviewRtlThemeContent {
        Step3JobDetailsStep(
            uiState = OccurrenceUiState(
                currentStep = OccurrenceStep.JOB_DETAILS,
                jobDetails = JobDetailsStepState(
                    fullName = "علی محمدی",
                    nationality = "ایرانی",
                    gender = "01",
                    insuranceType = "اجباری",
                    employmentDate = "1395/06/01",
                    maritalStatus = MaritalStatus.MARRIED.code,
                    jobTitle = "مهندس نرم‌افزار",
                    workLocation = "تهران، خیابان ولیعصر",
                ),
            ),
            onIntent = {},
            onBack = {},
            onClose = {}
        )
    }
}
