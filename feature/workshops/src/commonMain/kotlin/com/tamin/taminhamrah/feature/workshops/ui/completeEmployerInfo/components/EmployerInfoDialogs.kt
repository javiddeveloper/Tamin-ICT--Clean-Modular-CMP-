package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoDialog
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.employer_info_dialog_expiry_body
import taminx.core.core_ui.employer_info_dialog_expiry_title
import taminx.core.core_ui.employer_info_dialog_legal_success_body
import taminx.core.core_ui.employer_info_dialog_legal_success_title
import taminx.core.core_ui.employer_info_dialog_real_success_body
import taminx.core.core_ui.employer_info_dialog_real_success_title

/**
 * The three endings this screen can reach: the legal workshop registered, the real workshop's
 * request filed, and the validation ticket expiring.
 *
 * All three are core-ui's [TaminConfirmationDialog] with a different icon and palette — only the
 * expiry is a failure, so only it is red. It takes no dismiss button because every one of them has
 * a single way out.
 */
@Composable
fun EmployerInfoDialogs(
    dialogState: CompleteEmployerInfoDialog?,
    onDismiss: () -> Unit,
) {
    if (dialogState == null) return

    val colors = LocalTaminColors.current
    val isError = dialogState == CompleteEmployerInfoDialog.TIMER_EXPIRED

    val title = when (dialogState) {
        CompleteEmployerInfoDialog.SUCCESS_LEGAL -> Res.string.employer_info_dialog_legal_success_title
        CompleteEmployerInfoDialog.SUCCESS_REAL -> Res.string.employer_info_dialog_real_success_title
        CompleteEmployerInfoDialog.TIMER_EXPIRED -> Res.string.employer_info_dialog_expiry_title
    }

    val body = when (dialogState) {
        CompleteEmployerInfoDialog.SUCCESS_LEGAL -> Res.string.employer_info_dialog_legal_success_body
        CompleteEmployerInfoDialog.SUCCESS_REAL -> Res.string.employer_info_dialog_real_success_body
        CompleteEmployerInfoDialog.TIMER_EXPIRED -> Res.string.employer_info_dialog_expiry_body
    }

    TaminConfirmationDialog(
        title = stringResource(title),
        description = stringResource(body),
        icon = if (isError) Icons.Default.Warning else Icons.Default.Check,
        iconTint = if (isError) colors.dangerText else colors.greenText,
        iconBackground = if (isError) colors.dangerBg else colors.greenBg,
        onDismissRequest = onDismiss,
        confirmButton = {
            TaminPrimaryButton(
                text = stringResource(Res.string.btn_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
    )
}
