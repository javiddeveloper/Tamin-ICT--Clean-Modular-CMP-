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
 * Two fields are spelled the way the service expects rather than the way Kotlin would default to,
 * and both were answered with `ProxyRuntimeException` when they were not:
 *
 * - [deposit] is a string of `"1"` or `"0"`, never `"true"`/`"false"`. It carries no default, so it
 *   is always written — `encodeDefaults` is off for this client, and a value equal to its default
 *   would be dropped from the body entirely.
 * - [agreementRow] is absent rather than blank when the debt has none. The list answers
 *   `"peymanSequence": null` for such a debt. When an empty string `""` is sent, Oracle OSB fails
 *   with `ProxyRuntimeException` during type conversion/validation. Null here with default `null`
 *   ensures the field is omitted from the JSON body when absent.
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
