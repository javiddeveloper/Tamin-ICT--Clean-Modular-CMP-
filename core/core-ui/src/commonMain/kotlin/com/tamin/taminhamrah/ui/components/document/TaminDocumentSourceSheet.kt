package com.tamin.taminhamrah.ui.components.document

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.IconSize
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
    /**
     * Glyphs for the two source rows. Both null — the default — keeps the text-only sheet every
     * existing caller was built against.
     */
    cameraIcon: ImageVector? = null,
    galleryIcon: ImageVector? = null,
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
                    icon = cameraIcon,
                    onClick = onSelectCamera,
                )
                TaminDocumentSourceRow(
                    title = stringResource(Res.string.occurrence_document_source_gallery_title),
                    subtitle = stringResource(Res.string.occurrence_document_source_gallery_subtitle),
                    icon = galleryIcon,
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
    icon: ImageVector? = null,
    isDestructive: Boolean = false,
) {
    val colors = LocalTaminColors.current
    val titleColor = if (isDestructive) colors.dangerText else colors.textPrimary
    val background = if (isDestructive) colors.dangerBg else colors.bgSurface
    val border = if (isDestructive) colors.dangerBorder else colors.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = OptionRowMinHeight)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(background)
            .border(Thickness.border, border, RoundedCornerShape(CornerRadius.lg))
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(IconTileSize)
                    .background(colors.blueBg, RoundedCornerShape(CornerRadius.textFieldIcon)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) colors.dangerText else colors.blueText,
                    modifier = Modifier.size(IconSize.small),
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
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
}

private val IconTileSize = 30.dp
