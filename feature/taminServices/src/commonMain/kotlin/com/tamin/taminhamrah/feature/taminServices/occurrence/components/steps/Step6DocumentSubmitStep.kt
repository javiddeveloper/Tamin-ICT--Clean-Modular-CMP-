package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceStepScaffold
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.PersonInfoCard
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.PersonInfoGridItem
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.AccidentStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.DocumentSubmitStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.JobDetailsStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceIntent
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceStep
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.OccurrenceUiState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.WorkHoursStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.contract.WorkshopStepState
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.OccurrenceDocTypePR
import com.tamin.taminhamrah.feature.taminServices.occurrence.model.WorkshopItemPR
import com.tamin.taminhamrah.model.occurrence.OccurrenceUploadedDocDN
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.rememberCameraPermission
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.occurrence_add_document
import taminx.core.core_ui.occurrence_camera_permission_denied
import taminx.core.core_ui.occurrence_doc_format_error
import taminx.core.core_ui.occurrence_doc_format_hint
import taminx.core.core_ui.occurrence_doc_required_hint
import taminx.core.core_ui.occurrence_documents_min_hint
import taminx.core.core_ui.occurrence_documents_section_title
import taminx.core.core_ui.occurrence_prev_step
import taminx.core.core_ui.occurrence_sheet_doc_type_title
import taminx.core.core_ui.occurrence_step6_title
import taminx.core.core_ui.occurrence_submit
import taminx.core.core_ui.occurrence_submit_disclaimer
import taminx.core.core_ui.occurrence_summary_accident_date
import taminx.core.core_ui.occurrence_summary_accident_time
import taminx.core.core_ui.occurrence_summary_employer
import taminx.core.core_ui.occurrence_summary_exact_location
import taminx.core.core_ui.occurrence_summary_outcome
import taminx.core.core_ui.occurrence_summary_person
import taminx.core.core_ui.occurrence_summary_section
import taminx.core.core_ui.occurrence_summary_transport
import taminx.core.core_ui.occurrence_summary_workshop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/** Matches OrotezProtezViewModel's document constraints — kept in sync since occurrence has no equivalent ViewModel-side check (see [handlePicked] doc). */
private const val MAX_DOCUMENT_SIZE_BYTES = 2 * 1024 * 1024

private fun isJpegFileName(fileName: String): Boolean {
    val lower = fileName.lowercase()
    return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
}

/**
 * Encodes off the composition/main thread instead of inline in a `remember` block — a multi-MB
 * camera photo blocked the main thread the first time each card composed, most visibly right as
 * the upload-success wave animation should play. Returns null (card shows its loading state)
 * until the encode finishes.
 */
