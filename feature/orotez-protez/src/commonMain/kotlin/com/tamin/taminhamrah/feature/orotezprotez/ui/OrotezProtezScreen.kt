package com.tamin.taminhamrah.feature.orotezprotez.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.orotezprotez.camera.CameraPermission
import com.tamin.taminhamrah.feature.orotezprotez.camera.rememberCameraPermission
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezDocumentSourceSheet
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezHeader
import com.tamin.taminhamrah.feature.orotezprotez.ui.components.OrotezProtezOptionSheet
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentChecklist
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezDocumentUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezEvent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezImageSource
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezInsuredDetailUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezIntent
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezOptionUi
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezPicker
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.OrotezProtezUiState
import com.tamin.taminhamrah.feature.orotezprotez.ui.contract.bytesOrNull
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.util.PersianDateFormatter
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_branch
import taminx.core.core_ui.ic_place
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ic_tamin_user
import taminx.core.core_ui.ic_warning
import taminx.core.core_ui.orotez_protez_confirm_and_continue
import taminx.core.core_ui.orotez_protez_detail_birth_certificate_number
import taminx.core.core_ui.orotez_protez_detail_birth_date
import taminx.core.core_ui.orotez_protez_detail_booklet_valid_until
import taminx.core.core_ui.orotez_protez_detail_full_name
import taminx.core.core_ui.orotez_protez_detail_issue_place
import taminx.core.core_ui.orotez_protez_detail_national_code
import taminx.core.core_ui.orotez_protez_detail_relation
import taminx.core.core_ui.orotez_protez_document_camera_permission_error
import taminx.core.core_ui.orotez_protez_document_optional
import taminx.core.core_ui.orotez_protez_document_pick_placeholder
import taminx.core.core_ui.orotez_protez_document_required
import taminx.core.core_ui.orotez_protez_document_status_error_tap_to_retry
import taminx.core.core_ui.orotez_protez_document_status_uploaded
import taminx.core.core_ui.orotez_protez_document_status_uploading
import taminx.core.core_ui.orotez_protez_documents_description
import taminx.core.core_ui.orotez_protez_documents_title
import taminx.core.core_ui.orotez_protez_field_branch_placeholder
import taminx.core.core_ui.orotez_protez_field_insured_person
import taminx.core.core_ui.orotez_protez_field_prescription_date
import taminx.core.core_ui.orotez_protez_next_step
import taminx.core.core_ui.orotez_protez_pick_branch_subtitle
import taminx.core.core_ui.orotez_protez_pick_branch_title
import taminx.core.core_ui.orotez_protez_pick_insured_person_subtitle
import taminx.core.core_ui.orotez_protez_pick_insured_person_title
import taminx.core.core_ui.orotez_protez_step_documents
import taminx.core.core_ui.orotez_protez_step_info
import taminx.core.core_ui.orotez_protez_step_info_description
import taminx.core.core_ui.orotez_protez_step_info_title
import taminx.core.core_ui.orotez_protez_step_user
import taminx.core.core_ui.orotez_protez_step_user_description
import taminx.core.core_ui.orotez_protez_step_user_title
import taminx.core.core_ui.orotez_protez_submit_request
import taminx.core.core_ui.orotez_protez_submit_success_confirm
import taminx.core.core_ui.orotez_protez_submit_success_fallback
import taminx.core.core_ui.orotez_protez_submit_success_title
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun OrotezProtezScreen(
    viewModel: OrotezProtezViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var pendingDocumentId by remember { mutableStateOf<String?>(null) }
    val cameraPermission = rememberCameraPermission()
    val cameraPermissionDeniedMessage = stringResource(Res.string.orotez_protez_document_camera_permission_error)
    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? ->
        val documentId = pendingDocumentId
        pendingDocumentId = null
        if (documentId != null && file != null) {
            viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(documentId, file))
        }
    }
    val cameraLauncher = rememberCameraPickerLauncher { file: PlatformFile? ->
        val documentId = pendingDocumentId
        pendingDocumentId = null
        if (documentId != null && file != null) {
            viewModel.sendIntent(OrotezProtezIntent.OnDocumentImagePicked(documentId, file))
        }
    }

    HandleOrotezProtezEvents(
        events = viewModel.events,
        cameraPermission = cameraPermission,
        cameraPermissionDeniedMessage = cameraPermissionDeniedMessage,
        scope = scope,
        onBackClicked = onBackClicked,
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

    val handleBack: () -> Unit = {
        if (uiState.currentStep == OrotezProtezStep.UserSelection) {
            onBackClicked()
        } else {
            viewModel.sendIntent(OrotezProtezIntent.BackToPreviousStep)
        }
    }

    OrotezProtezContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = handleBack,
    )
}

