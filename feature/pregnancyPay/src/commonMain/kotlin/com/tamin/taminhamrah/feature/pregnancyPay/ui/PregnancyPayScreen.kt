package com.tamin.taminhamrah.feature.pregnancyPay.ui

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.pregnancyPay.camera.CameraPermission
import com.tamin.taminhamrah.feature.pregnancyPay.camera.rememberCameraPermission
import com.tamin.taminhamrah.feature.pregnancyPay.ui.components.PregnancyPayDocumentSourceSheet
import com.tamin.taminhamrah.feature.pregnancyPay.ui.components.PregnancyPayHeader
import com.tamin.taminhamrah.feature.pregnancyPay.ui.components.PregnancyPayOptionSheet
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayDocumentChecklist
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayDocumentState
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayDocumentUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayEvent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayImageSource
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayIntent
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayMainInfoUi
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayPicker
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayRequiredDocumentIds
import com.tamin.taminhamrah.feature.pregnancyPay.ui.contract.PregnancyPayUiState
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.taminSurface
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_info
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.pregnancy_pay_documents_description
import taminx.core.core_ui.pregnancy_pay_documents_title
import taminx.core.core_ui.pregnancy_pay_document_camera_permission_error
import taminx.core.core_ui.pregnancy_pay_document_optional
import taminx.core.core_ui.pregnancy_pay_document_pick_placeholder
import taminx.core.core_ui.pregnancy_pay_document_required
import taminx.core.core_ui.pregnancy_pay_document_required_counter
import taminx.core.core_ui.pregnancy_pay_document_status_error_tap_to_retry
import taminx.core.core_ui.pregnancy_pay_document_status_uploaded
import taminx.core.core_ui.pregnancy_pay_document_status_uploading
import taminx.core.core_ui.pregnancy_pay_field_baby_birth_date
import taminx.core.core_ui.pregnancy_pay_field_branch_placeholder
import taminx.core.core_ui.pregnancy_pay_field_child_national_code
import taminx.core.core_ui.pregnancy_pay_field_child_national_code_2
import taminx.core.core_ui.pregnancy_pay_field_child_national_code_3
import taminx.core.core_ui.pregnancy_pay_field_child_national_code_hint
import taminx.core.core_ui.pregnancy_pay_field_doctor_code
import taminx.core.core_ui.pregnancy_pay_field_doctor_name
import taminx.core.core_ui.pregnancy_pay_field_pregnancy_status
import taminx.core.core_ui.pregnancy_pay_field_pregnancy_type
import taminx.core.core_ui.pregnancy_pay_field_request_type
import taminx.core.core_ui.pregnancy_pay_field_rest_end_date
import taminx.core.core_ui.pregnancy_pay_field_rest_start_date
import taminx.core.core_ui.pregnancy_pay_gender_error_confirm
import taminx.core.core_ui.pregnancy_pay_gender_error_message
import taminx.core.core_ui.pregnancy_pay_gender_error_title
import taminx.core.core_ui.pregnancy_pay_landing_calculate_estimate
import taminx.core.core_ui.pregnancy_pay_landing_label_account
import taminx.core.core_ui.pregnancy_pay_landing_label_bank
import taminx.core.core_ui.pregnancy_pay_landing_label_full_name
import taminx.core.core_ui.pregnancy_pay_landing_label_insurance_status
import taminx.core.core_ui.pregnancy_pay_landing_label_insurance_type
import taminx.core.core_ui.pregnancy_pay_landing_label_mobile
import taminx.core.core_ui.pregnancy_pay_landing_start
import taminx.core.core_ui.pregnancy_pay_landing_summary_title
import taminx.core.core_ui.pregnancy_pay_next_step
import taminx.core.core_ui.pregnancy_pay_pick_branch_subtitle
import taminx.core.core_ui.pregnancy_pay_pick_branch_title
import taminx.core.core_ui.pregnancy_pay_pick_pregnancy_status_subtitle
import taminx.core.core_ui.pregnancy_pay_pick_pregnancy_status_title
import taminx.core.core_ui.pregnancy_pay_pick_pregnancy_type_subtitle
import taminx.core.core_ui.pregnancy_pay_pick_pregnancy_type_title
import taminx.core.core_ui.pregnancy_pay_pick_request_type_subtitle
import taminx.core.core_ui.pregnancy_pay_pick_request_type_title
import taminx.core.core_ui.pregnancy_pay_rest_days_label
import taminx.core.core_ui.pregnancy_pay_step_branch_and_rest
import taminx.core.core_ui.pregnancy_pay_step_branch_and_rest_description
import taminx.core.core_ui.pregnancy_pay_step_branch_and_rest_title
import taminx.core.core_ui.pregnancy_pay_step_doctor_and_request
import taminx.core.core_ui.pregnancy_pay_step_doctor_and_request_description
import taminx.core.core_ui.pregnancy_pay_step_doctor_and_request_title
import taminx.core.core_ui.pregnancy_pay_step_documents
import taminx.core.core_ui.pregnancy_pay_step_pregnancy_and_newborn
import taminx.core.core_ui.pregnancy_pay_step_pregnancy_and_newborn_description
import taminx.core.core_ui.pregnancy_pay_step_pregnancy_and_newborn_title
import taminx.core.core_ui.pregnancy_pay_submit_request
import taminx.core.core_ui.pregnancy_pay_submit_success_confirm
import taminx.core.core_ui.pregnancy_pay_submit_success_fallback
import taminx.core.core_ui.pregnancy_pay_submit_success_title

