package com.tamin.taminhamrah.feature.requestPaymentForIllDays.ui.wizard

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.BackHandler
import com.tamin.taminhamrah.ui.components.ErrorStateView
import com.tamin.taminhamrah.ui.components.LoadingButton
import com.tamin.taminhamrah.ui.components.LoadingButtonIconPosition
import com.tamin.taminhamrah.ui.components.PickerRow
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.StepIndicator
import com.tamin.taminhamrah.ui.components.StepIndicatorModel
import com.tamin.taminhamrah.ui.components.StepState
import com.tamin.taminhamrah.ui.components.TaminBottomBar
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.TaminJalaliDatePicker
import com.tamin.taminhamrah.ui.components.TaminSwitchButton
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.TaminTopAppBar
import com.tamin.taminhamrah.ui.components.TaminTopAppBarButton
import com.tamin.taminhamrah.ui.components.bottomsheet.TaminSearchableListSheet
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.toast.success
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.ButtonDimens
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.PersianDateFormatter
import com.tamin.taminhamrah.util.rememberCameraPermission
import com.tamin.taminhamrah.util.toPersianDigits
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.ic_branch
import taminx.core.core_ui.ic_calculator
import taminx.core.core_ui.ic_place
import taminx.core.core_ui.ic_tamin_calendar
import taminx.core.core_ui.ic_tamin_chevron_back
import taminx.core.core_ui.ic_tamin_chevron_forward
import taminx.core.core_ui.ill_days_cd_back
import taminx.core.core_ui.ill_days_cd_calculator
import taminx.core.core_ui.ill_days_covid_end_label
import taminx.core.core_ui.ill_days_covid_info_prefix
import taminx.core.core_ui.ill_days_covid_start_label
import taminx.core.core_ui.ill_days_covid_toggle_caption
import taminx.core.core_ui.ill_days_covid_toggle_label
import taminx.core.core_ui.ill_days_day_count_badge
import taminx.core.core_ui.ill_days_doc_title
import taminx.core.core_ui.ill_days_doc_uploaded_status
import taminx.core.core_ui.ill_days_docs_add
import taminx.core.core_ui.ill_days_docs_count_badge
import taminx.core.core_ui.ill_days_docs_instructions
import taminx.core.core_ui.ill_days_docs_section_title
import taminx.core.core_ui.ill_days_docs_source_title
import taminx.core.core_ui.ill_days_doctor_code_label
import taminx.core.core_ui.ill_days_doctor_code_placeholder
import taminx.core.core_ui.ill_days_doctor_name_label
import taminx.core.core_ui.ill_days_doctor_name_placeholder
import taminx.core.core_ui.ill_days_medical_record_toggle_caption
import taminx.core.core_ui.ill_days_medical_record_toggle_label
import taminx.core.core_ui.ill_days_next_step
import taminx.core.core_ui.ill_days_pick_date_placeholder
import taminx.core.core_ui.ill_days_rest_end_label
import taminx.core.core_ui.ill_days_rest_start_label
import taminx.core.core_ui.ill_days_submit_btn
import taminx.core.core_ui.ill_days_title
import taminx.core.core_ui.ill_days_wizard_branch_label
import taminx.core.core_ui.ill_days_wizard_branch_placeholder
import taminx.core.core_ui.ill_days_wizard_city_label
import taminx.core.core_ui.ill_days_wizard_city_placeholder
import taminx.core.core_ui.ill_days_wizard_city_search
import taminx.core.core_ui.ill_days_wizard_pick_branch_subtitle
import taminx.core.core_ui.ill_days_wizard_pick_branch_title
import taminx.core.core_ui.ill_days_wizard_pick_city_subtitle
import taminx.core.core_ui.ill_days_wizard_pick_city_title
import taminx.core.core_ui.ill_days_wizard_step1_description
import taminx.core.core_ui.ill_days_wizard_step1_title
import taminx.core.core_ui.ill_days_wizard_step2_description
import taminx.core.core_ui.ill_days_wizard_step2_title
import taminx.core.core_ui.ill_days_wizard_step3_description
import taminx.core.core_ui.ill_days_wizard_step3_title
import taminx.core.core_ui.ill_days_wizard_step4_description
import taminx.core.core_ui.ill_days_wizard_step4_title
import taminx.core.core_ui.ill_days_wizard_step_branch_city
import taminx.core.core_ui.ill_days_wizard_step_doctor
import taminx.core.core_ui.ill_days_wizard_step_documents
import taminx.core.core_ui.ill_days_wizard_step_rest_days
import taminx.core.core_ui.occurrence_camera_permission_denied
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

