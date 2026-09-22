package com.tamin.taminhamrah.model.constructionInsurance

/**
 * The paid/unpaid state of a [PaymentSheetConstructionFileDN.status]. Legacy compares the server's
 * status as a bare code — `status == "1"` → paid, anything else → unpaid — a strict binary. Which
 * shape the live `getPaymentSheetConstructionInfo` service actually sends (a code or free text)
 * couldn't be confirmed from client code alone (MR !244 review item 9), so this also accepts the
 * literal legacy text as paid; every other non-null value is unpaid.
 *
 * [from] takes that legacy text as a parameter rather than hardcoding it here: this module has no
 * access to the `payment_sheet_status_paid` Compose string resource (`core-ui` depends on
 * `core-domain`, never the reverse), so the caller resolves it from `strings.xml` and passes it in.
 */
enum class PaymentSheetStatusDN {
    PAID,
    UNPAID,
    UNKNOWN;

    companion object {
        private const val PAID_CODE = "1"

        fun from(status: String?, paidText: String): PaymentSheetStatusDN = when (status) {
            null -> UNKNOWN
            PAID_CODE, paidText -> PAID
            else -> UNPAID
        }
    }
}
