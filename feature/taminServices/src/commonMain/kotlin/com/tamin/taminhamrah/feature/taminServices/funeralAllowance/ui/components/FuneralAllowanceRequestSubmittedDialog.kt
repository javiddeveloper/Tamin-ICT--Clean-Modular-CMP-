package com.tamin.taminhamrah.feature.taminServices.funeralAllowance.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.funeral_allowance_back_to_services
import taminx.core.core_ui.funeral_allowance_submit_success_title

/**
 * Terminal success modal shown after a funeral-allowance request is submitted (or an
 * account-correction is confirmed). [message] is the backend's success text, which already
 * names the reviewing branch. The single action returns the user to the services menu.
 */
@Composable
internal fun FuneralAllowanceRequestSubmittedDialog(
    message: String,
    onBackToServices: () -> Unit,
) {
    val taminColors = LocalTaminColors.current

    TaminConfirmationDialog(
        title = stringResource(Res.string.funeral_allowance_submit_success_title),
        description = message,
        confirmButton = {
            TaminFilledButton(
                text = stringResource(Res.string.funeral_allowance_back_to_services),
                onClick = onBackToServices,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
            )
        },
        dismissButton = {},
        onDismissRequest = onBackToServices,
        icon = Icons.Default.Check,
        iconTint = taminColors.greenText,
        iconBackground = taminColors.greenBg,
    )
}