private const val MAX_DOCUMENTS = 5
private const val CITY_SEARCH_DEBOUNCE_MS = 1000L

@Composable
fun IllDaysWizardScreen(
    onBack: () -> Unit,
    onOpenCalculate: () -> Unit,
    viewModel: IllDaysWizardViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()
    val cameraPermission = rememberCameraPermission()

    fun handlePicked(file: PlatformFile?) {
        if (file == null) return
        scope.launch {
            try {
                val bytes = file.readBytes()
                viewModel.sendIntent(
                    IllDaysWizardIntent.DocumentImagePicked(
                        fileName = file.name,
                        bytes = bytes,
                    )
                )
            } catch (_: Exception) {
                toaster.error(getString(Res.string.error_file_read_fallback))
            }
        }
    }

    val galleryLauncher =
        rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? ->
            handlePicked(file)
        }
    val cameraLauncher = rememberCameraPickerLauncher { file: PlatformFile? ->
        handlePicked(file)
    }

    HandleIllDaysWizardEvents(
        events = viewModel.events,
        onBack = onBack,
        onOpenCalculate = onOpenCalculate,
        onShowToast = { toaster.error(it) },
        onShowSuccess = { toaster.success(it) },
        onLaunchGallery = { galleryLauncher.launch() },
        onLaunchCamera = {
            if (cameraPermission.granted) {
                cameraLauncher.launch()
            } else {
                cameraPermission.request { granted ->
                    if (granted) {
                        cameraLauncher.launch()
                    } else {
                        scope.launch {
                            toaster.error(getString(Res.string.occurrence_camera_permission_denied))
                        }
                    }
                }
            }
        },
    )

    BackHandler(onBack = { viewModel.sendIntent(IllDaysWizardIntent.Back) })

    IllDaysWizardContent(
        state = state,
        onIntent = viewModel::sendIntent,
    )
}

@Composable
private fun HandleIllDaysWizardEvents(
    events: Flow<IllDaysWizardEvent>,
    onBack: () -> Unit,
    onOpenCalculate: () -> Unit,
    onShowToast: (String) -> Unit,
    onShowSuccess: (String) -> Unit,
    onLaunchGallery: () -> Unit,
    onLaunchCamera: () -> Unit,
) {
    events.collectWithLifecycleAware { event ->
        when (event) {
            IllDaysWizardEvent.NavigateBack -> onBack()
            IllDaysWizardEvent.NavigateToCalculate -> onOpenCalculate()
            IllDaysWizardEvent.LaunchCamera -> onLaunchCamera()
            IllDaysWizardEvent.LaunchGallery -> onLaunchGallery()
            is IllDaysWizardEvent.ShowToast -> onShowToast(event.message)
            is IllDaysWizardEvent.ShowSuccess -> onShowSuccess(event.message)
        }
    }
}

