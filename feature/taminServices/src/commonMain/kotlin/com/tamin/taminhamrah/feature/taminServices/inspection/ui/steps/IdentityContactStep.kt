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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.IdentityContactShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.inspection.ui.components.InspectionRequestStepScaffold
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.IdentityContactStepState
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionIntent
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionRequestErrorSource
import com.tamin.taminhamrah.feature.taminServices.inspection.contract.InspectionUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CustomChip
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.ValidationUtils
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_request_field_email_error
import taminx.core.core_ui.inspection_request_field_email_optional
import taminx.core.core_ui.inspection_request_field_landline_error
import taminx.core.core_ui.inspection_request_field_landline_optional
import taminx.core.core_ui.inspection_request_field_mobile_error
import taminx.core.core_ui.inspection_request_field_mobile_optional
import taminx.core.core_ui.inspection_request_field_national_code
import taminx.core.core_ui.inspection_request_field_full_name
import taminx.core.core_ui.inspection_request_field_placeholder
import taminx.core.core_ui.inspection_request_next_step
import taminx.core.core_ui.inspection_request_prev_step
import taminx.core.core_ui.inspection_request_source_chip_format
import taminx.core.core_ui.inspection_request_step1_section_title

@Composable
internal fun IdentityContactStep(
    uiState: InspectionUiState,
    onIntent: (InspectionIntent) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val step = uiState.identityContact
    val placeholder = stringResource(Res.string.inspection_request_field_placeholder)
    val colors = LocalTaminColors.current

    InspectionRequestStepScaffold(
        modifier = modifier,
        primaryText = stringResource(Res.string.inspection_request_next_step),
        primaryEnabled = uiState.isRequestStep1Valid && !uiState.isLoading,
        onPrimaryClick = { onIntent(InspectionIntent.GoToNextRequestStep) },
        secondaryText = stringResource(Res.string.inspection_request_prev_step),
        onSecondaryClick = onBack,
    ) { padding ->
        InspectionRequestErrorWrapper(
            isLoading = uiState.isLoading,
            error = error,
            onRetry = { onIntent(InspectionIntent.RetrySource(InspectionRequestErrorSource.USER_INFO)) },
            modifier = Modifier.padding(padding),
            shimmerContent = { IdentityContactShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(Modifier.height(Spacing.md))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = stringResource(Res.string.inspection_request_step1_section_title),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    )
                    if (uiState.isObjectionRequest && !uiState.requestInspectionNo.isNullOrBlank()) {
                        CustomChip(
                            text = stringResource(
                                Res.string.inspection_request_source_chip_format,
                                uiState.requestInspectionNo.orEmpty().toPersianDigits(),
                            ),
                            containerColor = colors.blueBg,
                            textColor = colors.blueText,
                        )
                    }
                }

                Spacer(Modifier.height(Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    TaminStyledTextField(
                        value = step.nationalCode,
                        onValueChange = {},
                        label = stringResource(Res.string.inspection_request_field_national_code),
                        placeholder = placeholder,
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                    )
                    TaminStyledTextField(
                        value = step.fullName,
                        onValueChange = {},
                        label = stringResource(Res.string.inspection_request_field_full_name),
                        placeholder = placeholder,
                        readOnly = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(Spacing.lg))

                TaminStyledTextField(
                    value = step.mobile,
                    onValueChange = { onIntent(InspectionIntent.UpdateIdentityContact(step.copy(mobile = it))) },
                    label = stringResource(Res.string.inspection_request_field_mobile_optional),
                    placeholder = placeholder,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    inputRestriction = InputRestriction.DigitsOnly,
                    maxLength = 11,
                    isValid = step.mobile.takeIf { it.isNotBlank() }?.let { ValidationUtils.isPhoneNumberValid(it) },
                    errorText = stringResource(Res.string.inspection_request_field_mobile_error),
                )

                Spacer(Modifier.height(Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    TaminStyledTextField(
                        value = step.landline,
                        onValueChange = { onIntent(InspectionIntent.UpdateIdentityContact(step.copy(landline = it))) },
                        label = stringResource(Res.string.inspection_request_field_landline_optional),
                        placeholder = placeholder,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        inputRestriction = InputRestriction.DigitsOnly,
                        maxLength = 11,
                        isValid = step.landline.takeIf { it.isNotBlank() }?.let { ValidationUtils.isLandlineValid(it) },
                        errorText = stringResource(Res.string.inspection_request_field_landline_error),
                        modifier = Modifier.weight(1f),
                    )
                    TaminStyledTextField(
                        value = step.email,
                        onValueChange = { onIntent(InspectionIntent.UpdateIdentityContact(step.copy(email = it))) },
                        label = stringResource(Res.string.inspection_request_field_email_optional),
                        placeholder = placeholder,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isValid = step.email.takeIf { it.isNotBlank() }?.let { ValidationUtils.isEmailValid(it) },
                        errorText = stringResource(Res.string.inspection_request_field_email_error),
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(Modifier.height(Spacing.lg))
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun IdentityContactStepPreview() {
    PreviewRtlThemeContent {
        IdentityContactStep(
            uiState = InspectionUiState(
                identityContact = IdentityContactStepState(
                    fullName = "رضا دریکوند",
                    nationalCode = "4060434061",
                    mobile = "09338042024",
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun IdentityContactStepEmptyPreview() {
    PreviewRtlThemeContent {
        IdentityContactStep(uiState = InspectionUiState(), onIntent = {}, onBack = {})
    }
}
