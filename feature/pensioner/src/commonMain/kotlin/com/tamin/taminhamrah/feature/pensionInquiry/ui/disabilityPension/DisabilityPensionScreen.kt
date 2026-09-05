package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionRulesDialog
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionTermsStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminHeroStepProgress
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.close_content_description
import taminx.core.core_ui.disability_pension_next_step
import taminx.core.core_ui.disability_pension_step_subtitle
import taminx.core.core_ui.disability_pension_step_terms_title
import taminx.core.core_ui.disability_pension_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_cross

private const val DISABILITY_PENSION_TOTAL_STEPS = 7
private const val DISABILITY_PENSION_TERMS_STEP = 1

@Composable
fun DisabilityPensionScreen(
    onBack: () -> Unit,
    viewModel: DisabilityPensionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current

    HandleDisabilityPensionEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
    )

    DisabilityPensionContent(
        state = state,
        onBack = onBack,
        onIntent = viewModel::sendIntent,
    )

    if (state.showRules) {
        DisabilityPensionRulesDialog(
            onDismiss = { viewModel.sendIntent(DisabilityPensionIntent.DismissRules) },
        )
    }
}

@Composable
private fun HandleDisabilityPensionEvents(
    events: Flow<DisabilityPensionEvent>,
    onShowToast: (String) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is DisabilityPensionEvent.ShowToast -> onShowToast(event.message)
        }
    }
}

@Composable
private fun DisabilityPensionContent(
    state: DisabilityPensionUiState,
    onBack: () -> Unit,
    onIntent: (DisabilityPensionIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val headerBrush = Brush.horizontalGradient(taminColors.profileGradientStops)
    val stepTitle = stringResource(Res.string.disability_pension_step_terms_title)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.disability_pension_title),
                background = headerBrush,
                bottomPadding = Spacing.none,
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.back_content_description),
                        onClick = onBack,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = stringResource(Res.string.close_content_description),
                        onClick = onBack,
                        bordered = true,
                    )
                },
            ) {
                Column(
                    modifier = Modifier.padding(vertical = Spacing.lg),
                ) {
                    TaminHeroStepProgress(
                        stepTitle = stepTitle,
                        currentStep = DISABILITY_PENSION_TERMS_STEP,
                        totalSteps = DISABILITY_PENSION_TOTAL_STEPS,
                    )
                    Text(
                        text = stringResource(Res.string.disability_pension_step_subtitle),
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textHeaderSubtitle,
                        modifier = Modifier.padding(top = Spacing.sm),
                    )
                }
            }
        },
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.disability_pension_next_step),
                onPrimaryClick = { onIntent(DisabilityPensionIntent.NextStepClicked) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
        ) {
            DisabilityPensionTermsStep(
                state = state,
                onTermsAcceptedChange = { onIntent(DisabilityPensionIntent.TermsAcceptedChanged(it)) },
                onShowRules = { onIntent(DisabilityPensionIntent.ShowRulesClicked) },
            )
        }
    }
}
