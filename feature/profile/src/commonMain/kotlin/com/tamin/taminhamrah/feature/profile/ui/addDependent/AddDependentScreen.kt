package com.tamin.taminhamrah.feature.profile.ui.addDependent

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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentEvent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.AnimatedRingHeaderIcon
import com.tamin.taminhamrah.ui.components.DecorativeBackgroundCircle
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheet
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetResult
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminBottomSheetType
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
import taminx.core.core_ui.picker_branch_title
import taminx.core.core_ui.picker_relationship_title
import taminx.core.core_ui.step_complete
import taminx.core.core_ui.step_get_info
import taminx.core.core_ui.step_upload_docs
import taminx.core.core_ui.step_verify_info
import taminx.core.core_ui.success_view_list
import taminx.core.core_ui.upload_submit_final
import taminx.core.core_ui.verify_birth_place_label
import taminx.core.core_ui.verify_next_step

private const val STEP_INQUIRY = 1
private const val STEP_VERIFICATION = 2
private const val STEP_DOCUMENTS = 3
private const val STEP_SUCCESS = 4

@Composable
fun AddDependentRoute(
    viewModel: AddDependentViewModel = koinViewModel(),
    onBackClicked: () -> Unit
) {
    val uiStateState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val onIntent = remember(viewModel) {
        { intent: AddDependentIntent -> viewModel.sendIntent(intent) }
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AddDependentIntent.InitData)
    }

    HandleAddDependentEvents(
        events = viewModel.events,
        onBackClicked = onBackClicked,
        snackbarHostState = snackbarHostState
    )

    AddDependentContent(
        uiStateState = uiStateState,
        onBackClicked = onBackClicked,
        onIntent = onIntent,
        snackbarHostState = snackbarHostState
    )

    val birthTitle = stringResource(Res.string.verify_birth_place_label)
    val relTitle = stringResource(Res.string.picker_relationship_title)
    val branchTitle = stringResource(Res.string.picker_branch_title)

    uiStateState.value.bottomSheetConfig?.let { config ->
        TaminBottomSheet(
            config = config,
            onDismissRequest = { onIntent(AddDependentIntent.DismissBottomSheet) },
            onSubmit = { result ->
                handleBottomSheetResult(result, uiStateState.value, onIntent, birthTitle, relTitle, branchTitle)
            }
        )
    }
}

private fun handleBottomSheetResult(
    result: TaminBottomSheetResult,
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit,
    birthTitle: String,
    relTitle: String,
    branchTitle: String
) {
    val selectedId = result.selectedItemIds.firstOrNull() ?: return

    when (result.type) {
        TaminBottomSheetType.CITY -> {
            val cityName = state.bottomSheetConfig?.items?.find { it.id == selectedId }?.title.orEmpty()
            if (state.bottomSheetConfig?.title?.contains(birthTitle) == true) {
                onIntent(AddDependentIntent.OnCityBirthSelected(com.tamin.taminhamrah.model.common.CityPR(selectedId.toString().padStart(2, '0'), cityName)))
            } else {
                onIntent(AddDependentIntent.OnCityIssuanceSelected(com.tamin.taminhamrah.model.common.CityPR(selectedId.toString().padStart(2, '0'), cityName)))
            }
        }
        TaminBottomSheetType.CUSTOM -> {
            if (state.bottomSheetConfig?.title?.contains(relTitle) == true) {
                val relationship = state.familyRelationships.find { it.id == selectedId }
                relationship?.let { onIntent(AddDependentIntent.OnRelationshipSelected(it)) }
            } else if (state.bottomSheetConfig?.title?.contains(branchTitle) == true) {
                val branch = state.activeBranches.find { it.branchCode.toIntOrNull() == selectedId }
                branch?.let { onIntent(AddDependentIntent.OnBranchSelected(it)) }
            }
        }
        else -> {}
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
    uiStateState: State<AddDependentState>,
    onBackClicked: () -> Unit,
    onIntent: (AddDependentIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val taminColors = LocalTaminColors.current
    val state = uiStateState.value
    val profileGradientBrush = remember(taminColors.profileGradientStops) {
        Brush.horizontalGradient(taminColors.profileGradientStops)
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
            AddDependentBottomBar(
                state = state,
                onIntent = onIntent,
                onFinish = onBackClicked
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)

        ) {
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
                        modifier = Modifier.weight(1f)
                    )

                }
            }
            STEP_DOCUMENTS -> {
                val activeDocTypes = state.requiredDocTypes.filter { !it.isDisabled }
                val uploadedTypes = state.uploadedDocuments.map { it.docType }.toSet()
                val allUploaded = activeDocTypes.all { uploadedTypes.contains(it.code) }

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

    return remember(currentStep, getInfoTitle, verifyInfoTitle, uploadDocsTitle, completeTitle) {
        val documentsTitle = if (currentStep >= STEP_SUCCESS) completeTitle else uploadDocsTitle
        persistentListOf(
            StepIndicatorModel(
                title = getInfoTitle,
                stepNumber = "۱",
                state = if (currentStep <= STEP_INQUIRY) StepState.Active else StepState.Completed
            ),
            StepIndicatorModel(
                title = verifyInfoTitle,
                stepNumber = "۲",
                state = when {
                    currentStep == STEP_VERIFICATION -> StepState.Active
                    currentStep > STEP_VERIFICATION -> StepState.Completed
                    else -> StepState.Inactive
                }
            ),
            StepIndicatorModel(
                title = documentsTitle,
                stepNumber = "۳",
                state = when {
                    currentStep == STEP_DOCUMENTS -> StepState.Active
                    currentStep >= STEP_SUCCESS -> StepState.Completed
                    else -> StepState.Inactive
                }
            )
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentScreenPreview() {
    PreviewRtlThemeContent {
        val uiStateState = androidx.compose.runtime.mutableStateOf(AddDependentState())
        AddDependentContent(
            uiStateState = uiStateState,
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}

@PreviewRtlTheme
@Composable
private fun AddDependentScreenPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        val uiStateState = androidx.compose.runtime.mutableStateOf(AddDependentState(currentStep = STEP_VERIFICATION))
        AddDependentContent(
            uiStateState = uiStateState,
            onBackClicked = {},
            onIntent = {},
            snackbarHostState = remember { SnackbarHostState() }
        )
    }
}
