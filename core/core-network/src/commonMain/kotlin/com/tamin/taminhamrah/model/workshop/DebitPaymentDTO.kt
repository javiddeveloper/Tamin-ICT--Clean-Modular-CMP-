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
 * Every field is declared without a default, so every field is written. That is deliberate: this
 * client serializes with `encodeDefaults` off, which silently drops any value equal to its declared
 * default — a default on [deposit] once removed `seporde` from the body altogether, and the service
 * answered `ProxyRuntimeException`.
 *
 * The shape matches the old client exactly, which sends all five keys on every request:
 *
 * - [deposit] is `"1"` or `"0"`, never `"true"`/`"false"`.
 * - [agreementRow] is present but **empty** for a debt that has none. The list answers
 *   `"peymanSequence": null` for such a debt and the old client coalesces that to `""` before the
 *   request is built, so the key is always on the wire. Omitting it is not the same thing, and is
 *   refused.
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
