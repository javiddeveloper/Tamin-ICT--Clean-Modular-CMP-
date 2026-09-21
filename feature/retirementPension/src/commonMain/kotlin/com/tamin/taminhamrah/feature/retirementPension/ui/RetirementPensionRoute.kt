package com.tamin.taminhamrah.feature.retirementPension.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.model.personal.pdfDownload.InputStreamPR
import com.tamin.taminhamrah.model.personal.pdfDownload.PdfDownloadPR
import com.tamin.taminhamrah.ui.components.TaminPdfViewer
import io.ktor.utils.io.ByteReadChannel
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementAuthStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementDialogHost
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementDocumentsStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementFinalStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementHistoryStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementIdentityStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementIntroContent
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementRulesStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementTrackContent
import com.tamin.taminhamrah.feature.retirementPension.ui.components.RetirementWorkshopStep
import com.tamin.taminhamrah.feature.retirementPension.ui.components.label
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDialog
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDocumentType
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionEvent
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionIntent
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementPensionUiState
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementScreen
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementStep
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminHeroStepProgress
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.taminTopAppBarGradient
import com.tamin.taminhamrah.ui.components.buttons.SquareIconButton
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.rememberCameraPermission
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_camera
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_cross
import taminx.core.core_ui.ic_tamin_gallery
import taminx.core.core_ui.retirement_pension_next_step
import taminx.core.core_ui.retirement_pension_camera_permission_denied
import taminx.core.core_ui.retirement_pension_request_creation_failed
import taminx.core.core_ui.retirement_pension_start_request
import taminx.core.core_ui.retirement_pension_step_authentication_hint
import taminx.core.core_ui.retirement_pension_step_authentication_title
import taminx.core.core_ui.retirement_pension_dialog_rules_title
import taminx.core.core_ui.retirement_pension_rules_unavailable
import taminx.core.core_ui.retirement_pension_step_final_hint
import taminx.core.core_ui.retirement_pension_step_final_title
import taminx.core.core_ui.retirement_pension_step_history_title
import taminx.core.core_ui.retirement_pension_step_identity_documents_hint
import taminx.core.core_ui.retirement_pension_step_identity_documents_title
import taminx.core.core_ui.retirement_pension_step_identity_hint
import taminx.core.core_ui.retirement_pension_step_identity_title
import taminx.core.core_ui.retirement_pension_step_quit_letter_hint
import taminx.core.core_ui.retirement_pension_step_quit_letter_title
import taminx.core.core_ui.retirement_pension_step_rules_hint
import taminx.core.core_ui.retirement_pension_step_rules_title
import taminx.core.core_ui.retirement_pension_step_workshop_hint
import taminx.core.core_ui.retirement_pension_step_workshop_title
import taminx.core.core_ui.retirement_pension_submit_final
import taminx.core.core_ui.retirement_pension_title
import taminx.core.core_ui.retirement_pension_track_title
import kotlin.time.Duration.Companion.milliseconds

/** How long the success card on step 2 is left up before the wizard moves itself on. */
private const val AUTH_ADVANCE_DELAY_MILLIS = 700L

/** Legacy asset: rulesAndRegulationsHtmlFile/rules_retirement.pdf */
private const val RULES_PDF_RESOURCE_PATH = "files/rules_retirement.pdf"
private const val RULES_PDF_FILE_NAME = "rules_retirement.pdf"

private val HeroShape = RoundedCornerShape(
    bottomStart = CornerRadius.x3l,
    bottomEnd = CornerRadius.x3l,
)

