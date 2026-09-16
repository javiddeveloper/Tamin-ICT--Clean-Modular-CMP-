package com.tamin.taminhamrah.feature.historyobjection.ui.stepper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperEvent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperIntent
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionStepperState
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.STEP_BRANCH
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.STEP_RECORD
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.STEP_WORKSHOP
import com.tamin.taminhamrah.feature.historyobjection.ui.stepper.contract.HistoryObjectionBottomSheetTarget
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.GlassIconTile
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminFormAbandonDialog
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetResult
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_back
import taminx.core.core_ui.history_objection_confirm
import taminx.core.core_ui.history_objection_next_step
import taminx.core.core_ui.history_objection_step_branch_title
import taminx.core.core_ui.history_objection_step_record_title
import taminx.core.core_ui.history_objection_step_workshop_title
import taminx.core.core_ui.history_objection_submit_success_confirm
import taminx.core.core_ui.history_objection_submit_success_message
import taminx.core.core_ui.history_objection_submit_success_title
import taminx.core.core_ui.history_objection_title
import taminx.core.core_ui.ic_close
import taminx.core.core_ui.ic_moon
import taminx.core.core_ui.ic_sun
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.orotez_protez_confirm_and_continue
import taminx.core.core_ui.step_number_1
import taminx.core.core_ui.step_number_2
import taminx.core.core_ui.step_number_3
import taminx.feature.history_objection.generated.resources.Res as FeatureRes
import taminx.feature.history_objection.generated.resources.ic_history_objection

@Composable
fun HistoryObjectionStepperScreen(
    requestNumber: String?,
    rowIndex: String? = null,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: HistoryObjectionStepperViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(requestNumber, rowIndex) {
        viewModel.sendIntent(HistoryObjectionStepperIntent.Load(requestNumber, rowIndex))
    }

    HandleHistoryObjectionStepperEvents(
        events = viewModel.events,
        onNavigateBack = onNavigateBack,
        snackbarHostState = snackbarHostState,
    )

    HistoryObjectionStepperContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::sendIntent,
        onNavigateBack = onNavigateBack,
        onNavigateHome = onNavigateHome,
    )

    state.bottomSheetConfig?.let { config ->
        HistoryObjectionPickerBottomSheet(
            config = config,
            onDismissRequest = { viewModel.sendIntent(HistoryObjectionStepperIntent.OnDismissBottomSheet) },
            onSubmit = { result ->
                resolvePickerSelection(result, state)?.let { viewModel.sendIntent(it) }
            },
        )
    }
}

private fun resolvePickerSelection(
    result: TaminBottomSheetResult,
    state: HistoryObjectionStepperState,
): HistoryObjectionStepperIntent? {
    val selectedId = result.selectedItemIds.firstOrNull() ?: return null

    return when (state.bottomSheetTarget) {
        HistoryObjectionBottomSheetTarget.PROVINCE ->
            state.provinces.getOrNull(selectedId)?.let { HistoryObjectionStepperIntent.OnProvinceSelected(it) }

        HistoryObjectionBottomSheetTarget.CITY ->
            state.cities.getOrNull(selectedId)?.let { HistoryObjectionStepperIntent.OnCitySelected(it) }

        HistoryObjectionBottomSheetTarget.BRANCH ->
            state.branches.getOrNull(selectedId)?.let { HistoryObjectionStepperIntent.OnBranchSelected(it) }

        HistoryObjectionBottomSheetTarget.INSURANCE_TYPE ->
            state.insuranceTypes.getOrNull(selectedId)?.let { HistoryObjectionStepperIntent.OnInsuranceTypeSelected(it) }

        null -> null
    }
}

@Composable
private fun HandleHistoryObjectionStepperEvents(
    events: Flow<HistoryObjectionStepperEvent>,
    onNavigateBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            HistoryObjectionStepperEvent.NavigateBack -> onNavigateBack()
            is HistoryObjectionStepperEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
        }
    }
}

