package com.tamin.taminhamrah.feature.girlSurvivor.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.girlSurvivor.ui.components.GirlSurvivorCommitmentStep
import com.tamin.taminhamrah.feature.girlSurvivor.ui.components.GirlSurvivorDetailsSkeleton
import com.tamin.taminhamrah.feature.girlSurvivor.ui.components.GirlSurvivorDetailsStep
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorEvent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorIntent
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorStep
import com.tamin.taminhamrah.feature.girlSurvivor.ui.contract.GirlSurvivorUiState
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.IconBox
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.girl_survivor_step_commitment
import taminx.core.core_ui.girl_survivor_step_details
import taminx.core.core_ui.girl_survivor_subtitle
import taminx.core.core_ui.girl_survivor_download_form
import taminx.core.core_ui.girl_survivor_send_request
import taminx.core.core_ui.girl_survivor_success_message
import taminx.core.core_ui.girl_survivor_success_title
import taminx.core.core_ui.girl_survivor_title
import taminx.core.core_ui.ic_request
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_user

@Composable
fun GirlSurvivorScreen(
    onBack: () -> Unit,
    viewModel: GirlSurvivorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    var showPdfViewer by remember { mutableStateOf(false) }

    HandleGirlSurvivorEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBack,
        onOpenPdfViewer = { showPdfViewer = true },
    )

    GirlSurvivorContent(
        state = state,
        onBack = {
            if (state.currentStep == GirlSurvivorStep.Details) onBack()
            else viewModel.sendIntent(GirlSurvivorIntent.GoToPreviousStep)
        },
        onIntent = viewModel::sendIntent,
        onDownloadPdf = { showPdfViewer = true },
    )

    if (showPdfViewer) {
        TaminPdfViewer(
            fileName = "girl_survivor_commitment_${state.nationalId}.pdf",
            pdf = state.viewerPdf,
            downloadFailed = state.viewerDownloadFailed,
            onRequestDownload = { viewModel.sendIntent(GirlSurvivorIntent.RetryPdfDownload) },
            onDismiss = {
                showPdfViewer = false
                viewModel.sendIntent(GirlSurvivorIntent.DismissPdfViewer)
            },
            title = stringResource(Res.string.girl_survivor_title),
        )
    }

    if (state.showSuccessDialog) {
        GirlSurvivorSuccessDialog(
            onConfirm = { viewModel.sendIntent(GirlSurvivorIntent.DismissSuccessDialog) },
        )
    }
}

@Composable
private fun HandleGirlSurvivorEvents(
    events: Flow<GirlSurvivorEvent>,
    onShowToast: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onOpenPdfViewer: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is GirlSurvivorEvent.ShowToast -> onShowToast(event.message)
            GirlSurvivorEvent.NavigateBack -> onNavigateBack()
            GirlSurvivorEvent.OpenPdfViewer -> onOpenPdfViewer()
        }
    }
}

@Composable
private fun GirlSurvivorContent(
    state: GirlSurvivorUiState,
    onBack: () -> Unit,
    onIntent: (GirlSurvivorIntent) -> Unit,
    onDownloadPdf: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    val headerBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    val steps = remember(state.currentStep) {
        persistentListOf(
            StepIndicatorModel(
                title = "", // filled below via stringResource in composable scope
                stepNumber = "۱",
                state = when (state.currentStep) {
                    GirlSurvivorStep.Details -> StepState.Active
                    GirlSurvivorStep.Commitment -> StepState.Completed
                },
            ),
            StepIndicatorModel(
                title = "",
                stepNumber = "۲",
                state = when (state.currentStep) {
                    GirlSurvivorStep.Details -> StepState.Inactive
                    GirlSurvivorStep.Commitment -> StepState.Active
                },
            ),
        )
    }
    val step1Title = stringResource(Res.string.girl_survivor_step_details)
    val step2Title = stringResource(Res.string.girl_survivor_step_commitment)
    val resolvedSteps = remember(state.currentStep, step1Title, step2Title) {
        persistentListOf(
            steps[0].copy(title = step1Title),
            steps[1].copy(title = step2Title),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.girl_survivor_title),
                background = headerBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
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
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_request))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.girl_survivor_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle,
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (!state.isProfileLoading) {
                GirlSurvivorBottomBar(
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
                steps = resolvedSteps,
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
                    if (targetState == GirlSurvivorStep.Commitment) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "girlSurvivorStep",
            ) { step ->
                when (step) {
                    GirlSurvivorStep.Details -> {
                        if (state.isProfileLoading) {
                            GirlSurvivorDetailsSkeleton()
                        } else {
                            GirlSurvivorDetailsStep(
                                state = state,
                                onIntent = onIntent,
                            )
                        }
                    }
                    GirlSurvivorStep.Commitment -> GirlSurvivorCommitmentStep(
                        state = state,
                        onIntent = onIntent,
                        onDownloadPdf = onDownloadPdf,
                    )
                }
            }
        }
    }
}

@Composable
private fun GirlSurvivorBottomBar(
    state: GirlSurvivorUiState,
    onIntent: (GirlSurvivorIntent) -> Unit,
) {
    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.currentStep) {
            GirlSurvivorStep.Details -> {
                LoadingButton(
                    text = stringResource(Res.string.girl_survivor_download_form),
                    onClick = { onIntent(GirlSurvivorIntent.DownloadAndViewForm) },
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }
            GirlSurvivorStep.Commitment -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    LoadingButton(
                        text = stringResource(Res.string.girl_survivor_send_request),
                        onClick = { onIntent(GirlSurvivorIntent.SubmitRequest) },
                        isLoading = state.isSubmitting,
                        enabled = state.isPdfConfirmed,
                        modifier = Modifier.weight(1f),
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = { onIntent(GirlSurvivorIntent.GoToPreviousStep) },
                        bordered = true,
                    )
                }
            }
        }
    }
}

@Composable
private fun GirlSurvivorSuccessDialog(onConfirm: () -> Unit) {
    val taminColors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.girl_survivor_success_title),
        description = stringResource(Res.string.girl_survivor_success_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.girl_survivor_success_title),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onConfirm,
        icon = Icons.Default.Check,
        iconTint = taminColors.greenText,
        iconBackground = taminColors.greenBg,
    )
}
