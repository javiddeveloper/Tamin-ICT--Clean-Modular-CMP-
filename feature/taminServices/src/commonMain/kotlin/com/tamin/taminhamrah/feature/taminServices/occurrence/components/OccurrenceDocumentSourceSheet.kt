package com.tamin.taminhamrah.feature.taminServices.occurrence.components

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

private val OptionRowMinHeight = 64.dp

/**
 * Camera-vs-gallery picker shown after a document type has been chosen in Step6's document-type
 * sheet — [title] is the selected document type's own name (e.g. "مدارک درمانی"), matching how the
 * reference design reuses the type as the sheet heading rather than a generic label.
 */
@Composable
fun OccurrenceDocumentSourceSheet(
    title: String,
    onSelectCamera: () -> Unit,
    onSelectGallery: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colors.bgPage,
        shape = RoundedCornerShape(topStart = CornerRadius.sheet, topEnd = CornerRadius.sheet),
        modifier = modifier,
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
            Spacer(Modifier.height(Spacing.lg))

            Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                OccurrenceDocumentSourceRow(
                    title = stringResource(Res.string.occurrence_document_source_camera_title),
                    subtitle = stringResource(Res.string.occurrence_document_source_camera_subtitle),
                    onClick = onSelectCamera,
                )
                OccurrenceDocumentSourceRow(
                    title = stringResource(Res.string.occurrence_document_source_gallery_title),
                    subtitle = stringResource(Res.string.occurrence_document_source_gallery_subtitle),
                    onClick = onSelectGallery,
                )
            }
        }
    }
}

@Composable
private fun OccurrenceDocumentSourceRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    val colors = LocalTaminColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OptionRowMinHeight)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(colors.bgSurface)
            .border(Thickness.border, colors.border, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.textPrimary,
        )
        Spacer(Modifier.height(Spacing.xxs))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = colors.textMuted,
        )
    }
}
