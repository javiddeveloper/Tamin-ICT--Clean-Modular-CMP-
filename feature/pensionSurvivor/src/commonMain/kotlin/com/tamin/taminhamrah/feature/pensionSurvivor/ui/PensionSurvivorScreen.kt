package com.tamin.taminhamrah.feature.pensionSurvivor.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.tamin.taminhamrah.feature.pensionSurvivor.NavigateToSurvivorInfoArgs
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.DeceasedDocumentsStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.DeceasedStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.FinalStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.RulesStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.components.SurvivorsStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorEvent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorStep
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.SurvivorContactDraft
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminHeroStepProgress
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_search
import taminx.core.core_ui.inquiry_submit_button
import taminx.core.core_ui.orotez_protez_confirm_and_continue
import taminx.core.core_ui.pension_survivor_next_step
import taminx.core.core_ui.pension_survivor_final_submit_success_message
import taminx.core.core_ui.pension_survivor_final_submit_success_title
import taminx.core.core_ui.pension_survivor_rules_unavailable
import taminx.core.core_ui.pension_survivor_step_deceased
import taminx.core.core_ui.pension_survivor_step_deceased_documents
import taminx.core.core_ui.pension_survivor_step_final
import taminx.core.core_ui.pension_survivor_step_rules
import taminx.core.core_ui.pension_survivor_step_survivors
import taminx.core.core_ui.pension_survivor_title
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.upload_submit_final

