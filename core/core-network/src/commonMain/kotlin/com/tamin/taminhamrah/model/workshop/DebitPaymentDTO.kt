package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `debit-online-payment/debit-select-pre-check/{debitNumber}/{branchCode}/1`.
 *
 * `functionResult == "1"` is the go-ahead; anything else means the debt cannot be paid online.
 */
@Serializable
data class DebitPaymentPreCheckDTO(
    @SerialName("functionResult") val functionResult: String? = null,
    @SerialName("days") val days: String? = null,
    @SerialName("queryResult") val queryResult: Int? = null,
)

/**
 * Body of `POST debit-online-payment/pay-normal-debit`.
 *
 * Every field is non-null and carries no default, so all five keys are always written:
 * `encodeDefaults` is off for this client, and a property equal to its declared default would be
 * dropped from the body entirely.
 *
 * - [deposit] is a string of `"1"` or `"0"`, never `"true"`/`"false"` — the service answers
 *   `ProxyRuntimeException` for the boolean spelling.
 * - [agreementRow] is sent as an empty string, not omitted, when the debt has no agreement row.
 *   The list answers `"peymanSequence": null` for such a debt, and the old client coalesces that
 *   null before building its request (`item.peymanSequence ?: ""`), so the key is on the wire
 *   there too.
 *
 * Serialized through this client's `Json`, the body matches the old client's byte for byte apart
 * from whitespace. A `ProxyRuntimeException` still seen on some debts is therefore not explained
 * by this shape — do not "fix" it by omitting a key without a capture of both clients paying the
 * same debt.
 */
@Serializable
data class DebitPaymentRequestDTO(
    @SerialName("branchCode") val branchCode: String,
    @SerialName("workshopId") val workshopId: String,
    @SerialName("debitNumber") val debitNumber: String,
    @SerialName("peymanSequence") val agreementRow: String,
    @SerialName("seporde") val deposit: String,
)

/**
 * Result of `pay-normal-debit`. [succeed] false is a business rejection, not a transport failure,
 * and [responseMessage] carries the reason — the old app dropped it when it was blank, which is
 * the one path that left the user with no feedback at all.
 */
@Serializable
data class DebitPaymentDTO(
    @SerialName("paymentTicket") val paymentTicket: String? = null,
    @SerialName("paymentURL") val paymentUrl: String? = null,
    @SerialName("responseMessage") val responseMessage: String? = null,
    @SerialName("succeed") val succeed: Boolean? = null,
)
