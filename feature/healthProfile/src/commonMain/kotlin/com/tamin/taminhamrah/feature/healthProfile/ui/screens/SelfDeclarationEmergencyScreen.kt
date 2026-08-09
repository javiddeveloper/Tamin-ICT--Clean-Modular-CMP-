package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.EmergencyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.util.ValidationUtils
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationEmergencyScreen(
    state: EmergencyStepState,
    relationTypeOptions: List<LookupItemPR>,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    isLoading: Boolean = false,
    error: String? = null
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var showRelationBottomSheet by remember { mutableStateOf(false) }

    var emergencyTouched by remember { mutableStateOf(false) }
    var emergencyFocused by remember { mutableStateOf(false) }

    val isMobileValid = ValidationUtils.isPhoneNumberValid(state.emergencyMobile)

    val isNextEnabled =
        isMobileValid && state.emergencyName.isNotEmpty() && state.emergencyFamily.isNotEmpty() && state.emergencyRelationId != null && state.emergencyMobile.length >= 10

    val selectedRelationLabel = state.emergencyRelationLabel.ifEmpty {
        relationTypeOptions.firstOrNull { it.id == state.emergencyRelationId }?.label ?: ""
    }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 4,
                totalSteps = 10,
                onBackClicked = onBackClicked,
                onCloseClicked = onCloseClicked,
                title = stringResource(Res.string.health_emergency_title)
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.PHYSICAL)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        HealthProfileErrorWrapper(
            isLoading = isLoading,
            error = error,
            onRetry = { onIntent(HealthProfileIntent.RetryStep) },
            modifier = Modifier.padding(paddingValues),
            shimmerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(taminColors.bgPage)
                ) {
                    FormFieldsShimmerSkeleton(fieldCount = 4)
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .background(taminColors.bgPage)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                WarningBanner(
                    message = stringResource(Res.string.health_emergency_desc),
                )

                StyledTextField(
                    inputRestriction = InputRestriction.LettersOnly,
                    maxLength = 20,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    value = state.emergencyName,
                    onValueChange = { valStr ->
                        onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyName = valStr)))
                    },
                    label = stringResource(Res.string.health_emergency_name_label),
                    placeholder = stringResource(Res.string.health_placeholder_enter),
                    leadingIconPainter = painterResource(Res.drawable.ic_emergency_name),
                    isRequired = true
                )

                StyledTextField(
                    inputRestriction = InputRestriction.LettersOnly,
                    maxLength = 20,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = state.emergencyFamily,
                    onValueChange = { valStr ->
                        onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyFamily = valStr)))
                    },
                    label = stringResource(Res.string.health_label_last_name),
                    placeholder = stringResource(Res.string.health_placeholder_enter),
                    leadingIconPainter = painterResource(Res.drawable.ic_emergency_name),
                    isRequired = true
                )

                val showMobileError = emergencyTouched && !isMobileValid

                StyledTextField(
                    inputRestriction = InputRestriction.DigitsOnly,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    value = state.emergencyMobile,
                    onValueChange = { valStr ->
                        val filtered = ValidationUtils.validatePhoneNumber(valStr)
                        onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyMobile = filtered)))
                    },
                    label = stringResource(Res.string.health_emergency_mobile_label),
                    placeholder = stringResource(Res.string.health_contact_mobile_placeholder),
                    leadingIconPainter = painterResource(Res.drawable.ic_contact_mobile),
                    isValid = if (showMobileError) false else null,
                    errorText = if (showMobileError) stringResource(Res.string.health_contact_mobile_error) else null,
                    isRequired = true,
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            emergencyFocused = true
                        } else if (emergencyFocused) {
                            emergencyTouched = true
                        }
                    }
                )

                StyledTextField(
                    value = selectedRelationLabel,
                    onValueChange = {},
                    label = "نسبت با فرد",
                    placeholder = "انتخاب کنید",
                    leadingIconPainter = painterResource(Res.drawable.ic_family),
                    trailingIcon = Icons.Default.KeyboardArrowDown,
                    readOnly = true,
                    isRequired = true,
                    onClick = { showRelationBottomSheet = true }
                )
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }

    if (showRelationBottomSheet) {
        val bottomSheetItems = relationTypeOptions.map { option ->
            BottomSheetItem(
                id = option.id,
                title = option.label,
                isSelected = option.id == state.emergencyRelationId
            )
        }

        HealthBottomSheet(
            config = BottomSheetConfig(
                title = "نسبت با فرد",
                subtitle = "نسبت فرد تماس اضطراری با شما را انتخاب کنید",
                type = BottomSheetType.RELATION_TYPE,
                singleSelection = true,
                items = bottomSheetItems
            ),
            onDismissRequest = { showRelationBottomSheet = false },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                val selectedOption = relationTypeOptions.firstOrNull { it.id == selectedId }
                if (selectedOption != null) {
                    onIntent(
                        HealthProfileIntent.UpdateEmergency(
                            state.copy(
                                emergencyRelationId = selectedOption.id,
                                emergencyRelationLabel = selectedOption.label
                            )
                        )
                    )
                }
                showRelationBottomSheet = false
            }
        )
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationEmergencyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationEmergencyScreen(
            state = EmergencyStepState(
                emergencyName = "مریم",
                emergencyRelationId = 1,
                emergencyRelationLabel = "همسر",
                emergencyMobile = "09129876543"
            ),
            relationTypeOptions = listOf(
                LookupItemPR(1, "همسر"),
                LookupItemPR(2, "فرزند"),
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
