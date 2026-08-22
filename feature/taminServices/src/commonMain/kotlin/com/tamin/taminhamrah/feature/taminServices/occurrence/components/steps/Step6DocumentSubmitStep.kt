package com.tamin.taminhamrah.feature.taminServices.occurrence.components.steps

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.tamin.taminhamrah.feature.taminServices.occurrence.camera.rememberCameraPermission
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.InfoBanner
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceDocumentSourceSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSelectionBottomSheet
import com.tamin.taminhamrah.feature.taminServices.occurrence.components.OccurrenceSheetOption
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
import com.tamin.taminhamrah.ui.components.LiquidWaveProgressBar
import com.tamin.taminhamrah.ui.components.LoadingStateOverlay
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminBottomActionBar
import com.tamin.taminhamrah.ui.components.TaminDivider
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.components.toast.AppToastHost
import com.tamin.taminhamrah.ui.components.toast.LocalToaster
import com.tamin.taminhamrah.ui.components.toast.error
import com.tamin.taminhamrah.ui.components.topbars.TaminStepTopAppBar
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberCameraPickerLauncher
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.occurrence_add_document
import taminx.core.core_ui.occurrence_camera_permission_denied
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

import androidx.compose.ui.layout.ContentScale
import com.tamin.taminhamrah.ui.components.LoadAsyncImage
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
@Composable
internal fun Step6DocumentSubmitStep(
    uiState: OccurrenceUiState,
    onIntent: (OccurrenceIntent) -> Unit,
    onBack: () -> Unit,
    onClose:() -> Unit,
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
    val newlyUploadedDoc = if (showUploadWave && step.uploadedDocuments.size > uploadedCountBeforeCurrent) {
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
        scope.launch {
            try {
                val bytes = file.readBytes()
                onIntent(OccurrenceIntent.UploadDocument(typeId = docType.id, typeName = docType.title, fileName = file.name, fileBytes = bytes))
            } catch (e: Exception) {
                val msg = try { getString(Res.string.error_file_read_fallback) } catch (_: Exception) { e.message.orEmpty() }
                toaster.error(msg)
            } finally {
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(pendingDocType = null)))
            }
        }
    }

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file: PlatformFile? -> handlePicked(file) }
    val cameraLauncher = rememberCameraPickerLauncher { file: PlatformFile? -> handlePicked(file) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TaminStepTopAppBar(
                title = stringResource(Res.string.occurrence_step6_title),
                onBackClicked = onBack,
                onCloseClicked = onClose,
                currentStep = uiState.stepNumber,
                totalSteps = OccurrenceStep.entries.size,
            )
        },
        bottomBar = {
            TaminBottomActionBar(
                primaryText = stringResource(Res.string.occurrence_submit),
                primaryEnabled = uiState.isStep6Valid && !uiState.isLoading && !uiState.isSubmitting,
                isPrimaryLoading = uiState.isSubmitting,
                onPrimaryClick = { onIntent(OccurrenceIntent.SubmitOccurrence) },
                secondaryText = stringResource(Res.string.occurrence_prev_step),
                onSecondaryClick = onBack,
            )
        },
        contentWindowInsets = WindowInsets(0),
    ) { padding ->
        if (uiState.isLoading) {
            LoadingStateOverlay(modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
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
                            text = stringResource(Res.string.occurrence_documents_min_hint, step.uploadedDocuments.size.toString()),
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
                                UploadedDocRow(
                                    doc = doc,
                                    onDelete = { onIntent(OccurrenceIntent.RemoveDocument(doc.guid)) },
                                    onPreview = { previewDoc = it },
                                )
                            }
                            if (showUploadWave) {
                                UploadingDocRow(
                                    typeName = newlyUploadedDoc?.typeName ?: pendingUploadTypeName,
                                    fileName = newlyUploadedDoc?.fileName ?: pendingUploadFileName,
                                    fileBytes = newlyUploadedDoc?.bytes ?: pendingUploadFileBytes,
                                    onFillComplete = { waveAnimationComplete = true },
                                )
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
                                onClick = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showDocTypeSheet = true))) }
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
                InfoBanner(message =  stringResource(Res.string.occurrence_submit_disclaimer))
                Spacer(modifier = Modifier.height(Spacing.lg))
                Spacer(modifier = Modifier.height(padding.calculateBottomPadding()))
            }
        }
    }

    val targetPreviewDoc = previewDoc
    val targetPreviewBytes = targetPreviewDoc?.bytes
    if (targetPreviewDoc != null && targetPreviewBytes != null) {
        val base64 = remember(targetPreviewBytes) { Base64.Default.encode(targetPreviewBytes) }
        TaminImageViewer(
            title = targetPreviewDoc.typeName,
            url = base64,
            onDismiss = { previewDoc = null },
        )
    }

    if (uiState.dialogs.showDocTypeSheet) {
        OccurrenceSelectionBottomSheet(
            title = stringResource(Res.string.occurrence_sheet_doc_type_title),
            options = step.docTypes.map { OccurrenceSheetOption(id = it.id.toString(), title = it.title) },
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
            onDismiss = { onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showDocTypeSheet = false))) },
        )
    }

    val currentPendingDocType = uiState.dialogs.pendingDocType
    if (uiState.dialogs.showDocumentSourceSheet && currentPendingDocType != null) {
        OccurrenceDocumentSourceSheet(
            title = currentPendingDocType.title,
            onSelectCamera = {
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showDocumentSourceSheet = false)))
                if (cameraPermission.granted) {
                    cameraLauncher.launch()
                } else {
                    cameraPermission.request { granted ->
                        if (granted) {
                            cameraLauncher.launch()
                        } else {
                            onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(pendingDocType = null)))
                            scope.launch {
                                val msg = try {
                                    getString(Res.string.occurrence_camera_permission_denied)
                                } catch (_: Exception) {
                                    ""
                                }
                                toaster.error(msg)
                            }
                        }
                    }
                }
            },
            onSelectGallery = {
                onIntent(OccurrenceIntent.UpdateDialogs(uiState.dialogs.copy(showDocumentSourceSheet = false)))
                galleryLauncher.launch()
            },
            onDismiss = {
                onIntent(
                    OccurrenceIntent.UpdateDialogs(
                        uiState.dialogs.copy(showDocumentSourceSheet = false, pendingDocType = null)
                    )
                )
            },
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun UploadedDocRow(
    doc: OccurrenceUploadedDocDN,
    onDelete: () -> Unit,
    onPreview: (OccurrenceUploadedDocDN) -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val base64 = remember(doc.bytes) { doc.bytes?.let { Base64.Default.encode(it) } }
    val shape = RoundedCornerShape(CornerRadius.lg)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(taminColors.greenBg)
            .border(Thickness.border, taminColors.greenBorder, shape)
            .padding(Spacing.md),
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.xlarge)
                .clip(RoundedCornerShape(CornerRadius.iconTile))
                .background(if (base64 != null) taminColors.blueBg else taminColors.greenText)
                .then(
                    if (doc.bytes != null) {
                        Modifier.clickable { onPreview(doc) }
                    } else Modifier
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (base64 != null) {
                LoadAsyncImage(
                    model = base64,
                    contentDescription = doc.typeName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.banner),
                )
            }
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Column(
            modifier = Modifier
                .weight(1f)
                .then(
                    if (doc.bytes != null) {
                        Modifier.clickable { onPreview(doc) }
                    } else Modifier
                )
        ) {
            Text(text = doc.typeName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = taminColors.textPrimary)
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(text = doc.fileName, style = MaterialTheme.typography.bodySmall, color = taminColors.textMuted)
        }
        Spacer(modifier = Modifier.width(Spacing.sm))
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(IconSize.large)
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = taminColors.dangerText,
                modifier = Modifier.size(IconSize.small),
            )
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
private fun UploadingDocRow(
    typeName: String,
    fileName: String,
    fileBytes: ByteArray? = null,
    onFillComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)
    val base64 = remember(fileBytes) { fileBytes?.let { Base64.Default.encode(it) } }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .background(taminColors.bgSurface),
        )
        LiquidWaveProgressBar(
            modifier = Modifier
                .matchParentSize()
                .clip(shape),
            onFillComplete = onFillComplete,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .border(Thickness.border, taminColors.border, shape)
                .padding(Spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .size(IconSize.xlarge)
                    .clip(RoundedCornerShape(CornerRadius.iconTile))
                    .background(taminColors.blueBg),
                contentAlignment = Alignment.Center,
            ) {
                if (base64 != null) {
                    LoadAsyncImage(
                        model = base64,
                        contentDescription = typeName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = taminColors.blueText,
                        modifier = Modifier.size(IconSize.medium),
                    )
                }
            }
            Spacer(modifier = Modifier.width(Spacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = typeName, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = taminColors.textPrimary)
                Spacer(modifier = Modifier.height(Spacing.xxs))
                Text(text = fileName, style = MaterialTheme.typography.bodySmall, color = taminColors.textMuted)
            }
        }
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
                        selectedWorkshop = WorkshopItemPR(id = "1", workshopCode = "1412345", branchCode = "014", name = "کارگاه تولیدی الف", employerName = "شرکت الف", employerPhone = "02112345678", address = "تهران، خیابان ولیعصر", postalCode = "1234567890", phone = "02112345678"),
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
                        docTypes = listOf(OccurrenceDocTypePR(id = 1, title = "گزارش حادثه"), OccurrenceDocTypePR(id = 2, title = "مدارک پزشکی")),
                        uploadedDocuments = listOf(
                            OccurrenceUploadedDocDN(guid = "abc-1", typeId = 1, typeName = "گزارش حادثه", fileName = "report.jpg"),
                            OccurrenceUploadedDocDN(guid = "abc-2", typeId = 2, typeName = "مدارک پزشکی", fileName = "medical.jpg"),
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
