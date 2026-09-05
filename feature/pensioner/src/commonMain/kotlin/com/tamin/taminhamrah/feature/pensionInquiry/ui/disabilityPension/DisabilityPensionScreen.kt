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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionCommissionRecordStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionDependentsStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionDocumentsStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionIdentityContactStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionRegisteredRequestsSheet
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionRulesDialog
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionTermsStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components.DisabilityPensionWorkshopStep
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentChecklist
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentImageSource
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentState
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
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.CameraPermission
import com.tamin.taminhamrah.util.rememberCameraPermission
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.action_confirm
import taminx.core.core_ui.back_content_description
import taminx.core.core_ui.close_content_description
import taminx.core.core_ui.disability_pension_commission_pdf_title
import taminx.core.core_ui.disability_pension_documents_confirm_message
import taminx.core.core_ui.disability_pension_documents_confirm_title
import taminx.core.core_ui.disability_pension_next_step
import taminx.core.core_ui.disability_pension_refresh_confirm_message
import taminx.core.core_ui.disability_pension_refresh_confirm_title
import taminx.core.core_ui.disability_pension_step_commission_record_subtitle
import taminx.core.core_ui.disability_pension_step_commission_record_title
import taminx.core.core_ui.disability_pension_step_dependents_subtitle
import taminx.core.core_ui.disability_pension_step_dependents_title
import taminx.core.core_ui.disability_pension_step_documents_subtitle
import taminx.core.core_ui.disability_pension_step_documents_title
import taminx.core.core_ui.disability_pension_step_identity_subtitle
import taminx.core.core_ui.disability_pension_step_identity_title
import taminx.core.core_ui.disability_pension_step_subtitle
import taminx.core.core_ui.disability_pension_step_terms_title
import taminx.core.core_ui.disability_pension_step_workshop_subtitle
import taminx.core.core_ui.disability_pension_step_workshop_title
import taminx.core.core_ui.disability_pension_title
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.orotez_protez_document_camera_permission_error

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
    val scope = rememberCoroutineScope()
    var pendingDocumentId by remember { mutableStateOf<String?>(null) }
    val cameraPermission = rememberCameraPermission()
    val cameraPermissionDeniedMessage = stringResource(Res.string.orotez_protez_document_camera_permission_error)
    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? ->
        val documentId = pendingDocumentId
        pendingDocumentId = null
        if (documentId != null && file != null) {
            viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked(documentId, file))
        }
    }
    val cameraLauncher = rememberCameraPickerLauncher { file: PlatformFile? ->
        val documentId = pendingDocumentId
        pendingDocumentId = null
        if (documentId != null && file != null) {
            viewModel.sendIntent(DisabilityPensionIntent.DocumentImagePicked(documentId, file))
        }
    }

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
        cameraPermission = cameraPermission,
        cameraPermissionDeniedMessage = cameraPermissionDeniedMessage,
        scope = scope,
        onShowToast = { toaster.error(it) },
        onNavigateToAddDependent = {
            refreshDependentsOnResume = true
            onNavigateToAddDependent()
        },
        onLaunchGallery = { documentId ->
            pendingDocumentId = documentId
            galleryLauncher.launch()
        },
        onLaunchCamera = { documentId ->
            pendingDocumentId = documentId
            cameraLauncher.launch()
        },
        onIntent = viewModel::sendIntent,
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

    if (state.showRegisteredRequestsSheet) {
        DisabilityPensionRegisteredRequestsSheet(
            isLoading = state.isRegisteredRequestsLoading,
            requests = state.registeredRequests,
            onDismiss = { viewModel.sendIntent(DisabilityPensionIntent.DismissRegisteredRequestsSheet) },
        )
    }

    if (state.showMedicalCommissionPdfViewer) {
        TaminPdfViewer(
            fileName = "disability_pension_medical_commission_${state.identityInfo?.insuranceId.orEmpty()}.pdf",
            pdf = state.medicalCommissionPdf,
            downloadFailed = state.medicalCommissionPdfDownloadFailed,
            onRequestDownload = { viewModel.sendIntent(DisabilityPensionIntent.DownloadMedicalCommissionPdfClicked) },
            onDismiss = { viewModel.sendIntent(DisabilityPensionIntent.DismissMedicalCommissionPdfViewer) },
            title = stringResource(Res.string.disability_pension_commission_pdf_title),
        )
    }

    if (state.showDocumentSourceSheet) {
        val activeDocumentId = state.activeDocumentId
        val activeDocument = DisabilityDocumentChecklist.find { it.id == activeDocumentId }
        if (activeDocumentId != null && activeDocument != null) {
            TaminDocumentSourceSheet(
                title = stringResource(activeDocument.titleRes),
                showRemoveOption = state.documents[activeDocumentId] is DisabilityDocumentState.Uploaded,
                onSelectCamera = {
                    viewModel.sendIntent(
                        DisabilityPensionIntent.DocumentSourceSelected(activeDocumentId, DisabilityDocumentImageSource.CAMERA),
                    )
                },
                onSelectGallery = {
                    viewModel.sendIntent(
                        DisabilityPensionIntent.DocumentSourceSelected(activeDocumentId, DisabilityDocumentImageSource.GALLERY),
                    )
                },
                onRemove = { viewModel.sendIntent(DisabilityPensionIntent.DocumentRemoveClicked(activeDocumentId)) },
                onDismiss = { viewModel.sendIntent(DisabilityPensionIntent.DismissDocumentSourceSheet) },
            )
        }
    }

    if (state.showDocumentsConfirmDialog) {
        TaminConfirmationDialog(
            title = stringResource(Res.string.disability_pension_documents_confirm_title),
            description = stringResource(Res.string.disability_pension_documents_confirm_message),
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.action_confirm),
                    onClick = { viewModel.sendIntent(DisabilityPensionIntent.ConfirmDocumentsSubmission) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {
                TaminOutlinedButton(
                    text = stringResource(Res.string.action_cancel),
                    onClick = { viewModel.sendIntent(DisabilityPensionIntent.DismissDocumentsConfirmDialog) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            onDismissRequest = { viewModel.sendIntent(DisabilityPensionIntent.DismissDocumentsConfirmDialog) },
        )
    }
}

@Composable
private fun HandleDisabilityPensionEvents(
    events: Flow<DisabilityPensionEvent>,
    cameraPermission: CameraPermission,
    cameraPermissionDeniedMessage: String,
    scope: CoroutineScope,
    onShowToast: (String) -> Unit,
    onNavigateToAddDependent: () -> Unit,
    onLaunchGallery: (documentId: String) -> Unit,
    onLaunchCamera: (documentId: String) -> Unit,
    onIntent: (DisabilityPensionIntent) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            is DisabilityPensionEvent.ShowToast -> onShowToast(event.message)
            DisabilityPensionEvent.NavigateToAddDependent -> onNavigateToAddDependent()
            is DisabilityPensionEvent.LaunchImagePicker -> when (event.source) {
                DisabilityDocumentImageSource.GALLERY -> onLaunchGallery(event.documentId)
                DisabilityDocumentImageSource.CAMERA -> {
                    if (cameraPermission.granted) {
                        onLaunchCamera(event.documentId)
                    } else {
                        cameraPermission.request { granted ->
                            scope.launch {
                                if (granted) {
                                    onLaunchCamera(event.documentId)
                                } else {
                                    onIntent(DisabilityPensionIntent.DocumentImagePickFailed(cameraPermissionDeniedMessage))
                                }
                            }
                        }
                    }
                }
            }
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
    val identityTitle = stringResource(Res.string.disability_pension_step_identity_title)
    val workshopTitle = stringResource(Res.string.disability_pension_step_workshop_title)
    val commissionRecordTitle = stringResource(Res.string.disability_pension_step_commission_record_title)
    val documentsTitle = stringResource(Res.string.disability_pension_step_documents_title)
    val termsSubtitle = stringResource(Res.string.disability_pension_step_subtitle)
    val dependentsSubtitle = stringResource(Res.string.disability_pension_step_dependents_subtitle)
    val identitySubtitle = stringResource(Res.string.disability_pension_step_identity_subtitle)
    val workshopSubtitle = stringResource(Res.string.disability_pension_step_workshop_subtitle)
    val commissionRecordSubtitle = stringResource(Res.string.disability_pension_step_commission_record_subtitle)
    val documentsSubtitle = stringResource(Res.string.disability_pension_step_documents_subtitle)
    val currentStepIndex = state.currentStep.ordinal + 1
    val stepTitle = when (state.currentStep) {
        DisabilityPensionStep.Terms -> termsTitle
        DisabilityPensionStep.Dependents -> dependentsTitle
        DisabilityPensionStep.IdentityContact -> identityTitle
        DisabilityPensionStep.Workshop -> workshopTitle
        DisabilityPensionStep.CommissionRecord -> commissionRecordTitle
        DisabilityPensionStep.Documents -> documentsTitle
    }
    val stepSubtitle = when (state.currentStep) {
        DisabilityPensionStep.Terms -> termsSubtitle
        DisabilityPensionStep.Dependents -> dependentsSubtitle
        DisabilityPensionStep.IdentityContact -> identitySubtitle
        DisabilityPensionStep.Workshop -> workshopSubtitle
        DisabilityPensionStep.CommissionRecord -> commissionRecordSubtitle
        DisabilityPensionStep.Documents -> documentsSubtitle
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
                    DisabilityPensionStep.IdentityContact -> DisabilityPensionIdentityContactStep(
                        state = state,
                        onIntent = onIntent,
                    )
                    DisabilityPensionStep.Workshop -> DisabilityPensionWorkshopStep(
                        state = state,
                        onIntent = onIntent,
                    )
                    DisabilityPensionStep.CommissionRecord -> DisabilityPensionCommissionRecordStep(
                        state = state,
                        onIntent = onIntent,
                    )
                    DisabilityPensionStep.Documents -> DisabilityPensionDocumentsStep(
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
        DisabilityPensionStep.Dependents,
        DisabilityPensionStep.IdentityContact,
        DisabilityPensionStep.Workshop,
        DisabilityPensionStep.CommissionRecord,
        DisabilityPensionStep.Documents,
        -> {
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
                        enabled = !state.isRefreshingDependents &&
                            !state.isDependentsLoading &&
                            !state.isAnyDocumentUploading &&
                            state.hasCommissionObjection != true,
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
