package com.tamin.taminhamrah.feature.pensionInquiry.ui.edict.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_cancel
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.edict_confirm_send
import taminx.core.core_ui.edict_no_pensioner_desc
import taminx.core.core_ui.edict_no_pensioner_title
import taminx.core.core_ui.edict_send_confirm_desc
import taminx.core.core_ui.edict_send_confirm_title
import taminx.core.core_ui.edict_send_success_desc
import taminx.core.core_ui.edict_send_success_title

@Composable
fun EdictSendConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.edict_send_confirm_title),
        description = stringResource(Res.string.edict_send_confirm_desc),
        icon = Icons.Default.Check,
        iconTint = taminColors.blueText,
        iconBackground = taminColors.blueBg,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.edict_confirm_send),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(CornerRadius.md),
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.action_cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(CornerRadius.md),
            )
        },
        onDismissRequest = onDismiss,
    )
}

@Composable
fun EdictSuccessDialog(
    title: String,
    description: String,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = title,
        description = description,
        icon = Icons.Default.Check,
        iconTint = Color.White,
        iconBackground = taminColors.greenText,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.btn_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(CornerRadius.md),
                background = Brush.horizontalGradient(listOf(taminColors.greenText, taminColors.teal)),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
    )
}

@Composable
fun EdictNoPensionerDialog(onDismiss: () -> Unit) {
    val taminColors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.edict_no_pensioner_title),
        description = stringResource(Res.string.edict_no_pensioner_desc),
        icon = Icons.Outlined.Info,
        iconTint = taminColors.blueText,
        iconBackground = taminColors.blueBg,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.btn_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                height = 50.dp,
                shape = RoundedCornerShape(CornerRadius.md),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
    )
}

@PreviewRtlTheme
@Composable
private fun EdictSendConfirmationDialogPreview() {
    PreviewRtlThemeContent {
        EdictSendConfirmationDialog(onConfirm = {}, onDismiss = {})
    }
}

@PreviewRtlTheme
@Composable
private fun EdictSuccessDialogPreview() {
    PreviewRtlThemeContent {
        EdictSuccessDialog(
            title = stringResource(Res.string.edict_send_success_title),
            description = stringResource(Res.string.edict_send_success_desc),
            onDismiss = {},
        )
    }
}