@Composable
fun PregnancyPayScreen(
    viewModel: PregnancyPayViewModel = koinViewModel(),
    onBackClicked: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var pendingDocumentId by remember { mutableStateOf<String?>(null) }
    val cameraPermission = rememberCameraPermission()
    val cameraPermissionDeniedMessage = stringResource(Res.string.pregnancy_pay_document_camera_permission_error)
    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? ->
        val documentId = pendingDocumentId
        pendingDocumentId = null
        if (documentId != null && file != null) {
            viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(documentId, file))
        }
    }
    val cameraLauncher = rememberCameraPickerLauncher { file: PlatformFile? ->
        val documentId = pendingDocumentId
        pendingDocumentId = null
        if (documentId != null && file != null) {
            viewModel.sendIntent(PregnancyPayIntent.OnDocumentImagePicked(documentId, file))
        }
    }

    HandlePregnancyPayEvents(
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
        if (uiState.currentStep == PregnancyPayStep.Landing) {
            onBackClicked()
        } else {
            viewModel.sendIntent(PregnancyPayIntent.BackToPreviousStep)
        }
    }

    PregnancyPayContent(
        state = uiState,
        onIntent = viewModel::sendIntent,
        onBackClicked = handleBack,
    )
}

@Composable
private fun HandlePregnancyPayEvents(
    events: Flow<PregnancyPayEvent>,
    cameraPermission: CameraPermission,
    cameraPermissionDeniedMessage: String,
    scope: CoroutineScope,
    onBackClicked: () -> Unit,
    onLaunchGallery: (documentId: String) -> Unit,
    onLaunchCamera: (documentId: String) -> Unit,
    onIntent: (PregnancyPayIntent) -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            PregnancyPayEvent.NavigateBack -> onBackClicked()
            is PregnancyPayEvent.LaunchImagePicker -> when (event.source) {
                PregnancyPayImageSource.GALLERY -> onLaunchGallery(event.documentId)
                PregnancyPayImageSource.CAMERA -> {
                    if (cameraPermission.granted) {
                        onLaunchCamera(event.documentId)
                    } else {
                        cameraPermission.request { granted ->
                            scope.launch {
                                if (granted) {
                                    onLaunchCamera(event.documentId)
                                } else {
                                    onIntent(PregnancyPayIntent.OnDocumentImagePickFailed(cameraPermissionDeniedMessage))
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
private fun PregnancyPayContent(
    modifier: Modifier = Modifier,
    state: PregnancyPayUiState,
    onIntent: (PregnancyPayIntent) -> Unit,
    onBackClicked: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.bgPage),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PregnancyPayHeader(onBackClicked = onBackClicked)

            if (state.currentStep != PregnancyPayStep.Landing) {
                PregnancyPayStepIndicator(
                    currentStep = state.currentStep,
                    modifier = Modifier.padding(horizontal = Spacing.page, vertical = Spacing.lg),
                )
            }

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
                label = "PregnancyPayStepTransition",
                modifier = Modifier.weight(1f),
            ) { step ->
                when (step) {
                    PregnancyPayStep.Landing -> Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        if (state.isLoading) {
                            PregnancyPayLandingShimmer()
                        } else {
                            PregnancyPayLandingStep(state = state, onIntent = onIntent)
                        }
                    }

                    PregnancyPayStep.BranchAndRest -> PregnancyPayBranchAndRestStep(state = state, onIntent = onIntent)

                    PregnancyPayStep.PregnancyAndNewborn -> PregnancyPayPregnancyAndNewbornStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = onBackClicked,
                    )

                    PregnancyPayStep.DoctorAndRequest -> PregnancyPayDoctorAndRequestStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = onBackClicked,
                    )

                    PregnancyPayStep.Documents -> PregnancyPayDocumentsStep(
                        state = state,
                        onIntent = onIntent,
                        onBack = onBackClicked,
                    )
                }
            }
        }
    }

    when (state.picker) {
        PregnancyPayPicker.BRANCH -> PregnancyPayOptionSheet(
            title = stringResource(Res.string.pregnancy_pay_pick_branch_title),
            subtitle = stringResource(Res.string.pregnancy_pay_pick_branch_subtitle),
            options = state.branchOptions,
            selectedId = state.branch?.id,
            onSelect = { onIntent(PregnancyPayIntent.OnBranchPicked(it)) },
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
        )

        PregnancyPayPicker.PREGNANCY_STATUS -> PregnancyPayOptionSheet(
            title = stringResource(Res.string.pregnancy_pay_pick_pregnancy_status_title),
            subtitle = stringResource(Res.string.pregnancy_pay_pick_pregnancy_status_subtitle),
            options = state.pregnancyStatusOptions,
            selectedId = state.pregnancyStatus?.id,
            onSelect = { onIntent(PregnancyPayIntent.OnPregnancyStatusPicked(it)) },
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
        )

        PregnancyPayPicker.PREGNANCY_TYPE -> PregnancyPayOptionSheet(
            title = stringResource(Res.string.pregnancy_pay_pick_pregnancy_type_title),
            subtitle = stringResource(Res.string.pregnancy_pay_pick_pregnancy_type_subtitle),
            options = state.pregnancyTypeOptions,
            selectedId = state.pregnancyType?.id,
            onSelect = { onIntent(PregnancyPayIntent.OnPregnancyTypePicked(it)) },
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
        )

        PregnancyPayPicker.REQUEST_TYPE -> PregnancyPayOptionSheet(
            title = stringResource(Res.string.pregnancy_pay_pick_request_type_title),
            subtitle = stringResource(Res.string.pregnancy_pay_pick_request_type_subtitle),
            options = state.requestTypeOptions,
            selectedId = state.requestType?.id,
            onSelect = { onIntent(PregnancyPayIntent.OnRequestTypePicked(it)) },
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
        )

        PregnancyPayPicker.REST_START_DATE -> TaminJalaliDatePicker(
            title = stringResource(Res.string.pregnancy_pay_field_rest_start_date),
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(
                    PregnancyPayIntent.OnRestStartDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )

        PregnancyPayPicker.REST_END_DATE -> TaminJalaliDatePicker(
            title = stringResource(Res.string.pregnancy_pay_field_rest_end_date),
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(
                    PregnancyPayIntent.OnRestEndDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )

        PregnancyPayPicker.BABY_BIRTH_DATE -> TaminJalaliDatePicker(
            title = stringResource(Res.string.pregnancy_pay_field_baby_birth_date),
            onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
            onConfirm = { year, month, day ->
                onIntent(
                    PregnancyPayIntent.OnBabyBirthDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )

        PregnancyPayPicker.DOCUMENT_SOURCE -> {
            val activeDocument = PregnancyPayDocumentChecklist.find { it.id == state.activeDocumentId }
            if (activeDocument != null) {
                val hasFile = state.documents[activeDocument.id]?.let {
                    it !is PregnancyPayDocumentState.Empty
                } == true
                PregnancyPayDocumentSourceSheet(
                    title = stringResource(activeDocument.titleRes),
                    showRemoveOption = hasFile,
                    onSelect = {
                        onIntent(
                            PregnancyPayIntent.OnDocumentSourceSelected(
                                documentId = activeDocument.id,
                                source = it,
                            )
                        )
                    },
                    onRemove = { onIntent(PregnancyPayIntent.OnDocumentRemoveClicked(activeDocument.id)) },
                    onDismiss = { onIntent(PregnancyPayIntent.OnPickerDismissed) },
                )
            }
        }

        PregnancyPayPicker.NONE -> Unit
    }

    if (state.isMaleBlocked) {
        val dialogColors = LocalTaminColors.current
        TaminConfirmationDialog(
            title = stringResource(Res.string.pregnancy_pay_gender_error_title),
            description = stringResource(Res.string.pregnancy_pay_gender_error_message),
            icon = Icons.Filled.Warning,
            iconTint = dialogColors.dangerText,
            iconBackground = dialogColors.dangerBg,
            confirmButton = {
                TaminFilledButton(
                    text = stringResource(Res.string.pregnancy_pay_gender_error_confirm),
                    onClick = { onIntent(PregnancyPayIntent.OnGenderBlockAcknowledged) },
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            dismissButton = {},
            onDismissRequest = { onIntent(PregnancyPayIntent.OnGenderBlockAcknowledged) },
        )
    }

    ErrorStateView(
        message = state.error,
        onDismiss = onBackClicked,
        onRetry = { onIntent(PregnancyPayIntent.LoadInitialData) },
    )
}

@Composable
private fun PregnancyPayStepIndicator(
    currentStep: PregnancyPayStep,
    modifier: Modifier = Modifier,
) {
    val currentIndex = currentStep.index
    StepIndicator(
        modifier = modifier,
        steps = persistentListOf(
            StepIndicatorModel(
                title = stringResource(Res.string.pregnancy_pay_step_branch_and_rest),
                stepNumber = "۱",
                state = when {
                    currentIndex == 0 -> StepState.Active
                    currentIndex > 0 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.pregnancy_pay_step_pregnancy_and_newborn),
                stepNumber = "۲",
                state = when {
                    currentIndex == 1 -> StepState.Active
                    currentIndex > 1 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.pregnancy_pay_step_doctor_and_request),
                stepNumber = "۳",
                state = when {
                    currentIndex == 2 -> StepState.Active
                    currentIndex > 2 -> StepState.Completed
                    else -> StepState.Inactive
                },
            ),
            StepIndicatorModel(
                title = stringResource(Res.string.pregnancy_pay_step_documents),
                stepNumber = "۴",
                state = if (currentIndex == 3) StepState.Active else StepState.Inactive,
            ),
        ),
    )
}

@Composable
private fun PregnancyPayLandingStep(
    state: PregnancyPayUiState,
    onIntent: (PregnancyPayIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val info = state.mainInfo

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        if (info != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .taminSurface()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
            ) {
                Text(
                    text = stringResource(Res.string.pregnancy_pay_landing_summary_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                Spacer(Modifier.height(Spacing.sm))
                DetailRow(
                    label = stringResource(Res.string.pregnancy_pay_landing_label_full_name),
                    value = info.fullName,
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                DetailRow(
                    label = stringResource(Res.string.pregnancy_pay_landing_label_insurance_type),
                    value = info.insuranceTypeDesc.orEmpty(),
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.pregnancy_pay_landing_label_insurance_status),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textMuted,
                    )
                    StatusPill(
                        text = info.insuranceStatusDesc.orEmpty(),
                        containerColor = colors.greenBg,
                        contentColor = colors.greenText,
                    )
                }
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                DetailRow(
                    label = stringResource(Res.string.pregnancy_pay_landing_label_mobile),
                    value = info.mobileNumber.orEmpty(),
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                DetailRow(
                    label = stringResource(Res.string.pregnancy_pay_landing_label_bank),
                    value = info.bankName.orEmpty(),
                    numeric = false,
                )
                TaminDivider(modifier = Modifier.padding(vertical = Spacing.xs))
                DetailRow(
                    label = stringResource(Res.string.pregnancy_pay_landing_label_account),
                    value = info.bankAccount.orEmpty(),
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(colors.blueBg)
                .clickable { onIntent(PregnancyPayIntent.OnCalculateEstimateClicked) }
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Icon(
                imageVector = vectorResource(Res.drawable.ic_info),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
            Text(
                text = stringResource(Res.string.pregnancy_pay_landing_calculate_estimate),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.blueText,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = vectorResource(Res.drawable.ic_tamin_chevron_back),
                contentDescription = null,
                tint = colors.blueText,
                modifier = Modifier.size(IconSize.small),
            )
        }

        LoadingButton(
            text = stringResource(Res.string.pregnancy_pay_landing_start),
            onClick = { onIntent(PregnancyPayIntent.OnLandingStartClicked) },
            icon = Icons.Filled.Add,
        )
    }
}

@Composable
private fun PregnancyPayLandingShimmer(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .shimmer(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(ButtonDimensHeightFallback)
                .clip(RoundedCornerShape(CornerRadius.xl))
                .shimmer(),
        )
    }
}

private val ButtonDimensHeightFallback = 52.dp

@Composable
private fun PregnancyPayBranchAndRestStep(
    state: PregnancyPayUiState,
    onIntent: (PregnancyPayIntent) -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.pregnancy_pay_step_branch_and_rest_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.xs))
        Text(
            text = stringResource(Res.string.pregnancy_pay_step_branch_and_rest_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Spacer(Modifier.height(Spacing.lg))

        PickerRow(
            text = state.branch?.label ?: stringResource(Res.string.pregnancy_pay_field_branch_placeholder),
            isPlaceholder = state.branch == null,
            showChevron = true,
            onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.BRANCH)) },
        )
        Spacer(Modifier.height(Spacing.sm))

        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            PickerRow(
                modifier = Modifier.weight(1f),
                text = state.restStartDateLabel ?: stringResource(Res.string.pregnancy_pay_field_rest_start_date),
                isPlaceholder = state.restStartDateLabel == null,
                onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.REST_START_DATE)) },
            )
            PickerRow(
                modifier = Modifier.weight(1f),
                text = state.restEndDateLabel ?: stringResource(Res.string.pregnancy_pay_field_rest_end_date),
                isPlaceholder = state.restEndDateLabel == null,
                onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.REST_END_DATE)) },
            )
        }

        state.restDaysCount?.let { days ->
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.pregnancy_pay_rest_days_label, days.toString()),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        LoadingButton(
            text = stringResource(Res.string.pregnancy_pay_next_step),
            onClick = { onIntent(PregnancyPayIntent.OnNextFromBranchAndRestClicked) },
            enabled = state.canGoNextFromBranchAndRest,
        )
    }
}

