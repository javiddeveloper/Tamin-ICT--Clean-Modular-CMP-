package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.QuestionMark
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
import com.tamin.taminhamrah.ui.theme.Spacing

@Composable
fun SelfDeclarationBloodScreen(
    state: BloodGroupStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    bloodGroupOptions: List<LookupItemPR>
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val rhFactors = listOf("+", "-")

    val isNextEnabled = state.isBloodGroupUnknown || (state.selectedBloodGroupLetter != null && state.selectedBloodGroupRh != null)

    LaunchedEffect(Unit) {
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "Screen opened. Initial state:")
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "selectedBloodGroupId: ${state.selectedBloodGroupId}")
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "selectedBloodGroupLetter: ${state.selectedBloodGroupLetter}")
        com.tamin.taminhamrah.util.Logger.d("BloodGroupScreen", "selectedBloodGroupRh: ${state.selectedBloodGroupRh}")
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
                selectedRh = state.selectedBloodGroupRh,
                isUnknown = state.isBloodGroupUnknown,
                modifier = Modifier.padding(vertical = 12.dp),
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
                        val newUnknown = !state.isBloodGroupUnknown
                        onIntent(
                            HealthProfileIntent.UpdateBloodGroup(
                                state.copy(
                                    selectedBloodGroupRh = if (newUnknown) null else state.selectedBloodGroupRh,
                                    selectedBloodGroupId = letter?.id,
                                    selectedBloodGroupLetter = letter?.label
                                )
                            )
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TaminText(
                text = "فاکتور Rh",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textTertiary,
                modifier = Modifier.align(Alignment.Start)
            )

            RhFactor(
                selectedRh = state.selectedBloodGroupRh,
                enabled = !state.isBloodGroupUnknown,
                onRhSelected = { rh ->
                    onIntent(
                        HealthProfileIntent.UpdateBloodGroup(
                            state.copy(
                                selectedBloodGroupRh = rh
                            )
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(Spacing.xxl))
                    .background(if (state.isBloodGroupUnknown) taminColors.blueBg else taminColors.bgSurface)
                    .border(
                        width = 1.dp,
                        color = if (state.isBloodGroupUnknown) {
                            taminColors.blueText
                        } else {
                            taminColors.border
                        },
                        shape = RoundedCornerShape(Spacing.xxl)
                    )
                    .clickable {
                        val newUnknown = !state.isBloodGroupUnknown

                        val newId = if (newUnknown) {
                            bloodGroupOptions.find {
                                it.label.contains("نامشخص") ||
                                    it.label.contains("نمی دانم") ||
                                    it.label.contains("نمیدانم")
                            }?.id
                        } else {
                            null
                        }

                        onIntent(
                            HealthProfileIntent.UpdateBloodGroup(
                                state.copy(
                                    isBloodGroupUnknown = newUnknown,
                                    selectedBloodGroupRh = if (newUnknown) null else state.selectedBloodGroupRh,
                                    selectedBloodGroupLetter = if (newUnknown) null else state.selectedBloodGroupLetter,
                                    selectedBloodGroupId = newId
                                )
                            )
                        )
                    }
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TaminText(
                    modifier = Modifier.padding(start = 4.dp),
                    text = "گروه خونی خود را نمی‌دانم",
                    style = MaterialTheme.typography.bodySmall,
                    color = taminColors.textSecondary,
                )
                Icon(
                    imageVector = Icons.Outlined.Cancel,
                    contentDescription = null,
                    tint = taminColors.dangerText.copy(alpha = 0.8f)
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
            state = BloodGroupStepState(selectedBloodGroupLetter = "O", selectedBloodGroupRh = "-"),
            onIntent = {},
            onBackClicked = {},
            bloodGroupOptions = emptyList()
        )
    }
}