@Composable
fun HandleOrotezProtezEvents(
    events: Flow<OrotezProtezEvent>,
    cameraPermission: CameraPermission,
    cameraPermissionDeniedMessage: String,
    scope: CoroutineScope,
    onBackClicked: () -> Unit,
    onLaunchGallery: (documentId: String) -> Unit,
    onLaunchCamera: (documentId: String) -> Unit,
    onIntent: (OrotezProtezIntent) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            OrotezProtezEvent.NavigateBack -> onBackClicked()
            is OrotezProtezEvent.LaunchImagePicker -> when (event.source) {
                OrotezProtezImageSource.GALLERY -> onLaunchGallery(event.documentId)
                OrotezProtezImageSource.CAMERA -> {
                    if (cameraPermission.granted) {
                        onLaunchCamera(event.documentId)
                    } else {
                        cameraPermission.request { granted ->
                            scope.launch {
                                if (granted) {
                                    onLaunchCamera(event.documentId)
                                } else {
                                    onIntent(OrotezProtezIntent.OnDocumentImagePickFailed(cameraPermissionDeniedMessage))
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
private fun OrotezProtezContent(
    modifier: Modifier = Modifier,
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            OrotezProtezHeader(onBackClicked = onBackClicked)

            OrotezProtezStepIndicator(
                currentStep = state.currentStep,
                modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.lg),
            )

            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.index > initialState.index) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                            slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                            slideOutHorizontally { -it } + fadeOut()
                    }
                },
                label = "OrotezProtezStepTransition",
                modifier = Modifier.weight(1f),
            ) { step ->
                when (step) {
                    OrotezProtezStep.UserSelection -> Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        if (state.isLoading) {
                            OrotezProtezUserStepShimmer()
                        } else {
                            OrotezProtezUserStep(
                                state = state,
                                onIntent = onIntent,
                            )
                        }
                    }

                    OrotezProtezStep.InsuredInfo -> OrotezProtezInsuredInfoStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = onBackClicked,
                    )

                    OrotezProtezStep.Documents -> OrotezProtezDocumentsStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = onBackClicked,
                    )
                }
            }
        }
    }

    when (state.picker) {
        OrotezProtezPicker.BRANCH -> OrotezProtezOptionSheet(
            title = stringResource(Res.string.orotez_protez_pick_branch_title),
            subtitle = stringResource(Res.string.orotez_protez_pick_branch_subtitle),
            options = state.branchOptions,
            selectedId = state.branch?.id,
            onSelect = { onIntent(OrotezProtezIntent.OnBranchPicked(it)) },
            onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
        )

        OrotezProtezPicker.INSURED_PERSON -> OrotezProtezOptionSheet(
            title = stringResource(Res.string.orotez_protez_pick_insured_person_title),
            subtitle = stringResource(Res.string.orotez_protez_pick_insured_person_subtitle),
            options = state.insuredPersonOptions,
            selectedId = state.insuredPerson?.id,
            onSelect = { onIntent(OrotezProtezIntent.OnInsuredPersonPicked(it)) },
            onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
        )

        OrotezProtezPicker.DOCUMENT_SOURCE -> {
            val activeDocument = OrotezProtezDocumentChecklist.find { it.id == state.activeDocumentId }
            if (activeDocument != null) {
                val hasFile = state.documents[activeDocument.id]?.let {
                    it !is OrotezProtezDocumentState.Empty
                } == true
                OrotezProtezDocumentSourceSheet(
                    title = stringResource(activeDocument.titleRes),
                    showRemoveOption = hasFile,
                    onSelect = {
                        onIntent(
                            OrotezProtezIntent.OnDocumentSourceSelected(
                                documentId = activeDocument.id,
                                source = it,
                            )
                        )
                    },
                    onRemove = { onIntent(OrotezProtezIntent.OnDocumentRemoveClicked(activeDocument.id)) },
                    onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
                )
            }
        }

        OrotezProtezPicker.DATE -> TaminJalaliDatePicker(
            title = stringResource(Res.string.orotez_protez_field_prescription_date),
            onDismiss = { onIntent(OrotezProtezIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(
                    OrotezProtezIntent.OnPrescriptionDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )

        OrotezProtezPicker.NONE -> Unit
    }

    ErrorStateView(
        message = state.error,
        onDismiss = onBackClicked,
        onRetry = { onIntent(OrotezProtezIntent.LoadInitialData) },
    )
}
@Composable
private fun OrotezProtezStepIndicator(
    currentStep: OrotezProtezStep,
    modifier: Modifier = Modifier,
) {
    val currentIndex = currentStep.index

    StepIndicator(
        modifier = modifier,
        steps = persistentListOf(
            StepIndicatorModel(
                title = stringResource(Res.string.orotez_protez_step_user),
                stepNumber = "۱",
                state = when {
                    currentIndex == 0 -> StepState.Active
                    currentIndex > 0 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.orotez_protez_step_info),
                stepNumber = "۲",
                state = when {
                    currentIndex == 1 -> StepState.Active
                    currentIndex > 1 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.orotez_protez_step_documents),
                stepNumber = "۳",
                state = if (currentIndex == 2) StepState.Active else StepState.Inactive,
            ),
        ),
    )
}

@Composable
private fun OrotezProtezUserStep(
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.orotez_protez_step_user_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
            color = colors.textPrimary,
        )

        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = stringResource(Res.string.orotez_protez_step_user_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Spacer(Modifier.height(Spacing.lg))

        PickerRow(
            text = state.branch?.label ?: stringResource(Res.string.orotez_protez_field_branch_placeholder),
            isPlaceholder = state.branch == null,
            icon = vectorResource(Res.drawable.ic_branch),
            iconTint = colors.blueText,
            showChevron = true,
            onClick = { onIntent(OrotezProtezIntent.OnPickerRequested(OrotezProtezPicker.BRANCH)) },
        )

        Spacer(Modifier.height(Spacing.sm))

        PickerRow(
            text = state.insuredPerson?.label ?: stringResource(Res.string.orotez_protez_field_insured_person),
            isPlaceholder = state.insuredPerson == null,
            icon = vectorResource(Res.drawable.ic_tamin_user),
            iconTint = colors.blueText,
            showChevron = true,
            onClick = { onIntent(OrotezProtezIntent.OnPickerRequested(OrotezProtezPicker.INSURED_PERSON)) },
        )

        Spacer(Modifier.height(Spacing.sm))

        PickerRow(
            text = state.prescriptionDateLabel ?: stringResource(Res.string.orotez_protez_field_prescription_date),
            isPlaceholder = state.prescriptionDateLabel == null,
            icon = vectorResource(Res.drawable.ic_tamin_calendar),
            iconTint = colors.blueText,
            onClick = { onIntent(OrotezProtezIntent.OnPickerRequested(OrotezProtezPicker.DATE)) },
        )

        Spacer(Modifier.height(Spacing.xl))

        LoadingButton(
            text = stringResource(Res.string.orotez_protez_next_step),
            onClick = { onIntent(OrotezProtezIntent.OnNextStepClicked) },
            enabled = state.canGoNext,
            icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
            iconPosition = LoadingButtonIconPosition.TRAILING,
        )
    }
}

private val ShimmerTitleWidth = 160.dp
private val ShimmerLineHeight = 16.dp
private const val ShimmerSubtitleWidthFraction = 0.7f
private val ShimmerPickerRowHeight = 56.dp

@Composable
private fun OrotezProtezUserStepShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .width(ShimmerTitleWidth)
                .height(ShimmerLineHeight)
                .clip(RoundedCornerShape(CornerRadius.sm))
                .shimmer(),
        )

        Spacer(Modifier.height(Spacing.xs))

        Box(
            modifier = Modifier
                .fillMaxWidth(ShimmerSubtitleWidthFraction)
                .height(ShimmerLineHeight)
                .clip(RoundedCornerShape(CornerRadius.sm))
                .shimmer(),
        )

        Spacer(Modifier.height(Spacing.lg))

        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ShimmerPickerRowHeight)
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .shimmer(),
            )
            if (index != 2) {
                Spacer(Modifier.height(Spacing.sm))
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDimens.height)
                .clip(RoundedCornerShape(CornerRadius.xl))
                .shimmer(),
        )
    }
}

