package com.tamin.taminhamrah.ui.contractFlow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.CopyIconButton
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminOutlinedButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.rememberCopyAction
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.Elevation
import com.tamin.taminhamrah.ui.theme.IconSize
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.action_copy
import taminx.core.core_ui.contract_payment_dialog_dismiss
import taminx.core.core_ui.contract_payment_dialog_pay
import taminx.core.core_ui.contract_submit_failure_message_fallback
import taminx.core.core_ui.contract_submit_failure_title
import taminx.core.core_ui.contract_submit_success_message
import taminx.core.core_ui.contract_submit_success_title
import taminx.core.core_ui.contract_update_success_message
import taminx.core.core_ui.contract_update_success_title
import taminx.core.core_ui.ic_error
import taminx.core.core_ui.ic_tamin_check_circle
import taminx.core.core_ui.retry

sealed interface ContractSubmitResult {
    data class Success(
        val contractNumber: String,
        val contractDate: String,
        val amount: Long,
        val canPayOnline: Boolean,
    ) : ContractSubmitResult

    data object UpdateSuccess : ContractSubmitResult

    data class Failure(
        val message: String,
    ) : ContractSubmitResult
}

@Composable
fun ContractSubmitResultDialog(
    result: ContractSubmitResult,
    onDismiss: () -> Unit,
    onPay: (contractNumber: String, amount: Long) -> Unit = { _, _ -> },
    onRetry: () -> Unit = {},
) {
    when (result) {
        is ContractSubmitResult.Success -> ContractSubmitSuccessDialog(
            contractNumber = result.contractNumber,
            contractDate = result.contractDate,
            amount = result.amount,
            canPayOnline = result.canPayOnline,
            onDismiss = onDismiss,
            onPay = onPay,
        )
        ContractSubmitResult.UpdateSuccess -> ContractUpdateSuccessDialog(onDismiss = onDismiss)
        is ContractSubmitResult.Failure -> ContractSubmitFailureDialog(
            message = result.message,
            onDismiss = onDismiss,
            onRetry = onRetry,
        )
    }
}

@Composable
private fun ContractUpdateSuccessDialog(
    onDismiss: () -> Unit,
) {
    val colors = LocalTaminColors.current
    ContractSubmitResultScaffold(
        onDismissRequest = onDismiss,
        icon = {
            ResultStatusIcon(
                background = colors.greenBg,
                iconRes = Res.drawable.ic_tamin_check_circle,
                tint = colors.greenText,
            )
        },
        title = stringResource(Res.string.contract_update_success_title),
        description = stringResource(Res.string.contract_update_success_message),
        content = null,
        actions = {
            TaminFilledButton(
                text = stringResource(Res.string.contract_payment_dialog_dismiss),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                background = Brush.linearGradient(
                    listOf(colors.greenText, colors.greenText),
                ),
                shape = RoundedCornerShape(CornerRadius.lg),
            )
        },
    )
}

@Composable
private fun ContractSubmitSuccessDialog(
    contractNumber: String,
    contractDate: String,
    amount: Long,
    canPayOnline: Boolean,
    onDismiss: () -> Unit,
    onPay: (contractNumber: String, amount: Long) -> Unit,
) {
    val colors = LocalTaminColors.current
    val displayNumber = contractNumber.toPersianDigits()

    ContractSubmitResultScaffold(
        onDismissRequest = onDismiss,
        icon = {
            ResultStatusIcon(
                background = colors.greenBg,
                iconRes = Res.drawable.ic_tamin_check_circle,
                tint = colors.greenText,
            )
        },
        title = stringResource(Res.string.contract_submit_success_title),
        description = stringResource(
            Res.string.contract_submit_success_message,
            contractDate.toPersianDigits(),
        ),
        content = {
            ContractNumberChip(
                rawNumber = contractNumber,
                displayNumber = displayNumber,
            )
        },
        actions = {
            if (canPayOnline) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TaminFilledButton(
                        text = stringResource(Res.string.contract_payment_dialog_pay),
                        onClick = {
                            onPay(contractNumber, amount)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        background = Brush.linearGradient(
                            listOf(colors.greenText, colors.greenText),
                        ),
                        shape = RoundedCornerShape(CornerRadius.lg),
                    )
                    TaminOutlinedButton(
                        text = stringResource(Res.string.contract_payment_dialog_dismiss),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(CornerRadius.lg),
                        contentColor = colors.textSecondary,
                    )
                }
            } else {
                TaminFilledButton(
                    text = stringResource(Res.string.contract_payment_dialog_dismiss),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    background = Brush.linearGradient(
                        listOf(colors.greenText, colors.greenText),
                    ),
                    shape = RoundedCornerShape(CornerRadius.lg),
                )
            }
        },
    )
}