@Composable
private fun PregnancyPayPregnancyAndNewbornStep(
    state: PregnancyPayUiState,
    onIntent: (PregnancyPayIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.pregnancy_pay_step_pregnancy_and_newborn_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.pregnancy_pay_step_pregnancy_and_newborn_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.lg))

            PickerRow(
                text = state.babyBirthDateLabel ?: stringResource(Res.string.pregnancy_pay_field_baby_birth_date),
                isPlaceholder = state.babyBirthDateLabel == null,
                onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.BABY_BIRTH_DATE)) },
            )
            Spacer(Modifier.height(Spacing.sm))

            PickerRow(
                text = state.pregnancyStatus?.label ?: stringResource(Res.string.pregnancy_pay_field_pregnancy_status),
                isPlaceholder = state.pregnancyStatus == null,
                showChevron = true,
                onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.PREGNANCY_STATUS)) },
            )
            Spacer(Modifier.height(Spacing.sm))

            PickerRow(
                text = state.pregnancyType?.label ?: stringResource(Res.string.pregnancy_pay_field_pregnancy_type),
                isPlaceholder = state.pregnancyType == null,
                showChevron = true,
                onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.PREGNANCY_TYPE)) },
            )
            Spacer(Modifier.height(Spacing.sm))

            TaminTextField(
                value = state.childNationalCode,
                onValueChange = { onIntent(PregnancyPayIntent.OnChildNationalCodeChanged(1, it)) },
                label = stringResource(Res.string.pregnancy_pay_field_child_national_code),
                placeholder = stringResource(Res.string.pregnancy_pay_field_child_national_code_hint),
                keyboardType = KeyboardType.Number,
            )

            if (state.requiredChildNationalCodeCount >= 2) {
                Spacer(Modifier.height(Spacing.sm))
                TaminTextField(
                    value = state.childNationalCode2,
                    onValueChange = { onIntent(PregnancyPayIntent.OnChildNationalCodeChanged(2, it)) },
                    label = stringResource(Res.string.pregnancy_pay_field_child_national_code_2),
                    placeholder = stringResource(Res.string.pregnancy_pay_field_child_national_code_hint),
                    keyboardType = KeyboardType.Number,
                )
            }

            if (state.requiredChildNationalCodeCount >= 3) {
                Spacer(Modifier.height(Spacing.sm))
                TaminTextField(
                    value = state.childNationalCode3,
                    onValueChange = { onIntent(PregnancyPayIntent.OnChildNationalCodeChanged(3, it)) },
                    label = stringResource(Res.string.pregnancy_pay_field_child_national_code_3),
                    placeholder = stringResource(Res.string.pregnancy_pay_field_child_national_code_hint),
                    keyboardType = KeyboardType.Number,
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
            PregnancyPayBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.pregnancy_pay_next_step),
                onClick = { onIntent(PregnancyPayIntent.OnNextFromPregnancyAndNewbornClicked) },
                enabled = state.canGoNextFromPregnancyAndNewborn,
            )
        }
    }
}

