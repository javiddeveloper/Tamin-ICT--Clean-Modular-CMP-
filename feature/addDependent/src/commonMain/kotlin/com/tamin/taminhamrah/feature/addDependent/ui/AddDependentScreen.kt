package com.tamin.taminhamrah.feature.addDependent.ui

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentEvent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState
import com.tamin.taminhamrah.feature.addDependent.ui.contract.BottomSheetTarget
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_DOCUMENTS
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_INQUIRY
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_SUCCESS
import com.tamin.taminhamrah.feature.addDependent.ui.contract.STEP_VERIFICATION
import com.tamin.taminhamrah.ui.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheet
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetResult
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.add_dependent_close
import taminx.core.core_ui.add_dependent_subtitle
import taminx.core.core_ui.add_dependent_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.inquiry_submit_button
import taminx.core.core_ui.step_complete
import taminx.core.core_ui.step_get_info
import taminx.core.core_ui.step_number_1
import taminx.core.core_ui.step_number_2
import taminx.core.core_ui.step_number_3
import taminx.core.core_ui.step_upload_docs
import taminx.core.core_ui.step_verify_info
import taminx.core.core_ui.success_view_list
import taminx.core.core_ui.upload_submit_final
import taminx.core.core_ui.verify_next_step

@Composable
fun AddDependentRoute(
    viewModel: AddDependentViewModel = koinViewModel(),
    onBackClicked: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AddDependentIntent.InitData)
    }

    HandleAddDependentEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState
    )

    AddDependentContent(
        state = state,
        onBackClicked = onBackClicked,
        onIntent = viewModel::sendIntent,
        snackbarHostState = snackbarHostState
    )

    state.bottomSheetConfig?.let { config ->
        TaminBottomSheet(
            config = config,
            onDismissRequest = { viewModel.sendIntent(AddDependentIntent.DismissBottomSheet) },
            onSubmit = { result ->
                resolvePickerSelection(result, state)?.let { viewModel.sendIntent(it) }
            }
        )
    }
}

private fun resolvePickerSelection(
    result: TaminBottomSheetResult,
    state: AddDependentState
): AddDependentIntent? {
    val selectedId = result.selectedItemIds.firstOrNull() ?: return null

    return when (state.bottomSheetTarget) {
        BottomSheetTarget.RELATIONSHIP ->
            state.familyRelationships.find { it.id == selectedId }
                ?.let { AddDependentIntent.OnRelationshipSelected(it) }

        BottomSheetTarget.CITY_BIRTH ->
            state.cities.getOrNull(selectedId)
                ?.let { AddDependentIntent.OnCityBirthSelected(it) }

        BottomSheetTarget.CITY_ISSUANCE ->
            state.cities.getOrNull(selectedId)
                ?.let { AddDependentIntent.OnCityIssuanceSelected(it) }

        BottomSheetTarget.BRANCH ->
            state.activeBranches.getOrNull(selectedId)
                ?.let { AddDependentIntent.OnBranchSelected(it) }

        null -> null
    }
}

@Composable
fun HandleAddDependentEvents(
    events: Flow<AddDependentEvent>,
    onBackClicked: () -> Unit,
    snackbarHostState: SnackbarHostState
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            AddDependentEvent.NavigateBack -> onBackClicked()
            is AddDependentEvent.ShowToast -> snackbarHostState.showSnackbar(event.message)
            is AddDependentEvent.ShowErrorDialog -> snackbarHostState.showSnackbar("${event.title}: ${event.message}")
        }
    }
}

