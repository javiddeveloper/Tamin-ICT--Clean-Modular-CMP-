package com.tamin.taminhamrah.feature.addDependent.ui

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.addDependent.ui.contract.AddDependentState
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DOC_TYPE_ID_FIRST_PAGE
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DOC_TYPE_MARRIAGE_CERTIFICATE
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DOC_TYPE_SPOUSE_ID
import com.tamin.taminhamrah.feature.addDependent.ui.contract.DocType
import com.tamin.taminhamrah.feature.addDependent.ui.contract.UploadedDocument
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.upload_banner_error
import taminx.core.core_ui.upload_banner_no_need
import taminx.core.core_ui.upload_banner_warning
import taminx.core.core_ui.upload_desc
import taminx.core.core_ui.upload_slot_placeholder
import taminx.core.core_ui.upload_slot_success
import taminx.core.core_ui.upload_title
import taminx.core.core_ui.doc_type_id_first_page
import taminx.core.core_ui.doc_type_marriage_certificate
import taminx.core.core_ui.doc_type_spouse_id
import taminx.core.core_ui.error_file_read_fallback
import org.jetbrains.compose.resources.getString

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

@Composable
fun DocumentUploadStep(
    state: AddDependentState,
    onIntent: (AddDependentIntent) -> Unit
) {
    val colors = LocalTaminColors.current
    val activeDocTypes =
        remember(state.requiredDocTypes) { state.requiredDocTypes.filter { !it.isDisabled } }
    val uploadedTypes =
        remember(state.uploadedDocuments) { state.uploadedDocuments.map { it.docType }.toSet() }
    val allUploaded = activeDocTypes.all { uploadedTypes.contains(it.code) }

    val scope = rememberCoroutineScope()
    var pendingDocType by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberFilePickerLauncher(
        type = FileKitType.Image,
    ) { file: PlatformFile? ->
        if (file == null) return@rememberFilePickerLauncher
        val targetDocType = pendingDocType ?: return@rememberFilePickerLauncher

        scope.launch {
            try {
                val bytes = file.readBytes()
                onIntent(
                    AddDependentIntent.UploadDocument(
                        fileBytes = bytes,
                        fileName = file.name,
                        docType = targetDocType
                    )
                )
            } catch (e: Exception) {
                val errorMsg = getString(Res.string.error_file_read_fallback)
                onIntent(
                    AddDependentIntent.OnFileReadError(
                        e.message ?: errorMsg
                    )
                )
            } finally {
                pendingDocType = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.lg)
    ) {
        StepSectionTitle(title = stringResource(Res.string.upload_title))
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = stringResource(Res.string.upload_desc),
            style = MaterialTheme.typography.bodySmall,
            color = colors.textMuted
        )
        Spacer(modifier = Modifier.height(Spacing.md))

        if (activeDocTypes.isEmpty()) {
            BannerCard(
                message = stringResource(Res.string.upload_banner_no_need),
                type = BannerType.Success
            )
        } else {
            BannerCard(
                message = stringResource(Res.string.upload_banner_warning),
                type = BannerType.Warning
            )
            Spacer(modifier = Modifier.height(Spacing.md))
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                activeDocTypes.forEach { docType ->
                    val uploaded = state.uploadedDocuments.find { it.docType == docType.code }
                    DocumentSlotCard(
                        docType = docType,
                        uploaded = uploaded,
                        onUploadClicked = {
                            pendingDocType = docType.code
                            filePickerLauncher.launch()
                        },
                        onDeleteClicked = { onIntent(AddDependentIntent.DeleteDocument(docType.code)) }
                    )
                }
            }
            if (!allUploaded) {
                Spacer(modifier = Modifier.height(Spacing.md))
                BannerCard(
                    message = stringResource(Res.string.upload_banner_error),
                    type = BannerType.Error
                )
            }
        }
    }
}

@Composable
private fun DocumentSlotCard(
    docType: DocType,
    uploaded: UploadedDocument?,
    onUploadClicked: () -> Unit,
    onDeleteClicked: () -> Unit
) {
    val colors = LocalTaminColors.current
    val borderColor = if (uploaded != null) colors.greenText else colors.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(1.dp, borderColor, RoundedCornerShape(CornerRadius.lg))
            .clickable(enabled = uploaded == null, onClick = onUploadClicked)
            .padding(Spacing.lg),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                text = documentTypeTitle(docType.code),
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = if (uploaded != null) stringResource(Res.string.upload_slot_success) else stringResource(Res.string.upload_slot_placeholder),
                color = if (uploaded != null) colors.greenText else colors.textMuted,
                style = MaterialTheme.typography.labelSmall
            )
        }
        Spacer(modifier = Modifier.width(Spacing.md))
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(
                    brush = if (uploaded != null) {
                        colors.iconGradientSuccess
                    } else {
                        Brush.linearGradient(listOf(colors.blueBg, colors.blueBg))
                    }
                )
                .clickable(onClick = if (uploaded != null) onDeleteClicked else onUploadClicked),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (uploaded != null) Icons.Filled.CheckCircle else Icons.Filled.Add,
                contentDescription = null,
                tint = if (uploaded != null) Color.White else colors.blueText,
                modifier = Modifier.size(21.dp)
            )
        }
    }
}

@Composable
private fun documentTypeTitle(code: String): String = stringResource(
    when (code) {
        DOC_TYPE_ID_FIRST_PAGE -> Res.string.doc_type_id_first_page
        DOC_TYPE_SPOUSE_ID -> Res.string.doc_type_spouse_id
        DOC_TYPE_MARRIAGE_CERTIFICATE -> Res.string.doc_type_marriage_certificate
        else -> Res.string.doc_type_id_first_page
    }
)
