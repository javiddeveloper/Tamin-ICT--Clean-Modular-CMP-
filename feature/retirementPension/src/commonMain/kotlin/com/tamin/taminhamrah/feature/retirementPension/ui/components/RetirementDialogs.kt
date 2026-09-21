package com.tamin.taminhamrah.feature.retirementPension.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.tamin.taminhamrah.feature.retirementPension.ui.contract.RetirementDialog
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminFormAbandonDialog
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.ic_tamin_alert_triangle
import taminx.core.core_ui.ic_tamin_check
import taminx.core.core_ui.ic_tamin_document_lines
import taminx.core.core_ui.ic_tamin_exclamation
import taminx.core.core_ui.retirement_pension_dialog_age_gate_body
import taminx.core.core_ui.retirement_pension_dialog_age_gate_title
import taminx.core.core_ui.retirement_pension_dialog_done_action
import taminx.core.core_ui.retirement_pension_dialog_done_body
import taminx.core.core_ui.retirement_pension_dialog_done_title
import taminx.core.core_ui.retirement_pension_dialog_objection_body
import taminx.core.core_ui.retirement_pension_dialog_objection_title
import taminx.core.core_ui.retirement_pension_dialog_rules_body
import taminx.core.core_ui.retirement_pension_dialog_rules_title
import taminx.core.core_ui.retirement_pension_dialog_understood
import taminx.core.core_ui.retirement_pension_title
import taminx.core.core_ui.retirement_pension_track_code_label

/**
 * Every modal this service shows.
 *
 * One entry point so the screen has a single `if` rather than five, and so the four informational
 * dialogs share one shape — they differ only in colour, glyph and copy.
 */
@Composable
internal fun RetirementDialogHost(
    dialog: RetirementDialog,
    trackingCode: String?,
    onDismiss: () -> Unit,
    onLeaveConfirmed: () -> Unit,
) {
    val colors = LocalTaminColors.current

    when (dialog) {
        RetirementDialog.Rules -> RetirementInfoDialog(
            title = stringResource(Res.string.retirement_pension_dialog_rules_title),
            body = stringResource(Res.string.retirement_pension_dialog_rules_body),
            icon = Res.drawable.ic_tamin_document_lines,
            tint = colors.blueText,
            background = colors.blueBg,
            onDismiss = onDismiss,
        )

        RetirementDialog.Objection -> RetirementInfoDialog(
            title = stringResource(Res.string.retirement_pension_dialog_objection_title),
            body = stringResource(Res.string.retirement_pension_dialog_objection_body),
            icon = Res.drawable.ic_tamin_alert_triangle,
            tint = colors.orangeText,
            background = colors.orangeBg,
            onDismiss = onDismiss,
        )

        // Closing this one leaves the service: the request cannot be made at all.
        RetirementDialog.AgeGate -> RetirementInfoDialog(
            title = stringResource(Res.string.retirement_pension_dialog_age_gate_title),
            body = stringResource(Res.string.retirement_pension_dialog_age_gate_body),
            icon = Res.drawable.ic_tamin_exclamation,
            tint = colors.dangerText,
            background = colors.dangerBg,
            onDismiss = onDismiss,
        )

        RetirementDialog.Done -> RetirementInfoDialog(
            title = stringResource(Res.string.retirement_pension_dialog_done_title),
            body = stringResource(Res.string.retirement_pension_dialog_done_body),
            icon = Res.drawable.ic_tamin_check,
            tint = colors.greenText,
            background = colors.greenBg,
            actionText = stringResource(Res.string.retirement_pension_dialog_done_action),
            onDismiss = onDismiss,
            content = trackingCode?.takeIf(String::isNotBlank)?.let { code ->
                { RetirementTrackingCodeChip(code) }
            },
        )

        RetirementDialog.Leave -> TaminFormAbandonDialog(
            formName = stringResource(Res.string.retirement_pension_title),
            onStay = onDismiss,
            onAbandon = onLeaveConfirmed,
        )
    }
}

@Composable
private fun RetirementInfoDialog(
    title: String,
    body: String,
    icon: org.jetbrains.compose.resources.DrawableResource,
    tint: androidx.compose.ui.graphics.Color,
    background: androidx.compose.ui.graphics.Color,
    onDismiss: () -> Unit,
    actionText: String = stringResource(Res.string.retirement_pension_dialog_understood),
    content: (@Composable () -> Unit)? = null,
) {
    TaminConfirmationDialog(
        title = title,
        description = body,
        icon = vectorResource(icon),
        iconTint = tint,
        iconBackground = background,
        content = content,
        confirmButton = {
            TaminFilledButton(
                text = actionText,
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
    )
}

/** The tracking code, in the dashed chip the design puts inside the success dialog. */
@Composable
private fun RetirementTrackingCodeChip(code: String) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.blueBg, RoundedCornerShape(CornerRadius.listRow))
            .padding(Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CopyIconButton(
            value = code,
            label = stringResource(Res.string.retirement_pension_track_code_label),
            tint = colors.blueText,
        )
        NumericText(
            text = code.toPersianDigits(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colors.blueText,
        )
    }
}