@Composable
private fun PregnancyPayDoctorAndRequestStep(
    state: PregnancyPayUiState,
    onIntent: (PregnancyPayIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.pregnancy_pay_step_doctor_and_request_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.pregnancy_pay_step_doctor_and_request_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.lg))

            PickerRow(
                text = state.requestType?.label ?: stringResource(Res.string.pregnancy_pay_field_request_type),
                isPlaceholder = state.requestType == null,
                showChevron = true,
                onClick = { onIntent(PregnancyPayIntent.OnPickerRequested(PregnancyPayPicker.REQUEST_TYPE)) },
            )
            Spacer(Modifier.height(Spacing.sm))

            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                modifier = Modifier.fillMaxWidth(),
            ) {
                TaminTextField(
                    modifier = Modifier.weight(1f),
                    value = state.doctorCode,
                    onValueChange = { onIntent(PregnancyPayIntent.OnDoctorCodeChanged(it)) },
                    label = stringResource(Res.string.pregnancy_pay_field_doctor_code),
                    keyboardType = KeyboardType.Number,
                )
                TaminTextField(
                    modifier = Modifier.weight(1f),
                    value = state.doctorName,
                    onValueChange = { onIntent(PregnancyPayIntent.OnDoctorNameChanged(it)) },
                    label = stringResource(Res.string.pregnancy_pay_field_doctor_name),
                )
            }

            state.doctorAndRequestError?.let { message ->
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
            PregnancyPayBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.pregnancy_pay_next_step),
                onClick = { onIntent(PregnancyPayIntent.OnNextFromDoctorAndRequestClicked) },
                enabled = state.canGoNextFromDoctorAndRequest,
            )
        }
    }
}