@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun rememberBase64Thumbnail(bytes: ByteArray?): String? {
    val state = produceState<String?>(initialValue = null, bytes) {
        value = bytes?.let { withContext(Dispatchers.Default) { Base64.Default.encode(it) } }
    }
    return state.value
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
internal fun Step6DocumentSubmitStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()
    val step = uiState.documentSubmit
    val cameraPermission = rememberCameraPermission()
    var previewDoc by remember { mutableStateOf<OccurrenceUploadedDocDN?>(null) }

    var waveAnimationComplete by remember { mutableStateOf(!step.isUploadingDoc) }
    var pendingUploadTypeName by remember { mutableStateOf("") }
    var pendingUploadFileName by remember { mutableStateOf("") }
    var pendingUploadFileBytes by remember { mutableStateOf<ByteArray?>(null) }
    var uploadedCountBeforeCurrent by remember { mutableStateOf(step.uploadedDocuments.size) }
    LaunchedEffect(step.isUploadingDoc) {
        if (step.isUploadingDoc) {
            waveAnimationComplete = false
            pendingUploadTypeName = step.uploadingTypeName
            pendingUploadFileName = step.uploadingFileName
            pendingUploadFileBytes = step.uploadingFileBytes
            uploadedCountBeforeCurrent = step.uploadedDocuments.size
        }
    }
    val showUploadWave = step.isUploadingDoc || !waveAnimationComplete
    val newlyUploadedDoc =
        if (showUploadWave && step.uploadedDocuments.size > uploadedCountBeforeCurrent) {
            step.uploadedDocuments.last()
        } else {
            null
        }

    fun handlePicked(file: PlatformFile?) {
        val docType = uiState.dialogs.pendingDocType ?: return
        if (file == null) {
            onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(pendingDocType = null)))
            return
        }
        if (!isJpegFileName(file.name)) {
            onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(pendingDocType = null)))
            scope.launch {
                toaster.error(getString(Res.string.occurrence_doc_format_error))
            }
            return
        }
        scope.launch {
            try {
                val bytes = file.readBytes()
                if (bytes.size > MAX_DOCUMENT_SIZE_BYTES) {
                    toaster.error(getString(Res.string.occurrence_doc_format_error))
                    return@launch
                }
                onIntent(
                    OccurrenceIntent.UploadDocument(
                        typeId = docType.id,
                        typeName = docType.title,
                        fileName = file.name,
                        fileBytes = bytes
                    )
                )
            } catch (e: Exception) {
                val msg = try {
                    getString(Res.string.error_file_read_fallback)
                } catch (_: Exception) {
                    e.message.orEmpty()
                }
                toaster.error(msg)
            } finally {
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(pendingDocType = null)))
            }
        }
    }

    val galleryLauncher =
        rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? ->
            handlePicked(file)
        }
    val cameraLauncher = rememberCameraPickerLauncher { file: PlatformFile? -> handlePicked(file) }

    OccurrenceStepScaffold(
        modifier = modifier,
        title = stringResource(Res.string.occurrence_step6_title),
        stepNumber = uiState.stepNumber,
        totalSteps = OccurrenceStep.entries.size,
        onBackClicked = onBack,
        onCloseClicked = onClose,
        primaryText = stringResource(Res.string.occurrence_submit),
        primaryEnabled = uiState.isStep6Valid && !uiState.isLoading && !uiState.isSubmitting,
        isPrimaryLoading = uiState.isSubmitting,
        onPrimaryClick = { onIntent(OccurrenceIntent.SubmitOccurrence) },
        secondaryText = stringResource(Res.string.occurrence_prev_step),
        onSecondaryClick = onBack,
    ) { padding ->
        if (uiState.isLoading) {
            LoadingStateOverlay(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.lg),
            ) {
                Spacer(modifier = Modifier.height(Spacing.md))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .taminSurface(CornerRadius.card)
                        .padding(Spacing.lg),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(Res.string.occurrence_documents_section_title),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.textPrimary,
                        )
                        StatusPill(
                            text = stringResource(
                                Res.string.occurrence_documents_min_hint,
                                step.uploadedDocuments.size.toString()
                            ),
                            containerColor = if (uiState.isStep6Valid) taminColors.greenBg else taminColors.orangeBg,
                            contentColor = if (uiState.isStep6Valid) taminColors.greenText else taminColors.orangeText,
                        )
                    }

                    val docsToShowNormally = if (newlyUploadedDoc != null) {
                        step.uploadedDocuments.dropLast(1)
                    } else {
                        step.uploadedDocuments
                    }
                    if (docsToShowNormally.isNotEmpty() || showUploadWave) {
                        Spacer(modifier = Modifier.height(Spacing.md))
                        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                            docsToShowNormally.forEach { doc ->
                                val base64 = rememberBase64Thumbnail(doc.bytes)
                                TaminDocumentUploadCard(
                                    title = doc.typeName,
                                    state = TaminDocumentUploadState.Uploaded,
                                    statusText = doc.fileName,
                                    thumbnailBase64 = base64,
                                    onPreviewClick = { previewDoc = doc },
                                    onDeleteClick = { onIntent(OccurrenceIntent.RemoveDocument(doc.guid)) },
                                )
                            }
                            if (showUploadWave) {
                                val typeName = newlyUploadedDoc?.typeName ?: pendingUploadTypeName
                                val fileName = newlyUploadedDoc?.fileName ?: pendingUploadFileName
                                val fileBytes = newlyUploadedDoc?.bytes ?: pendingUploadFileBytes
                                val base64 = rememberBase64Thumbnail(fileBytes)
                                TaminDocumentUploadCard(
                                    title = typeName,
                                    state = TaminDocumentUploadState.Uploading,
                                    statusText = fileName,
                                    thumbnailBase64 = base64,
                                    onCardClick = null,
                                )
                                LaunchedEffect(showUploadWave) {
                                    kotlinx.coroutines.delay(1600)
                                    waveAnimationComplete = true
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Spacing.md))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(if (showUploadWave) 0.5f else 1f)
                            .clip(RoundedCornerShape(CornerRadius.lg))
                            .background(taminColors.blueBg)
                            .dashedOutline(taminColors.blueText, CornerRadius.lg, Thickness.border)
                            .clickable(
                                enabled = !showUploadWave,
                                onClick = {
                                    onIntent(
                                        OccurrenceIntent.UpdateDialogs(
                                            uiState.dialogs.copy(
                                                showDocTypeSheet = true
                                            )
                                        )
                                    )
                                }
                            )
                            .padding(vertical = Spacing.md),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = stringResource(Res.string.occurrence_add_document),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = taminColors.blueText,
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.sm))

                    Text(
                        text = stringResource(Res.string.occurrence_doc_required_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted,
                    )
                    Text(
                        text = stringResource(Res.string.occurrence_doc_format_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = taminColors.textMuted,
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.lg))
                TaminDivider()
                Spacer(modifier = Modifier.height(Spacing.md))

                PersonInfoCard(title = stringResource(Res.string.occurrence_summary_section)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_person),
                            value = uiState.jobDetails.fullName,
                        )
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_workshop),
                            value = uiState.workshop.selectedWorkshop?.name.orEmpty(),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_employer),
                            value = uiState.workshop.employerName,
                        )
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_accident_date),
                            value = uiState.accident.accidentDate,
                            numeric = true,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_outcome),
                            value = uiState.accident.accidentOutcomeTitle,
                        )
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_accident_time),
                            value = uiState.accident.accidentTime,
                            numeric = true,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.lg),
                    ) {
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_transport),
                            value = uiState.workHours.transportation,
                        )
                        PersonInfoGridItem(
                            modifier = Modifier.weight(1f),
                            label = stringResource(Res.string.occurrence_summary_exact_location),
                            value = uiState.accident.exactLocation,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(Spacing.md))
                InfoBanner(message = stringResource(Res.string.occurrence_submit_disclaimer))
                Spacer(modifier = Modifier.height(Spacing.lg))
            }
        }
    }

    val targetPreviewDoc = previewDoc
    val targetPreviewBase64 = rememberBase64Thumbnail(targetPreviewDoc?.bytes)
    if (targetPreviewDoc != null && targetPreviewBase64 != null) {
        TaminImageViewer(
            title = targetPreviewDoc.typeName,
            url = targetPreviewBase64,
            onDismiss = { previewDoc = null },
        )
    }

    if (uiState.dialogs.showDocTypeSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_doc_type_title),
            options = step.docTypes.map {
                OccurrenceSheetOption(
                    id = it.id.toString(),
                    title = it.title
                )
            },
            selectedId = null,
            onSelect = { option ->
                val docType = step.docTypes.first { it.id.toString() == option.id }
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            pendingDocType = docType,
                            showDocTypeSheet = false,
                            showDocumentSourceSheet = true,
                        )
                    )
                )
            },
            onDismiss = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            showDocTypeSheet = false
                        )
                    )
                )
            },
        )
    }

    val currentPendingDocType = uiState.dialogs.pendingDocType
    if (uiState.dialogs.showDocumentSourceSheet && currentPendingDocType != null) {
        TaminDocumentSourceSheet(
            title = currentPendingDocType.title,
            showRemoveOption = false,

            onSelectCamera = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            showDocumentSourceSheet = false
                        )
                    )
                )

                cameraPermission.request { granted ->
                    if (granted) {
                        cameraLauncher.launch()
                    } else {
                        onIntent(
                            OccurrenceIntent.UpdateDialogs(
                                uiState.dialogs.copy(
                                    pendingDocType = null
                                )
                            )
                        )

                        scope.launch {
                            val message = try {
                                getString(
                                    Res.string.occurrence_camera_permission_denied
                                )
                            } catch (_: Exception) {
                                ""
                            }

                            toaster.error(message)
                        }
                    }
                }
            },

            onSelectGallery = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            showDocumentSourceSheet = false
                        )
                    )
                )

                galleryLauncher.launch()
            },

            onDismiss = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(
                            showDocumentSourceSheet = false,
                            pendingDocType = null
                        )
                    )
                )
            }
        )
    }
}


