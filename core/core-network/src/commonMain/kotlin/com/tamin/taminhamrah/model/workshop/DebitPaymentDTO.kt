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
 * **No property here carries a default**, which is what keeps all seven keys on the wire:
 * `encodeDefaults` is off for this client, so a property equal to its declared default would be
 * dropped from the body entirely — including [nationalId], which the service expects to be
 * present and `null` rather than missing.
 *
 * The shape was confirmed by the service team against the web client's own request:
 *
 * ```json
 * { "debitNumber": "…", "workshopId": "…", "branchCode": "…",
 *   "peymanSequence": "", "seporde": "0", "nationalId": null, "nationalType": "01" }
 * ```
 *
 * - [deposit] is a string of `"1"` or `"0"`, never `"true"`/`"false"` — the service answers
 *   `ProxyRuntimeException` for the boolean spelling.
 * - [agreementRow] is sent as an empty string, not omitted, when the debt has no agreement row.
 *   The list answers `"peymanSequence": null` for such a debt, and the web client coalesces that
 *   null before building its request, so the key is on the wire there too.
 * - [nationalType] is the workshop's `character.characterCode` — `"01"` حقیقی, `"02"` حقوقی.
 * - [nationalId] is the حقوقی workshop's own national id, and `null` for a حقیقی one. Both
 *   fields were added when tokenized payment arrived; a request without them is refused.
 *
 * Not covered here: branches migrating to the centralised gateway take a different route
 * entirely (the web client redirects to `pay-neo-workshop`), and a debt with
 * `debitCreateReasonCode == "60"` and `debitStepCode` `"05"`/`"19"` goes to the installment
 * flow instead of this endpoint.
 */
@Serializable
data class DebitPaymentRequestDTO(
    @SerialName("branchCode") val branchCode: String,
    @SerialName("workshopId") val workshopId: String,
    @SerialName("debitNumber") val debitNumber: String,
    @SerialName("peymanSequence") val agreementRow: String,
    @SerialName("seporde") val deposit: String,
    /** Nullable, but **never** give it a default — `= null` would drop the key from the body. */
    @SerialName("nationalId") val nationalId: String?,
    @SerialName("nationalType") val nationalType: String,
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
