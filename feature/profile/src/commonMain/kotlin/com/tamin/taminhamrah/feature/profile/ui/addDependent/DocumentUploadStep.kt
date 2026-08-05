package com.tamin.taminhamrah.feature.profile.ui.addDependent

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
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentIntent
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.AddDependentState
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.DocType
import com.tamin.taminhamrah.feature.profile.ui.addDependent.contract.UploadedDocument
import com.tamin.taminhamrah.ui.components.BannerCard
import com.tamin.taminhamrah.ui.components.BannerType
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.upload_banner_error
import taminx.core.core_ui.upload_banner_no_need
import taminx.core.core_ui.upload_banner_warning
import taminx.core.core_ui.upload_desc
import taminx.core.core_ui.upload_slot_placeholder
import taminx.core.core_ui.upload_slot_success
import taminx.core.core_ui.upload_title

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
                            onIntent(
                                AddDependentIntent.UploadDocument(
                                    fileBytes = ByteArray(100),
                                    fileName = "${docType.code}.jpg",
                                    docType = docType.code
                                )
                            )
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
                text = docType.title,
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
