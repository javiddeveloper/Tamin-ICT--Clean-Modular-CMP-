package com.tamin.taminhamrah.feature.changemobile.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileEvent
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileIntent
import com.tamin.taminhamrah.feature.changemobile.ui.contract.ChangeMobileUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_mobile
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.profile_change_mobile
import taminx.core.core_ui.profile_change_mobile_subtitle

@Composable
fun ChangeMobileScreen(
    viewModel: ChangeMobileViewModel = koinViewModel(),
    onBack: () -> Unit
) {
    val taminColors = LocalTaminColors.current
    val uiStateState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val onIntent = remember(viewModel) {
        { intent: ChangeMobileIntent -> viewModel.sendIntent(intent) }
    }
    val currentStep by remember { derivedStateOf { uiStateState.value.currentStep } }
    val handleBack = remember(currentStep) {
        {
            if (currentStep == ChangeMobileStep.EnterMobile || currentStep == ChangeMobileStep.Success) {
                onBack()
            } else {
                onIntent(ChangeMobileIntent.BackToPreviousStep)
            }
        }
    }
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    HandleChangeMobileEvents(
        events = viewModel.events,
        onBack = onBack,
        snackbarHostState = snackbarHostState
    )

    ChangeMobileContent(
        uiStateState = uiStateState,
        currentStep = currentStep,
        onBack = handleBack,
        profileGradientBrush = profileGradientBrush,
        onIntent = onIntent,
        onNavigateBack = onBack,
        snackbarHostState = snackbarHostState
    )
}

@Composable
fun HandleChangeMobileEvents(
    events: Flow<ChangeMobileEvent>,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is ChangeMobileEvent.NavigateBack -> onBack()
            is ChangeMobileEvent.ShowError -> {
                snackbarHostState.showSnackbar(event.message)
            }
        }
    }
}

@Composable
fun ChangeMobileContent(
    uiStateState: State<ChangeMobileUiState>,
    currentStep: ChangeMobileStep,
    onBack: () -> Unit,
    profileGradientBrush: Brush,
    onIntent: (ChangeMobileIntent) -> Unit,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.profile_change_mobile),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = 40.dp,
                    bottomEnd = 40.dp
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = onBack,
                        bordered = true
                    )
                }
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_mobile))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.profile_change_mobile_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle
                        )
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .navigationBarsPadding()
                .imePadding()
        ) {
            val currentStepIndex = currentStep.index
            val steps = remember(currentStepIndex) {
                persistentListOf(
                    StepIndicatorModel(
                        title = "شماره جدید",
                        stepNumber = "۱",
                        state = when {
                            currentStepIndex == 0 -> StepState.Active
                            currentStepIndex > 0 -> StepState.Completed
                            else -> StepState.Inactive
                        }
                    ),
                    StepIndicatorModel(
                        title = "تأییدیه",
                        stepNumber = "۲",
                        state = when {
                            currentStepIndex == 1 -> StepState.Active
                            currentStepIndex > 1 -> StepState.Completed
                            else -> StepState.Inactive
                        }
                    ),
                    StepIndicatorModel(
                        title = "تکمیل",
                        stepNumber = "۳",
                        state = when {
                            currentStepIndex == 2 -> StepState.Completed
                            else -> StepState.Inactive
                        }
                    )
                )
            }

            StepIndicator(
                steps = steps,
                modifier = Modifier.padding(
                    start = Spacing.lg,
                    end = Spacing.lg,
                    top = Spacing.md,
                    bottom = Spacing.md
                )
            )

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.index > initialState.index) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "StepTransition",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    ChangeMobileStep.EnterMobile -> {
                        EnterMobileStep(
                            uiState = uiStateState.value,
                            onIntent = onIntent
                        )
                    }

                    ChangeMobileStep.VerifyOtp -> {
                        VerifyOtpStep(uiStateState.value, onIntent)
                    }

                    ChangeMobileStep.Success -> {
                        SuccessStep(uiStateState.value, onNavigateBack)
                    }
                }
            }
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ChangeMobileScreenPreview() {
    PreviewRtlThemeContent {
        val uiStateState = remember { mutableStateOf(ChangeMobileUiState()) }
        val taminColors = LocalTaminColors.current
        val profileGradientBrush = remember(taminColors.profileGradientStops) {
            Brush.horizontalGradient(taminColors.profileGradientStops)
        }
        ChangeMobileContent(
            uiStateState = uiStateState,
            currentStep = uiStateState.value.currentStep,
            onBack = {},
            profileGradientBrush = profileGradientBrush,
            onIntent = {},
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ChangeMobileScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        val uiStateState = remember { mutableStateOf(ChangeMobileUiState()) }
        val taminColors = LocalTaminColors.current
        val profileGradientBrush = remember(taminColors.profileGradientStops) {
            Brush.horizontalGradient(taminColors.profileGradientStops)
        }
        ChangeMobileContent(
            uiStateState = uiStateState,
            currentStep = uiStateState.value.currentStep,
            onBack = {},
            profileGradientBrush = profileGradientBrush,
            onIntent = {},
            onNavigateBack = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
