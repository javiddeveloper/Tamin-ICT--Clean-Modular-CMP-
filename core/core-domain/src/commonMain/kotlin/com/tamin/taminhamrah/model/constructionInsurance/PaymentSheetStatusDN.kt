package com.tamin.taminhamrah.model.constructionInsurance

/**
 * The paid/unpaid state of a [PaymentSheetConstructionFileDN.status]. Legacy compares the server's
 * status as a bare code — `status == "1"` → paid, anything else → unpaid — a strict binary. Which
 * shape the live `getPaymentSheetConstructionInfo` service actually sends (a code or free text)
 * couldn't be confirmed from client code alone (MR !244 review item 9), so this also accepts the
 * literal legacy text as paid; every other non-null value is unpaid.
 */
enum class PaymentSheetStatusDN {
    PAID,
    UNPAID,
    UNKNOWN;

    companion object {
        private const val PAID_CODE = "1"
        private const val PAID_TEXT = "پرداخت شده"

        fun from(status: String?): PaymentSheetStatusDN = when (status) {
            null -> UNKNOWN
            PAID_CODE, PAID_TEXT -> PAID
            else -> UNPAID
        }
    }
}