@PreviewRtlTheme
@Preview
@Composable
private fun Step6DocumentSubmitStepPreview() {
    PreviewRtlThemeContent {
        AppToastHost {
            Step6DocumentSubmitStep(
                uiState = OccurrenceUiState(
                    currentStep = OccurrenceStep.DOCUMENT_SUBMIT,
                    jobDetails = JobDetailsStepState(
                        fullName = "علی محمدی",
                    ),
                    workshop = WorkshopStepState(
                        selectedWorkshop = WorkshopItemPR(
                            id = "1",
                            workshopCode = "1412345",
                            branchCode = "014",
                            name = "کارگاه تولیدی الف",
                            employerName = "شرکت الف",
                            employerPhone = "02112345678",
                            address = "تهران، خیابان ولیعصر",
                            postalCode = "1234567890",
                            phone = "02112345678"
                        ),
                        employerName = "شرکت الف",
                    ),
                    accident = AccidentStepState(
                        accidentDate = "1402/06/15",
                        accidentTime = "۱۴:۳۰",
                        accidentOutcomeTitle = "استراحت پزشکی",
                        exactLocation = "طبقه دوم، سالن تولید",
                    ),
                    workHours = WorkHoursStepState(
                        transportation = "وسیله نقلیه شخصی",
                    ),
                    documentSubmit = DocumentSubmitStepState(
                        docTypes = listOf(
                            OccurrenceDocTypePR(id = 1, title = "گزارش حادثه"),
                            OccurrenceDocTypePR(id = 2, title = "مدارک پزشکی")
                        ),
                        uploadedDocuments = listOf(
                            OccurrenceUploadedDocDN(
                                guid = "abc-1",
                                typeId = 1,
                                typeName = "گزارش حادثه",
                                fileName = "report.jpg"
                            ),
                            OccurrenceUploadedDocDN(
                                guid = "abc-2",
                                typeId = 2,
                                typeName = "مدارک پزشکی",
                                fileName = "medical.jpg"
                            ),
                        ),
                    ),
                ),
                onIntent = {},
                onBack = {},
                onClose = {}
            )
        }
    }
}