@Composable
fun RetirementPensionRoute(
    onBack: () -> Unit,
    viewModel: RetirementPensionViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val rulesUnavailableMessage = stringResource(Res.string.retirement_pension_rules_unavailable)
    val scope = rememberCoroutineScope()
    var showRulesPdfViewer by remember { mutableStateOf(false) }
    var rulesPdf by remember { mutableStateOf<PdfDownloadPR?>(null) }
    var rulesPdfLoadFailed by remember { mutableStateOf(false) }

    HandleRetirementPensionEvents(
        events = viewModel.events,
        onNavigateBack = onBack,
        onAuthenticationSucceeded = { viewModel.sendIntent(RetirementPensionIntent.NextStep) },
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
    )

    BackHandler { viewModel.sendIntent(RetirementPensionIntent.Back) }

    RetirementPensionScreen(
        state = state,
        onIntent = viewModel::sendIntent,
    )

    // The viewer is an overlay over the whole service, and the bytes it reads are a Route concern:
    // keeping it here leaves RetirementPensionScreen stateless, as pension-survivor does.
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
            title = stringResource(Res.string.retirement_pension_dialog_rules_title),
        )
    }
}

@Composable
private fun HandleRetirementPensionEvents(
    events: Flow<RetirementPensionEvent>,
    onNavigateBack: () -> Unit,
    onAuthenticationSucceeded: () -> Unit,
    onOpenRulesDocument: () -> Unit,
) {
    val toaster = LocalToaster.current
    val creationFailed = stringResource(Res.string.retirement_pension_request_creation_failed)
    val cameraDenied = stringResource(Res.string.retirement_pension_camera_permission_denied)

    events.collectWithLifecycleAware { event ->
        when (event) {
            is RetirementPensionEvent.ShowError -> toaster.error(event.message)
            RetirementPensionEvent.RequestCreationFailed -> toaster.error(creationFailed)
            RetirementPensionEvent.CameraPermissionDenied -> toaster.error(cameraDenied)
            RetirementPensionEvent.NavigateBack -> onNavigateBack()
            RetirementPensionEvent.OpenRulesDocument -> onOpenRulesDocument()
            // Left up long enough to be read, then the wizard moves on by itself.
            RetirementPensionEvent.AuthenticationSucceeded -> {
                delay(AUTH_ADVANCE_DELAY_MILLIS.milliseconds)
                onAuthenticationSucceeded()
            }
        }
    }
}

@Composable
internal fun RetirementPensionScreen(
    state: RetirementPensionUiState,
    onIntent: (RetirementPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Narrow values, not the whole state: the hero is on screen throughout, and nothing it
            // draws changes while a field is being typed into.
            RetirementHeader(
                screen = state.screen,
                step = state.step,
                maxReachedStep = state.maxReachedStep,
                onIntent = onIntent,
            )
        },
        bottomBar = {
            if (state.screen != RetirementScreen.Track) {
                RetirementBottomBar(
                    screen = state.screen,
                    step = state.step,
                    isLoading = state.isLoading,
                    isBusy = state.isIntroLoading || state.isDocumentUploading,
                    onIntent = onIntent,
                )
            }
        },
    ) { padding ->
        AnimatedContent(
            targetState = state.screen to state.step,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = Spacing.sm),
            transitionSpec = {
                val forward = targetState.second.ordinal >= initialState.second.ordinal
                val direction = if (forward) -1 else 1
                slideInHorizontally { width -> direction * width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -direction * width } + fadeOut()
            },
            label = "RetirementPensionBody",
        ) { (screen, step) ->
            when (screen) {
                RetirementScreen.Intro -> RetirementIntroContent(
                    insured = state.insured,
                    isLoading = state.isIntroLoading,
                    isAgeEligible = state.isAgeEligible,
                    hasExistingRequest = state.hasExistingRequest,
                    onOpenTrack = { onIntent(RetirementPensionIntent.OpenTrack) },
                )

                RetirementScreen.Track -> RetirementTrackContent(
                    trackingCode = state.requestId,
                    statusCode = state.statusCode,
                    insured = state.insured,
                    phoneNumber = state.phoneNumber,
                    address = state.address,
                    workshopName = state.workshopName,
                    workshopCode = state.workshopCode,
                    branchName = state.branch?.branchName.orEmpty(),
                    history = state.history,
                )

                RetirementScreen.Form -> RetirementFormBody(
                    state = state,
                    step = step,
                    onIntent = onIntent,
                )
            }
        }
    }

    state.activeDocument?.let { type ->
        RetirementDocumentPicker(
            type = type,
            onPicked = { file -> onIntent(RetirementPensionIntent.DocumentPicked(type, file)) },
            onCameraDenied = { onIntent(RetirementPensionIntent.CameraDenied) },
            onDismiss = { onIntent(RetirementPensionIntent.DismissDocumentSource) },
        )
    }

    state.dialog?.let { dialog ->
        RetirementDialogHost(
            dialog = dialog,
            trackingCode = state.requestId,
            onDismiss = { onIntent(RetirementPensionIntent.DismissDialog) },
            onLeaveConfirmed = { onIntent(RetirementPensionIntent.LeaveConfirmed) },
        )
    }

}

