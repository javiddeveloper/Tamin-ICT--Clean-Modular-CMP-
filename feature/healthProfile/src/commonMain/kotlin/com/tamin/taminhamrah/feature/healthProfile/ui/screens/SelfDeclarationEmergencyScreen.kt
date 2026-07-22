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
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationEmergencyScreen(
    state: EmergencyStepState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isNextEnabled = state.emergencyName.isNotEmpty() && state.emergencyFamily.isNotEmpty() && state.emergencyRelation.isNotEmpty() && state.emergencyMobile.length >= 10

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 4,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.PHYSICAL)) },
                secondaryText = "مرحلهٔ قبلی",
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
                text = "تماس اضطراری",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "اطلاعات یکی از نزدیکان خود را وارد کنید تا در مواقع اضطراری با وی تماس گرفته شود.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            StyledTextField(
                value = state.emergencyName,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateEmergency(state.copy(emergencyName = valStr)))
                },
                label = "نام مخاطب اضطراری",
                placeholder = "وارد کنید"
            )

            StyledTextField(
                value = state.emergencyFamily,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateEmergency(state.copy(emergencyFamily = valStr)))
                },
                label = "نام خانوادگی",
                placeholder = "وارد کنید"
            )

            StyledTextField(
                value = state.emergencyRelation,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateEmergency(state.copy(emergencyRelation = valStr)))
                },
                label = "نسبت با شما",
                placeholder = "مثلاً همسر، پدر، خواهر و..."
            )

            StyledTextField(
                value = state.emergencyMobile,
                onValueChange = { valStr ->
                    onIntent(SelfDeclarationIntent.UpdateEmergency(state.copy(emergencyMobile = valStr)))
                },
                label = "شماره تلفن همراه اضطراری",
                placeholder = "09123456789",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
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

