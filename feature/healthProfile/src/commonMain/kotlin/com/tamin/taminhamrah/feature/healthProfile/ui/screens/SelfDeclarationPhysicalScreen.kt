package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PhysicalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationPhysicalScreen(
    state: PhysicalStepState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    // Calculate BMI
    val heightInMeters = state.height / 100f
    val bmiValue = if (heightInMeters > 0) state.weight / (heightInMeters * heightInMeters) else 0f

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 5,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
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
                .padding(top = paddingValues.calculateTopPadding())
                .background(taminColors.bgPage)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

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
                    onIntent(SelfDeclarationIntent.UpdatePhysical(state.copy(height = h)))
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
                    onIntent(SelfDeclarationIntent.UpdatePhysical(state.copy(weight = w)))
                },
                range = 40..150,
                unit = "کیلوگرم",
                accentColor = taminColors.teal
            )

            Spacer(modifier = Modifier.height(12.dp))

            BmiMeter(bmi = bmiValue)
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationPhysicalScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationPhysicalScreen(
            state = PhysicalStepState(height = 175, weight = 75),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

