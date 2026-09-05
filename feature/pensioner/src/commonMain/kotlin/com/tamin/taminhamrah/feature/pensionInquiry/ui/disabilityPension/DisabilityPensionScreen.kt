package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionDependentsStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionRulesDialog
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionTermsStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionEvent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminHeroStepProgress
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.close_content_description
import taminx.core.core_ui.disability_pension_next_step
import taminx.core.core_ui.disability_pension_refresh_confirm_message
import taminx.core.core_ui.disability_pension_refresh_confirm_title
import taminx.core.core_ui.disability_pension_step_dependents_subtitle
import taminx.core.core_ui.disability_pension_step_dependents_title
import taminx.core.core_ui.disability_pension_step_subtitle
import taminx.core.core_ui.disability_pension_step_terms_title
import taminx.core.core_ui.disability_pension_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross

private const val DISABILITY_PENSION_TOTAL_STEPS = 7

@Composable
fun DisabilityPensionScreen(
    onBack: () -> Unit,
    onNavigateToAddDependent: () -> Unit = {},
    viewModel: DisabilityPensionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshDependentsOnResume by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, state.currentStep, refreshDependentsOnResume) {
        val observer = LifecycleEventObserver { _, event ->
            if (
                event == Lifecycle.Event.ON_RESUME &&
                refreshDependentsOnResume &&
                state.currentStep == DisabilityPensionStep.Dependents
            ) {
                refreshDependentsOnResume = false
                viewModel.sendIntent(DisabilityPensionIntent.DependentsResumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HandleDisabilityPensionEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateToAddDependent = {
            refreshDependentsOnResume = true
            onNavigateToAddDependent()
        },
    )

    DisabilityPensionContent(
        state = state,
        onBack = {
            if (state.currentStep == DisabilityPensionStep.Terms) {
                onBack()
            } else {
                viewModel.sendIntent(DisabilityPensionIntent.PreviousStepClicked)
            }
        },
        onIntent = viewModel::sendIntent,
    )

    if (state.showRules) {
        DisabilityPensionRulesDialog(
            onDismiss = { viewModel.sendIntent(DisabilityPensionIntent.DismissRules) },
        )
    }

    if (state.showRefreshConfirmDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.disability_pension_refresh_confirm_title),
            description = stringResource(Res.string.disability_pension_refresh_confirm_message),
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.action_confirm),
                    onClick = { viewModel.sendIntent(DisabilityPensionIntent.ConfirmRefreshDependents) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.action_cancel),
                    onClick = { viewModel.sendIntent(DisabilityPensionIntent.DismissRefreshConfirm) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            onDismissRequest = { viewModel.sendIntent(DisabilityPensionIntent.DismissRefreshConfirm) },
        )
    }
}

@Composable
private fun HandleDisabilityPensionEvents(
    events: Flow<DisabilityPensionEvent>,
    onShowToast: (String) -> Unit,
    onNavigateToAddDependent: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is DisabilityPensionEvent.ShowToast -> onShowToast(event.message)
            DisabilityPensionEvent.NavigateToAddDependent -> onNavigateToAddDependent()
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
    val termsTitle = stringResource(Res.string.disability_pension_step_terms_title)
    val dependentsTitle = stringResource(Res.string.disability_pension_step_dependents_title)
    val termsSubtitle = stringResource(Res.string.disability_pension_step_subtitle)
    val dependentsSubtitle = stringResource(Res.string.disability_pension_step_dependents_subtitle)
    val currentStepIndex = state.currentStep.ordinal + 1
    val stepTitle = when (state.currentStep) {
        DisabilityPensionStep.Terms -> termsTitle
        DisabilityPensionStep.Dependents -> dependentsTitle
    }
    val stepSubtitle = when (state.currentStep) {
        DisabilityPensionStep.Terms -> termsSubtitle
        DisabilityPensionStep.Dependents -> dependentsSubtitle
    }

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
                        currentStep = currentStepIndex,
                        totalSteps = DISABILITY_PENSION_TOTAL_STEPS,
                    )
                    Text(
                        text = stepSubtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = taminColors.textHeaderSubtitle,
                        modifier = Modifier.padding(top = Spacing.sm),
                    )
                }
            }
        },
        bottomBar = {
            DisabilityPensionBottomBar(state = state, onIntent = onIntent)
        },
    ) { padding ->
        AnimatedContent(
            targetState = state.currentStep,
            modifier = Modifier.fillMaxSize().padding(padding),
            transitionSpec = {
                val forward = targetState.ordinal > initialState.ordinal
                if (forward) {
                    slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
                } else {
                    slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
                }
            },
            label = "disabilityPensionStep",
        ) { step ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            ) {
                when (step) {
                    DisabilityPensionStep.Terms -> DisabilityPensionTermsStep(
                        state = state,
                        onTermsAcceptedChange = { onIntent(DisabilityPensionIntent.TermsAcceptedChanged(it)) },
                        onShowRules = { onIntent(DisabilityPensionIntent.ShowRulesClicked) },
                    )
                    DisabilityPensionStep.Dependents -> DisabilityPensionDependentsStep(
                        state = state,
                        onIntent = onIntent,
                    )
                }
            }
        }
    }
}

@Composable
private fun DisabilityPensionBottomBar(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
) {
    when (state.currentStep) {
        DisabilityPensionStep.Terms -> {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.disability_pension_next_step),
                onPrimaryClick = { onIntent(DisabilityPensionIntent.NextStepClicked) },
            )
        }
        DisabilityPensionStep.Dependents -> {
            TaminBottomBar(
                modifier = Modifier.navigationBarsPadding().imePadding(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = { onIntent(DisabilityPensionIntent.PreviousStepClicked) },
                    )
                    LoadingButton(
                        text = stringResource(Res.string.disability_pension_next_step),
                        onClick = { onIntent(DisabilityPensionIntent.NextStepClicked) },
                        enabled = !state.isRefreshingDependents && !state.isDependentsLoading,
                        isLoading = state.isRefreshingDependents,
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
