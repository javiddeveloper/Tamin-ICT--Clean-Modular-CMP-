package com.tamin.taminhamrah.feature.taminServices.sendHistoryToInstitutions.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.send_history_success_action
import taminx.core.core_ui.send_history_success_description
import taminx.core.core_ui.send_history_success_title

@Composable
internal fun SuccessModal(onDismiss: () -> Unit) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.send_history_success_title),
        description = stringResource(Res.string.send_history_success_description),
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.send_history_success_action),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Check,
        iconTint = taminColors.greenText,
        iconBackground = taminColors.greenBg
    )
}