@Composable
fun PensionSurvivorScreen(
    onBack: () -> Unit,
    onNavigateToSurvivorInfo: (NavigateToSurvivorInfoArgs) -> Unit,
    savedDraftNationalId: String? = null,
    savedDraft: SurvivorContactDraft? = null,
    onSavedDraftConsumed: () -> Unit = {},
    viewModel: PensionSurvivorViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val rulesUnavailableMessage = stringResource(Res.string.pension_survivor_rules_unavailable)
    val rulesTitle = stringResource(Res.string.pension_survivor_step_rules)
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshSurvivorsOnResume by remember { mutableStateOf(false) }
    var showPdfViewer by remember { mutableStateOf(false) }
    var showRulesPdfViewer by remember { mutableStateOf(false) }
    var rulesPdf by remember { mutableStateOf<PdfDownloadPR?>(null) }
    var rulesPdfLoadFailed by remember { mutableStateOf(false) }

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

    LaunchedEffect(savedDraftNationalId, savedDraft) {
        if (!savedDraftNationalId.isNullOrBlank() && savedDraft != null) {
            viewModel.sendIntent(
                PensionSurvivorIntent.SurvivorContactSaved(
                    nationalId = savedDraftNationalId,
                    draft = savedDraft,
                ),
            )
            onSavedDraftConsumed()
        }
    }

    HandlePensionSurvivorEvents(
        events = viewModel.events,
        onShowToast = { toaster.error(it) },
        onNavigateBack = onBack,
        onNavigateToSurvivorInfo = { args ->
            refreshSurvivorsOnResume = true
            onNavigateToSurvivorInfo(args)
        },
        onOpenRulesDocument = {
            scope.launch {
                try {
                    val bytes = Res.readBytes(RULES_PDF_RESOURCE_PATH)
                    rulesPdf = PdfDownloadPR(InputStreamPR(ByteReadChannel(bytes)))
                    rulesPdfLoadFailed = false
                    showRulesPdfViewer = true
                } catch (_: Exception) {
                    rulesPdf = null
                    rulesPdfLoadFailed = true
                    toaster.error(rulesUnavailableMessage)
                }
            }
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

    if (showRulesPdfViewer) {
        TaminPdfViewer(
            fileName = RULES_PDF_FILE_NAME,
            pdf = rulesPdf,
            downloadFailed = rulesPdfLoadFailed,
            onRequestDownload = {},
            onDismiss = {
                showRulesPdfViewer = false
                rulesPdf = null
                rulesPdfLoadFailed = false
            },
            title = rulesTitle,
        )
    }

    if (showPdfViewer) {
        TaminPdfViewer(
            fileName = "pension_survivor_final_${state.requestId ?: state.applicantNationalId}_r${state.finalPdfRevision}.pdf",
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
    onNavigateToSurvivorInfo: (NavigateToSurvivorInfoArgs) -> Unit,
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
                onNavigateToSurvivorInfo(
                    NavigateToSurvivorInfoArgs(
                        survivor = event.survivor,
                        deceasedNationalId = event.deceasedNationalId,
                        draft = event.draft,
                        branchCode = event.branchCode,
                        deceasedInsuranceId = event.deceasedInsuranceId,
                        sharedDeceasedDocuments = event.sharedDeceasedDocuments,
                    ),
                )
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
    val step1Title = stringResource(Res.string.pension_survivor_step_rules)
    val step2Title = stringResource(Res.string.pension_survivor_step_deceased)
    val step3Title = stringResource(Res.string.pension_survivor_step_deceased_documents)
    val step4Title = stringResource(Res.string.pension_survivor_step_survivors)
    val step5Title = stringResource(Res.string.pension_survivor_step_final)
    val currentStepIndex = state.currentStep.ordinal + 1
    val currentStepTitle = when (state.currentStep) {
        PensionSurvivorStep.Rules -> step1Title
        PensionSurvivorStep.Deceased -> step2Title
        PensionSurvivorStep.DeceasedDocuments -> step3Title
        PensionSurvivorStep.Survivors -> step4Title
        PensionSurvivorStep.Final -> step5Title
    }
    val colors = LocalTaminColors.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bgPage),
        containerColor = colors.bgPage,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.pension_survivor_title),
                background = taminTopAppBarGradient(colors.profileGradientStops),
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
                TaminHeroStepProgress(
                    stepTitle = currentStepTitle,
                    currentStep = currentStepIndex,
                    totalSteps = PENSION_SURVIVOR_TOTAL_STEPS,
                    modifier = Modifier.padding(top = Spacing.md),
                )
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
        AnimatedContent(
            targetState = state.currentStep,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding).padding(top = Spacing.sm),
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
                PensionSurvivorStep.DeceasedDocuments -> DeceasedDocumentsStep(
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

@Composable
private fun PensionSurvivorBottomBar(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
) {
    val nextEnabled = when (state.currentStep) {
        PensionSurvivorStep.Rules -> state.commitmentAccepted && !state.isProfileLoading
        PensionSurvivorStep.Deceased -> state.deceasedInfo != null && !state.isLoading
        PensionSurvivorStep.DeceasedDocuments -> state.isDeceasedHistoryConfirmed &&
            state.areDeceasedDocumentsComplete &&
            !state.isLoading &&
            !state.isDeceasedDocumentUploading
        PensionSurvivorStep.Survivors -> !state.isLoading
        PensionSurvivorStep.Final -> state.isPdfConfirmed && state.requestId != null && !state.isLoading
    }
    val canSearchDeceased = state.deceasedNationalId.length == DECEASED_NATIONAL_ID_LENGTH && !state.isLoading

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
                    SquareIconButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        onClick = { onIntent(PensionSurvivorIntent.PreviousStep) },
                    )
                    when {
                        state.currentStep == PensionSurvivorStep.Deceased && state.deceasedInfo == null -> {
                            LoadingButton(
                                text = stringResource(Res.string.inquiry_submit_button),
                                onClick = { onIntent(PensionSurvivorIntent.SearchDeceased) },
                                enabled = canSearchDeceased,
                                isLoading = state.isLoading,
                                modifier = Modifier.weight(1f),
                                icon = vectorResource(Res.drawable.ic_tamin_search),
                                iconPosition = LoadingButtonIconPosition.TRAILING,
                            )
                        }

                        else -> {
                            LoadingButton(
                                text = when (state.currentStep) {
                                    PensionSurvivorStep.Final ->
                                        stringResource(Res.string.upload_submit_final)
                                    PensionSurvivorStep.Deceased ->
                                        stringResource(Res.string.orotez_protez_confirm_and_continue)
                                    else -> stringResource(Res.string.pension_survivor_next_step)
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
                        }
                    }
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

private const val DECEASED_NATIONAL_ID_LENGTH = 10
private const val PENSION_SURVIVOR_TOTAL_STEPS = 5

/** Legacy asset: rulesAndRegulationsHtmlFile/rule_pension_survivor.pdf */
private const val RULES_PDF_RESOURCE_PATH = "files/rule_pension_survivor.pdf"
private const val RULES_PDF_FILE_NAME = "rule_pension_survivor.pdf"
