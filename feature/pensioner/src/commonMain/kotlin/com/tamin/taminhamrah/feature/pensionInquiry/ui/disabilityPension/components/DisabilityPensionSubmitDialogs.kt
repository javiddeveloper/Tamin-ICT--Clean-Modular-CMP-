package com.tamin.taminhamrah.feature.pensionInquiry.ui.disabilityPension.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.dashedOutline
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_copy
import taminx.core.core_ui.disability_pension_submit_success_description
import taminx.core.core_ui.disability_pension_submit_success_dismiss_button
import taminx.core.core_ui.disability_pension_submit_success_title
import taminx.core.core_ui.disability_pension_submitting_message

/** Non-dismissable overlay shown while the 3-call save/document/confirm submit chain runs. */
@Composable
fun DisabilityPensionSubmittingDialog() {
    val colors = LocalTaminColors.current

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = Spacing.xxxxl)
                .clip(RoundedCornerShape(CornerRadius.card))
                .background(colors.bgSurface)
                .padding(horizontal = Spacing.xl, vertical = Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            CircularProgressIndicator(color = colors.blueText)
            Text(
                text = stringResource(Res.string.disability_pension_submitting_message),
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = colors.textPrimary,
            )
        }
    }
}

@Composable
fun DisabilityPensionSubmitSuccessDialog(
    trackingCode: String,
    onAcknowledged: () -> Unit,
) {
    val colors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.disability_pension_submit_success_title),
        description = stringResource(Res.string.disability_pension_submit_success_description),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.disability_pension_submit_success_dismiss_button),
                onClick = onAcknowledged,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onAcknowledged,
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
        content = {
            val copyAction = rememberCopyAction(trackingCode)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(colors.blueBg)
                    .dashedOutline(colors.blueBorder, CornerRadius.lg, 1.dp)
                    .clickable(onClick = copyAction)
                    .padding(horizontal = Spacing.smd, vertical = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                NumericText(
                    text = trackingCode,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = colors.blueText,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(Res.string.action_copy),
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textMuted,
                    )
                    CopyIconButton(value = trackingCode, tint = colors.textMuted, interactive = false)
                }
            }
        },
    )
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionSubmittingDialogPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionSubmittingDialog()
    }
}

@PreviewRtlTheme
@Composable
private fun DisabilityPensionSubmitSuccessDialogPreview() {
    PreviewRtlThemeContent {
        DisabilityPensionSubmitSuccessDialog(trackingCode = "3829147205", onAcknowledged = {})
    }
}
