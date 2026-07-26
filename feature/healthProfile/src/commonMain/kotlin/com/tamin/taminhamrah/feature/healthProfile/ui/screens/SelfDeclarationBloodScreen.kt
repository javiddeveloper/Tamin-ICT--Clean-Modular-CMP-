package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.BloodGroupStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR

@Composable
fun SelfDeclarationBloodScreen(
    state: BloodGroupStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    bloodGroupOptions: List<LookupItemPR>
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isNextEnabled =
        state.isBloodGroupUnknown || (state.selectedBloodGroupLetter != null)

    LaunchedEffect(Unit) {
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "Screen opened. Initial state:")
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "selectedBloodGroupId: ${state.selectedBloodGroupId}")
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "selectedBloodGroupLetter: ${state.selectedBloodGroupLetter}")
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "isBloodGroupUnknown: ${state.isBloodGroupUnknown}")
    }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 8,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE)) },
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
                text = "گروه خونی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            // Blood Droplet Graphic
            BloodDropletGraphic(
                selectedLetter = state.selectedBloodGroupLetter,
                isUnknown = state.isBloodGroupUnknown,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            TaminText(
                text = "گروه خونی\u200Cتان را انتخاب کنید",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = taminColors.textPrimary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            TaminText(
                text = "ابتدا گروه خونی را انتخاب کنید",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = taminColors.textSecondary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(6.dp))

            TaminText(
                text = "نوع گروه خونی",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textTertiary,
                modifier = Modifier.align(Alignment.Start)
            )

            val chipsAlpha = if (state.isBloodGroupUnknown) 0.5f else 1f

            InteractiveChoiceChips(
                modifier = Modifier.alpha(chipsAlpha),
                options = bloodGroupOptions.map { it.label },
                selectedIndices = state.selectedBloodGroupId?.let { id ->
                    val idx = bloodGroupOptions.indexOfFirst { it.id == id }
                    if (idx >= 0) setOf(idx) else emptySet()
                } ?: emptySet(),
                onSelectionChanged = { idxs ->
                    if (!state.isBloodGroupUnknown) {
                        val letter = idxs.lastOrNull()?.let { bloodGroupOptions[it] }
                        onIntent(
                            HealthProfileIntent.UpdateBloodGroup(
                                state.copy(
                                    selectedBloodGroupId = letter?.id,
                                    selectedBloodGroupLetter = letter?.label
                                )
                            )
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(taminColors.bgSurface)
                    .border(1.dp, taminColors.border, RoundedCornerShape(12.dp))
                    .clickable {
                        val newUnknown = !state.isBloodGroupUnknown
                        val newId = if (newUnknown) bloodGroupOptions.find { it.label.contains("نامشخص") || it.label.contains("نمی دانم") || it.label.contains("نمیدانم") }?.id else null
                        onIntent(
                            HealthProfileIntent.UpdateBloodGroup(
                                state.copy(
                                    isBloodGroupUnknown = newUnknown,
                                    selectedBloodGroupLetter = if (newUnknown) null else state.selectedBloodGroupLetter,
                                    selectedBloodGroupId = newId
                                )
                            )
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isBloodGroupUnknown,
                    onCheckedChange = { chk ->
                        val newId = if (chk) bloodGroupOptions.find { it.label.contains("نامشخص") || it.label.contains("نمی دانم") || it.label.contains("نمیدانم") }?.id else null
                        onIntent(
                            HealthProfileIntent.UpdateBloodGroup(
                                state.copy(
                                    isBloodGroupUnknown = chk,
                                    selectedBloodGroupLetter = if (chk) null else state.selectedBloodGroupLetter,
                                    selectedBloodGroupId = newId
                                )
                            )
                        )
                    },
                    colors = CheckboxDefaults.colors(checkedColor = taminColors.blueText)
                )
                Spacer(modifier = Modifier.width(10.dp))
                TaminText(
                    text = "گروه خونی خود را نمی‌دانم",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textPrimary
                )
            }
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationBloodScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationBloodScreen(
            state = BloodGroupStepState(selectedBloodGroupLetter = "O+"),
            onIntent = {},
            onBackClicked = {},
            bloodGroupOptions = emptyList()
        )
    }
}