private val BackStepButtonSize = 56.dp

@Composable
private fun PregnancyPayBackStepButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.xl)

    Box(
        modifier = modifier
            .size(BackStepButtonSize)
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
private fun PregnancyPayDocumentsStep(
    state: PregnancyPayUiState,
    onIntent: (PregnancyPayIntent) -> Unit,
    onBack: () -> Unit,
) {
    val colors = LocalTaminColors.current

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        ) {
            Text(
                text = stringResource(Res.string.pregnancy_pay_documents_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight(700)),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = stringResource(Res.string.pregnancy_pay_documents_description),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.md))

            StatusPill(
                text = stringResource(
                    Res.string.pregnancy_pay_document_required_counter,
                    state.requiredDocumentsUploadedCount.toString(),
                    PregnancyPayRequiredDocumentIds.size.toString(),
                ),
                containerColor = colors.bgSurface,
                contentColor = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.md))

            PregnancyPayDocumentChecklist.forEachIndexed { index, document ->
                PregnancyPayDocumentCard(
                    document = document,
                    documentState = state.documents[document.id] ?: PregnancyPayDocumentState.Empty,
                    onClick = { onIntent(PregnancyPayIntent.OnDocumentCardClicked(document.id)) },
                )
                if (index != PregnancyPayDocumentChecklist.lastIndex) {
                    Spacer(Modifier.height(Spacing.md))
                }
            }

            state.documentPickError?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = colors.dangerText)
            }
            state.documentValidationError?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = colors.dangerText)
            }
            state.submitError?.let { message ->
                Spacer(Modifier.height(Spacing.sm))
                Text(text = message, style = MaterialTheme.typography.bodySmall, color = colors.dangerText)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.page, vertical = Spacing.md)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            PregnancyPayBackStepButton(onClick = onBack)
            LoadingButton(
                modifier = Modifier.weight(1f),
                text = stringResource(Res.string.pregnancy_pay_submit_request),
                onClick = { onIntent(PregnancyPayIntent.OnSubmitDocumentsClicked) },
                enabled = state.canSubmitDocuments,
                isLoading = state.isSubmitting,
            )
        }
    }

    if (state.hasSubmitted) {
        PregnancyPaySubmitSuccessDialog(
            message = state.submittedResultMessage,
            onAcknowledged = { onIntent(PregnancyPayIntent.OnSubmitSuccessAcknowledged) },
        )
    }
}

