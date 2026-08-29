package com.tamin.taminhamrah.feature.pensionSurvivor.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedDocumentChecklist
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedDocumentType
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.DeceasedUploadedDocument
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorIntent
import com.tamin.taminhamrah.feature.pensionSurvivor.ui.contract.PensionSurvivorUiState
import com.tamin.taminhamrah.ui.components.StatusPill
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.document.TaminDocumentSourceSheet
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.taminSurface
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
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.error_file_read_fallback
import taminx.core.core_ui.occurrence_camera_permission_denied
import taminx.core.core_ui.orotez_protez_document_pick_placeholder
import taminx.core.core_ui.orotez_protez_document_status_error_tap_to_retry
import taminx.core.core_ui.orotez_protez_document_status_uploaded
import taminx.core.core_ui.pension_survivor_deceased_death_certificate
import taminx.core.core_ui.pension_survivor_deceased_documents_count
import taminx.core.core_ui.pension_survivor_deceased_documents_upload_hint
import taminx.core.core_ui.pension_survivor_deceased_documents_upload_title
import taminx.core.core_ui.pension_survivor_deceased_history_confirm_label
import taminx.core.core_ui.pension_survivor_deceased_id_children_page
import taminx.core.core_ui.pension_survivor_deceased_id_first_page
import kotlin.io.encoding.Base64

@Composable
fun DeceasedDocumentsStep(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        DeceasedDocumentsSection(
            state = state,
            onIntent = onIntent,
        )
        DeceasedHistoryConfirmCard(
            confirmed = state.isDeceasedHistoryConfirmed,
            onConfirmedChange = {
                onIntent(PensionSurvivorIntent.DeceasedHistoryConfirmedChanged(it))
            },
        )
    }
}