@Composable
private fun OrotezProtezInsuredInfoStep(
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val detail = state.selectedInsuredDetail

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.orotez_protez_step_info_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = stringResource(Res.string.orotez_protez_step_info_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )

            Spacer(Modifier.height(Spacing.lg))

            if (detail != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .taminSurface()
                        .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
                ) {
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_full_name),
                        value = detail.fullName,
                        numeric = false,
                    )
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_relation),
                        value = detail.relation,
                        numeric = false,
                    )
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_national_code),
                        value = detail.nationalCode,
                    )
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_birth_certificate_number),
                        value = detail.birthCertificateNumber,
                    )
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_issue_place),
                        value = detail.issuePlace,
                        numeric = false,
                    )
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_birth_date),
                        value = detail.birthDateLabel,
                    )
                    TaminDivider(modifier = Modifier.padding(vertical = Spacing.sm))
                    DetailRow(
                        label = stringResource(Res.string.orotez_protez_detail_booklet_valid_until),
                        value = detail.bookletValidUntilLabel,
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.md)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OrotezProtezBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.orotez_protez_confirm_and_continue),
                onClick = { onIntent(OrotezProtezIntent.OnConfirmInsuredInfoClicked) },
                enabled = detail != null,
                icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                iconPosition = LoadingButtonIconPosition.TRAILING,
            )
        }
    }
}

