package com.tamin.taminhamrah.feature.fractionContract.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.fractionContract.ui.components.FractionContractScreenShimmer
import com.tamin.taminhamrah.feature.fractionContract.ui.components.FractionEligibilityStep
import com.tamin.taminhamrah.feature.fractionContract.ui.components.FractionPlaceholderStepShimmer
import com.tamin.taminhamrah.feature.fractionContract.ui.components.FractionTermsStep
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractEvent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractIntent
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractState
import com.tamin.taminhamrah.feature.fractionContract.ui.contract.FractionContractStep
import com.tamin.taminhamrah.feature.fractionContract.ui.preview.FractionContractPreviewData
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminLocalPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.HeaderDecoration
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_rules_pdf_title
import taminx.core.core_ui.fraction_contract_guide_body
import taminx.core.core_ui.fraction_contract_guide_confirm
import taminx.core.core_ui.fraction_contract_guide_title
import taminx.core.core_ui.fraction_contract_header_birth
import taminx.core.core_ui.fraction_contract_header_national_id
import taminx.core.core_ui.fraction_contract_next_step
import taminx.core.core_ui.fraction_contract_step_eligibility
import taminx.core.core_ui.fraction_contract_step_submit
import taminx.core.core_ui.fraction_contract_step_terms
import taminx.core.core_ui.fraction_contract_step_user_info
import taminx.core.core_ui.fraction_contract_title
import taminx.core.core_ui.ic_help
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.step_number_1
import taminx.core.core_ui.step_number_2
import taminx.core.core_ui.step_number_3
import taminx.core.core_ui.step_number_4
@Composable
fun FractionContractRoute(
    viewModel: FractionContractViewModel = koinViewModel(),
    onBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(FractionContractIntent.InitData)
    }

    BackHandler(onBack = { viewModel.sendIntent(FractionContractIntent.OnBackClicked) })

    HandleFractionContractEvents(
        events = viewModel.events,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )

    FractionContractScreen(
        state = state,
        onIntent = viewModel::sendIntent,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun HandleFractionContractEvents(
    events: Flow<FractionContractEvent>,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            FractionContractEvent.NavigateBack -> onBack()
            is FractionContractEvent.ShowToast -> snackbarHostState.showSnackbar(event.message)
        }
    }
}

@Composable
fun FractionContractScreen(
    state: FractionContractState,
    onIntent: (FractionContractIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }
    val showBlockingError = state.error != null && state.registrationInfo == null && !state.isLoading
    val showInitialShimmer = state.isLoading && state.registrationInfo == null
    var showRulesPdf by remember { mutableStateOf(false) }
    var rulesPdfBytes by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(showRulesPdf) {
        if (!showRulesPdf) {
            rulesPdfBytes = null
            return@LaunchedEffect
        }
        rulesPdfBytes = null
        rulesPdfBytes = withContext(Dispatchers.Default) {
            runCatching { Res.readBytes(FractionContractState.RULES_PDF_PATH) }
                .getOrElse { ByteArray(0) }
        }
    }

    if (showInitialShimmer) {
        FractionContractScreenShimmer(modifier = modifier)
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.fraction_contract_title),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = null,
                        onClick = { onIntent(FractionContractIntent.OnBackClicked) },
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_help),
                        contentDescription = stringResource(Res.string.fraction_contract_guide_title),
                        onClick = { onIntent(FractionContractIntent.OnGuideClicked) },
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
                    state.registrationInfo?.let { info ->
                        FractionContractIdentityHeader(info = info)
                    }
                }
            }
        },
        bottomBar = {
            if (!showBlockingError && state.registrationInfo != null) {
                FractionContractBottomBar(
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
            when {
                showBlockingError -> {
                    ErrorStateView(
                        message = state.error.orEmpty(),
                        onRetry = { onIntent(FractionContractIntent.InitData) },
                        onDismiss = { onIntent(FractionContractIntent.OnBackClicked) },
                    )
                }

                else -> {
                    StepIndicator(
                        steps = rememberFractionSteps(state.currentStep),
                        modifier = Modifier.padding(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = Spacing.md,
                        ),
                    )

                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = {
                            if (targetState.ordinal > initialState.ordinal) {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                            } else {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            }
                        },
                        label = "FractionContractStepTransition",
                        modifier = Modifier.weight(1f),
                    ) { step ->
                        when (step) {
                            FractionContractStep.Eligibility -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(horizontal = Spacing.page, vertical = Spacing.md),
                                ) {
                                    FractionEligibilityStep(
                                        eligibility = state.eligibility,
                                        insuranceId = state.registrationInfo?.insuranceId.orEmpty(),
                                        isEligible = state.isEligible,
                                        birthEpoch = state.registrationInfo?.dateOfBirthEpoch ?: 0L,
                                    )
                                }
                            }

                            FractionContractStep.Terms -> {
                                val info = state.registrationInfo
                                if (info != null) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState())
                                            .padding(horizontal = Spacing.page, vertical = Spacing.md),
                                    ) {
                                        FractionTermsStep(
                                            info = info,
                                            isRulesConfirmed = state.isRulesConfirmed,
                                            onRulesConfirmedChange = {
                                                onIntent(FractionContractIntent.SetRulesConfirmed(it))
                                            },
                                            onShowRules = { showRulesPdf = true },
                                        )
                                    }
                                }
                            }

                            FractionContractStep.UserInfo,
                            FractionContractStep.Submit,
                            -> {
                                FractionPlaceholderStepShimmer()
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showGuideDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.fraction_contract_guide_title),
            description = stringResource(Res.string.fraction_contract_guide_body),
            icon = vectorResource(Res.drawable.ic_info),
            iconTint = Color.White,
            iconBackgroundBrush = taminColors.iconGradientPrimary,
            onDismissRequest = { onIntent(FractionContractIntent.DismissGuideDialog) },
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.fraction_contract_guide_confirm),
                    onClick = { onIntent(FractionContractIntent.DismissGuideDialog) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
        )
    }

    if (showRulesPdf) {
        TaminLocalPdfViewer(
            pdfBytes = rulesPdfBytes,
            title = stringResource(Res.string.contract_rules_pdf_title),
            onDismiss = { showRulesPdf = false },
        )
    }

    LaunchedEffect(state.error) {
        val message = state.error
        if (message != null && state.registrationInfo != null) {
            snackbarHostState.showSnackbar(message)
        }
    }
}

