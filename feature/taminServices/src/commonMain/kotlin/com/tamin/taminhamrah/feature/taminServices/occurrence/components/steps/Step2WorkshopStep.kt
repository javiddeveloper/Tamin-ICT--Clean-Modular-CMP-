package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceErrorWrapper
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.Step2WorkshopShimmerSkeleton
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.ErrorSource
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.WorkshopStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.WorkshopItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.InputRestriction
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminStyledTextField
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.TaminTextArea
import com.tamin.taminhamrah.ui.components.topbars.TaminStepTopAppBar
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.ShimmerBlock
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.ValidationUtils
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_field_employer_name
import taminx.core.core_ui.occurrence_field_employer_name_error
import taminx.core.core_ui.occurrence_field_employer_phone
import taminx.core.core_ui.occurrence_field_employer_phone_error
import taminx.core.core_ui.occurrence_field_phone
import taminx.core.core_ui.occurrence_field_phone_error
import taminx.core.core_ui.occurrence_field_postal_code
import taminx.core.core_ui.occurrence_field_postal_code_error
import taminx.core.core_ui.occurrence_field_workshop_address
import taminx.core.core_ui.occurrence_field_workshop_address_error
import taminx.core.core_ui.occurrence_field_workshop_code
import taminx.core.core_ui.occurrence_field_workshop_code_hint
import taminx.core.core_ui.occurrence_field_workshop_name
import taminx.core.core_ui.occurrence_next_step
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_sheet_select_workshop
import taminx.core.core_ui.occurrence_step2_title

