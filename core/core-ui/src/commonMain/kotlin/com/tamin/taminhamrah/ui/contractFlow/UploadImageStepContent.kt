package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import com.tamin.taminhamrah.model.contractFlow.UploadImagePR
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.contract_upload_add_documents
import taminx.core.core_ui.contract_upload_description_label
import taminx.core.core_ui.contract_upload_format_hint
import taminx.core.core_ui.contract_upload_image_content_description
import taminx.core.core_ui.contract_upload_read_error
import taminx.core.core_ui.contract_upload_remove_image

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
) {
    val scope = rememberCoroutineScope()
    var pickError by remember { mutableStateOf<String?>(null) }
    val hasDocument = previewBytes != null || uploadedDocuments.isNotEmpty()
    val uploadedImageDescription = stringResource(Res.string.contract_upload_image_content_description)
    val readErrorMessage = stringResource(Res.string.contract_upload_read_error)

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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = stringResource(Res.string.contract_upload_format_hint),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(Res.string.contract_upload_description_label)) },
            placeholder = { Text(stringResource(Res.string.contract_upload_description_label)) },
            enabled = !isUploading,
            singleLine = false,
        )

        if (hasDocument) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                previewBytes?.let { bytes ->
                    SubcomposeAsyncImage(
                        model = bytes,
                        contentDescription = description.ifBlank { uploadedImageDescription },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        },
                    )
                }

                if (isUploading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                            modifier = Modifier.fillMaxSize(),
                        ) {}
                        CircularProgressIndicator()
                    }
                }

                IconButton(
                    onClick = {
                        pickError = null
                        onClearDocument()
                    },
                    enabled = !isUploading,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp),
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.contract_upload_remove_image),
                            modifier = Modifier.padding(4.dp),
                        )
                    }
                }
            }
        } else {
            Button(
                onClick = { filePickerLauncher.launch() },
                enabled = !isUploading && description.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(stringResource(Res.string.contract_upload_add_documents))
            }
        }

        uploadError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        pickError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