@Composable
private fun IllDaysWizardContent(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val headerBrush = Brush.horizontalGradient(colors.profileGradientStops)
    val step1 = stringResource(Res.string.ill_days_wizard_step_branch_city)
    val step2 = stringResource(Res.string.ill_days_wizard_step_rest_days)
    val step3 = stringResource(Res.string.ill_days_wizard_step_doctor)
    val step4 = stringResource(Res.string.ill_days_wizard_step_documents)
    val currentOrdinal = state.currentStep.ordinal
    val steps = remember(currentOrdinal, step1, step2, step3, step4) {
        persistentListOf(
            StepIndicatorModel(
                title = step1,
                stepNumber = "۱",
                state = stepStateFor(0, currentOrdinal),
            ),
            StepIndicatorModel(
                title = step2,
                stepNumber = "۲",
                state = stepStateFor(1, currentOrdinal),
            ),
            StepIndicatorModel(
                title = step3,
                stepNumber = "۳",
                state = stepStateFor(2, currentOrdinal),
            ),
            StepIndicatorModel(
                title = step4,
                stepNumber = "۴",
                state = stepStateFor(3, currentOrdinal),
            ),
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = colors.bgPage,
        topBar = {
            TaminTopAppBar(
                title = stringResource(Res.string.ill_days_title),
                background = headerBrush,
                shape = RoundedCornerShape(
                    bottomStart = CornerRadius.x3l,
                    bottomEnd = CornerRadius.x3l,
                ),
                navigationIcon = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_back),
                        contentDescription = stringResource(Res.string.ill_days_cd_back),
                        onClick = { onIntent(IllDaysWizardIntent.Back) },
                        bordered = true,
                    )
                },
                action = {
                    TaminTopAppBarButton(
                        icon = vectorResource(Res.drawable.ic_calculator),
                        contentDescription = stringResource(Res.string.ill_days_cd_calculator),
                        onClick = { onIntent(IllDaysWizardIntent.OpenCalculate) },
                        bordered = true,
                    )
                },
            )
        },
        bottomBar = {
            if (!state.isLoading && state.errorMessage == null) {
                IllDaysWizardBottomBar(state = state, onIntent = onIntent)
            }
        },
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = colors.blueText)
                }
            }
            state.errorMessage != null -> {
                ErrorStateView(
                    message = state.errorMessage,
                    onDismiss = { onIntent(IllDaysWizardIntent.Back) },
                    onRetry = { onIntent(IllDaysWizardIntent.Retry) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                ) {
                    StepIndicator(
                        steps = steps,
                        modifier = Modifier.padding(
                            start = Spacing.lg,
                            end = Spacing.lg,
                            top = Spacing.md,
                            bottom = Spacing.md,
                        ),
                    )
                    AnimatedContent(
                        targetState = state.currentStep,
                        modifier = Modifier.weight(1f),
                        transitionSpec = {
                            if (targetState.ordinal > initialState.ordinal) {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                            } else {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                            }
                        },
                        label = "illDaysWizardStep",
                    ) { step ->
                        when (step) {
                            IllDaysWizardStep.BranchCity -> BranchCityStep(
                                state = state,
                                onIntent = onIntent,
                            )
                            IllDaysWizardStep.RestDays -> RestDaysStep(
                                state = state,
                                onIntent = onIntent,
                            )
                            IllDaysWizardStep.Doctor -> DoctorStep(
                                state = state,
                                onIntent = onIntent,
                            )
                            IllDaysWizardStep.Documents -> DocumentsStep(
                                state = state,
                                onIntent = onIntent,
                            )
                        }
                    }
                }
            }
        }
    }

    IllDaysWizardPickers(state = state, onIntent = onIntent)

    val previewDoc = remember(state.previewDocumentLocalId, state.documents) {
        state.documents.firstOrNull { it.localId == state.previewDocumentLocalId }
    }
    val previewBase64 = rememberBase64Thumbnail(previewDoc?.bytes)
    if (previewDoc != null && previewBase64 != null) {
        TaminImageViewer(
            title = previewDoc.title,
            url = previewBase64,
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPreview) },
        )
    }
}

private fun stepStateFor(stepOrdinal: Int, currentOrdinal: Int): StepState = when {
    stepOrdinal < currentOrdinal -> StepState.Completed
    stepOrdinal == currentOrdinal -> StepState.Active
    else -> StepState.Inactive
}

