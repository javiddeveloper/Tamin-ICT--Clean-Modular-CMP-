package com.tamin.taminhamrah.model.workshop

import androidx.compose.runtime.Immutable

/** One برگ پرداخت row, formatted for display. */
@Immutable
data class PaymentSheetPR(
    val debitNumber: String = "",
    val agreementRow: String = "",
    val amount: String = "",
    val collectDate: String = "",
    val issueDate: String = "",
    val status: PaymentSheetStatus = PaymentSheetStatus.UNKNOWN,
    val statusLabel: String = "",
    val debitReason: String = "",
    val payKind: String = "",
    val documentNumber: String = "",
)

/** One entry of the علت ایجاد بدهی picker. [code] is what the search sends. */
@Immutable
data class DebitReasonPR(
    val code: String = "",
    val title: String = "",
)
