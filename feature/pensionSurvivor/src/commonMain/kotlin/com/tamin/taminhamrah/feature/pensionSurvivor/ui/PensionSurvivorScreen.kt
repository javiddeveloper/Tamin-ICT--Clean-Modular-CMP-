package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.DeceasedStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.FinalStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.RulesStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.SurvivorsStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.model.personal.survivorDependent.SurvivorDependentPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
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
import taminx.core.core_ui.pension_survivor_final_submit_success_message
import taminx.core.core_ui.pension_survivor_final_submit_success_title
import taminx.core.core_ui.pension_survivor_rules_unavailable
import taminx.core.core_ui.pension_survivor_step_deceased
import taminx.core.core_ui.pension_survivor_step_final
import taminx.core.core_ui.pension_survivor_step_rules
import taminx.core.core_ui.pension_survivor_step_survivors
import taminx.core.core_ui.pension_survivor_title
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.upload_submit_final

@Composable
fun PensionSurvivorScreen(
    onBack: () -> Unit,
    onNavigateToSurvivorInfo: (SurvivorDependentPR, String) -> Unit,
    viewModel: PensionSurvivorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val rulesUnavailableMessage = stringResource(Res.string.pension_survivor_rules_unavailable)
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshSurvivorsOnResume by remember { mutableStateOf(false) }
    var showPdfViewer by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner, state.currentStep, refreshSurvivorsOnResume) {
        val observer = LifecycleEventObserver { _, event ->
            if (
                event == Lifecycle.Event.ON_RESUME &&
                refreshSurvivorsOnResume &&
                state.currentStep == PensionSurvivorStep.Survivors
            ) {
                refreshSurvivorsOnResume = false
                viewModel.sendIntent(PensionSurvivorIntent.RefreshSurvivors)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    HandlePensionSurvivorEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBack,
        onNavigateToSurvivorInfo = { survivor, deceasedNationalId ->
            refreshSurvivorsOnResume = true
            onNavigateToSurvivorInfo(survivor, deceasedNationalId)
        },
        onOpenRulesDocument = {
            // TODO(rules-url): replace this toast with the legacy rules document URL/PDF when found.
            toaster.error(rulesUnavailableMessage)
        },
        onOpenPdfViewer = { showPdfViewer = true },
    )

    PensionSurvivorContent(
        state = state,
        onBack = {
            if (state.currentStep == PensionSurvivorStep.Rules) onBack()
            else viewModel.sendIntent(PensionSurvivorIntent.PreviousStep)
        },
        onIntent = viewModel::sendIntent,
        onOpenFinalPdf = { showPdfViewer = true },
    )

    if (showPdfViewer) {
        TaminPdfViewer(
            fileName = "pension_survivor_final_${state.requestId ?: state.applicantNationalId}.pdf",
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { viewModel.sendIntent(PensionSurvivorIntent.RetryPdfDownload) },
            onDismiss = {
                showPdfViewer = false
                viewModel.sendIntent(PensionSurvivorIntent.DismissPdfViewer)
            },
            title = stringResource(Res.string.pension_survivor_title),
        )
    }

    if (state.showSuccessDialog) {
        PensionSurvivorSuccessDialog(
            onConfirm = { viewModel.sendIntent(PensionSurvivorIntent.DismissSuccessDialog) },
        )
    }
}

@Composable
private fun HandlePensionSurvivorEvents(
    events: Flow<PensionSurvivorEvent>,
    onShowToast: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSurvivorInfo: (SurvivorDependentPR, String) -> Unit,
    onOpenRulesDocument: () -> Unit,
    onOpenPdfViewer: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is PensionSurvivorEvent.ShowToast -> onShowToast(event.message)
            PensionSurvivorEvent.NavigateBack -> onNavigateBack()
            PensionSurvivorEvent.OpenRulesDocument -> onOpenRulesDocument()
            PensionSurvivorEvent.OpenPdfViewer -> onOpenPdfViewer()
            is PensionSurvivorEvent.NavigateToSurvivorInfo -> {
                onNavigateToSurvivorInfo(event.survivor, event.deceasedNationalId)
            }
        }
    }
}

@Composable
private fun PensionSurvivorContent(
    state: PensionSurvivorUiState,
    onBack: () -> Unit,
    onIntent: (PensionSurvivorIntent) -> Unit,
    onOpenFinalPdf: () -> Unit,
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
                    PensionSurvivorStep.Deceased -> DeceasedStep(
                        state = state,
                        onIntent = onIntent,
                    )
                    PensionSurvivorStep.Survivors -> SurvivorsStep(
                        state = state,
                        onIntent = onIntent,
                    )
                    PensionSurvivorStep.Final -> FinalStep(
                        state = state,
                        onIntent = onIntent,
                        onDownloadPdf = onOpenFinalPdf,
                    )
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
        PensionSurvivorStep.Final -> state.isPdfConfirmed && state.requestId != null && !state.isLoading
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
                        text = if (state.currentStep == PensionSurvivorStep.Final) {
                            stringResource(Res.string.upload_submit_final)
                        } else {
                            stringResource(Res.string.pension_survivor_next_step)
                        },
                        onClick = {
                            onIntent(
                                if (state.currentStep == PensionSurvivorStep.Final) {
                                    PensionSurvivorIntent.SubmitFinal
                                } else {
                                    PensionSurvivorIntent.NextStep
                                },
                            )
                        },
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
private fun PensionSurvivorSuccessDialog(onConfirm: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.pension_survivor_final_submit_success_title),
        description = stringResource(Res.string.pension_survivor_final_submit_success_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.action_confirm),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onConfirm,
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
    )
}
