package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceStepScaffold
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.Step4WorkHoursShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.rememberFieldTouchState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.WorkHoursStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.isWorkEndAfterWorkStart
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminJalaliTimePickerBottomSheet
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.ValidationUtils
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_field_home_address
import taminx.core.core_ui.occurrence_field_home_address_error
import taminx.core.core_ui.occurrence_field_home_phone
import taminx.core.core_ui.occurrence_field_home_phone_error
import taminx.core.core_ui.occurrence_field_home_phone_hint
import taminx.core.core_ui.occurrence_field_home_postal_code
import taminx.core.core_ui.occurrence_field_home_postal_code_error
import taminx.core.core_ui.occurrence_field_home_postal_code_hint
import taminx.core.core_ui.occurrence_field_transportation
import taminx.core.core_ui.occurrence_field_transportation_error
import taminx.core.core_ui.occurrence_field_transportation_hint
import taminx.core.core_ui.occurrence_field_work_end_time
import taminx.core.core_ui.occurrence_field_work_end_time_error
import taminx.core.core_ui.occurrence_field_work_end_time_hint
import taminx.core.core_ui.occurrence_field_work_end_time_order_error
import taminx.core.core_ui.occurrence_field_work_start_time
import taminx.core.core_ui.occurrence_field_work_start_time_error
import taminx.core.core_ui.occurrence_field_work_start_time_hint
import taminx.core.core_ui.occurrence_next_step
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_step4_title
import taminx.core.core_ui.province_city_address_hint

