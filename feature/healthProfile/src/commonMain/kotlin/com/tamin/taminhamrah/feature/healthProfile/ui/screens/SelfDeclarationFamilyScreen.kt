package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationUiState
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationFamilyScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val optionsYesNo = listOf("خیر", "بله")

    Scaffold(
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.BLOOD)) },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HealthProgressBar(currentStep = 7, totalSteps = 10)

            TaminText(
                text = "سلامتی خانواده درجه یک",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "آیا در اعضای درجه یک خانواده (پدر، مادر، خواهر، برادر) سابقهٔ ابتلا به موارد زیر وجود دارد؟",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("قند خون بالا (دیابت)", style = MaterialTheme.typography.bodyMedium, color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.familyHighBloodSugar == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(familyHighBloodSugar = idx == 1) })
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("فشار خون بالا", style = MaterialTheme.typography.bodyMedium, color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.familyHighBloodPressure == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(familyHighBloodPressure = idx == 1) })
                    }
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("چربی خون یا کلسترول بالا", style = MaterialTheme.typography.bodyMedium, color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.familyHighCholesterol == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(familyHighCholesterol = idx == 1) })
                    }
                )
            }

            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("سابقهٔ ابتلا به سرطان در خانواده؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.familyHasCancer == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(familyHasCancer = idx == 1) })
                    }
                )
            }

            if (state.familyHasCancer == true) {
                val cancerOptions = listOf("سرطان خون", "سرطان ریه", "سرطان پستان", "سرطان دستگاه گوارش", "سرطان پروستات", "سرطان رحم و تخمدان", "سرطان پوست", "سرطان تیرویید", "سایر")
                TaminText("نوع سرطان عضو درجه یک خانواده را انتخاب کنید:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = cancerOptions,
                    selectedIndices = state.familyCancers,
                    onSelectionChanged = { indices ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(familyCancers = indices) })
                    }
                )
            }
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationFamilyScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationFamilyScreen(
            state = SelfDeclarationUiState(familyHasCancer = true, familyCancers = setOf(1)),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
