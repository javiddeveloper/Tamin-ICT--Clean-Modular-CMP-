package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
fun SelfDeclarationBloodScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val bloodLetters = listOf("A", "B", "AB", "O")
    val rhFactors = listOf("+", "-")

    val isNextEnabled = state.isBloodGroupUnknown || (state.selectedBloodGroupLetter != null && state.selectedBloodGroupRh != null)

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
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE)) },
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
                selectedRh = state.selectedBloodGroupRh,
                isUnknown = state.isBloodGroupUnknown,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(taminColors.bgSurface)
                    .border(1.dp, taminColors.border, RoundedCornerShape(12.dp))
                    .clickable {
                        onIntent(SelfDeclarationIntent.UpdateState {
                            copy(
                                isBloodGroupUnknown = !isBloodGroupUnknown,
                                selectedBloodGroupLetter = if (!isBloodGroupUnknown) null else selectedBloodGroupLetter,
                                selectedBloodGroupRh = if (!isBloodGroupUnknown) null else selectedBloodGroupRh
                            )
                        })
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.isBloodGroupUnknown,
                    onCheckedChange = { chk ->
                        onIntent(SelfDeclarationIntent.UpdateState {
                            copy(
                                isBloodGroupUnknown = chk,
                                selectedBloodGroupLetter = if (chk) null else selectedBloodGroupLetter,
                                selectedBloodGroupRh = if (chk) null else selectedBloodGroupRh
                            )
                        })
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

            if (!state.isBloodGroupUnknown) {
                Spacer(modifier = Modifier.height(6.dp))

                TaminText(
                    text = "نوع گروه خونی",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textTertiary,
                    modifier = Modifier.align(Alignment.Start)
                )

                InteractiveChoiceChips(
                    options = bloodLetters,
                    selectedIndices = state.selectedBloodGroupLetter?.let { setOf(bloodLetters.indexOf(it)) } ?: emptySet(),
                    onSelectionChanged = { idxs ->
                        val letter = idxs.firstOrNull()?.let { bloodLetters[it] }
                        onIntent(SelfDeclarationIntent.UpdateState { copy(selectedBloodGroupLetter = letter) })
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                TaminText(
                    text = "فاکتور Rh",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = taminColors.textTertiary,
                    modifier = Modifier.align(Alignment.Start)
                )

                InteractiveChoiceChips(
                    options = rhFactors,
                    selectedIndices = state.selectedBloodGroupRh?.let { setOf(rhFactors.indexOf(it)) } ?: emptySet(),
                    onSelectionChanged = { idxs ->
                        val rh = idxs.firstOrNull()?.let { rhFactors[it] }
                        onIntent(SelfDeclarationIntent.UpdateState { copy(selectedBloodGroupRh = rh) })
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
fun SelfDeclarationBloodScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationBloodScreen(
            state = SelfDeclarationUiState(selectedBloodGroupLetter = "O", selectedBloodGroupRh = "+"),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
