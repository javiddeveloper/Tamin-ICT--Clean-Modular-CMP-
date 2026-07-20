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
fun SelfDeclarationPersonalScreen(
    state: SelfDeclarationUiState,
    onIntent: (SelfDeclarationIntent) -> Unit,
    onBackClicked: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    val isNextEnabled = state.maritalStatus.isNotEmpty() && state.job.isNotEmpty()

    Scaffold(
        topBar = {
            HealthTopAppBar(onBackClicked = onBackClicked)
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = "مرحلهٔ بعدی",
                primaryEnabled = isNextEnabled,
                onPrimaryClick = { onIntent(SelfDeclarationIntent.ChangeStep(SelfDeclarationStep.CONTACT)) },
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
            HealthProgressBar(currentStep = 2, totalSteps = 10)

            TaminText(
                text = "اطلاعات تکمیلی فردی",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = taminColors.textPrimary
                )
            )

            TaminText(
                text = "لطفاً وضعیت تاهل و شغل خود را به همراه اطلاعات تابعیت وارد کنید.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = taminColors.textTertiary,
                    lineHeight = 22.sp
                )
            )

            // Marital status
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TaminText(
                    text = "وضعیت تأهل",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = taminColors.textPrimary
                    )
                )
                val maritalOptions = listOf("مجرد", "متأهل", "مطلقه", "همسر فوت شده")
                val maritalIndex = maritalOptions.indexOf(state.maritalStatus)
                SegmentedControl(
                    options = maritalOptions,
                    selectedIndex = if (maritalIndex >= 0) maritalIndex else 0,
                    onOptionSelected = { idx ->
                        onIntent(SelfDeclarationIntent.UpdateState { copy(maritalStatus = maritalOptions[idx]) })
                    }
                )
            }
            // Initialize if empty
            LaunchedEffect(state.maritalStatus) {
                if (state.maritalStatus.isEmpty()) {
                    onIntent(SelfDeclarationIntent.UpdateState { copy(maritalStatus = "مجرد") })
                }
            }

            StyledTextField(
                value = state.job,
                onValueChange = { jobStr ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(job = jobStr) })
                },
                label = "شغل / نوع فعالیت",
                placeholder = "مثلاً کارمند، آزاد و..."
            )

            StyledTextField(
                value = state.citizenship,
                onValueChange = { cit ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(citizenship = cit) })
                },
                label = "تابعیت",
                placeholder = "وارد کنید"
            )

            StyledTextField(
                value = state.nationality,
                onValueChange = { nat ->
                    onIntent(SelfDeclarationIntent.UpdateState { copy(nationality = nat) })
                },
                label = "ملیت",
                placeholder = "مثلاً ایرانی"
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
            state = SelfDeclarationUiState(maritalStatus = "مجرد", job = "برنامه‌نویس"),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
