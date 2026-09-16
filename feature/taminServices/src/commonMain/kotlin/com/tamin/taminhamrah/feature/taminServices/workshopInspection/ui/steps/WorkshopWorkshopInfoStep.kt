package com.tamin.taminhamrah.feature.taminServices.workshopInspection.ui.steps

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestStepScaffold
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSelectionOption
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSelectionSheet
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.WorkshopInfoShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionIntent
import com.tamin.taminhamrah.feature.taminServices.workshopInspection.contract.WorkshopInspectionUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.ValidationUtils
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.inspection_request_field_branch
import taminx.core.core_ui.inspection_request_field_branch_placeholder
import taminx.core.core_ui.inspection_request_field_employer_name
import taminx.core.core_ui.inspection_request_field_employer_name_placeholder
import taminx.core.core_ui.inspection_request_field_employment_end
import taminx.core.core_ui.inspection_request_field_employment_end_error
import taminx.core.core_ui.inspection_request_field_employment_end_placeholder
import taminx.core.core_ui.inspection_request_field_employment_start
import taminx.core.core_ui.inspection_request_field_employment_start_placeholder
import taminx.core.core_ui.inspection_request_field_job_title
import taminx.core.core_ui.inspection_request_field_job_title_placeholder
import taminx.core.core_ui.inspection_request_field_workshop_address
import taminx.core.core_ui.inspection_request_field_workshop_address_placeholder
import taminx.core.core_ui.inspection_request_field_workshop_name
import taminx.core.core_ui.inspection_request_field_workshop_name_placeholder
import taminx.core.core_ui.inspection_request_field_workshop_phone_error
import taminx.core.core_ui.inspection_request_field_workshop_phone_optional
import taminx.core.core_ui.inspection_request_field_workshop_phone_placeholder
import taminx.core.core_ui.inspection_request_next_step
import taminx.core.core_ui.inspection_request_prev_step
import taminx.core.core_ui.inspection_request_source_chip_format
import taminx.core.core_ui.inspection_request_step2_section_banner
import taminx.core.core_ui.inspection_request_step2_section_title
import taminx.core.core_ui.search_hint

/**
 * The employer variant always opens as an objection against an existing list item, so — unlike the
 * insured-side step this is ported from — the workshop name/code/branch fields are always prefilled
 * and locked (no blank "request new inspection" path exists here).
 */
