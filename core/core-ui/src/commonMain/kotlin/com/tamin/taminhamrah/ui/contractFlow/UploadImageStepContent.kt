package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.tamin.taminhamrah.model.contractFlow.UploadImagePR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.components.TaminTextField
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadCard
import com.tamin.taminhamrah.ui.components.document.TaminDocumentUploadState
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_upload_add_documents
import taminx.core.core_ui.contract_upload_description_empty_error
import taminx.core.core_ui.contract_upload_description_placeholder
import taminx.core.core_ui.contract_upload_description_required
import taminx.core.core_ui.contract_upload_format_hint
import taminx.core.core_ui.contract_upload_read_error
import taminx.core.core_ui.contract_upload_status_uploaded
import taminx.core.core_ui.contract_upload_status_uploading

@Composable
fun UploadImageStepContent(
    description: String,
    previewBytes: ByteArray?,
    uploadedDocuments: List<UploadImagePR>,
    isUploading: Boolean,
    uploadError: String?,
    onDescriptionChange: (String) -> Unit,
    onImagePicked: (fileName: String, bytes: ByteArray) -> Unit,
    onClearDocument: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        UploadImageStepShimmerSkeleton(modifier = modifier)
        return
    }

    val colors = LocalTaminColors.current
    val cardShape = RoundedCornerShape(CornerRadius.x2l)
    val scope = rememberCoroutineScope()
    var pickError by remember { mutableStateOf<String?>(null) }
    var showDescriptionError by remember { mutableStateOf(false) }
    val readErrorMessage = stringResource(Res.string.contract_upload_read_error)
    val descriptionEmptyError = stringResource(Res.string.contract_upload_description_empty_error)
    val uploadedDocument = uploadedDocuments.firstOrNull()
    val hasDocument = previewBytes != null || uploadedDocument != null
    val thumbnailBase64 = rememberBase64Thumbnail(previewBytes)
    val uploadState = when {
        isUploading -> TaminDocumentUploadState.Uploading
        uploadError != null && !hasDocument -> TaminDocumentUploadState.Failed
        hasDocument -> TaminDocumentUploadState.Uploaded
        else -> TaminDocumentUploadState.Empty
    }
    val canPick = !isUploading &&
        (uploadState == TaminDocumentUploadState.Empty || uploadState == TaminDocumentUploadState.Failed)

    val filePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file: PlatformFile? ->
        if (file == null) return@rememberFilePickerLauncher
        scope.launch {
            try {
                pickError = null
                val bytes = file.readBytes()
                onImagePicked(file.name, bytes)
            } catch (_: Exception) {
                pickError = readErrorMessage
            }
        }
    }

    val cardTitle = when (uploadState) {
        TaminDocumentUploadState.Empty,
        TaminDocumentUploadState.Failed,
        -> stringResource(Res.string.contract_upload_add_documents)
        TaminDocumentUploadState.Uploading,
        TaminDocumentUploadState.Uploaded,
        -> description.ifBlank {
            uploadedDocument?.fileName
                ?: stringResource(Res.string.contract_upload_add_documents)
        }
    }

    val statusText = when (uploadState) {
        TaminDocumentUploadState.Empty -> null
        TaminDocumentUploadState.Uploading -> stringResource(Res.string.contract_upload_status_uploading)
        TaminDocumentUploadState.Uploaded -> uploadedDocument?.fileName
            ?: stringResource(Res.string.contract_upload_status_uploaded)
        TaminDocumentUploadState.Failed -> uploadError
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, cardShape)
            .padding(Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        BannerCard(
            message = stringResource(Res.string.contract_upload_format_hint),
            type = BannerType.Info,
        )

        TaminTextField(
            value = description,
            onValueChange = {
                if (showDescriptionError && it.isNotBlank()) {
                    showDescriptionError = false
                }
                onDescriptionChange(it)
            },
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.contract_upload_description_required),
            placeholder = stringResource(Res.string.contract_upload_description_placeholder),
            enabled = !isUploading,
            singleLine = true,
            isError = showDescriptionError && description.isBlank(),
            errorMessage = if (showDescriptionError && description.isBlank()) {
                descriptionEmptyError
            } else {
                null
            },
        )

        TaminDocumentUploadCard(
            title = cardTitle,
            state = uploadState,
            statusText = statusText,
            thumbnailBase64 = thumbnailBase64,
            onCardClick = if (canPick) {
                {
                    if (description.isBlank()) {
                        showDescriptionError = true
                    } else {
                        showDescriptionError = false
                        filePickerLauncher.launch()
                    }
                }
            } else {
                null
            },
            onDeleteClick = if (uploadState == TaminDocumentUploadState.Uploaded) {
                {
                    pickError = null
                    onClearDocument()
                }
            } else {
                null
            },
        )

        uploadError?.takeIf { uploadState != TaminDocumentUploadState.Failed }?.let { error ->
            Text(
                text = error,
                color = colors.dangerText,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        pickError?.let { error ->
            Text(
                text = error,
                color = colors.dangerText,
                style = MaterialTheme.typography.bodySmall,
            )
        }
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

// -------------------------------------------------------------------------
// Previews
// -------------------------------------------------------------------------

@PreviewRtlTheme
@Composable
private fun UploadImageStepContentEmptyPreview() {
    PreviewRtlThemeContent {
        UploadImageStepContent(
            description = "",
            previewBytes = null,
            uploadedDocuments = emptyList(),
            isUploading = false,
            uploadError = null,
            onDescriptionChange = {},
            onImagePicked = { _, _ -> },
            onClearDocument = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun UploadImageStepContentFilledPreview() {
    PreviewRtlThemeContent {
        UploadImageStepContent(
            description = "گواهی اشتغال به تحصیل ترم جاری",
            previewBytes = null,
            uploadedDocuments = emptyList(),
            isUploading = false,
            uploadError = null,
            onDescriptionChange = {},
            onImagePicked = { _, _ -> },
            onClearDocument = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun UploadImageStepContentUploadingPreview() {
    PreviewRtlThemeContent {
        UploadImageStepContent(
            description = "گواهی اشتغال به تحصیل",
            previewBytes = null,
            uploadedDocuments = emptyList(),
            isUploading = true,
            uploadError = null,
            onDescriptionChange = {},
            onImagePicked = { _, _ -> },
            onClearDocument = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun UploadImageStepContentUploadedPreview() {
    PreviewRtlThemeContent {
        UploadImageStepContent(
            description = "گواهی اشتغال به تحصیل",
            previewBytes = null,
            uploadedDocuments = listOf(
                UploadImagePR(
                    imageId = "doc-1",
                    fileName = "student_card.jpg",
                    description = "گواهی اشتغال به تحصیل",
                ),
            ),
            isUploading = false,
            uploadError = null,
            onDescriptionChange = {},
            onImagePicked = { _, _ -> },
            onClearDocument = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun UploadImageStepContentFailedPreview() {
    PreviewRtlThemeContent {
        UploadImageStepContent(
            description = "گواهی اشتغال به تحصیل",
            previewBytes = null,
            uploadedDocuments = emptyList(),
            isUploading = false,
            uploadError = "خطا در بارگذاری تصویر",
            onDescriptionChange = {},
            onImagePicked = { _, _ -> },
            onClearDocument = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun UploadImageStepContentShimmerPreview() {
    PreviewRtlThemeContent {
        UploadImageStepContent(
            description = "",
            previewBytes = null,
            uploadedDocuments = emptyList(),
            isUploading = false,
            uploadError = null,
            onDescriptionChange = {},
            onImagePicked = { _, _ -> },
            onClearDocument = {},
            isLoading = true,
        )
    }
}
