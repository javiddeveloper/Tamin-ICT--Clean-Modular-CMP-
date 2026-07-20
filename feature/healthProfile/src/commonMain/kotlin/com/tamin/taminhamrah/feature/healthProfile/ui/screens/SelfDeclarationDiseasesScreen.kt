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
fun SelfDeclarationDiseasesScreen(
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
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.FAMILY)) },
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
            HealthProgressBar(currentStep = 6, totalSteps = 10)

            TaminText(
                text = "سابقهٔ بیماری‌های فردی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            // High parameters
            TaminText(
                text = "آیا سابقهٔ بالا بودن هر یک از موارد زیر را دارید؟",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textPrimary
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("قند خون بالا", style = MaterialTheme.typography.bodyMedium, color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasHighBloodSugar == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(hasHighBloodSugar = idx == 1) })
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
                    selectedIndex = if (state.hasHighBloodPressure == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(hasHighBloodPressure = idx == 1) })
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
                    selectedIndex = if (state.hasHighCholesterol == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(hasHighCholesterol = idx == 1) })
                    }
                )
            }

            HorizontalDivider(color = taminColors.divider, thickness = 1.dp)

            // Chronic illness
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("سابقهٔ ابتلا به بیماری‌های مزمن؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasChronicDisease == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(hasChronicDisease = idx == 1) })
                    }
                )
            }

            if (state.hasChronicDisease == true) {
                val chronicOptions = listOf("قلبی و عروقی", "مغزی", "ریوی (آسم، تنگی نفس، COPD)", "دستگاه گوارش", "تیرویید", "روماتیسمی", "سایر")
                TaminText("نوع بیماری خود را انتخاب کنید:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = chronicOptions,
                    selectedIndices = state.chronicDiseases,
                    onSelectionChanged = { indices ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(chronicDiseases = indices) })
                    }
                )
            }

            // Mental Illness
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("سابقهٔ بیماری‌های اعصاب و روان؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasMentalIllness == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(hasMentalIllness = idx == 1) })
                    }
                )
            }

            if (state.hasMentalIllness == true) {
                val mentalOptions = listOf("افسردگی", "اضطراب", "وسواس فکری", "سایر")
                TaminText("نوع عارضه را انتخاب کنید:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = mentalOptions,
                    selectedIndices = state.mentalIllnesses,
                    onSelectionChanged = { indices ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(mentalIllnesses = indices) })
                    }
                )
            }

            // Cancer
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaminText("سابقهٔ ابتلا به سرطان؟", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = taminColors.textPrimary)
                SegmentedControl(
                    options = optionsYesNo,
                    selectedIndex = if (state.hasCancer == true) 1 else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(hasCancer = idx == 1) })
                    }
                )
            }

            if (state.hasCancer == true) {
                val cancerOptions = listOf("سرطان خون", "سرطان ریه", "سرطان پستان", "سرطان دستگاه گوارش", "سرطان پروستات", "سرطان رحم و تخمدان", "سرطان پوست", "سرطان تیرویید", "سایر")
                TaminText("نوع سرطان را انتخاب کنید:", fontSize = 12.sp, color = taminColors.textTertiary)
                InteractiveChoiceChips(
                    options = cancerOptions,
                    selectedIndices = state.cancers,
                    onSelectionChanged = { indices ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(cancers = indices) })
                    }
                )
            }
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationDiseasesScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationDiseasesScreen(
            state = SelfDeclarationUiState(hasChronicDisease = true, chronicDiseases = setOf(0, 2)),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
