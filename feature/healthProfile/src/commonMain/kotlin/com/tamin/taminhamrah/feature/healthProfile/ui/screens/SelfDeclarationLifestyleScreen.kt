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
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.LifestyleStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SelfDeclarationLifestyleScreen(
    state: LifestyleStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val optionsYesNo = listOf("خیر", "بله")

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 9,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.ALLERGY)) },
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
                text = "سبک زندگی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "فرآیند اطلاعات شما کاملاً محرمانه بوده و تنها برای ارزیابی پروندهٔ سلامت استفاده می‌شود.",
                style = MaterialTheme.typography.bodySmall.copy(color = taminColors.textTertiary),
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Smoking
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("مصرف دخانیات / سیگار؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isSmoking == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isSmoking = idx == 1)))
                    }
                )
            }

            if (state.isSmoking == true) {
                val patterns = listOf("روزانه", "هفتگی", "تفریحی / به ندرت")
                TaminText("الگوی مصرف دخانیات خود را انتخاب کنید:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = patterns,
                    selectedIndices = state.smokingPattern?.let { setOf(patterns.indexOf(it)) } ?: emptySet(),
                    onSelectionChanged = { idxs ->
                        val pat = idxs.firstOrNull()?.let { patterns[it] }
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(smokingPattern = pat)))
                    }
                )
            }

            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

            // Alcohol
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("مصرف الکل؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isDrinking == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isDrinking = idx == 1)))
                    }
                )
            }

            if (state.isDrinking == true) {
                val patterns = listOf("روزانه", "هفتگی", "ماهانه", "تفریحی / به ندرت")
                TaminText("الگوی مصرف الکل:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = patterns,
                    selectedIndices = state.drinkingPattern?.let { setOf(patterns.indexOf(it)) } ?: emptySet(),
                    onSelectionChanged = { idxs ->
                        val pat = idxs.firstOrNull()?.let { patterns[it] }
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(drinkingPattern = pat)))
                    }
                )
            }

            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

            // Exercise
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("فعالیت ورزشی منظم؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.isExercising == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(isExercising = idx == 1)))
                    }
                )
            }

            if (state.isExercising == true) {
                val frequencies = listOf("هر روز", "یک روز در میان", "۳ تا ۴ بار در هفته", "کمتر از ۲ بار در هفته")
                TaminText("میزان فعالیت ورزشی خود را انتخاب کنید:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = frequencies,
                    selectedIndices = state.exerciseFrequency?.let { setOf(frequencies.indexOf(it)) } ?: emptySet(),
                    onSelectionChanged = { idxs ->
                        val freq = idxs.firstOrNull()?.let { frequencies[it] }
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(exerciseFrequency = freq)))
                    }
                )
            }

            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

            // Addiction
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("سوء مصرف مواد یا اعتیاد؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasAddiction == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(HealthProfileIntent.UpdateLifestyle(state.copy(hasAddiction = idx == 1)))
                    }
                )
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationLifestyleScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationLifestyleScreen(
            state = LifestyleStepState(isSmoking = true, smokingPattern = "روزانه"),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