@Composable
private fun PregnancyPaySubmitSuccessDialog(
    message: String?,
    onAcknowledged: () -> Unit,
) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.pregnancy_pay_submit_success_title),
        description = message ?: stringResource(Res.string.pregnancy_pay_submit_success_fallback),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.pregnancy_pay_submit_success_confirm),
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
private fun PregnancyPayDocumentCard(
    document: PregnancyPayDocumentUi,
    documentState: PregnancyPayDocumentState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.card)

    val (background, border) = when (documentState) {
        is PregnancyPayDocumentState.Uploaded -> colors.greenBg to colors.greenBorder
        is PregnancyPayDocumentState.Failed -> colors.dangerBg to colors.dangerBorder
        else -> colors.bgSurface to colors.border
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .border(Thickness.border, border, shape)
            .clickable(enabled = documentState !is PregnancyPayDocumentState.Uploading, onClick = onClick)
            .padding(Spacing.lg),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            PregnancyPayDocumentIconTile(documentState)

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
                                Res.string.pregnancy_pay_document_required
                            } else {
                                Res.string.pregnancy_pay_document_optional
                            },
                        ),
                        containerColor = if (document.isRequired) colors.blueBg else colors.bgPage,
                        contentColor = if (document.isRequired) colors.blueText else colors.textMuted,
                    )
                }

                Spacer(Modifier.height(Spacing.xxs))

                Text(
                    text = document.subtitleRes?.let { stringResource(it) } ?: documentState.statusText(),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (documentState is PregnancyPayDocumentState.Failed) {
                        colors.dangerText
                    } else {
                        colors.textMuted
                    },
                )
            }
        }

        if (documentState is PregnancyPayDocumentState.Uploading) {
            CircularProgressIndicator(
                modifier = Modifier.size(IconSize.small),
                color = colors.blueText,
                strokeWidth = Thickness.medium,
            )
        }
    }
}

