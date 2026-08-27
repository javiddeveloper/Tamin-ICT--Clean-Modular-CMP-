package com.tamin.taminhamrah.feature.taminServices.inspection.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.inspection_request_success_dismiss_button
import taminx.core.core_ui.inspection_request_success_subtitle_format
import taminx.core.core_ui.inspection_request_success_subtitle_objection_format
import taminx.core.core_ui.inspection_request_success_title
import taminx.core.core_ui.inspection_request_success_title_objection
import taminx.core.core_ui.inspection_request_success_tracking_label

@Composable
internal fun InspectionRequestSuccessDialog(
    isObjection: Boolean,
    branchName: String,
    trackingId: Long?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(
            if (isObjection) Res.string.inspection_request_success_title_objection
            else Res.string.inspection_request_success_title
        ),
        description = stringResource(
            if (isObjection) Res.string.inspection_request_success_subtitle_objection_format
            else Res.string.inspection_request_success_subtitle_format,
            branchName,
        ),
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (trackingId != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = taminColors.chipBg)
                            .border(
                                width = 1.dp,
                                color = taminColors.blueBg,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TaminText(
                            text = stringResource(Res.string.inspection_request_success_tracking_label),
                            color = taminColors.blueText
                        )
                        NumericText(
                            text = trackingId.toString(),
                            color = taminColors.textPrimary,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(Modifier.height(Spacing.md))
                }
                TaminFilledButton(
                    text = stringResource(Res.string.inspection_request_success_dismiss_button),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Check,
        iconTint = taminColors.greenText,
        iconBackground = taminColors.greenBg,
        modifier = modifier,
    )
}

@PreviewRtlTheme
@Composable
private fun InspectionRequestSuccessDialogPreviewLight() {
    PreviewRtlThemeContent {
        InspectionRequestSuccessDialog(
            isObjection = false,
            branchName = "پنج تهران",
            trackingId = 121213224234L,
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun InspectionRequestSuccessDialogObjectionPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        InspectionRequestSuccessDialog(
            isObjection = true,
            branchName = "پنج تهران",
            trackingId = 121213224234L,
            onDismiss = {},
        )
    }
}