@Composable
internal fun Step2WorkshopStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val taminColors = LocalTaminColors.current
    val step = uiState.workshop

    var employerNameHasFocused by rememberSaveable { mutableStateOf(false) }
    var employerNameTouched by rememberSaveable { mutableStateOf(false) }

    var employerPhoneHasFocused by rememberSaveable { mutableStateOf(false) }
    var employerPhoneTouched by rememberSaveable { mutableStateOf(false) }

    var workshopAddressHasFocused by rememberSaveable { mutableStateOf(false) }
    var workshopAddressTouched by rememberSaveable { mutableStateOf(false) }

    var workshopPhoneHasFocused by rememberSaveable { mutableStateOf(false) }
    var workshopPhoneTouched by rememberSaveable { mutableStateOf(false) }

    var workshopPostalCodeHasFocused by rememberSaveable { mutableStateOf(false) }
    var workshopPostalCodeTouched by rememberSaveable { mutableStateOf(false) }

    val isEmployerNameValid = step.employerName.isNotBlank()
    val isEmployerPhoneValid = ValidationUtils.isPhoneNumberValid(step.employerPhone)
    val isWorkshopAddressValid = step.workshopAddress.isNotBlank()
    val isWorkshopPhoneValid = ValidationUtils.isPhoneNumberValid(step.workshopPhone)
    val isWorkshopPostalCodeValid = step.workshopPostalCode.isNotBlank() &&
        ValidationUtils.isPostcodeValid(step.workshopPostalCode)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminStepTopAppBar(
                title = stringResource(Res.string.occurrence_step2_title),
                onBackClicked = onBack,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
                onCloseClicked = onClose
            )
        },
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.occurrence_next_step),
                primaryEnabled = uiState.isStep2Valid && !uiState.isLoading && !uiState.isSubmitting,
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
            onRetry = { onIntent(OccurrenceIntent.RetrySource(ErrorSource.WORKSHOPS)) },
            modifier = Modifier.padding(padding),
            shimmerContent = { Step2WorkshopShimmerSkeleton(modifier = Modifier.padding(padding)) },
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                TaminStyledTextField(
                    value = step.selectedWorkshop?.displayCode ?: "",
                    label = stringResource(Res.string.occurrence_field_workshop_code),
                    placeholder = stringResource(Res.string.occurrence_field_workshop_code_hint),
                    onClick = {
                        onIntent(
                            OccurrenceIntent.UpdateDialogs(
                                uiState.dialogs.copy(
                                    showWorkshopSheet = true
                                )
                            )
                        )
                    },
                    onValueChange = {},
                    leadingIcon = Icons.Default.KeyboardArrowDown,
                    isRequired = true,
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                step.selectedWorkshop?.let { workshop ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(13.dp))
                            .background(color = taminColors.chipBg)
                            .border(
                                width = 1.dp,
                                shape = RoundedCornerShape(13.dp),
                                color = taminColors.blueBg
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaminText(
                            stringResource(Res.string.occurrence_field_workshop_name),
                            fontSize = 13.5.sp,
                            color = taminColors.blueText,
                            fontWeight = FontWeight.Normal
                        )
                        if (step.isWorkshopSpecLoading) {
                            ShimmerBlock(
                                modifier = Modifier.width(120.dp).height(16.dp),
                                cornerRadius = 4.dp,
                            )
                        } else {
                            TaminText(
                                workshop.name,
                                color = taminColors.textPrimary,
                                fontSize = 13.5.sp,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }

                val showEmployerNameError = employerNameTouched && !isEmployerNameValid
                TaminStyledTextField(
                    value = step.employerName,
                    onValueChange = {
                        onIntent(
                            OccurrenceIntent.UpdateWorkshop(
                                step.copy(
                                    employerName = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_employer_name),
                    placeholder = "",
                    isValid = if (showEmployerNameError) false else null,
                    errorText = if (showEmployerNameError) stringResource(Res.string.occurrence_field_employer_name_error) else null,
                    isRequired = true,
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            employerNameHasFocused = true
                        } else if (employerNameHasFocused) {
                            employerNameTouched = true
                        }
                    },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                val showEmployerPhoneError = employerPhoneTouched && !isEmployerPhoneValid
                TaminStyledTextField(
                    value = step.employerPhone,
                    onValueChange = {
                        val filtered = ValidationUtils.validatePhoneNumber(it)
                        onIntent(
                            OccurrenceIntent.UpdateWorkshop(
                                step.copy(
                                    employerPhone = filtered
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_employer_phone),
                    placeholder = "09121111111",
                    inputRestriction = InputRestriction.DigitsOnly,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isValid = if (showEmployerPhoneError) false else null,
                    errorText = if (showEmployerPhoneError) stringResource(Res.string.occurrence_field_employer_phone_error) else null,
                    isRequired = true,
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            employerPhoneHasFocused = true
                        } else if (employerPhoneHasFocused) {
                            employerPhoneTouched = true
                        }
                    },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                val showWorkshopAddressError = workshopAddressTouched && !isWorkshopAddressValid
                TaminTextArea(
                    value = step.workshopAddress,
                    onValueChange = {
                        onIntent(
                            OccurrenceIntent.UpdateWorkshop(
                                step.copy(
                                    workshopAddress = it
                                )
                            )
                        )
                    },
                    label = stringResource(Res.string.occurrence_field_workshop_address),
                    placeholder = "استان، شهر، خیابان، پلاک",
                    error = showWorkshopAddressError,
                    errorMessage = if (showWorkshopAddressError) stringResource(Res.string.occurrence_field_workshop_address_error) else null,
                    isRequired = true,
                    modifier = Modifier.onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            workshopAddressHasFocused = true
                        } else if (workshopAddressHasFocused) {
                            workshopAddressTouched = true
                        }
                    },
                )

                Spacer(modifier = Modifier.height(Spacing.sm))

                val showWorkshopPhoneError = workshopPhoneTouched && !isWorkshopPhoneValid
                val showWorkshopPostalCodeError =
                    workshopPostalCodeTouched && !isWorkshopPostalCodeValid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.workshopPhone,
                        onValueChange = {
                            val filtered = ValidationUtils.validatePhoneNumber(it)
                            onIntent(
                                OccurrenceIntent.UpdateWorkshop(
                                    step.copy(
                                        workshopPhone = filtered
                                    )
                                )
                            )
                        },
                        label = stringResource(Res.string.occurrence_field_phone),
                        placeholder = "09121234567",
                        inputRestriction = InputRestriction.DigitsOnly,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        isValid = if (showWorkshopPhoneError) false else null,
                        errorText = if (showWorkshopPhoneError) stringResource(Res.string.occurrence_field_phone_error) else null,
                        isRequired = true,
                        onFocusChanged = { isFocused ->
                            if (isFocused) {
                                workshopPhoneHasFocused = true
                            } else if (workshopPhoneHasFocused) {
                                workshopPhoneTouched = true
                            }
                        },
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    TaminStyledTextField(
                        modifier = Modifier.weight(1f),
                        value = step.workshopPostalCode,
                        onValueChange = {
                            val filtered = ValidationUtils.validatePostcode(it)
                            onIntent(
                                OccurrenceIntent.UpdateWorkshop(
                                    step.copy(
                                        workshopPostalCode = filtered
                                    )
                                )
                            )
                        },
                        label = stringResource(Res.string.occurrence_field_postal_code),
                        placeholder = "کد پستی 10 رقمی",
                        inputRestriction = InputRestriction.DigitsOnly,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isValid = if (showWorkshopPostalCodeError) false else null,
                        errorText = if (showWorkshopPostalCodeError) stringResource(Res.string.occurrence_field_postal_code_error) else null,
                        isRequired = true,
                        onFocusChanged = { isFocused ->
                            if (isFocused) {
                                workshopPostalCodeHasFocused = true
                            } else if (workshopPostalCodeHasFocused) {
                                workshopPostalCodeTouched = true
                            }
                        },
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }

    if (uiState.dialogs.showWorkshopSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_select_workshop),
            options = step.workshops.map { workshop ->
                OccurrenceSheetOption(id = workshop.id, title = workshop.displayCode)
            },
            selectedId = step.selectedWorkshop?.id,
            onSelect = { option ->
                val workshop = step.workshops.first { it.id == option.id }
                onIntent(OccurrenceIntent.SelectWorkshop(workshop))
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showWorkshopSheet = false)))
            },
            onDismiss = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            showWorkshopSheet = false
                        )
                    )
                )
            },
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
private fun Step2WorkshopStepPreview() {
    PreviewRtlThemeContent {
        Step2WorkshopStep(
            uiState = OccurrenceUiState(
                currentStep = OccurrenceStep.WORKSHOP_INFO,
                workshop = WorkshopStepState(
                    workshops = listOf(
                        WorkshopItemPR(
                            id = "1",
                            workshopCode = "1412345",
                            branchCode = "014",
                            name = "کارگاه تولیدی الف",
                            employerName = "شرکت الف",
                            employerPhone = "02112345678",
                            address = "تهران، خیابان ولیعصر",
                            postalCode = "1234567890",
                            phone = "02112345678"
                        ),
                        WorkshopItemPR(
                            id = "2",
                            workshopCode = "1465432",
                            branchCode = "014",
                            name = "کارگاه صنعتی ب",
                            employerName = "شرکت ب",
                            employerPhone = "",
                            address = "",
                            postalCode = "",
                            phone = ""
                        ),
                    ),
                    selectedWorkshop = WorkshopItemPR(
                        id = "1",
                        workshopCode = "1412345",
                        branchCode = "014",
                        name = "کارگاه تولیدی الف",
                        employerName = "شرکت الف",
                        employerPhone = "02112345678",
                        address = "تهران، خیابان ولیعصر",
                        postalCode = "1234567890",
                        phone = "02112345678"
                    ),
                    employerName = "شرکت الف",
                    employerPhone = "02112345678",
                    workshopAddress = "تهران، خیابان ولیعصر",
                    workshopPostalCode = "1234567890",
                    workshopPhone = "02198765432",
                ),
            ),
            onIntent = {},
            onBack = {},
            onClose = {}
        )
    }
}
