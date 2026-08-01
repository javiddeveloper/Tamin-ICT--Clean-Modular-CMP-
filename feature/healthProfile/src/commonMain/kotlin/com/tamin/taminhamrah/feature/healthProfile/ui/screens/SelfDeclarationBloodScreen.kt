package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthProfileErrorWrapper
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.BloodGroupStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.feature.healthProfile.ui.mapper.bloodGroupLetters
import com.tamin.taminhamrah.feature.healthProfile.ui.mapper.findBloodGroupId
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.Res
import taminx.feature.healthprofile.generated.resources.health_allergy_unknows
import taminx.feature.healthprofile.generated.resources.health_blood_blood_group_type
import taminx.feature.healthprofile.generated.resources.health_blood_choose_your_blood_group
import taminx.feature.healthprofile.generated.resources.health_blood_choose_your_blood_group_first
import taminx.feature.healthprofile.generated.resources.health_blood_i_dont_know_my_blood_group
import taminx.feature.healthprofile.generated.resources.health_blood_rh_factor
import taminx.feature.healthprofile.generated.resources.health_btn_next_step
import taminx.feature.healthprofile.generated.resources.health_btn_prev_step
import taminx.feature.healthprofile.generated.resources.health_type_blood_group
import taminx.feature.healthprofile.generated.resources.i_dont_know
import taminx.feature.healthprofile.generated.resources.i_dont_knoww

@Composable
fun SelfDeclarationBloodScreen(
    state: BloodGroupStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    bloodGroupOptions: List<LookupItemPR>,
    isLoading: Boolean = false,
    error: String? = null
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val bloodGroupLetters = remember(bloodGroupOptions) { bloodGroupLetters(bloodGroupOptions) }
    val chipsAlpha = if (state.isBloodGroupUnknown) 0.5f else 1f

    val isNextEnabled = state.isBloodGroupUnknown || (state.selectedBloodGroupLetter != null && state.selectedBloodGroupRh != null)

    val unknownText = stringResource(Res.string.health_allergy_unknows)
    val dontKnowText = stringResource(Res.string.i_dont_know)
    val dontKnowTextt = stringResource(Res.string.i_dont_knoww)

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_type_blood_group),
                currentStep = 6,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.LIFESTYLE)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        HealthProfileErrorWrapper(
            isLoading = isLoading,
            error = error,
            onRetry = { onIntent(HealthProfileIntent.RetryStep) },
            modifier = Modifier.padding(paddingValues),
            shimmerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(taminColors.bgPage)
                ) {
                    BloodShimmerSkeleton()
                }
            }
        ) {
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
                // Blood Droplet Graphic
            BloodDropletGraphic(
                selectedLetter = state.selectedBloodGroupLetter,
                selectedRh = state.selectedBloodGroupRh,
                isUnknown = state.isBloodGroupUnknown,
                modifier = Modifier.padding(vertical = 12.dp),
            )

            TaminText(
                text = stringResource(Res.string.health_blood_choose_your_blood_group),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = taminColors.textPrimary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            TaminText(
                text = stringResource(Res.string.health_blood_choose_your_blood_group_first),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = taminColors.textSecondary
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(6.dp))

            TaminText(
                text = stringResource(Res.string.health_blood_blood_group_type),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = taminColors.textTertiary,
                modifier = Modifier.align(Alignment.Start)
            )

            BloodGroupChipsRow(
                letters = bloodGroupLetters,
                selectedLetter = state.selectedBloodGroupLetter,
                enabled = !state.isBloodGroupUnknown,
                modifier = Modifier.alpha(chipsAlpha),
                onLetterSelected = { letter ->
                    onIntent(
                        HealthProfileIntent.UpdateBloodGroup(
                            state.copy(
                                selectedBloodGroupLetter = letter,
                                selectedBloodGroupId = findBloodGroupId(bloodGroupOptions, letter, state.selectedBloodGroupRh)
                            )
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TaminText(
                text = stringResource(Res.string.health_blood_rh_factor),
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
                                selectedBloodGroupRh = rh,
                                selectedBloodGroupId = findBloodGroupId(bloodGroupOptions, state.selectedBloodGroupLetter, rh)
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
                                it.label.contains(unknownText) ||
                                    it.label.contains(dontKnowText) ||
                                    it.label.contains(dontKnowTextt)
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
                    text = stringResource(Res.string.health_blood_i_dont_know_my_blood_group),
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