@Composable
private fun OrotezProtezBackStepButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)

    Box(
        modifier = modifier
            .size(ButtonDimens.height)
            .clip(shape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
            contentDescription = null,
            tint = colors.textPrimary,
        )
    }
}

@Composable
private fun OrotezProtezDocumentsStep(
    state: OrotezProtezUiState,
    onIntent: (OrotezProtezIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current
    var previewDocumentId by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.orotez_protez_documents_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )

            Spacer(Modifier.height(Spacing.xs))

            Text(
                text = stringResource(Res.string.orotez_protez_documents_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )

            Spacer(Modifier.height(Spacing.lg))

            OrotezProtezDocumentChecklist.forEachIndexed { index, document ->
                OrotezProtezDocumentCard(
                    document = document,
                    documentState = state.documents[document.id] ?: OrotezProtezDocumentState.Empty,
                    onClick = { onIntent(OrotezProtezIntent.OnDocumentCardClicked(document.id)) },
                    onPreviewRequested = { previewDocumentId = document.id },
                )
                if (index != OrotezProtezDocumentChecklist.lastIndex) {
                    Spacer(Modifier.height(Spacing.md))
                }
            }

            state.documentPickError?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                )
            }

            state.documentValidationError?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                )
            }

            state.submitError?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.dangerText,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.md)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            OrotezProtezBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.orotez_protez_submit_request),
                onClick = { onIntent(OrotezProtezIntent.OnSubmitDocumentsClicked) },
                enabled = !state.isAnyDocumentUploading && !state.isSubmitting && !state.hasSubmitted,
                isLoading = state.isSubmitting,
            )
        }
    }

    val previewDocument = OrotezProtezDocumentChecklist.find { it.id == previewDocumentId }
    val previewBytes = previewDocumentId?.let { state.documents[it]?.bytesOrNull() }
    if (previewDocument != null && previewBytes != null) {
        OrotezProtezDocumentPreviewDialog(
            title = stringResource(previewDocument.titleRes),
            bytes = previewBytes,
            onDismiss = { previewDocumentId = null },
        )
    }

    if (state.hasSubmitted) {
        OrotezProtezSubmitSuccessDialog(
            message = state.submittedResultMessage,
            onAcknowledged = { onIntent(OrotezProtezIntent.OnSubmitSuccessAcknowledged) },
        )
    }
}