@Composable
private fun ContractSubmitFailureDialog(
    message: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit,
) {
    val colors = LocalTaminColors.current
    val body = message.ifBlank {
        stringResource(Res.string.contract_submit_failure_message_fallback)
    }

    ContractSubmitResultScaffold(
        onDismissRequest = onDismiss,
        icon = {
            ResultStatusIcon(
                background = colors.dangerText.copy(alpha = 0.12f),
                iconRes = Res.drawable.ic_error,
                tint = colors.dangerText,
            )
        },
        title = stringResource(Res.string.contract_submit_failure_title),
        description = body,
        content = null,
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminFilledButton(
                    text = stringResource(Res.string.retry),
                    onClick = {
                        onDismiss()
                        onRetry()
                    },
                    modifier = Modifier.weight(1f),
                    background = Brush.linearGradient(
                        listOf(colors.greenText, colors.greenText),
                    ),
                    shape = RoundedCornerShape(CornerRadius.lg),
                )
                TaminOutlinedButton(
                    text = stringResource(Res.string.contract_payment_dialog_dismiss),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    contentColor = colors.textSecondary,
                )
            }
        },
    )
}

@Composable
private fun ContractSubmitResultScaffold(
    onDismissRequest: () -> Unit,
    icon: @Composable () -> Unit,
    title: String,
    description: String,
    content: (@Composable () -> Unit)?,
    actions: @Composable () -> Unit,
) {
    val colors = LocalTaminColors.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl),
            shape = RoundedCornerShape(CornerRadius.x2l),
            colors = CardDefaults.cardColors(containerColor = colors.bgSurface),
            elevation = CardDefaults.cardElevation(defaultElevation = Elevation.sm),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                icon()

                TaminText(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                )

                TaminText(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                )

                content?.invoke()

                actions()
            }
        }
    }
}

@Composable
private fun ResultStatusIcon(
    background: Color,
    iconRes: DrawableResource,
    tint: Color,
) {
    Box(
        modifier = Modifier
            .size(IconSize.xxlarge)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = vectorResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(IconSize.large),
        )
    }
}

@Composable
private fun ContractNumberChip(
    rawNumber: String,
    displayNumber: String,
) {
    val colors = LocalTaminColors.current
    val shape = RoundedCornerShape(CornerRadius.lg)
    val copyAction = rememberCopyAction(rawNumber)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(colors.blueBg)
            .border(Thickness.border, colors.blueBorder, shape)
            .clickable(onClick = copyAction)
            .padding(horizontal = Spacing.md, vertical = Spacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        TaminText(
            text = displayNumber,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = colors.blueText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        ) {
            TaminText(
                text = stringResource(Res.string.action_copy),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = colors.blueText,
            )
            CopyIconButton(
                value = rawNumber,
                tint = colors.blueText,
                interactive = false,
            )
        }
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSubmitSuccessDialogPreview() {
    PreviewRtlThemeContent {
        ContractSubmitResultDialog(
            result = ContractSubmitResult.Success(
                contractNumber = "3189362702",
                contractDate = "1405/06/07",
                amount = 22_596_000L,
                canPayOnline = true,
            ),
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractUpdateSuccessDialogPreview() {
    PreviewRtlThemeContent {
        ContractSubmitResultDialog(
            result = ContractSubmitResult.UpdateSuccess,
            onDismiss = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun ContractSubmitFailureDialogPreview() {
    PreviewRtlThemeContent {
        ContractSubmitResultDialog(
            result = ContractSubmitResult.Failure(
                message = "به دلیل اختلال در سامانه، ثبت قرارداد انجام نشد. لطفاً دوباره تلاش کنید.",
            ),
            onDismiss = {},
            onRetry = {},
        )
    }
}