@Composable
private fun PregnancyPayDocumentIconTile(documentState: PregnancyPayDocumentState) {
    val colors = LocalTaminColors.current
    val tileColor = when (documentState) {
        is PregnancyPayDocumentState.Uploaded -> colors.greenText
        is PregnancyPayDocumentState.Failed -> colors.dangerText
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
            is PregnancyPayDocumentState.Uploaded -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
            )
            is PregnancyPayDocumentState.Failed -> Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color.White,
            )
            else -> Unit
        }
    }
}

@Composable
private fun PregnancyPayDocumentState.statusText(): String = when (this) {
    PregnancyPayDocumentState.Empty -> stringResource(Res.string.pregnancy_pay_document_pick_placeholder)
    is PregnancyPayDocumentState.Uploading -> stringResource(Res.string.pregnancy_pay_document_status_uploading)
    is PregnancyPayDocumentState.Uploaded -> stringResource(Res.string.pregnancy_pay_document_status_uploaded)
    is PregnancyPayDocumentState.Failed ->
        "$message ${stringResource(Res.string.pregnancy_pay_document_status_error_tap_to_retry)}"
}

@PreviewRtlTheme
@Composable
private fun PreviewPregnancyPayLandingLight() {
    PreviewRtlThemeContent {
        PregnancyPayContent(state = PreviewLandingState, onIntent = {}, onBackClicked = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPregnancyPayLandingDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        PregnancyPayContent(state = PreviewLandingState, onIntent = {}, onBackClicked = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPregnancyPayBranchAndRestLight() {
    PreviewRtlThemeContent {
        PregnancyPayContent(state = PreviewBranchAndRestState, onIntent = {}, onBackClicked = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PreviewPregnancyPayDocumentsLight() {
    PreviewRtlThemeContent {
        PregnancyPayContent(state = PreviewDocumentsState, onIntent = {}, onBackClicked = {})
    }
}

private val PreviewMainInfo = PregnancyPayMainInfoUi(
    risuid = "risuid-1",
    nationalCode = "0012345678",
    firstName = "سروین",
    lastName = "نامی",
    mobileNumber = "09338042024",
    serviceDateTimeStamp = 0,
    bankAccount = "5678123459870012",
    bankName = "بانک ملت",
    insuranceTypeDesc = "بیمهٔ اجباری کارگری",
    insuranceStatusDesc = "برخوردار",
)

private val PreviewLandingState = PregnancyPayUiState(
    currentStep = PregnancyPayStep.Landing,
    mainInfo = PreviewMainInfo,
)

private val PreviewBranchAndRestState = PreviewLandingState.copy(
    currentStep = PregnancyPayStep.BranchAndRest,
)

private val PreviewDocumentsState = PreviewLandingState.copy(
    currentStep = PregnancyPayStep.Documents,
)