@Composable
private fun OrotezProtezSubmitSuccessDialog(
    message: String?,
    onAcknowledged: () -> Unit,
) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.orotez_protez_submit_success_title),
        description = message ?: stringResource(Res.string.orotez_protez_submit_success_fallback),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.orotez_protez_submit_success_confirm),
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

@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun OrotezProtezDocumentPreviewDialog(
    title: String,
    bytes: ByteArray,
    onDismiss: () -> Unit,
) {
    val base64 = remember(bytes) { Base64.encode(bytes) }
    TaminImageViewer(
        title = title,
        url = base64,
        onDismiss = onDismiss,
    )
}

@Composable
private fun OrotezProtezDocumentCard(
    document: OrotezProtezDocumentUi,
    documentState: OrotezProtezDocumentState,
    onClick: () -> Unit,
    onPreviewRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)
    var fillAnimationComplete by remember(document.id) {
        mutableStateOf(documentState is OrotezProtezDocumentState.Uploaded)
    }
    LaunchedEffect(document.id, documentState is OrotezProtezDocumentState.Uploading) {
        if (documentState is OrotezProtezDocumentState.Uploading) {
            fillAnimationComplete = false
        }
    }
    val displayState = if (documentState is OrotezProtezDocumentState.Uploaded && !fillAnimationComplete) {
        OrotezProtezDocumentState.Uploading(documentState.platformFile, documentState.bytes)
    } else {
        documentState
    }

    val rowModifier = when (displayState) {
        is OrotezProtezDocumentState.Empty -> modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = colors.border,
                    style = Stroke(
                        width = Thickness.medium.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(CornerRadius.card.toPx()),
                )
            }
            .clip(shape)
            .clickable(onClick = onClick)
            .background(colors.bgSurface)
            .padding(Spacing.lg)

        is OrotezProtezDocumentState.Uploaded -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.greenBg)
            .border(Thickness.border, colors.greenBorder, shape)
            .clickable(onClick = onClick)
            .padding(Spacing.lg)

        is OrotezProtezDocumentState.Failed -> modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.dangerBg)
            .border(Thickness.border, colors.dangerBorder, shape)
            .clickable(onClick = onClick)
            .padding(Spacing.lg)

        is OrotezProtezDocumentState.Uploading -> modifier
            .fillMaxWidth()
            .clip(shape)
            .border(Thickness.border, colors.border, shape)
            .clickable(onClick = onClick)
            .padding(Spacing.lg)
    }

    Box {
        if (displayState is OrotezProtezDocumentState.Uploading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
                    .background(colors.bgSurface),
            )
            LiquidWaveProgressBar(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape),
                onFillComplete = { fillAnimationComplete = true },
            )
        }

        Column(modifier = rowModifier) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OrotezProtezDocumentIconTile(displayState)

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(document.titleRes),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary,
                            )
                            StatusPill(
                                text = stringResource(
                                    if (document.isRequired) {
                                        Res.string.orotez_protez_document_required
                                    } else {
                                        Res.string.orotez_protez_document_optional
                                    },
                                ),
                                containerColor = if (document.isRequired) colors.blueBg else colors.bgPage,
                                contentColor = if (document.isRequired) colors.blueText else colors.textMuted,
                            )
                        }

                        Spacer(Modifier.height(Spacing.xxs))

                        Text(
                            text = displayState.statusText(),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (displayState is OrotezProtezDocumentState.Failed) {
                                colors.dangerText
                            } else {
                                colors.textMuted
                            },
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (displayState is OrotezProtezDocumentState.Uploaded) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = stringResource(document.titleRes),
                            tint = colors.greenText,
                            modifier = Modifier
                                .size(IconSize.small)
                                .clickable(onClick = onPreviewRequested),
                        )
                    }
                    if (displayState !is OrotezProtezDocumentState.Uploading) {
                        Icon(
                            imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                            contentDescription = null,
                            tint = colors.textMuted,
                            modifier = Modifier.size(IconSize.small),
                        )
                    }
                }
            }
        }
    }
}

