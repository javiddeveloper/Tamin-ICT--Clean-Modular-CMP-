package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.later
import taminx.core.core_ui.no_bank_account_desc
import taminx.core.core_ui.no_bank_account_title
import taminx.core.core_ui.register_bank_account

/**
 * Blocking modal shown when a short-term-benefit request flow is opened but the
 * user has no registered bank account. Confirm navigates to the bank-account
 * screen; dismiss ("later") backs out of the request flow.
 */
@Composable
fun NoBankAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    TaminConfirmationDialog(
        title = stringResource(Res.string.no_bank_account_title),
        description = stringResource(Res.string.no_bank_account_desc),
        icon = Icons.Outlined.Info,
        iconTint = Color.White,
        iconBackground = colors.dangerText.copy(alpha = 0.8f),
        onDismissRequest = onDismiss,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.register_bank_account),
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {
            TaminOutlinedButton(
                text = stringResource(Res.string.later),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
    )
}