@Composable
private fun DeceasedHistoryConfirmCard(
    confirmed: Boolean,
    onConfirmedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface(CornerRadius.card)
            .clickable { onConfirmedChange(!confirmed) }
            .padding(Spacing.smd),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = confirmed,
            onCheckedChange = onConfirmedChange,
        )
        Text(
            text = stringResource(Res.string.pension_survivor_deceased_history_confirm_label),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DeceasedDocumentsSection(
    state: PensionSurvivorUiState,
    onIntent: (PensionSurvivorIntent) -> Unit,
) {
    val colors = LocalTaminColors.current
    val toaster = LocalToaster.current
    val scope = rememberCoroutineScope()
    val cameraPermission = rememberCameraPermission()
    var previewDocument by remember { mutableStateOf<DeceasedUploadedDocument?>(null) }
    var pendingDocumentType by remember { mutableStateOf<DeceasedDocumentType?>(null) }

    fun handlePicked(file: PlatformFile?) {
        val documentType = pendingDocumentType ?: state.activeDeceasedDocument ?: return
        pendingDocumentType = null
        onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
        if (file == null) return

        scope.launch {
            try {
                val bytes = file.readBytes()
                onIntent(
                    PensionSurvivorIntent.DeceasedDocumentImagePicked(
                        type = documentType,
                        fileName = file.name,
                        bytes = bytes,
                    ),
                )
            } catch (_: Exception) {
                toaster.error(getString(Res.string.error_file_read_fallback))
            }
        }
    }

    val galleryLauncher = rememberFilePickerLauncher(type = FileKitType.Image) { file ->
        handlePicked(file)
    }
    val cameraLauncher = rememberCameraPickerLauncher { file ->
        handlePicked(file)
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.pension_survivor_deceased_documents_upload_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            StatusPill(
                text = stringResource(
                    Res.string.pension_survivor_deceased_documents_count,
                    state.deceasedDocumentsUploadedCount.toString(),
                ),
                containerColor = if (state.areDeceasedDocumentsComplete) colors.greenBg else colors.orangeBg,
                contentColor = if (state.areDeceasedDocumentsComplete) colors.greenText else colors.orangeText,
            )
        }

        Text(
            text = stringResource(Res.string.pension_survivor_deceased_documents_upload_hint),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted,
        )

        DeceasedDocumentChecklist.forEach { documentType ->
            val uploaded = state.deceasedDocuments[documentType]
            val uploadState = when {
                state.uploadingDeceasedDocument == documentType -> TaminDocumentUploadState.Uploading
                uploaded != null -> TaminDocumentUploadState.Uploaded
                state.failedDeceasedDocument == documentType -> TaminDocumentUploadState.Failed
                else -> TaminDocumentUploadState.Empty
            }
            val thumbnailBase64 = rememberBase64Thumbnail(uploaded?.bytes)
            val statusText = when (uploadState) {
                TaminDocumentUploadState.Uploaded -> uploaded?.fileName
                    ?: stringResource(Res.string.orotez_protez_document_status_uploaded)
                TaminDocumentUploadState.Failed -> state.deceasedDocumentError
                    ?: stringResource(Res.string.orotez_protez_document_status_error_tap_to_retry)
                else -> stringResource(Res.string.orotez_protez_document_pick_placeholder)
            }

            TaminDocumentUploadCard(
                title = deceasedDocumentTitle(documentType),
                state = uploadState,
                statusText = statusText,
                isRequired = true,
                thumbnailBase64 = thumbnailBase64,
                onCardClick = {
                    if (!state.isDeceasedDocumentUploading) {
                        onIntent(PensionSurvivorIntent.DeceasedDocumentClicked(documentType))
                    }
                },
                onPreviewClick = uploaded?.let { { previewDocument = it } },
                onDeleteClick = uploaded?.let {
                    { onIntent(PensionSurvivorIntent.RemoveDeceasedDocument(documentType)) }
                },
            )
        }

        state.deceasedDocumentError?.takeIf { state.failedDeceasedDocument == null }?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = colors.dangerText,
            )
        }
    }

    val preview = previewDocument
    val previewBase64 = rememberBase64Thumbnail(preview?.bytes)
    if (preview != null && previewBase64 != null) {
        TaminImageViewer(
            title = deceasedDocumentTitle(preview.type),
            url = previewBase64,
            onDismiss = { previewDocument = null },
        )
    }

    state.activeDeceasedDocument?.let { documentType ->
        TaminDocumentSourceSheet(
            title = deceasedDocumentTitle(documentType),
            showRemoveOption = state.deceasedDocuments.containsKey(documentType),
            onSelectCamera = {
                pendingDocumentType = documentType
                onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
                cameraPermission.request { granted ->
                    if (granted) {
                        cameraLauncher.launch()
                    } else {
                        scope.launch {
                            toaster.error(getString(Res.string.occurrence_camera_permission_denied))
                        }
                    }
                }
            },
            onSelectGallery = {
                pendingDocumentType = documentType
                onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
                galleryLauncher.launch()
            },
            onRemove = {
                onIntent(PensionSurvivorIntent.RemoveDeceasedDocument(documentType))
                onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource)
            },
            onDismiss = { onIntent(PensionSurvivorIntent.DismissDeceasedDocumentSource) },
        )
    }
}

@Composable
private fun deceasedDocumentTitle(type: DeceasedDocumentType): String = stringResource(
    when (type) {
        DeceasedDocumentType.DeathCertificate -> Res.string.pension_survivor_deceased_death_certificate
        DeceasedDocumentType.IdFirstPage -> Res.string.pension_survivor_deceased_id_first_page
        DeceasedDocumentType.IdChildrenPage -> Res.string.pension_survivor_deceased_id_children_page
    },
)

@Composable
private fun rememberBase64Thumbnail(bytes: ByteArray?): String? {
    val state = produceState<String?>(initialValue = null, bytes) {
        value = bytes?.let { withContext(Dispatchers.Default) { Base64.Default.encode(it) } }
    }
    return state.value
}