private val LiquidWaveAmplitudeMax = 8.dp
private const val LiquidWaveSampleCount = 32
private const val LiquidFillDurationMillis = 1600

@Composable
private fun LiquidWaveProgressBar(
    modifier: Modifier = Modifier,
    onFillComplete: () -> Unit = {},
) {
    val colors = LocalTaminColors.current
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidFill")

    val fillFraction = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        fillFraction.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = LiquidFillDurationMillis, easing = LinearEasing),
        )
        onFillComplete()
    }
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "LiquidWavePhase",
    )

    val fillColor = colors.blueText.copy(alpha = 0.18f)
    val waveColor = colors.blueText.copy(alpha = 0.3f)

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val leadingEdgeX = canvasWidth - canvasWidth * fillFraction.value
        val amplitude = (canvasHeight * 0.05f).coerceAtMost(LiquidWaveAmplitudeMax.toPx())
        val waveLength = canvasHeight * 1.4f
        val step = (canvasHeight / LiquidWaveSampleCount).coerceAtLeast(1f)

        fun edgeX(y: Float): Float =
            leadingEdgeX + amplitude * sin((y / waveLength) * 2f * PI.toFloat() + wavePhase)
        val edgeYs = buildList {
            var y = 0f
            while (y < canvasHeight) {
                add(y)
                y += step
            }
            add(canvasHeight)
        }

        val fillPath = Path().apply {
            moveTo(canvasWidth, 0f)
            lineTo(canvasWidth, canvasHeight)
            for (y in edgeYs.asReversed()) {
                lineTo(edgeX(y), y)
            }
            close()
        }
        drawPath(path = fillPath, color = fillColor)

        val wavePath = Path().apply {
            edgeYs.forEachIndexed { index, y ->
                val x = edgeX(y)
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        drawPath(
            path = wavePath,
            color = waveColor,
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}

@Composable
private fun OrotezProtezDocumentIconTile(documentState: OrotezProtezDocumentState) {
    val colors = LocalTaminColors.current
    val tileColor = when (documentState) {
        is OrotezProtezDocumentState.Uploaded -> colors.greenText
        is OrotezProtezDocumentState.Failed -> colors.dangerText
        else -> colors.chipBg
    }

    Box(
        modifier = Modifier
            .size(IconSize.xlarge)
            .clip(RoundedCornerShape(CornerRadius.iconTile))
            .background(tileColor),
        contentAlignment = Alignment.Center,
    ) {
        when (documentState) {
            is OrotezProtezDocumentState.Uploading -> Icon(
                imageVector = vectorResource(Res.drawable.ic_place),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.banner),
            )
            is OrotezProtezDocumentState.Uploaded -> Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_check),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.banner),
            )
            is OrotezProtezDocumentState.Failed -> Icon(
                imageVector = vectorResource(Res.drawable.ic_warning),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(IconSize.banner),
            )
            OrotezProtezDocumentState.Empty -> Icon(
                imageVector = vectorResource(Res.drawable.ic_place),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.banner),
            )
        }
    }
}