@Composable
private fun RetirementHeader(
    screen: RetirementScreen,
    step: RetirementStep,
    maxReachedStep: RetirementStep,
    onIntent: (RetirementPensionIntent) -> Unit,
) {
    val isForm = screen == RetirementScreen.Form
    val title = stringResource(
        if (screen == RetirementScreen.Track) {
            Res.string.retirement_pension_track_title
        } else {
            Res.string.retirement_pension_title
        },
    )

    TaminTopAppBar(
        title = title,
        // The wizard's hero carries a step row, a progress strip and a hint; it needs more room
        // under them than a plain title bar, or the hint sits on the rounded corner.
        bottomPadding = if (isForm) Spacing.xlg else Spacing.xl,
        shape = HeroShape,
        // This service's hero is the design's navy (#173D7E → #1F4FA3), not the app's default
        // teal. `profileGradientStops` is that navy pair, and it is themed for dark mode too.
        background = taminTopAppBarGradient(LocalTaminColors.current.profileGradientStops),
        navigationIcon = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                onClick = { onIntent(RetirementPensionIntent.Back) },
                bordered = true,
            )
        },
        action = {
            TaminTopAppBarButton(
                icon = vectorResource(Res.drawable.ic_tamin_cross),
                contentDescription = null,
                onClick = { onIntent(RetirementPensionIntent.CloseClicked) },
                bordered = true,
            )
        },
    ) {
        if (isForm) {
            TaminHeroStepProgress(
                stepTitle = step.title(),
                currentStep = step.number,
                totalSteps = RetirementStep.TOTAL,
                hint = step.hint(),
                maxReachedStep = maxReachedStep.number,
                onStepClick = { number ->
                    RetirementStep.entries.getOrNull(number - 1)?.let {
                        onIntent(RetirementPensionIntent.GoToStep(it))
                    }
                },
                modifier = Modifier.padding(top = Spacing.md),
            )
        }
    }
}

/**
 * The wizard's body.
 *
 * Every step is handed the fields it actually draws — never the whole state — so typing an address
 * recomposes one text field rather than the eight-row identity card above it.
 */
