package com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.feature.workshops.ui.completeEmployerInfo.contract.CompleteEmployerInfoDialog
import com.tamin.taminhamrah.ui.components.TaminPrimaryButton
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.btn_understood
import taminx.core.core_ui.employer_info_dialog_expiry_body
import taminx.core.core_ui.employer_info_dialog_expiry_title
import taminx.core.core_ui.employer_info_dialog_legal_success_body
import taminx.core.core_ui.employer_info_dialog_legal_success_title
import taminx.core.core_ui.employer_info_dialog_real_success_body
import taminx.core.core_ui.employer_info_dialog_real_success_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployerInfoDialogs(
    dialogState: CompleteEmployerInfoDialog?,
    onDismiss: () -> Unit,
) {
    if (dialogState == null) return

    val colors = LocalTaminColors.current
    val isError = dialogState == CompleteEmployerInfoDialog.TIMER_EXPIRED

    val title = when (dialogState) {
        CompleteEmployerInfoDialog.SUCCESS_LEGAL -> stringResource(Res.string.employer_info_dialog_legal_success_title)
        CompleteEmployerInfoDialog.SUCCESS_REAL -> stringResource(Res.string.employer_info_dialog_real_success_title)
        CompleteEmployerInfoDialog.TIMER_EXPIRED -> stringResource(Res.string.employer_info_dialog_expiry_title)
    }

    val body = when (dialogState) {
        CompleteEmployerInfoDialog.SUCCESS_LEGAL -> stringResource(Res.string.employer_info_dialog_legal_success_body)
        CompleteEmployerInfoDialog.SUCCESS_REAL -> stringResource(Res.string.employer_info_dialog_real_success_body)
        CompleteEmployerInfoDialog.TIMER_EXPIRED -> stringResource(Res.string.employer_info_dialog_expiry_body)
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(CornerRadius.sheet),
                )
                .clip(RoundedCornerShape(CornerRadius.sheet))
                .background(colors.bgSurface)
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Status Icon Circle
            val iconBg = if (isError) colors.dangerBg else colors.greenBg
            val iconFg = if (isError) colors.dangerText else colors.greenText

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                if (isError) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = iconFg,
                        modifier = Modifier.size(26.dp),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = iconFg,
                        modifier = Modifier.size(26.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.xs))

            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = colors.textSecondary,
                    lineHeight = 22.sp,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(Spacing.lg))

            TaminPrimaryButton(
                text = stringResource(Res.string.btn_understood),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
