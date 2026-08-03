package com.tamin.taminhamrah.feature.healthProfile.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.healthProfile.ui.components.*
import com.tamin.taminhamrah.feature.healthProfile.ui.components.HealthProfileErrorWrapper
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.HealthProfileIntent
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.PhysicalStepState
import com.tamin.taminhamrah.feature.healthProfile.ui.contract.SelfDeclarationStep
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import androidx.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import taminx.feature.healthprofile.generated.resources.*

@Composable
fun SelfDeclarationPhysicalScreen(
    state: PhysicalStepState,
    onIntent: (HealthProfileIntent) -> Unit,
    onBackClicked: () -> Unit,
    onCloseClicked: (() -> Unit)? = null,
    isLoading: Boolean = false,
    error: String? = null
) {
    val taminColors = LocalTaminColors.current
    val scrollState = rememberScrollState()

    // Calculate BMI if both height and weight are provided
    val bmiValue: Float? = if (state.height != null && state.weight != null && state.height > 0) {
        val heightInMeters = state.height / 100f
        state.weight / (heightInMeters * heightInMeters)
    } else null


    Scaffold(
        topBar = {
            HealthTopAppBar(
                currentStep = 5,
                totalSteps = 10,
                onBackClicked = onBackClicked,
                onCloseClicked = onCloseClicked,
                title = stringResource(Res.string.health_physical_title)
            )
        },
        bottomBar = {
            HealthIrritateNavigationBar(
                primaryText = stringResource(Res.string.health_btn_next_step),
                onPrimaryClick = { onIntent(HealthProfileIntent.ChangeStep(SelfDeclarationStep.BLOOD)) },
                secondaryText = stringResource(Res.string.health_btn_prev_step),
                onSecondaryClick = onBackClicked
            )
        }
    ) { paddingValues ->
        HealthProfileErrorWrapper(
            isLoading = isLoading,
            error = error,
            onRetry = { onIntent(HealthProfileIntent.RetryStep) },
            shimmerContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(taminColors.bgPage)
                ) {
                    PhysicalShimmerSkeleton()
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
                TaminText(
                    text = stringResource(Res.string.health_physical_desc),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = taminColors.textTertiary,
                        lineHeight = 22.sp
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                RulerPicker(
                    title = stringResource(Res.string.health_physical_height_label),
                    titleIconPainter = painterResource(Res.drawable.ic_physical_height),
                    titleIconBgColor = taminColors.blueBg,
                    titleIconTintColor = taminColors.blueText,
                    value = state.height,
                    onValueChange = { h ->
                        onIntent(HealthProfileIntent.UpdatePhysical(state.copy(height = h)))
                    },
                    range = 120..220,
                    unit = stringResource(Res.string.health_physical_unit_cm),
                    defaultPoint = 170
                )

                Spacer(modifier = Modifier.height(8.dp))

                RulerPicker(
                    title = stringResource(Res.string.health_physical_weight_label),
                    titleIconPainter = painterResource(Res.drawable.ic_weight),
                    titleIconBgColor = taminColors.teal.copy(alpha = 0.15f),
                    titleIconTintColor = taminColors.teal,
                    value = state.weight,
                    onValueChange = { w ->
                        onIntent(HealthProfileIntent.UpdatePhysical(state.copy(weight = w)))
                    },
                    range = 40..150,
                    unit = stringResource(Res.string.health_physical_unit_kg),
                    defaultPoint = 70,
                    accentColor = taminColors.teal
                )

                Spacer(modifier = Modifier.height(12.dp))

                BmiMeter(bmi = bmiValue)
                Spacer(modifier = Modifier.height(paddingValues.calculateBottomPadding()))
            }
        }
    }
}
@PreviewRtlTheme
@Preview
@Composable
fun SelfDeclarationPhysicalScreenPreview() {
    PreviewRtlThemeContent {
        SelfDeclarationPhysicalScreen(
            state = PhysicalStepState(height = 175, weight = 75),
            onIntent = {},
            onBackClicked = {}
        )
    }
}
