package com.tamin.taminhamrah.feature.taminServices.occurrence.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tamin.taminhamrah.ui.components.DetailRow
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.occurrence_success_action
import taminx.core.core_ui.occurrence_success_desc
import taminx.core.core_ui.occurrence_success_title
import taminx.core.core_ui.occurrence_success_tracking_label

@Composable
internal fun OccurrenceSuccessModal(
    trackingCode: String,
    onDismiss: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.occurrence_success_title),
        description = stringResource(Res.string.occurrence_success_desc),
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                DetailRow(
                    label = stringResource(Res.string.occurrence_success_tracking_label),
                    value = trackingCode,
                    numeric = true,
                )
                TaminFilledButton(
                    text = stringResource(Res.string.occurrence_success_action),
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
    )
}
