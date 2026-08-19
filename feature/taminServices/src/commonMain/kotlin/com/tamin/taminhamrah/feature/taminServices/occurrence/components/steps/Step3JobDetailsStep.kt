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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceNavigationBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceTopAppBar
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.PersonInfoCard
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.PersonInfoGridItem
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.StyledTextField
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.JobDetailsStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toGenderLabel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.occurrence_field_employment_date
import taminx.core.core_ui.occurrence_field_gender
import taminx.core.core_ui.occurrence_field_insurance_type
import taminx.core.core_ui.occurrence_field_job_title
import taminx.core.core_ui.occurrence_field_marital_married
import taminx.core.core_ui.occurrence_field_marital_single
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Step3JobDetailsStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose:() -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val step = uiState.jobDetails
    var showMaritalSheet by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.occurrence_field_employment_date),
            onDismiss = { showDatePicker = false },
            onConfirm = { year, month, day ->
                val dateStr =
                    "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
                onIntent(OccurrenceIntent.UpdateJobDetails(step.copy(employmentDate = dateStr)))
                showDatePicker = false
            },
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            OccurrenceTopAppBar(
                title = stringResource(Res.string.occurrence_step3_title),
                onBackClicked = onBack,
                onCloseClicked = onClose,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
            )
        },
        bottomBar = {
            OccurrenceNavigationBar(
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
            error = null,
            onRetry = { onIntent(OccurrenceIntent.LoadInitialData) },
            modifier = Modifier.padding(padding),
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StyledTextField(
                        leadingIcon = Icons.Default.KeyboardArrowDown,
                        modifier = Modifier.weight(1f),
                        value = step.maritalStatus,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_marital_status),
                        placeholder = stringResource(Res.string.occurrence_select_marital),
                        readOnly = true,
                        onClick = { showMaritalSheet = true },
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    StyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.employmentDate,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_employment_date),
                        placeholder = "انتخاب تاریخ",
                        trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                        readOnly = true,
                        onClick = { showDatePicker = true },
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
                    value = step.jobTitle,
                    onValueChange = { onIntent(OccurrenceIntent.UpdateJobDetails(step.copy(jobTitle = it))) },
                    label = stringResource(Res.string.occurrence_field_job_title),
                    placeholder = "",
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                StyledTextField(
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

    if (showMaritalSheet) {
        val single = stringResource(Res.string.occurrence_field_marital_single)
        val married = stringResource(Res.string.occurrence_field_marital_married)

        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_marital_title),
            options = listOf(single, married).map { OccurrenceSheetOption(id = it, title = it) },
            selectedId = step.maritalStatus,
            onSelect = { option ->
                onIntent(OccurrenceIntent.UpdateJobDetails(step.copy(maritalStatus = option.id)))
                showMaritalSheet = false
            },
            onDismiss = { showMaritalSheet = false },
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
                jobDetails = JobDetailsStepState(
                    fullName = "علی محمدی",
                    nationality = "ایرانی",
                    gender = "01",
                    insuranceType = "اجباری",
                    employmentDate = "1395/06/01",
                    maritalStatus = "متأهل",
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
