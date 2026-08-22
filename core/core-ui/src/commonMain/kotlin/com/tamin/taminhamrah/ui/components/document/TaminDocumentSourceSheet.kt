package com.tamin.taminhamrah.ui.components.document

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_document_source_camera_subtitle
import taminx.core.core_ui.occurrence_document_source_camera_title
import taminx.core.core_ui.occurrence_document_source_gallery_subtitle
import taminx.core.core_ui.occurrence_document_source_gallery_title
import taminx.core.core_ui.orotez_protez_document_source_remove
import taminx.core.core_ui.orotez_protez_document_source_subtitle

private val OptionRowMinHeight = 64.dp

@Composable
fun TaminDocumentSourceSheet(
    title: String,
    showRemoveOption: Boolean = false,
    onSelectCamera: () -> Unit,
    onSelectGallery: () -> Unit,
    onRemove: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = Spacing.page)
                .padding(bottom = Spacing.md),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = stringResource(Res.string.orotez_protez_document_source_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textMuted,
            )
            Spacer(Modifier.height(Spacing.lg))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                TaminDocumentSourceRow(
                    title = stringResource(Res.string.occurrence_document_source_camera_title),
                    subtitle = stringResource(Res.string.occurrence_document_source_camera_subtitle),
                    onClick = onSelectCamera,
                )
                TaminDocumentSourceRow(
                    title = stringResource(Res.string.occurrence_document_source_gallery_title),
                    subtitle = stringResource(Res.string.occurrence_document_source_gallery_subtitle),
                    onClick = onSelectGallery,
                )
                if (showRemoveOption && onRemove != null) {
                    TaminDocumentSourceRow(
                        title = stringResource(Res.string.orotez_protez_document_source_remove),
                        subtitle = null,
                        isDestructive = true,
                        onClick = onRemove,
                    )
                }
            }
        }
    }
}

@Composable
private fun TaminDocumentSourceRow(
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val titleColor = if (isDestructive) colors.dangerText else colors.textPrimary
    val background = if (isDestructive) colors.dangerBg else colors.bgSurface
    val border = if (isDestructive) colors.dangerBorder else colors.border

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OptionRowMinHeight)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(background)
            .border(Thickness.border, border, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = titleColor,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(Spacing.xxs))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textMuted,
            )
        }
    }
}