@Composable
private fun IllDaysWizardBottomBar(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    TaminBottomBar(
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding(),
    ) {
        when (state.currentStep) {
            IllDaysWizardStep.BranchCity -> {
                LoadingButton(
                    text = stringResource(Res.string.ill_days_next_step),
                    onClick = { onIntent(IllDaysWizardIntent.NextStep) },
                    enabled = state.canGoNextFromStep1,
                    modifier = Modifier.fillMaxWidth(),
                    icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    iconPosition = LoadingButtonIconPosition.TRAILING,
                )
            }
            IllDaysWizardStep.RestDays -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    IllDaysStepBackButton(onClick = { onIntent(IllDaysWizardIntent.PreviousStep) })
                    LoadingButton(
                        text = stringResource(Res.string.ill_days_next_step),
                        onClick = { onIntent(IllDaysWizardIntent.NextStep) },
                        enabled = state.canGoNextFromStep2 && !state.isCovidLoading,
                        isLoading = state.isCovidLoading,
                        modifier = Modifier.weight(1f),
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                }
            }
            IllDaysWizardStep.Doctor -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    IllDaysStepBackButton(onClick = { onIntent(IllDaysWizardIntent.PreviousStep) })
                    LoadingButton(
                        text = stringResource(Res.string.ill_days_next_step),
                        onClick = { onIntent(IllDaysWizardIntent.NextStep) },
                        enabled = state.canGoNextFromStep3,
                        modifier = Modifier.weight(1f),
                        icon = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                        iconPosition = LoadingButtonIconPosition.TRAILING,
                    )
                }
            }
            IllDaysWizardStep.Documents -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.smd),
                ) {
                    IllDaysStepBackButton(onClick = { onIntent(IllDaysWizardIntent.PreviousStep) })
                    LoadingButton(
                        text = stringResource(Res.string.ill_days_submit_btn),
                        onClick = { onIntent(IllDaysWizardIntent.Submit) },
                        enabled = state.canSubmit,
                        isLoading = state.isSubmitting,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun IllDaysStepBackButton(
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
            contentDescription = stringResource(Res.string.ill_days_cd_back),
            tint = colors.textPrimary,
        )
    }
}

@Composable
private fun BranchCityStep(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg)
            .taminSurface(cornerRadius = CornerRadius.cardCompact)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_wizard_step1_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.ill_days_wizard_step1_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = stringResource(Res.string.ill_days_wizard_branch_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
            )
            PickerRow(
                text = state.selectedBranch?.label
                    ?: stringResource(Res.string.ill_days_wizard_branch_placeholder),
                onClick = { onIntent(IllDaysWizardIntent.OpenBranchPicker) },
                isPlaceholder = state.selectedBranch == null,
                icon = vectorResource(Res.drawable.ic_branch),
                showChevron = true,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text(
                text = stringResource(Res.string.ill_days_wizard_city_label),
                style = MaterialTheme.typography.labelLarge,
                color = colors.textPrimary,
            )
            PickerRow(
                text = state.selectedCity?.cityName
                    ?: stringResource(Res.string.ill_days_wizard_city_placeholder),
                onClick = { onIntent(IllDaysWizardIntent.OpenCityPicker) },
                isPlaceholder = state.selectedCity == null,
                icon = vectorResource(Res.drawable.ic_place),
                showChevron = true,
            )
        }
    }
}

@Composable
private fun RestDaysStep(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_wizard_step2_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.ill_days_wizard_step2_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.cardCompact))
                .background(colors.bgSurface)
                .border(
                    width = Thickness.border,
                    color = if (state.isCovid) colors.blueText else colors.border,
                    shape = RoundedCornerShape(CornerRadius.cardCompact),
                )
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.ill_days_covid_toggle_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.ill_days_covid_toggle_caption),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
                TaminSwitchButton(
                    checked = state.isCovid,
                    onCheckedChange = { onIntent(IllDaysWizardIntent.CovidChanged(it)) },
                    enabled = !state.isCovidLoading,
                )
            }
        }

        if (state.isCovid) {
            CovidDatesPanel(state = state)
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = stringResource(Res.string.ill_days_rest_start_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary,
                    )
                    PickerRow(
                        text = state.startDateLabel.ifBlank {
                            stringResource(Res.string.ill_days_pick_date_placeholder)
                        },
                        onClick = { onIntent(IllDaysWizardIntent.OpenStartDatePicker) },
                        isPlaceholder = state.startDateLabel.isBlank(),
                        icon = vectorResource(Res.drawable.ic_tamin_calendar),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        text = stringResource(Res.string.ill_days_rest_end_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary,
                    )
                    PickerRow(
                        text = state.endDateLabel.ifBlank {
                            stringResource(Res.string.ill_days_pick_date_placeholder)
                        },
                        onClick = { onIntent(IllDaysWizardIntent.OpenEndDatePicker) },
                        isPlaceholder = state.endDateLabel.isBlank(),
                        icon = vectorResource(Res.drawable.ic_tamin_calendar),
                    )
                }
            }
            state.dayCount?.let { DayCountBadge(dayCount = it) }
        }
    }
}

