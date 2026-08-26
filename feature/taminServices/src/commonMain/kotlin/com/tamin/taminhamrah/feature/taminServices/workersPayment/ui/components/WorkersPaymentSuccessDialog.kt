package com.tamin.taminhamrah.feature.taminServices.workersPayment.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tamin.taminhamrah.feature.taminServices.workersPayment.contract.WorkersPaymentUiState
import com.tamin.taminhamrah.feature.taminServices.workersPayment.model.WorkersPaymentInfoPR
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.components.NumericText
import com.tamin.taminhamrah.ui.components.TaminConfirmationDialog
import com.tamin.taminhamrah.ui.components.TaminFilledButton
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.toPriceFormat
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.jalali_months
import taminx.core.core_ui.workers_payment_success_dismiss
import taminx.core.core_ui.workers_payment_success_subtitle
import taminx.core.core_ui.workers_payment_success_title
import taminx.core.core_ui.workers_payment_success_tracking_label

/**
 * Post-payment receipt — shown on screen 2 once `inspectTicket` confirms the gateway payment.
 * Same shell as the inspection branch's `InspectionRequestSuccessDialog`: [TaminConfirmationDialog]
 * with a green check, a summary line and a bordered tracking-code row above the dismiss button.
 */
@Composable
internal fun WorkersPaymentSuccessDialog(
    receipt: WorkersPaymentUiState.PaymentReceipt,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val months = stringArrayResource(Res.array.jalali_months)
    val item = receipt.item
    val monthName = try {
        item.month
            .toInt()
            .minus(1)
            .let { months.getOrNull(it) }
            ?: item.monthTitle
    } catch (e: Exception) {
        item.monthTitle
    }

    TaminConfirmationDialog(
        title = stringResource(Res.string.workers_payment_success_title),
        description = stringResource(
            Res.string.workers_payment_success_subtitle,
            "$monthName ${item.year}",
            item.totalPayable.toPriceFormat(),
        ),
        confirmButton = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(colors.chipBg)
                        .border(1.dp, colors.blueBg, RoundedCornerShape(CornerRadius.lg))
                        .padding(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TaminText(
                        text = stringResource(Res.string.workers_payment_success_tracking_label),
                        color = colors.blueText,
                    )
                    NumericText(
                        text = receipt.trackingCode,
                        color = colors.textPrimary,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    )
                }
                Spacer(Modifier.height(Spacing.md))
                TaminFilledButton(
                    text = stringResource(Res.string.workers_payment_success_dismiss),
                    onClick = onDismiss,
                    background = colors.iconGradientSuccess,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        dismissButton = {},
        onDismissRequest = onDismiss,
        icon = Icons.Default.Check,
        iconTint = colors.greenText,
        iconBackground = colors.greenBg,
        modifier = modifier,
    )
}

// ─── Preview ──────────────────────────────────────────────────────────────────

private val PreviewReceipt = WorkersPaymentUiState.PaymentReceipt(
    item = WorkersPaymentInfoPR(
        pay = false, payable = true, month = "05", monthTitle = "حق بیمه مرداد", year = "1404",
        professionalTitle = "بنّای سفت‌کار", professional = "7112", rate = "1.9", days = "31",
        fromDatePersian = "14040501", toDatePersian = "14040531",
        amount = 8940000, amountFines = 0, totalPayable = 8940000, salary = 2312000, payDay = 5541850,
        payableDes = "هست", paymentDate = null, fishStatus = "دارد", maharatStatus = "دارد",
        bazresiStatus = "دارد", kargarStatus = "فعال می‌باشد.", type = "Premium",
        fromDateToDate = "1404050114040531",
    ),
    trackingCode = "952622593384",
    message = "پرداخت با موفقیت انجام شد.",
)

@PreviewRtlTheme
@Composable
private fun WorkersPaymentSuccessDialogPreviewLight() {
    PreviewRtlThemeContent {
        WorkersPaymentSuccessDialog(receipt = PreviewReceipt, onDismiss = {})
    }
}

@PreviewRtlTheme
@Composable
private fun WorkersPaymentSuccessDialogPreviewDark() {
    PreviewRtlThemeContent(darkTheme = true) {
        WorkersPaymentSuccessDialog(receipt = PreviewReceipt, onDismiss = {})
    }
}
