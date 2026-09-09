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

/** Body of `POST debit-online-payment/pay-normal-debit`. */
@Serializable
data class DebitPaymentRequestDTO(
    @SerialName("branchCode") val branchCode: String,
    @SerialName("workshopId") val workshopId: String,
    @SerialName("debitNumber") val debitNumber: String,
    @SerialName("peymanSequence") val agreementRow: String,
    /** Whether the payment is filed as a bank deposit. The service takes the flag as a string. */
    @SerialName("seporde") val deposit: String = "false",
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