@Composable
internal fun WorkshopWorkshopInfoStep(
    uiState: WorkshopInspectionUiState,
    onIntent: (WorkshopInspectionIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val step = uiState.workshopInfo
    val colors = LocalTaminColors.current

    var showJobSheet by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    fun update(transform: (WorkshopInfoStepState) -> WorkshopInfoStepState) {
        onIntent(WorkshopInspectionIntent.UpdateWorkshopInfo(transform(step)))
    }

    InspectionRequestStepScaffold(
        modifier = modifier,
        primaryText = stringResource(Res.string.inspection_request_next_step),
        primaryEnabled = uiState.isRequestStep2Valid && !uiState.isLoading,
        // See WorkshopIdentityContactStep for why this is always false rather than omitted.
        isPrimaryLoading = false,
        onPrimaryClick = { onIntent(WorkshopInspectionIntent.GoToNextRequestStep) },
        secondaryText = stringResource(Res.string.inspection_request_prev_step),
        onSecondaryClick = onBack,
    ) { padding ->
        InspectionRequestErrorWrapper(
            isLoading = uiState.isRequestStep2Loading,
            error = error,
            onRetry = { onIntent(WorkshopInspectionIntent.RetrySource(InspectionRequestErrorSource.JOBS)) },
            modifier = Modifier.padding(padding),
            shimmerContent = { WorkshopInfoShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.md))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = stringResource(Res.string.inspection_request_step2_section_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    if (!uiState.requestInspectionNo.isNullOrBlank()) {
                        CustomChip(
                            text = stringResource(
                                Res.string.inspection_request_source_chip_format,
                                uiState.requestInspectionNo.orEmpty().toPersianDigits(),
                            ),
                            containerColor = colors.blueBg,
                            textColor = colors.blueText,
                            border = BorderStroke(width = 1.dp, color = colors.blueBorder),
                        )
                    }
                }
                BannerCard(
                    type = BannerType.Success,
                    message = stringResource(Res.string.inspection_request_step2_section_banner),
                    modifier = Modifier.padding(top = Spacing.sm),
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            TaminStyledTextField(
                value = step.workshopName,
                onValueChange = {},
                label = stringResource(Res.string.inspection_request_field_workshop_name),
                placeholder = stringResource(Res.string.inspection_request_field_workshop_name_placeholder),
                isRequired = true,
                readOnly = true,
            )

            Spacer(Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                TaminStyledTextField(
                    value = step.employerName,
                    onValueChange = { value -> update { it.copy(employerName = value) } },
                    label = stringResource(Res.string.inspection_request_field_employer_name),
                    placeholder = stringResource(Res.string.inspection_request_field_employer_name_placeholder),
                    isRequired = true,
                    modifier = Modifier.weight(1f),
                )
                TaminStyledTextField(
                    value = step.workshopPhone,
                    onValueChange = { value -> update { it.copy(workshopPhone = value) } },
                    label = stringResource(Res.string.inspection_request_field_workshop_phone_optional),
                    placeholder = stringResource(Res.string.inspection_request_field_workshop_phone_placeholder),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    inputRestriction = InputRestriction.DigitsOnly,
                    maxLength = 11,
                    isValid = step.workshopPhone.takeIf { it.isNotBlank() }
                        ?.let { ValidationUtils.isLandlineValid(it) },
                    errorText = stringResource(Res.string.inspection_request_field_workshop_phone_error),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                TaminStyledTextField(
                    value = step.branchName,
                    onValueChange = {},
                    label = stringResource(Res.string.inspection_request_field_branch),
                    placeholder = stringResource(Res.string.inspection_request_field_branch_placeholder),
                    isRequired = true,
                    readOnly = true,
                    modifier = Modifier.weight(1f),
                )
                TaminStyledTextField(
                    value = step.jobTitle,
                    onValueChange = {},
                    label = stringResource(Res.string.inspection_request_field_job_title),
                    placeholder = stringResource(Res.string.inspection_request_field_job_title_placeholder),
                    isRequired = true,
                    readOnly = true,
                    trailingIcon = Icons.Default.ArrowDropDown,
                    onClick = { showJobSheet = true },
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                TaminStyledTextField(
                    value = step.startDate,
                    onValueChange = {},
                    label = stringResource(Res.string.inspection_request_field_employment_start),
                    placeholder = stringResource(Res.string.inspection_request_field_employment_start_placeholder),
                    isRequired = true,
                    readOnly = true,
                    trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.weight(1f),
                )
                TaminStyledTextField(
                    value = step.endDate,
                    onValueChange = {},
                    label = stringResource(Res.string.inspection_request_field_employment_end),
                    placeholder = stringResource(Res.string.inspection_request_field_employment_end_placeholder),
                    isRequired = true,
                    readOnly = true,
                    trailingIcon = vectorResource(Res.drawable.ic_tamin_calendar),
                    onClick = { showEndDatePicker = true },
                    isValid = step.endDate.takeIf { it.isNotBlank() }?.let {
                        ValidationUtils.isDateRangeValid(step.startDateTimestamp, step.endDateTimestamp)
                    },
                    errorText = stringResource(Res.string.inspection_request_field_employment_end_error),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(Spacing.lg))

            TaminStyledTextField(
                value = step.workshopAddress,
                onValueChange = { value -> update { it.copy(workshopAddress = value) } },
                label = stringResource(Res.string.inspection_request_field_workshop_address),
                placeholder = stringResource(Res.string.inspection_request_field_workshop_address_placeholder),
                isRequired = true,
            )

            Spacer(Modifier.height(Spacing.lg))
        }
        }
    }

    if (showJobSheet) {
        val searchHint = stringResource(Res.string.search_hint)
        InspectionSelectionSheet(
            title = stringResource(Res.string.inspection_request_field_job_title),
            options = uiState.jobs.map {
                InspectionSelectionOption(
                    id = it.jobCode,
                    title = it.jobDescription
                )
            },
            selectedId = step.jobCode.ifBlank { null },
            query = uiState.jobQuery,
            onQueryChange = { onIntent(WorkshopInspectionIntent.SearchJobs(it)) },
            onSelect = { option ->
                update { it.copy(jobCode = option.id, jobTitle = option.title) }
                showJobSheet = false
            },
            onDismiss = { showJobSheet = false },
            onLoadMore = { onIntent(WorkshopInspectionIntent.LoadNextJobs) },
            onRetry = { onIntent(WorkshopInspectionIntent.RetryNextJobs) },
            isLoadingFirstPage = uiState.isLoadingJobs,
            isLoadingNextPage = uiState.isLoadingNextJobs,
            endReached = uiState.jobsEndReached,
            pagingError = uiState.jobsPagingError,
            searchPlaceholder = searchHint,
        )
    }

    if (showStartDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.inspection_request_field_employment_start),
            onDismiss = { showStartDatePicker = false },
            onConfirm = { year, month, day ->
                update {
                    it.copy(
                        startDate = PersianDateFormatter.format(year, month, day),
                        startDateTimestamp = PersianDateFormatter.toEpochMillis(year, month, day),
                    )
                }
                showStartDatePicker = false
            },
        )
    }

    if (showEndDatePicker) {
        TaminJalaliDatePicker(
            title = stringResource(Res.string.inspection_request_field_employment_end),
            onDismiss = { showEndDatePicker = false },
            onConfirm = { year, month, day ->
                update {
                    it.copy(
                        endDate = PersianDateFormatter.format(year, month, day),
                        endDateTimestamp = PersianDateFormatter.toEpochMillis(year, month, day),
                    )
                }
                showEndDatePicker = false
            },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopWorkshopInfoStepPreview() {
    PreviewRtlThemeContent {
        WorkshopWorkshopInfoStep(
            uiState = WorkshopInspectionUiState(
                workshopInfo = WorkshopInfoStepState(
                    workshopName = "دبستان کارن ۲ مجتبی غلامیان",
                    workshopCode = "9028212822",
                    branchName = "یک بجنورد",
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopWorkshopInfoStepEmptyPreview() {
    PreviewRtlThemeContent {
        WorkshopWorkshopInfoStep(uiState = WorkshopInspectionUiState(), onIntent = {}, onBack = {})
    }
}
