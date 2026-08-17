package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.send_history_access_denied_action
import taminx.core.core_ui.send_history_access_denied_title

@Composable
internal fun AccessDeniedModal(message: String, onDismiss: () -> Unit) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.send_history_access_denied_title),
        description = message,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.send_history_access_denied_action),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Lock,
        iconTint = taminColors.dangerText,
        iconBackground = taminColors.dangerBg,
    )
}