@Composable
fun AddDependentContent(
    state: AddDependentState,
    onBackClicked: () -> Unit,
    onIntent: (AddDependentIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
    }

    val errorMessage = state.error
    val isInitialLoad = state.currentStep == STEP_INQUIRY && state.activeBranches.isEmpty()
    val showBlockingError = errorMessage != null && isInitialLoad

    LaunchedEffect(errorMessage) {
        if (errorMessage != null && !showBlockingError) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.add_dependent_title),
                background = profileGradientBrush,
                bottomPadding = Spacing.xl,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.add_dependent_close),
                        onClick = onBackClicked,
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
                        AnimatedRingHeaderIcon(icon = vectorResource(Res.drawable.ic_tamin_user))
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Text(
                            text = stringResource(Res.string.add_dependent_subtitle),
                            style = MaterialTheme.typography.labelLarge,
                            color = taminColors.textHeaderSubtitle
                        )
                    }
                }
            }
        },
        bottomBar = {
            if (!showBlockingError) {
                AddDependentBottomBar(
                    state = state,
                    onIntent = onIntent,
                    onFinish = onBackClicked
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                state.isLoading && isInitialLoad -> AddDependentShimmer()

                errorMessage != null && isInitialLoad -> ErrorStateView(
                    message = errorMessage,
                    onRetry = { onIntent(AddDependentIntent.InitData) },
                    onDismiss = {onBackClicked()}
                )

                else -> {
                    StepIndicator(
                        steps = rememberAddDependentSteps(state.currentStep),
                        modifier = Modifier.padding(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = Spacing.md
                        )
                    )

                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = {
                            if (targetState > initialState) {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                            } else {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            }
                        },
                        label = "AddDependentStepTransition",
                        modifier = Modifier.weight(1f)
                    ) { step ->
                        when (step) {
                            STEP_INQUIRY -> InquiryInfoStep(state = state, onIntent = onIntent)
                            STEP_VERIFICATION -> VerificationStep(state = state, onIntent = onIntent)
                            STEP_DOCUMENTS -> DocumentUploadStep(state = state, onIntent = onIntent)
                            else -> AddDependentSuccessStep(state = state)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddDependentBottomBar(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit,
    onFinish: () -> Unit
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Spacing.lg)
            .navigationBarsPadding()
            .imePadding()
    ) {
        when (state.currentStep) {
            STEP_INQUIRY -> {
                LoadingButton(
                    text = stringResource(Res.string.inquiry_submit_button),
                    onClick = { onIntent(AddDependentIntent.SubmitInquiryRegistry) },
                    isLoading = state.isLoading,
                    icon = vectorResource(Res.drawable.ic_tamin_search),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            STEP_VERIFICATION -> {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = { onIntent(AddDependentIntent.OnPreviousStepClicked) }
                    )
                    LoadingButton(
                        text = stringResource(Res.string.verify_next_step),
                        onClick = { onIntent(AddDependentIntent.OnNextStepClicked) },
                        isLoading = state.isLoading,
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            STEP_DOCUMENTS -> {
                val uploadedTypes = state.uploadedDocuments.mapTo(mutableSetOf()) { it.docType }
                val allUploaded = state.requiredDocTypes
                    .filterNot { it.isDisabled }
                    .all { uploadedTypes.contains(it.code) }

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = { onIntent(AddDependentIntent.OnPreviousStepClicked) }
                    )
                    LoadingButton(
                        text = stringResource(Res.string.upload_submit_final),
                        onClick = { onIntent(AddDependentIntent.OnNextStepClicked) },
                        enabled = allUploaded,
                        isLoading = state.isLoading,
                        icon = Icons.Filled.CheckCircle,
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            STEP_SUCCESS -> {
                TaminFilledButton(
                    text = stringResource(Res.string.success_view_list),
                    onClick = onFinish,
                    background = colors.iconGradientSuccess,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun rememberAddDependentSteps(currentStep: Int): ImmutableList<StepIndicatorModel> {
    val getInfoTitle = stringResource(Res.string.step_get_info)
    val verifyInfoTitle = stringResource(Res.string.step_verify_info)
    val uploadDocsTitle = stringResource(Res.string.step_upload_docs)
    val completeTitle = stringResource(Res.string.step_complete)
    val step1Num = stringResource(Res.string.step_number_1)
    val step2Num = stringResource(Res.string.step_number_2)
    val step3Num = stringResource(Res.string.step_number_3)

    return remember(currentStep, getInfoTitle, verifyInfoTitle, uploadDocsTitle, completeTitle, step1Num, step2Num, step3Num) {
        val documentsTitle = if (currentStep >= STEP_SUCCESS) completeTitle else uploadDocsTitle
        persistentListOf(
            StepIndicatorModel(
                title = getInfoTitle,
                stepNumber = step1Num,
                state = if (currentStep <= STEP_INQUIRY) StepState.Active else StepState.Completed
            ),
            StepIndicatorModel(
                title = verifyInfoTitle,
                stepNumber = step2Num,
                state = when {
                    currentStep == STEP_VERIFICATION -> StepState.Active
                    currentStep > STEP_VERIFICATION -> StepState.Completed
                    else -> StepState.Inactive
                }
            ),
            StepIndicatorModel(
                title = documentsTitle,
                stepNumber = step3Num,
                state = when {
                    currentStep == STEP_DOCUMENTS -> StepState.Active
                    currentStep >= STEP_SUCCESS -> StepState.Completed
                    else -> StepState.Inactive
                }
            )
        )
    }
}
