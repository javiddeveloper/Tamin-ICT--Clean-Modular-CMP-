package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientDrugAllergyMock
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationPhysicalScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    // Calculate BMI
    val heightInMeters = state.height / 100f
    val bmiValue = if (heightInMeters > 0) state.weight / (heightInMeters * heightInMeters) else 0f

    Scaffold(
        bottomBar = {
            HealthNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.DISEASES)) },
                secondaryText = "مرحلهٔ قبلی",
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HealthProgressBar(currentStep = 5, totalSteps = 10)

            TaminText(
                text = "قد و وزن",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            TaminText(
                text = "برای تنظیم دقیق قد و وزن خود، خط کش‌ها را بکشید یا از دکمه‌های کناری استفاده کنید.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            TaminText(
                text = "قد (سانتی‌متر)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
                modifier = Modifier.align(Alignment.Start)
            )

            RulerPicker(
                value = state.height,
                onValueChange = { h ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(height = h) })
                },
                range = 120..220,
                unit = "سانتی‌متر"
            )

            Spacer(modifier = Modifier.height(8.dp))

            TaminText(
                text = "وزن (کیلوگرم)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary,
                modifier = Modifier.align(Alignment.Start)
            )

            RulerPicker(
                value = state.weight,
                onValueChange = { w ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(weight = w) })
                },
                range = 40..150,
                unit = "کیلوگرم",
                accentColor = taminColors.teal
            )

            Spacer(modifier = Modifier.height(12.dp))

            BmiMeter(bmi = bmiValue)
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationPhysicalScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationPhysicalScreen(
            state = SelfDeclarationUiState(height = 175, weight = 75),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