@Composable
private fun RetirementFormBody(
    state: RetirementPensionUiState,
    step: RetirementStep,
    onIntent: (RetirementPensionIntent) -> Unit,
) {
    val error = state.visibleFormError

    when (step) {
            RetirementStep.Rules -> RetirementRulesStep(
                applicantName = state.insured?.fullName.orEmpty(),
                consentAccepted = state.consentAccepted,
                error = error,
                onViewRules = {
                    onIntent(RetirementPensionIntent.ViewRules)
                },
                onConsentChange = { onIntent(RetirementPensionIntent.ConsentChanged(it)) },
            )

            RetirementStep.Authentication -> RetirementAuthStep(
                mobileNumber = state.otpMobile,
                otpValue = state.otpValue,
                otpSent = state.otpSent,
                otpVerified = state.otpVerified,
                otpInvalid = state.otpInvalid,
                isSending = state.isOtpSending,
                error = error,
                onRequestOtp = { onIntent(RetirementPensionIntent.RequestOtp) },
                onOtpChange = { onIntent(RetirementPensionIntent.OtpChanged(it)) },
            )

            RetirementStep.Identity -> RetirementIdentityStep(
                identity = state.identity,
                phoneNumber = state.phoneNumber,
                address = state.address,
                isConfirmed = state.identityConfirmed,
                error = error,
                onPhoneChange = { onIntent(RetirementPensionIntent.PhoneChanged(it)) },
                onAddressChange = { onIntent(RetirementPensionIntent.AddressChanged(it)) },
                onConfirmedChange = {
                    onIntent(RetirementPensionIntent.IdentityConfirmedChanged(it))
                },
            )

            RetirementStep.Workshop -> RetirementWorkshopStep(
                branch = state.branch,
                workshopName = state.workshopName,
                workshopCode = state.workshopCode,
                workshopAddress = state.workshopAddress,
                employerName = state.employerName,
                activityType = state.activityType,
                isConfirmed = state.workshopConfirmed,
                error = error,
                onWorkshopNameChange = {
                    onIntent(RetirementPensionIntent.WorkshopNameChanged(it))
                },
                onWorkshopCodeChange = {
                    onIntent(RetirementPensionIntent.WorkshopCodeChanged(it))
                },
                onWorkshopAddressChange = {
                    onIntent(RetirementPensionIntent.WorkshopAddressChanged(it))
                },
                onEmployerNameChange = {
                    onIntent(RetirementPensionIntent.EmployerNameChanged(it))
                },
                onActivityTypeChange = {
                    onIntent(RetirementPensionIntent.ActivityTypeChanged(it))
                },
                onConfirmedChange = {
                    onIntent(RetirementPensionIntent.WorkshopConfirmedChanged(it))
                },
            )

            RetirementStep.History -> RetirementHistoryStep(
                history = state.history,
                isLoading = state.isHistoryLoading,
                onRegisterObjection = {
                    onIntent(RetirementPensionIntent.ShowDialog(RetirementDialog.Objection))
                },
            )

            RetirementStep.IdentityDocuments -> RetirementDocumentsStep(
                types = IDENTITY_DOCUMENTS,
                documents = state.documents,
                missing = state::isDocumentMissing,
                isIdentityStep = true,
                onDocumentClick = { onIntent(RetirementPensionIntent.DocumentClicked(it)) },
            )

            RetirementStep.QuitLetter -> RetirementDocumentsStep(
                types = QUIT_LETTER_DOCUMENTS,
                documents = state.documents,
                missing = state::isDocumentMissing,
                isIdentityStep = false,
                onDocumentClick = { onIntent(RetirementPensionIntent.DocumentClicked(it)) },
            )

            RetirementStep.Final -> RetirementFinalStep(
                insured = state.insured,
                phoneNumber = state.phoneNumber,
                address = state.address,
                workshopName = state.workshopName,
                workshopCode = state.workshopCode,
                branchName = state.branch?.branchName.orEmpty(),
                history = state.history,
                isConfirmed = state.finalConfirmed,
                error = error,
                onConfirmedChange = { onIntent(RetirementPensionIntent.FinalConfirmedChanged(it)) },
            )
        }

}

@Composable
private fun RetirementBottomBar(
    screen: RetirementScreen,
    step: RetirementStep,
    isLoading: Boolean,
    isBusy: Boolean,
    onIntent: (RetirementPensionIntent) -> Unit,
) {
    val isFinalStep = step == RetirementStep.Final
    val label = stringResource(
        when {
            screen == RetirementScreen.Intro -> Res.string.retirement_pension_start_request
            isFinalStep -> Res.string.retirement_pension_submit_final
            else -> Res.string.retirement_pension_next_step
        },
    )

    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
        ) {
            if (screen == RetirementScreen.Form && step != RetirementStep.Rules) {
                SquareIconButton(
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                    onClick = { onIntent(RetirementPensionIntent.Back) },
                )
            }
            LoadingButton(
                text = label,
                onClick = {
                    onIntent(
                        if (screen == RetirementScreen.Intro) {
                            RetirementPensionIntent.StartRequest
                        } else {
                            RetirementPensionIntent.NextStep
                        },
                    )
                },
                enabled = !isLoading && !isBusy,
                isLoading = isLoading,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconPosition = LoadingButtonIconPosition.TRAILING,
                // The design paints the action that actually submits green, not blue.
                background = LocalTaminColors.current.successGradient
                    .takeIf { screen == RetirementScreen.Form && isFinalStep },
            )
        }
    }
}