@Composable
private fun DoctorStep(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_wizard_step3_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.ill_days_wizard_step3_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        TaminTextField(
            value = state.doctorName,
            onValueChange = { onIntent(IllDaysWizardIntent.DoctorNameChanged(it)) },
            label = stringResource(Res.string.ill_days_doctor_name_label),
            placeholder = stringResource(Res.string.ill_days_doctor_name_placeholder),
        )
        TaminTextField(
            value = state.doctorCode,
            onValueChange = { onIntent(IllDaysWizardIntent.DoctorCodeChanged(it)) },
            label = stringResource(Res.string.ill_days_doctor_code_label),
            placeholder = stringResource(Res.string.ill_days_doctor_code_placeholder),
            keyboardType = KeyboardType.Number,
            maxLength = 10
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.cardCompact))
                .background(colors.bgSurface)
                .border(
                    width = Thickness.border,
                    color = if (state.hasMedicalRecord) colors.blueText else colors.border,
                    shape = RoundedCornerShape(CornerRadius.cardCompact),
                )
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(Res.string.ill_days_medical_record_toggle_label),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = stringResource(Res.string.ill_days_medical_record_toggle_caption),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                }
                TaminSwitchButton(
                    checked = state.hasMedicalRecord,
                    onCheckedChange = { onIntent(IllDaysWizardIntent.MedicalRecordChanged(it)) },
                )
            }
        }
    }
}

@Composable
private fun DocumentsStep(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val hasDocs = state.documents.isNotEmpty()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.page, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_wizard_step4_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
        Text(
            text = stringResource(Res.string.ill_days_wizard_step4_description),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taminSurface(CornerRadius.cardCompact)
                .padding(Spacing.lg),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.ill_days_docs_section_title),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                )
                StatusPill(
                    text = stringResource(
                        Res.string.ill_days_docs_count_badge,
                        state.documents.size.toString().toPersianDigits(),
                    ),
                    containerColor = if (hasDocs) colors.greenBg else colors.orangeBg,
                    contentColor = if (hasDocs) colors.greenText else colors.orangeText,
                )
            }

            Text(
                text = stringResource(Res.string.ill_days_docs_instructions),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                state.documents.forEach { doc ->
                    val base64 = rememberBase64Thumbnail(doc.bytes)
                    TaminDocumentUploadCard(
                        title = doc.title,
                        state = TaminDocumentUploadState.Uploaded,
                        statusText = stringResource(Res.string.ill_days_doc_uploaded_status),
                        thumbnailBase64 = base64,
                        onPreviewClick = {
                            onIntent(IllDaysWizardIntent.PreviewDocument(doc.localId))
                        },
                        onDeleteClick = {
                            onIntent(IllDaysWizardIntent.RemoveDocument(doc.localId))
                        },
                    )
                }

                if (state.isUploadingDocument) {
                    val uploadTitle = stringResource(
                        Res.string.ill_days_doc_title,
                        (state.documents.size + 1).toString().toPersianDigits(),
                    )
                    val base64 = rememberBase64Thumbnail(state.uploadingBytes)
                    TaminDocumentUploadCard(
                        title = uploadTitle,
                        state = TaminDocumentUploadState.Uploading,
                        statusText = state.uploadingFileName,
                        thumbnailBase64 = base64,
                    )
                }

                if (state.documents.size < MAX_DOCUMENTS && !state.isUploadingDocument) {
                    TaminDocumentUploadCard(
                        title = stringResource(Res.string.ill_days_docs_add),
                        state = TaminDocumentUploadState.Empty,
                        onCardClick = { onIntent(IllDaysWizardIntent.OpenDocumentSource) },
                    )
                }
            }
        }
    }
}

