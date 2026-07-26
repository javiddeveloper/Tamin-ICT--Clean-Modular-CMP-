package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.EmergencyStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.util.ValidationUtils
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationEmergencyScreen(
    state: EmergencyStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    var emergencyTouched by remember { mutableStateOf(false) }
    var emergencyFocused by remember { mutableStateOf(false) }

    val isMobileValid = ValidationUtils.isPhoneNumberValid(state.emergencyMobile)

    val isNextEnabled = isMobileValid && state.emergencyName.isNotEmpty() && state.emergencyFamily.isNotEmpty() && state.emergencyRelation.isNotEmpty() && state.emergencyMobile.length >= 10

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 4,
                totalSteps = 10,
                onBackClicked = onBackClicked,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TaminText(
                text = stringResource(Res.string.health_emergency_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = stringResource(Res.string.health_emergency_desc),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            StyledTextField(
                value = state.emergencyName,
                onValueChange = { valStr ->
                    onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyName = valStr)))
                },
                label = stringResource(Res.string.health_emergency_name_label),
                placeholder = stringResource(Res.string.health_placeholder_enter)
            )

            StyledTextField(
                value = state.emergencyFamily,
                onValueChange = { valStr ->
                    onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyFamily = valStr)))
                },
                label = stringResource(Res.string.health_label_last_name),
                placeholder = stringResource(Res.string.health_placeholder_enter)
            )

            StyledTextField(
                value = state.emergencyRelation,
                onValueChange = { valStr ->
                    onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyRelation = valStr)))
                },
                label = stringResource(Res.string.health_emergency_relation_label),
                placeholder = stringResource(Res.string.health_emergency_relation_placeholder)
            )
            val showMobileError = emergencyTouched && !isMobileValid

            StyledTextField(
                value = state.emergencyMobile,
                onValueChange = { valStr ->
                    val filtered = ValidationUtils.validatePhoneNumber(valStr)
                    onIntent(HealthProfileIntent.UpdateEmergency(state.copy(emergencyMobile = filtered)))
                },
                label = stringResource(Res.string.health_emergency_mobile_label),
                placeholder = stringResource(Res.string.health_contact_mobile_placeholder),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isValid = if (showMobileError) false else null,
                errorText = if (showMobileError) stringResource(Res.string.health_contact_mobile_error) else null,
                onFocusChanged = { isFocused ->
                    if (isFocused) {
                        emergencyFocused = true
                    } else if (emergencyFocused) {
                        emergencyTouched = true
                    }
                }
            )
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationEmergencyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationEmergencyScreen(
            state = EmergencyStepState(emergencyName = "مریم", emergencyRelation = "همسر", emergencyMobile = "09129876543"),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