/**
 * The camera/gallery sheet and the two pickers behind it.
 *
 * The picked [PlatformFile] goes straight to the ViewModel, which reads and uploads it and keeps
 * only the returned guid — no image bytes ever reach the UI state.
 */
@Composable
private fun RetirementDocumentPicker(
    type: RetirementDocumentType,
    onPicked: (PlatformFile) -> Unit,
    onCameraDenied: () -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val cameraPermission = rememberCameraPermission()

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        if (file != null) onPicked(file) else onDismiss()
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        if (file != null) onPicked(file) else onDismiss()
    }

    TaminDocumentSourceSheet(
        title = type.label(),
        cameraIcon = vectorResource(Res.drawable.ic_tamin_camera),
        galleryIcon = vectorResource(Res.drawable.ic_tamin_gallery),
        onSelectCamera = {
            if (cameraPermission.granted) {
                cameraLauncher.launch()
            } else {
                cameraPermission.request { granted ->
                    scope.launch {
                        if (granted) cameraLauncher.launch() else onCameraDenied()
                    }
                }
            }
        },
        onSelectGallery = { galleryLauncher.launch() },
        onDismiss = onDismiss,
    )
}

/**
 * Which documents each step asks for.
 *
 * Top-level and immutable so the argument is both a stable type and the same instance every
 * recomposition — built at the call site it would be a fresh list each time.
 */
private val IDENTITY_DOCUMENTS: ImmutableList<RetirementDocumentType> =
    RetirementDocumentType.entries.filter(RetirementDocumentType::isIdentityDocument).toImmutableList()

private val QUIT_LETTER_DOCUMENTS: ImmutableList<RetirementDocumentType> =
    persistentListOf(RetirementDocumentType.QuitLetter)

@Composable
private fun RetirementStep.title(): String = stringResource(
    when (this) {
        RetirementStep.Rules -> Res.string.retirement_pension_step_rules_title
        RetirementStep.Authentication -> Res.string.retirement_pension_step_authentication_title
        RetirementStep.Identity -> Res.string.retirement_pension_step_identity_title
        RetirementStep.Workshop -> Res.string.retirement_pension_step_workshop_title
        RetirementStep.History -> Res.string.retirement_pension_step_history_title
        RetirementStep.IdentityDocuments ->
            Res.string.retirement_pension_step_identity_documents_title
        RetirementStep.QuitLetter -> Res.string.retirement_pension_step_quit_letter_title
        RetirementStep.Final -> Res.string.retirement_pension_step_final_title
    },
)

/** The history step is the one the design gives no hint to. */
@Composable
private fun RetirementStep.hint(): String? = when (this) {
    RetirementStep.Rules -> stringResource(Res.string.retirement_pension_step_rules_hint)
    RetirementStep.Authentication ->
        stringResource(Res.string.retirement_pension_step_authentication_hint)
    RetirementStep.Identity -> stringResource(Res.string.retirement_pension_step_identity_hint)
    RetirementStep.Workshop -> stringResource(Res.string.retirement_pension_step_workshop_hint)
    RetirementStep.History -> null
    RetirementStep.IdentityDocuments ->
        stringResource(Res.string.retirement_pension_step_identity_documents_hint)
    RetirementStep.QuitLetter -> stringResource(Res.string.retirement_pension_step_quit_letter_hint)
    RetirementStep.Final -> stringResource(Res.string.retirement_pension_step_final_hint)
}