@Composable
private fun OrotezProtezDocumentState.statusText(): String = when (this) {
    OrotezProtezDocumentState.Empty -> stringResource(Res.string.orotez_protez_document_pick_placeholder)
    is OrotezProtezDocumentState.Uploading -> stringResource(Res.string.orotez_protez_document_status_uploading)
    is OrotezProtezDocumentState.Uploaded -> stringResource(Res.string.orotez_protez_document_status_uploaded)
    is OrotezProtezDocumentState.Failed ->
        "$message ${stringResource(Res.string.orotez_protez_document_status_error_tap_to_retry)}"
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezScreenLight() {
    PreviewRtlThemeContent {
        OrotezProtezContent(
            state = PreviewState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezScreenDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        OrotezProtezContent(
            state = PreviewState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezInsuredInfoStepLight() {
    PreviewRtlThemeContent {
        OrotezProtezContent(
            state = PreviewInsuredInfoState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezInsuredInfoStepDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        OrotezProtezContent(
            state = PreviewInsuredInfoState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezDocumentsStepLight() {
    PreviewRtlThemeContent {
        OrotezProtezContent(
            state = PreviewDocumentsState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezDocumentsStepDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        OrotezProtezContent(
            state = PreviewDocumentsState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezDocumentSourceSheetLight() {
    PreviewRtlThemeContent {
        OrotezProtezContent(
            state = PreviewDocumentSourceState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewOrotezProtezDocumentSourceSheetDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        OrotezProtezContent(
            state = PreviewDocumentSourceState,
            onIntent = {},
            onBackClicked = {},
        )
    }
}

private val PreviewState = OrotezProtezUiState(
    branch = OrotezProtezOptionUi(
        id = "branch-1",
        label = "یک تهران - شرکت ارد پارس اسپادانا",
    ),
)

private val PreviewInsuredInfoState = OrotezProtezUiState(
    currentStep = OrotezProtezStep.InsuredInfo,
    branch = OrotezProtezOptionUi(
        id = "branch-2",
        label = "دو کرج - کارگاه صنعتی البرز",
    ),
    insuredPerson = OrotezProtezOptionUi(
        id = "insured-1",
        label = "اصلی (خود) - رضا دریکوند",
        subtitle = "شماره بیمه ۰۰۵۳۱۸۵۲۴۲",
    ),
    prescriptionDateLabel = "۱۴۰۵/۰۵/۲۵",
    insuredPersonDetails = persistentMapOf(
        "insured-1" to OrotezProtezInsuredDetailUi(
            fullName = "رضا دریکوند",
            firstName = "رضا",
            lastName = "دریکوند",
            relation = "اصلی (خود)",
            relationCode = "1",
            nationalCode = "۴۰۶۰۴۳۴۰۶۱",
            birthCertificateNumber = "۴۰۶۰۴۳۴۰۶۱",
            issuePlace = "خرم آباد",
            birthDateLabel = "۱۳۷۰/۰۷/۱۳",
            bookletValidUntilLabel = "۱۴۰۵/۰۶/۱۵",
        ),
    ),
)

private val PreviewDocumentsState = PreviewInsuredInfoState.copy(
    currentStep = OrotezProtezStep.Documents,
)

private val PreviewDocumentSourceState = PreviewDocumentsState.copy(
    picker = OrotezProtezPicker.DOCUMENT_SOURCE,
    activeDocumentId = OrotezProtezDocumentChecklist.first().id,
)
