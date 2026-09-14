package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentChecklist
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityDocumentState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionIntent
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.DisabilityPensionUiState
import com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.contract.bytesOrNull
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminImageViewer
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.components.taminSurface
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.disability_pension_step_documents_title
import taminx.core.core_ui.orotez_protez_document_pick_placeholder
import taminx.core.core_ui.orotez_protez_document_status_error_tap_to_retry
import taminx.core.core_ui.orotez_protez_document_status_uploaded
import taminx.core.core_ui.orotez_protez_document_status_uploading
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun DisabilityPensionDocumentsStep(
    state: DisabilityPensionUiState,
    onIntent: (DisabilityPensionIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    var previewDocumentId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .taminSurface()
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.smd),
    ) {
        Text(
            text = stringResource(Res.string.disability_pension_step_documents_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
        )

        DisabilityDocumentChecklist.forEach { document ->
            val documentState = state.documents[document.id] ?: DisabilityDocumentState.Empty
            val isUploaded = documentState is DisabilityDocumentState.Uploaded
            val thumbnailBase64 = rememberBase64Thumbnail(documentState.bytesOrNull())
            TaminDocumentUploadCard(
                title = stringResource(document.titleRes),
                state = documentState.toUploadState(),
                statusText = documentState.statusText(),
                thumbnailBase64 = thumbnailBase64,
                onCardClick = if (documentState is DisabilityDocumentState.Uploading || isUploaded) {
                    null
                } else {
                    { onIntent(DisabilityPensionIntent.DocumentCardClicked(document.id)) }
                },
                onPreviewClick = if (isUploaded) {
                    { previewDocumentId = document.id }
                } else {
                    null
                },
                onDeleteClick = if (isUploaded) {
                    { onIntent(DisabilityPensionIntent.DocumentRemoveClicked(document.id)) }
                } else {
                    null
                },
            )
        }

        val errorMessage = state.documentPickError ?: state.documentPickErrorRes?.let { stringResource(it) }
        errorMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = colors.dangerText,
            )
        }
    }

    val previewDocument = DisabilityDocumentChecklist.find { it.id == previewDocumentId }
    val previewBytes = previewDocumentId?.let { state.documents[it]?.bytesOrNull() }
    if (previewDocument != null && previewBytes != null) {
        DisabilityPensionDocumentPreviewDialog(
            title = stringResource(previewDocument.titleRes),
            bytes = previewBytes,
            onDismiss = { previewDocumentId = null },
        )
    }
}

@Composable
private fun DisabilityPensionDocumentPreviewDialog(
    title: String,
    bytes: ByteArray,
    onDismiss: () -> Unit,
) {
    val base64 = rememberBase64Thumbnail(bytes)
    if (base64 != null) {
        TaminImageViewer(
            title = title,
            url = base64,
            onDismiss = onDismiss,
        )
    }
}

/**
 * Encodes off the composition/main thread — a multi-MB camera photo would otherwise block the
 * main thread while the card is composing. Returns null (card shows its non-thumbnail state)
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

private fun DisabilityDocumentState.toUploadState(): TaminDocumentUploadState = when (this) {
    DisabilityDocumentState.Empty -> TaminDocumentUploadState.Empty
    is DisabilityDocumentState.Uploading -> TaminDocumentUploadState.Uploading
    is DisabilityDocumentState.Uploaded -> TaminDocumentUploadState.Uploaded
    is DisabilityDocumentState.Failed -> TaminDocumentUploadState.Failed
}

@Composable
private fun DisabilityDocumentState.statusText(): String = when (this) {
    DisabilityDocumentState.Empty -> stringResource(Res.string.orotez_protez_document_pick_placeholder)
    is DisabilityDocumentState.Uploading -> stringResource(Res.string.orotez_protez_document_status_uploading)
    is DisabilityDocumentState.Uploaded -> stringResource(Res.string.orotez_protez_document_status_uploaded)
    is DisabilityDocumentState.Failed -> {
        val resolvedMessage = message ?: messageRes?.let { stringResource(it) } ?: ""
        "$resolvedMessage ${stringResource(Res.string.orotez_protez_document_status_error_tap_to_retry)}"
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionDocumentsStepPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionDocumentsStep(
            state = DisabilityPensionUiState(),
            onIntent = {},
        )
    }
}
