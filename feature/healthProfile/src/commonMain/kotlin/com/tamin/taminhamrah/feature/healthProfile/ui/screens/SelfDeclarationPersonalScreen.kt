package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PersonalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.feature.healthProfile.ui.model.LookupItemPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationPersonalScreen(
    state: PersonalStepState,
    maritalStatusOptions: List<LookupItemPR>,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isNextEnabled = state.maritalStatusId != null && state.job.isNotEmpty()

    // Labels to show in SegmentedControl — fall back to API options if available,
    // otherwise show nothing (UI degrades gracefully until lookup loads)
    val maritalLabels = maritalStatusOptions.map { it.label }

    // Find which index in the chip list matches the currently selected ID
    val selectedMaritalIndex = maritalStatusOptions.indexOfFirst { it.id == state.maritalStatusId }

    Scaffold(
        topBar = {
            HealthTopAppBar(
                title = stringResource(Res.string.health_personal_title),
                currentStep = 2,
                totalSteps = 10,
                onBackClicked = onBackClicked
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.CONTACT)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
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
            // Header Step Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TaminText(
                    text = stringResource(Res.string.health_step_2_of_10),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = taminColors.blueText
                    )
                )
            }

            TaminText(
                text = stringResource(Res.string.health_personal_heading),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = stringResource(Res.string.health_personal_desc),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            // Marital Status — driven by API lookup list
            if (maritalLabels.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TaminText(
                        text = stringResource(Res.string.health_personal_marital_status),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = taminColors.textPrimary
                        )
                    )
                    SegmentedControl(
                        options = maritalLabels,
                        selectedIndex = if (selectedMaritalIndex >= 0) selectedMaritalIndex else 0,
                        onOptionSelected = { idx ->
                            val selected = maritalStatusOptions[idx]
                            onIntent(
                                HealthProfileIntent.UpdatePersonal(
                                    state.copy(
                                        maritalStatusId = selected.id,
                                        maritalStatusLabel = selected.label
                                    )
                                )
                            )
                        }
                    )
                }

                // Pre-select first option only when nothing has been selected yet
                LaunchedEffect(maritalStatusOptions) {
                    if (state.maritalStatusId == null && maritalStatusOptions.isNotEmpty()) {
                        val first = maritalStatusOptions.first()
                        onIntent(
                            HealthProfileIntent.UpdatePersonal(
                                state.copy(
                                    maritalStatusId = first.id,
                                    maritalStatusLabel = first.label
                                )
                            )
                        )
                    }
                }
            }

            StyledTextField(
                value = state.job,
                onValueChange = { jobStr ->
                    onIntent(HealthProfileIntent.UpdatePersonal(state.copy(job = jobStr)))
                },
                label = stringResource(Res.string.health_personal_job_label),
                placeholder = stringResource(Res.string.health_personal_job_placeholder)
            )

            StyledTextField(
                value = state.citizenship,
                onValueChange = { cit ->
                    onIntent(HealthProfileIntent.UpdatePersonal(state.copy(citizenship = cit)))
                },
                label = stringResource(Res.string.health_personal_citizenship_label),
                placeholder = stringResource(Res.string.health_personal_citizenship_placeholder)
            )

            StyledTextField(
                value = state.nationality,
                onValueChange = { nat ->
                    onIntent(HealthProfileIntent.UpdatePersonal(state.copy(nationality = nat)))
                },
                label = stringResource(Res.string.health_personal_nationality_label),
                placeholder = stringResource(Res.string.health_personal_nationality_placeholder)
            )
            Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
        }
    }
}

@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationPersonalScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationPersonalScreen(
            state = PersonalStepState(maritalStatusId = 1, maritalStatusLabel = "مجرد", job = "برنامه‌نویس"),
            maritalStatusOptions = listOf(
                LookupItemPR(1, "مجرد"),
                LookupItemPR(2, "متأهل"),
                LookupItemPR(3, "مطلقه"),
                LookupItemPR(4, "بیوه")
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
