package com.tamin.taminhamrah.feature.pensionInquiry.ui.payroll.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.edict_no_pensioner_desc
import taminx.core.core_ui.edict_no_pensioner_title
import taminx.core.core_ui.payroll_send_success_desc
import taminx.core.core_ui.payroll_send_success_title

@Composable
fun PayRollSuccessDialog(onDismiss: () -> Unit) {
    val taminColors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.payroll_send_success_title),
        description = stringResource(Res.string.payroll_send_success_desc),
        icon = Icons.Default.Check,
        iconTint = androidx.compose.ui.graphics.Color.White,
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
fun PayRollNoPensionerDialog(onDismiss: () -> Unit) {
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
private fun PayRollSuccessDialogPreview() {
    PreviewRtlThemeContent {
        PayRollSuccessDialog(onDismiss = {})
    }
}

@PreviewRtlTheme
@Composable
private fun PayRollNoPensionerDialogPreview() {
    PreviewRtlThemeContent {
        PayRollNoPensionerDialog(onDismiss = {})
    }
}