@Composable
internal fun Step4WorkHoursStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val step = uiState.workHours

    val transportationTouch = rememberFieldTouchState()

    var workStartTimeTouched by rememberSaveable { mutableStateOf(false) }
    var workEndTimeTouched by rememberSaveable { mutableStateOf(false) }

    val homeAddressTouch = rememberFieldTouchState()
    val homePhoneTouch = rememberFieldTouchState()
    val homePostalCodeTouch = rememberFieldTouchState()

    val isTransportationValid = step.transportation.isNotBlank()
    val isWorkStartTimeValid = step.workStartTime.isNotBlank()
    val isWorkEndTimeValid = step.workEndTime.isNotBlank()
    val isHomeAddressValid = step.homeAddress.isNotBlank()
    val isHomePhoneValid = ValidationUtils.isPhoneNumberValid(step.homePhone)
    val isHomePostalCodeValid = step.homePostalCode.isNotBlank() &&
        ValidationUtils.isPostcodeValid(step.homePostalCode)

    if (uiState.dialogs.showWorkStartTimePicker) {
        TaminJalaliTimePickerBottomSheet(
            title = stringResource(Res.string.occurrence_field_work_start_time),
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkStartTimePicker = false))) },
            onConfirm = { hour, minute ->
                val timeStr =
                    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                onIntent(OccurrenceIntent.UpdateWorkHours(step.copy(workStartTime = timeStr)))
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkStartTimePicker = false)))
            },
        )
    }

    if (uiState.dialogs.showWorkEndTimePicker) {
        TaminJalaliTimePickerBottomSheet(
            title = stringResource(Res.string.occurrence_field_work_end_time),
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkEndTimePicker = false))) },
            onConfirm = { hour, minute ->
                val timeStr =
                    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                onIntent(OccurrenceIntent.UpdateWorkHours(step.copy(workEndTime = timeStr)))
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkEndTimePicker = false)))
            },
        )
    }

    OccurrenceStepScaffold(
        modifier = modifier,
        title = stringResource(Res.string.occurrence_step4_title),
        stepNumber = uiState.stepNumber,
        totalSteps = OccurrenceStep.entries.size,
        onBackClicked = onBack,
        onCloseClicked = onClose,
        primaryText = stringResource(Res.string.occurrence_next_step),
        primaryEnabled = uiState.isStep4Valid && !uiState.isLoading && !uiState.isSubmitting,
        onPrimaryClick = { onIntent(OccurrenceIntent.GoToNextStep) },
        secondaryText = stringResource(Res.string.occurrence_prev_step),
        onSecondaryClick = onBack,
    ) { padding ->
        OccurrenceErrorWrapper(
            isLoading = uiState.isLoading,
            error = null,
            onRetry = { onIntent(OccurrenceIntent.LoadInitialData) },
            modifier = Modifier.padding(padding),
            shimmerContent = { Step4WorkHoursShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                val showTransportationError = transportationTouch.touched && !isTransportationValid
                TaminStyledTextField(
                    value = step.transportation,
                    onValueChange = {
                        onIntent(
                            OccurrenceIntent.UpdateWorkHours(
                                step.copy(
                                    transportation = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_transportation),
                    placeholder = stringResource(Res.string.occurrence_field_transportation_hint),
                    isValid = if (showTransportationError) false else null,
                    errorText = if (showTransportationError) stringResource(Res.string.occurrence_field_transportation_error) else null,
                    isRequired = true,
                    onFocusChanged = transportationTouch.onFocusChanged,
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                val showWorkStartTimeError = workStartTimeTouched && !isWorkStartTimeValid

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = androidx.compose.ui.Alignment.Top
                ) {
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.workStartTime,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_work_start_time),
                        placeholder = stringResource(Res.string.occurrence_field_work_start_time_hint),
                        trailingIcon = Icons.Default.AccessTime,
                        isValid = if (showWorkStartTimeError) false else null,
                        errorText = if (showWorkStartTimeError) stringResource(Res.string.occurrence_field_work_start_time_error) else null,
                        isRequired = true,
                        readOnly = true,
                        onClick = {
                            workStartTimeTouched = true
                            onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkStartTimePicker = true)))
                        },
                    )

                    Spacer(modifier = Modifier.width(Spacing.sm))

                    val isWorkEndOrderValid = isWorkEndAfterWorkStart(step.workStartTime, step.workEndTime)
                    val showWorkEndTimeError = workEndTimeTouched && (!isWorkEndTimeValid || !isWorkEndOrderValid)
                    val workEndTimeErrorText = when {
                        !workEndTimeTouched -> null
                        !isWorkEndTimeValid -> stringResource(Res.string.occurrence_field_work_end_time_error)
                        !isWorkEndOrderValid -> stringResource(Res.string.occurrence_field_work_end_time_order_error)
                        else -> null
                    }
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.workEndTime,
                        onValueChange = {},
                        label = stringResource(Res.string.occurrence_field_work_end_time),
                        placeholder = stringResource(Res.string.occurrence_field_work_end_time_hint),
                        trailingIcon = Icons.Default.AccessTime,
                        isValid = if (showWorkEndTimeError) false else null,
                        errorText = workEndTimeErrorText,
                        isRequired = true,
                        readOnly = true,
                        onClick = {
                            workEndTimeTouched = true
                            onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkEndTimePicker = true)))
                        },
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.sm))

                val showHomeAddressError = homeAddressTouch.touched && !isHomeAddressValid
                TaminTextArea(
                    value = step.homeAddress,
                    onValueChange = {
                        onIntent(
                            OccurrenceIntent.UpdateWorkHours(
                                step.copy(
                                    homeAddress = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_home_address),
                    placeholder = stringResource(Res.string.province_city_address_hint),
                    error = showHomeAddressError,
                    errorMessage = if (showHomeAddressError) stringResource(Res.string.occurrence_field_home_address_error) else null,
                    isRequired = true,
                    modifier = Modifier.onFocusChanged { focusState ->
                        homeAddressTouch.onFocusChanged(focusState.isFocused)
                    },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                val showHomePhoneError = homePhoneTouch.touched && !isHomePhoneValid

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {

                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.homePhone,
                        onValueChange = {
                            val filtered = ValidationUtils.validatePhoneNumber(it)
                            onIntent(OccurrenceIntent.UpdateWorkHours(step.copy(homePhone = filtered)))
                        },
                        label = stringResource(Res.string.occurrence_field_home_phone),
                        placeholder = stringResource(Res.string.occurrence_field_home_phone_hint),
                        inputRestriction = InputRestriction.DigitsOnly,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isValid = if (showHomePhoneError) false else null,
                        errorText = if (showHomePhoneError) stringResource(Res.string.occurrence_field_home_phone_error) else null,
                        isRequired = true,
                        onFocusChanged = homePhoneTouch.onFocusChanged,
                    )

                    Spacer(modifier = Modifier.width(Spacing.sm))

                    val showHomePostalCodeError = homePostalCodeTouch.touched && !isHomePostalCodeValid
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.homePostalCode,
                        onValueChange = {
                            val filtered = ValidationUtils.validatePostcode(it)
                            onIntent(OccurrenceIntent.UpdateWorkHours(step.copy(homePostalCode = filtered)))
                        },
                        label = stringResource(Res.string.occurrence_field_home_postal_code),
                        placeholder = stringResource(Res.string.occurrence_field_home_postal_code_hint),
                        inputRestriction = InputRestriction.DigitsOnly,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isValid = if (showHomePostalCodeError) false else null,
                        errorText = if (showHomePostalCodeError) stringResource(Res.string.occurrence_field_home_postal_code_error) else null,
                        isRequired = true,
                        onFocusChanged = homePostalCodeTouch.onFocusChanged,
                    )
                }


                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step4WorkHoursStepPreview() {
    PreviewRtlThemeContent {
        Step4WorkHoursStep(
            uiState = OccurrenceUiState(
                currentStep = OccurrenceStep.WORK_HOURS,
                workHours = WorkHoursStepState(
                    transportation = "وسیله نقلیه شخصی",
                    workStartTime = "۰۸:۰۰",
                    workEndTime = "۱۷:۰۰",
                    homeAddress = "تهران، خیابان انقلاب، پلاک ۱۲",
                    homePhone = "02112345678",
                    homePostalCode = "1234567890",
                ),
            ),
            onIntent = {},
            onBack = {},
            onClose = {}
        )
    }
}