@Composable
private fun HistoryObjectionStepperContent(
    state: HistoryObjectionStepperState,
    snackbarHostState: SnackbarHostState,
    onIntent: (HistoryObjectionStepperIntent) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var showAbandonDialog by remember { mutableStateOf(false) }
    // ✕ leaves to home; first-step back leaves via the nav back stack (same as OnBackClicked).
    var abandonToHome by remember { mutableStateOf(true) }

    val handleBack: () -> Unit = {
        if (state.currentStep == STEP_BRANCH) {
            abandonToHome = false
            showAbandonDialog = true
        } else {
            onIntent(HistoryObjectionStepperIntent.OnBackClicked)
        }
    }
    BackHandler(onBack = handleBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.history_objection_title),
                background = taminTopAppBarGradient(colors.profileGradientStops),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.action_back),
                        onClick = handleBack,
                        bordered = true,
                    )
                },
                action = {
                    Box() {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_close),
                            contentDescription = null,
                            onClick = {
                                abandonToHome = true
                                showAbandonDialog = true
                            },
                            bordered = true
                        )
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    DecorativeBackgroundCircle(
                        size = 190.dp,
                        xOffset = 450.dp,
                        yOffset = (-150).dp
                    )
                    AnimatedRingHeaderIcon(
                        icon = vectorResource(FeatureRes.drawable.ic_history_objection),
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .align(Alignment.Center),
                    )
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colors.bgPage)
                    .padding(Spacing.page)
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                if (state.currentStep == STEP_RECORD) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.page, vertical = Spacing.md)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = null,
                            onClick = handleBack,
                            bordered = true,
                            containerColor = LocalTaminColors.current.bgSurface,
                            contentColor = LocalTaminColors.current.textPrimary,
                            borderColor = LocalTaminColors.current.border,
                        )
                        LoadingButton(
                            text = stringResource(Res.string.history_objection_confirm),
                            onClick = { onIntent(HistoryObjectionStepperIntent.OnConfirmClicked) },
                            enabled = state.canGoNextFromCurrentStep && !state.isSubmitting,
                            isLoading = state.isSubmitting,
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.page, vertical = Spacing.md)
                            .navigationBarsPadding(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    ) {
                        TaminTopAppBarButton(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                            contentDescription = null,
                            onClick = handleBack,
                            bordered = true,
                            containerColor = LocalTaminColors.current.bgSurface,
                            contentColor = LocalTaminColors.current.textPrimary,
                            borderColor = LocalTaminColors.current.border,
                        )
                        TaminFilledButton(
                            icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                            text = stringResource(Res.string.history_objection_next_step),
                            enabled = state.canGoNextFromCurrentStep,
                            onClick = { onIntent(HistoryObjectionStepperIntent.OnNextClicked) },
                            background = colors.buttonGradient
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.bgPage)
                .padding(innerPadding),
        ) {
            StepIndicator(
                steps = rememberHistoryObjectionSteps(state.currentStep),
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
                    if (targetState > initialState) {
                        slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "HistoryObjectionStepperTransition",
                modifier = Modifier.weight(1f),
            ) { step ->
                when (step) {
                    STEP_BRANCH -> BranchInfoStep(state = state, onIntent = onIntent)
                    STEP_WORKSHOP -> WorkshopInfoStep(state = state, onIntent = onIntent)
                    STEP_RECORD -> RecordInfoStep(state = state, onIntent = onIntent)
                }
            }
        }
    }

    ErrorStateView(
        message = state.error,
        onDismiss = { onIntent(HistoryObjectionStepperIntent.OnErrorDismissed) },
    )

    if (state.hasSubmitted) {
        HistoryObjectionSubmitSuccessDialog(
            onAcknowledged = { onIntent(HistoryObjectionStepperIntent.OnSubmitSuccessAcknowledged) },
        )
    }

    if (showAbandonDialog) {
        TaminFormAbandonDialog(
            formName = stringResource(Res.string.history_objection_title),
            onStay = { showAbandonDialog = false },
            onAbandon = {
                showAbandonDialog = false
                if (abandonToHome) onNavigateHome() else onNavigateBack()
            },
        )
    }
}

@Composable
private fun HistoryObjectionSubmitSuccessDialog(onAcknowledged: () -> Unit) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.history_objection_submit_success_title),
        description = stringResource(Res.string.history_objection_submit_success_message),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.history_objection_submit_success_confirm),
                onClick = onAcknowledged,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onAcknowledged,
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
    )
}

@Composable
private fun rememberHistoryObjectionSteps(currentStep: Int): ImmutableList<StepIndicatorModel> {
    val branchTitle = stringResource(Res.string.history_objection_step_branch_title)
    val workshopTitle = stringResource(Res.string.history_objection_step_workshop_title)
    val recordTitle = stringResource(Res.string.history_objection_step_record_title)
    val step1Num = stringResource(Res.string.step_number_1)
    val step2Num = stringResource(Res.string.step_number_2)
    val step3Num = stringResource(Res.string.step_number_3)

    return persistentListOf(
        StepIndicatorModel(
            title = branchTitle,
            stepNumber = step1Num,
            state = if (currentStep <= STEP_BRANCH) StepState.Active else StepState.Completed,
        ),
        StepIndicatorModel(
            title = workshopTitle,
            stepNumber = step2Num,
            state = when {
                currentStep == STEP_WORKSHOP -> StepState.Active
                currentStep > STEP_WORKSHOP -> StepState.Completed
                else -> StepState.Inactive
            },
        ),
        StepIndicatorModel(
            title = recordTitle,
            stepNumber = step3Num,
            state = if (currentStep == STEP_RECORD) StepState.Active else StepState.Inactive,
        ),
    )
}