@Composable
private fun CovidDatesPanel(state: IllDaysWizardUiState) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.greenBg)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        Text(
            text = stringResource(Res.string.ill_days_covid_info_prefix),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textPrimary,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        ) {
            CovidDateBox(
                label = stringResource(Res.string.ill_days_covid_start_label),
                value = state.startDateLabel,
                modifier = Modifier.weight(1f),
            )
            CovidDateBox(
                label = stringResource(Res.string.ill_days_covid_end_label),
                value = state.endDateLabel,
                modifier = Modifier.weight(1f),
            )
        }
        state.dayCount?.let { DayCountBadge(dayCount = it) }
    }
}

@Composable
private fun CovidDateBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(CornerRadius.md))
            .background(colors.bgSurface)
            .padding(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.textPrimary,
        )
    }
}

@Composable
private fun DayCountBadge(dayCount: Int) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(colors.blueBg)
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
    ) {
        Icon(
            imageVector = Icons.Outlined.AccessTime,
            contentDescription = null,
            tint = colors.blueText,
            modifier = Modifier.size(IconSize.small),
        )
        Text(
            text = stringResource(
                Res.string.ill_days_day_count_badge,
                dayCount.toString().toPersianDigits(),
            ),
            style = MaterialTheme.typography.labelMedium,
            color = colors.blueText,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IllDaysWizardPickers(
    state: IllDaysWizardUiState,
    onIntent: (IllDaysWizardIntent) -> Unit,
) {
    when (state.picker) {
        IllDaysWizardPicker.Branch -> {
            TaminSearchableListSheet(
                title = stringResource(Res.string.ill_days_wizard_pick_branch_title),
                subtitle = stringResource(Res.string.ill_days_wizard_pick_branch_subtitle),
                items = state.branchOptions,
                itemLabel = { it.label },
                itemKey = { it.id },
                showSearch = false,
                onItemSelected = { onIntent(IllDaysWizardIntent.BranchPicked(it)) },
                onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
            )
        }
        IllDaysWizardPicker.City -> {
            TaminSearchableListSheet(
                title = stringResource(Res.string.ill_days_wizard_pick_city_title),
                subtitle = stringResource(Res.string.ill_days_wizard_pick_city_subtitle),
                items = state.cityOptions,
                itemLabel = { it.cityName },
                itemKey = { it.cityCode },
                searchPlaceholder = stringResource(Res.string.ill_days_wizard_city_search),
                onSearchQueryChange = { onIntent(IllDaysWizardIntent.CitySearchQueryChanged(it)) },
                searchDebounceMs = CITY_SEARCH_DEBOUNCE_MS,
                isLoading = state.isCitiesLoading,
                canLoadMore = state.canLoadMoreCities,
                isLoadingMore = state.isCitiesLoadingMore,
                onLoadMore = { onIntent(IllDaysWizardIntent.CityPickerLoadMore) },
                onItemSelected = { onIntent(IllDaysWizardIntent.CityPicked(it)) },
                onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
            )
        }
        IllDaysWizardPicker.StartDate -> TaminJalaliDatePicker(
            title = stringResource(Res.string.ill_days_rest_start_label),
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    IllDaysWizardIntent.StartDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )
        IllDaysWizardPicker.EndDate -> TaminJalaliDatePicker(
            title = stringResource(Res.string.ill_days_rest_end_label),
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
            onConfirm = { year, month, day ->
                onIntent(
                    IllDaysWizardIntent.EndDatePicked(
                        millis = PersianDateFormatter.toEpochMillis(year, month, day),
                        label = PersianDateFormatter.format(year, month, day),
                    )
                )
            },
        )
        IllDaysWizardPicker.DocumentSource -> TaminDocumentSourceSheet(
            title = stringResource(Res.string.ill_days_docs_source_title),
            onSelectCamera = { onIntent(IllDaysWizardIntent.OpenCamera) },
            onSelectGallery = { onIntent(IllDaysWizardIntent.OpenGallery) },
            onDismiss = { onIntent(IllDaysWizardIntent.DismissPicker) },
        )
        IllDaysWizardPicker.None -> Unit
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun rememberBase64Thumbnail(bytes: ByteArray?): String? {
    val state = produceState<String?>(initialValue = null, bytes) {
        value = bytes?.let { withContext(Dispatchers.Default) { Base64.Default.encode(it) } }
    }
    return state.value
}
