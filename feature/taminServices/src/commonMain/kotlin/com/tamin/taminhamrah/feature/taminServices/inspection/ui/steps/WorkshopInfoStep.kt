package com.tamin.taminhamrah.feature.taminServices.inspection.ui.steps

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.WorkshopInfoStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestStepScaffold
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSelectionOption
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionSelectionSheet
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.WorkshopInfoShimmerSkeleton
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.ValidationUtils
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
import taminx.core.core_ui.inspection_request_field_workshop_code
import taminx.core.core_ui.inspection_request_field_workshop_code_error
import taminx.core.core_ui.inspection_request_field_workshop_code_placeholder
import taminx.core.core_ui.inspection_request_field_workshop_name
import taminx.core.core_ui.inspection_request_field_workshop_name_placeholder
import taminx.core.core_ui.inspection_request_field_workshop_phone_error
import taminx.core.core_ui.inspection_request_field_workshop_phone_optional
import taminx.core.core_ui.inspection_request_field_workshop_phone_placeholder
import taminx.core.core_ui.inspection_request_next_step
import taminx.core.core_ui.inspection_request_step2_section_title
import taminx.core.core_ui.search_hint

@Composable
internal fun WorkshopInfoStep(
    uiState: InspectionUiState,
    onIntent: (InspectionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = uiState.workshopInfo

    var showBranchSheet by remember { mutableStateOf(false) }
    var showJobSheet by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    fun update(transform: (WorkshopInfoStepState) -> WorkshopInfoStepState) {
        onIntent(InspectionIntent.UpdateWorkshopInfo(transform(step)))
    }

    // Objection requests prefill workshop name/code/branch from the inspection card being
    // objected to — those fields are locked so the user can't change what they're objecting about.
    val isPrefillLocked = uiState.isObjectionRequest

    InspectionRequestStepScaffold(
        modifier = modifier,
        primaryText = stringResource(Res.string.inspection_request_next_step),
        primaryEnabled = uiState.isRequestStep2Valid && !uiState.isLoading,
        onPrimaryClick = { onIntent(InspectionIntent.GoToNextRequestStep) },
    ) { padding ->
        InspectionRequestErrorWrapper(
            isLoading = uiState.isLoading,
            error = null,
            onRetry = {},
            modifier = Modifier.padding(padding),
            shimmerContent = { WorkshopInfoShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg),
        ) {
            Spacer(Modifier.height(Spacing.md))

            TaminText(
                text = stringResource(Res.string.inspection_request_step2_section_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            )

            Spacer(Modifier.height(Spacing.lg))

            TaminStyledTextField(
                value = step.workshopName,
                onValueChange = { value -> update { it.copy(workshopName = value) } },
                label = stringResource(Res.string.inspection_request_field_workshop_name),
                placeholder = stringResource(Res.string.inspection_request_field_workshop_name_placeholder),
                isRequired = true,
                readOnly = isPrefillLocked,
            )

            Spacer(Modifier.height(Spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                TaminStyledTextField(
                    value = step.workshopCode,
                    onValueChange = { value -> update { it.copy(workshopCode = value) } },
                    label = stringResource(Res.string.inspection_request_field_workshop_code),
                    placeholder = stringResource(Res.string.inspection_request_field_workshop_code_placeholder),
                    isRequired = true,
                    readOnly = isPrefillLocked,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    inputRestriction = InputRestriction.DigitsOnly,
                    maxLength = 10,
                    isValid = step.workshopCode.takeIf { it.isNotBlank() }?.let { it.length == 10 },
                    errorText = stringResource(Res.string.inspection_request_field_workshop_code_error),
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

            TaminStyledTextField(
                value = step.employerName,
                onValueChange = { value -> update { it.copy(employerName = value) } },
                label = stringResource(Res.string.inspection_request_field_employer_name),
                placeholder = stringResource(Res.string.inspection_request_field_employer_name_placeholder),
                isRequired = true,
            )

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
                    trailingIcon = if (isPrefillLocked) null else Icons.Default.ArrowDropDown,
                    onClick = if (isPrefillLocked) null else {
                        { showBranchSheet = true }
                    },
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
            Spacer(Modifier.height(padding.calculateBottomPadding()))
        }
        }
    }

    if (showBranchSheet) {
        val searchHint = stringResource(Res.string.search_hint)
        InspectionSelectionSheet(
            title = stringResource(Res.string.inspection_request_field_branch),
            options = uiState.branches.map {
                InspectionSelectionOption(
                    id = it.code,
                    title = it.name
                )
            },
            selectedId = step.branchCode.ifBlank { null },
            onSelect = { option ->
                update { it.copy(branchCode = option.id, branchName = option.title) }
                showBranchSheet = false
            },
            onDismiss = { showBranchSheet = false },
            searchPlaceholder = searchHint,
        )
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
            onSelect = { option ->
                update { it.copy(jobCode = option.id, jobTitle = option.title) }
                showJobSheet = false
            },
            onDismiss = { showJobSheet = false },
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
private fun WorkshopInfoStepPreview() {
    PreviewRtlThemeContent {
        WorkshopInfoStep(
            uiState = InspectionUiState(
                workshopInfo = WorkshopInfoStepState(
                    workshopName = "شرکت پیمانکاری ساخت و ابنیهٔ کاوه",
                    workshopCode = "0117742260",
                ),
            ),
            onIntent = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun WorkshopInfoStepEmptyPreview() {
    PreviewRtlThemeContent {
        WorkshopInfoStep(uiState = InspectionUiState(), onIntent = {})
    }
}
