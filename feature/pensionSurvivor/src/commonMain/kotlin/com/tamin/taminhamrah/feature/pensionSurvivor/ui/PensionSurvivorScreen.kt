package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.RulesStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminEmptyState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.pension_survivor_next_step
import taminx.core.core_ui.pension_survivor_rules_unavailable
import taminx.core.core_ui.pension_survivor_step_deceased
import taminx.core.core_ui.pension_survivor_step_final
import taminx.core.core_ui.pension_survivor_step_rules
import taminx.core.core_ui.pension_survivor_step_survivors
import taminx.core.core_ui.pension_survivor_title
import taminx.core.core_ui.pension_survivor_todo_step

@Composable
fun PensionSurvivorScreen(
    onBack: () -> Unit,
    viewModel: PensionSurvivorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val rulesUnavailableMessage = stringResource(Res.string.pension_survivor_rules_unavailable)

    HandlePensionSurvivorEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBack,
        onOpenRulesDocument = {
            // TODO(rules-url): replace this toast with the legacy rules document URL/PDF when found.
            toaster.error(rulesUnavailableMessage)
        },
    )

    PensionSurvivorContent(
        state = state,
        onBack = {
            if (state.currentStep == PensionSurvivorStep.Rules) onBack()
            else viewModel.sendIntent(PensionSurvivorIntent.PreviousStep)
        },
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandlePensionSurvivorEvents(
    events: Flow<PensionSurvivorEvent>,
    onShowToast: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenRulesDocument: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is PensionSurvivorEvent.ShowToast -> onShowToast(event.message)
            PensionSurvivorEvent.NavigateBack -> onNavigateBack()
            PensionSurvivorEvent.OpenRulesDocument -> onOpenRulesDocument()
            PensionSurvivorEvent.OpenPdfViewer -> Unit
            is PensionSurvivorEvent.NavigateToSurvivorInfo -> Unit
        }
    }
}

@Composable
private fun PensionSurvivorContent(
    state: PensionSurvivorUiState,
    onBack: () -> Unit,
    onIntent: (PensionSurvivorIntent) -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val step1Title = stringResource(Res.string.pension_survivor_step_rules)
    val step2Title = stringResource(Res.string.pension_survivor_step_deceased)
    val step3Title = stringResource(Res.string.pension_survivor_step_survivors)
    val step4Title = stringResource(Res.string.pension_survivor_step_final)
    val currentStepTitle = when (state.currentStep) {
        PensionSurvivorStep.Rules -> step1Title
        PensionSurvivorStep.Deceased -> step2Title
        PensionSurvivorStep.Survivors -> step3Title
        PensionSurvivorStep.Final -> step4Title
    }
    val steps = remember(state.currentStep, step1Title, step2Title, step3Title, step4Title) {
        persistentListOf(
            StepIndicatorModel(
                title = step1Title,
                stepNumber = "۱",
                state = when (state.currentStep) {
                    PensionSurvivorStep.Rules -> StepState.Active
                    PensionSurvivorStep.Deceased,
                    PensionSurvivorStep.Survivors,
                    PensionSurvivorStep.Final -> StepState.Completed
                },
            ),
            StepIndicatorModel(
                title = step2Title,
                stepNumber = "۲",
                state = when (state.currentStep) {
                    PensionSurvivorStep.Rules -> StepState.Inactive
                    PensionSurvivorStep.Deceased -> StepState.Active
                    PensionSurvivorStep.Survivors,
                    PensionSurvivorStep.Final -> StepState.Completed
                },
            ),
            StepIndicatorModel(
                title = step3Title,
                stepNumber = "۳",
                state = when (state.currentStep) {
                    PensionSurvivorStep.Rules,
                    PensionSurvivorStep.Deceased -> StepState.Inactive
                    PensionSurvivorStep.Survivors -> StepState.Active
                    PensionSurvivorStep.Final -> StepState.Completed
                },
            ),
            StepIndicatorModel(
                title = step4Title,
                stepNumber = "۴",
                state = when (state.currentStep) {
                    PensionSurvivorStep.Final -> StepState.Active
                    else -> StepState.Inactive
                },
            ),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.pension_survivor_title),
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_cross),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true,
                    )
                },
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = HeaderDecoration.circleSize,
                        xOffset = HeaderDecoration.circleXOffset,
                        yOffset = HeaderDecoration.circleYOffset,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_request))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = currentStepTitle,
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle,
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (!state.isProfileLoading) {
                PensionSurvivorBottomBar(
                    state = state,
                    onIntent = onIntent,
                )
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            StepIndicator(
                steps = steps,
                modifier = Modifier.padding(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.md,
                    bottom = Spacing.md,
                ),
            )

            AnimatedContent(
                targetState = state.currentStep,
                modifier = Modifier.weight(1f),
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
                label = "pensionSurvivorStep",
            ) { step ->
                when (step) {
                    PensionSurvivorStep.Rules -> RulesStep(
                        state = state,
                        onIntent = onIntent,
                    )
                    PensionSurvivorStep.Deceased -> StepPlaceholder(stepTitle = step2Title)
                    PensionSurvivorStep.Survivors -> StepPlaceholder(stepTitle = step3Title)
                    PensionSurvivorStep.Final -> StepPlaceholder(stepTitle = step4Title)
                }
            }
        }
    }
}

@Composable
private fun PensionSurvivorBottomBar(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
) {
    val nextEnabled = when (state.currentStep) {
        PensionSurvivorStep.Rules -> state.commitmentAccepted && !state.isProfileLoading
        PensionSurvivorStep.Deceased -> state.deceasedInfo != null && !state.isLoading
        PensionSurvivorStep.Survivors -> !state.isLoading
        PensionSurvivorStep.Final -> false
    }

    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.currentStep) {
            PensionSurvivorStep.Rules -> {
                LoadingButton(
                    text = stringResource(Res.string.pension_survivor_next_step),
                    onClick = { onIntent(PensionSurvivorIntent.NextStep) },
                    enabled = nextEnabled,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }

            else -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    LoadingButton(
                        text = stringResource(Res.string.pension_survivor_next_step),
                        onClick = { onIntent(PensionSurvivorIntent.NextStep) },
                        enabled = nextEnabled,
                        isLoading = state.isLoading,
                        modifier = Modifier.weight(1f),
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = { onIntent(PensionSurvivorIntent.PreviousStep) },
                        bordered = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPlaceholder(stepTitle: String) {
    TaminEmptyState(
        message = stringResource(Res.string.pension_survivor_todo_step, stepTitle),
        modifier = Modifier.fillMaxSize(),
    )
}
