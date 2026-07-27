package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetConfig
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetItem
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.BottomSheetType
import com.tamin.taminhamrah.feature.healthProfile.ui.components.bottomSheet.HealthBottomSheet
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

    var showMaritalBottomSheet by remember { mutableStateOf(false) }


    val selectedMaritalLabel = state.maritalStatusLabel.ifEmpty {
        maritalStatusOptions.firstOrNull { it.id == state.maritalStatusId }?.label ?: ""
    }

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

            // Marital Status — Clickable field opening HealthBottomSheet
            StyledTextField(
                value = selectedMaritalLabel,
                onValueChange = {},
                label = stringResource(Res.string.health_personal_marital_status),
                placeholder = stringResource(Res.string.choose),
                trailingIcon = Icons.Default.KeyboardArrowDown,
                readOnly = true,
                onClick = { showMaritalBottomSheet = true }
            )

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

    if (showMaritalBottomSheet) {
        val bottomSheetItems = maritalStatusOptions.map { option ->
            BottomSheetItem(
                id = option.id,
                title = option.label,
                isSelected = option.id == state.maritalStatusId
            )
        }

        HealthBottomSheet(
            config = BottomSheetConfig(
                title = stringResource(Res.string.health_personal_marital_status),
                subtitle = "در قسمت زیر می‌توانید وضعیت تأهل خود را انتخاب کنید",
                type = BottomSheetType.MARITAL_STATUS,
                singleSelection = true,
                items = bottomSheetItems
            ),
            onDismissRequest = { showMaritalBottomSheet = false },
            onSubmit = { result ->
                val selectedId = result.selectedItemIds.firstOrNull()
                val selectedOption = maritalStatusOptions.firstOrNull { it.id == selectedId }
                if (selectedOption != null) {
                    onIntent(
                        HealthProfileIntent.UpdatePersonal(
                            state.copy(
                                maritalStatusId = selectedOption.id,
                                maritalStatusLabel = selectedOption.label
                            )
                        )
                    )
                }
                showMaritalBottomSheet = false
            }
        )
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
                LookupItemPR(4, "همسر فوت شده")
            ),
            onIntent = {},
            onBackClicked = {}
        )
    }
}