@Composable
private fun FractionContractIdentityHeader(
    info: RegistrationInfoPR,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        GlassIconTile(icon = vectorResource(Res.drawable.ic_tamin_user))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = info.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onGradient,
            )
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Row(modifier = Modifier , horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(
                        Res.string.fraction_contract_header_national_id,
                        info.nationalId.toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textHeaderSubtitle,
                )
                Text(
                    text = stringResource(
                        Res.string.fraction_contract_header_birth,
                        info.birthDateFormatted.toPersianDigits(),
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textHeaderSubtitle,
                )
            }

        }
    }
}

@Composable
private fun FractionContractBottomBar(
    state: FractionContractState,
    onIntent: (FractionContractIntent) -> Unit,
) {
    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.currentStep) {
            FractionContractStep.Eligibility -> {
                LoadingButton(
                    text = stringResource(Res.string.fraction_contract_next_step),
                    onClick = { onIntent(FractionContractIntent.OnNextStepClicked) },
                    enabled = state.isEligible && !state.isLoading,
                    isLoading = state.isLoading,
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            FractionContractStep.Terms,
            FractionContractStep.UserInfo,
            FractionContractStep.Submit,
            -> {
                val nextEnabled = when (state.currentStep) {
                    FractionContractStep.Terms -> state.isRulesConfirmed
                    FractionContractStep.UserInfo -> true
                    FractionContractStep.Submit -> false
                    FractionContractStep.Eligibility -> false
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                ) {
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = { onIntent(FractionContractIntent.OnPreviousStepClicked) },
                    )
                    LoadingButton(
                        text = stringResource(Res.string.fraction_contract_next_step),
                        onClick = { onIntent(FractionContractIntent.OnNextStepClicked) },
                        enabled = nextEnabled,
                        isLoading = false,
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberFractionSteps(currentStep: FractionContractStep): ImmutableList<StepIndicatorModel> {
    val step1 = stringResource(Res.string.fraction_contract_step_eligibility)
    val step2 = stringResource(Res.string.fraction_contract_step_terms)
    val step3 = stringResource(Res.string.fraction_contract_step_user_info)
    val step4 = stringResource(Res.string.fraction_contract_step_submit)
    val n1 = stringResource(Res.string.step_number_1)
    val n2 = stringResource(Res.string.step_number_2)
    val n3 = stringResource(Res.string.step_number_3)
    val n4 = stringResource(Res.string.step_number_4)

    return remember(currentStep, step1, step2, step3, step4, n1, n2, n3, n4) {
        persistentListOf(
            StepIndicatorModel(
                title = step1,
                stepNumber = n1,
                state = stepState(currentStep, FractionContractStep.Eligibility),
            ),
            StepIndicatorModel(
                title = step2,
                stepNumber = n2,
                state = stepState(currentStep, FractionContractStep.Terms),
            ),
            StepIndicatorModel(
                title = step3,
                stepNumber = n3,
                state = stepState(currentStep, FractionContractStep.UserInfo),
            ),
            StepIndicatorModel(
                title = step4,
                stepNumber = n4,
                state = stepState(currentStep, FractionContractStep.Submit),
            ),
        )
    }
}

private fun stepState(
    current: FractionContractStep,
    target: FractionContractStep,
): StepState = when {
    current == target -> StepState.Active
    current.ordinal > target.ordinal -> StepState.Completed
    else -> StepState.Inactive
}

@PreviewRtlTheme
@Composable
private fun FractionContractEligiblePreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.eligibleState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractIneligiblePreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.ineligibleState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractGuideDialogPreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.guideDialogState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractTermsStepPreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.termsStepState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractTermsConfirmedPreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.termsStepConfirmedState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractUserInfoStepPreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.userInfoStepState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractSubmitStepPreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.submitStepState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractLoadingPreview() {
    PreviewRtlThemeContent {
        FractionContractScreen(
            state = FractionContractPreviewData.loadingState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}

@PreviewRtlTheme
@Composable
private fun FractionContractEligibleDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        FractionContractScreen(
            state = FractionContractPreviewData.eligibleState,
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
