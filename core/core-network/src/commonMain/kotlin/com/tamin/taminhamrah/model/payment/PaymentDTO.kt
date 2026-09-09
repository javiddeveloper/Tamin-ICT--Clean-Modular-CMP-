package com.tamin.taminhamrah.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * `GET ticket/current-user/{ticket}` — everything the gateway knows about one ticket.
 *
 * The same shape answers both before payment (amount to pay, time left) and after it (what was
 * taken, reference and trace numbers), which is why one screen can preview a ticket and another
 * can print a receipt from it.
 */
@Serializable
data class PaymentInfoDTO(
    @SerialName("ticket") val ticket: String? = null,
    @SerialName("paymentId") val paymentId: String? = null,
    /** What the ticket asks for. */
    @SerialName("paymentAmount") val paymentAmount: Long? = null,
    /** What was actually taken; zero until the payment goes through. */
    @SerialName("affectiveAmount") val affectiveAmount: Long? = null,
    @SerialName("paymentDesc") val paymentDesc: String? = null,
    @SerialName("milliSecondsToExpire") val milliSecondsToExpire: Long? = null,
    /** `NOT_PAYED` / `VERIFYING` / `SUCCESSFUL` / `EXPIRED` / `FAILED` / `UNKNOWN`. */
    @SerialName("paymentStatus") val paymentStatus: String? = null,
    @SerialName("refNum") val refNum: String? = null,
    @SerialName("traceNo") val traceNo: String? = null,
    @SerialName("transactionResultDesc") val transactionResultDesc: String? = null,
)

/** Body of `POST payment-link/{ticket}`. */
@Serializable
data class PaymentLinkRequestDTO(
    /** The national code / national ID / foreign-national code the payment is filed under. */
    @SerialName("enteredNcodeByUser") val enteredNationalCode: String,
    /** `PayerType.code` as a string; the gateway rejects it as a number. */
    @SerialName("personType") val personType: String,
)

/**
 * Result of `POST payment-link/{ticket}`.
 *
 * [success] false is a business refusal — a payer the gateway will not accept — not a transport
 * failure, and [errorDesc] carries the reason.
 */
@Serializable
data class PaymentLinkDTO(
    @SerialName("paymentURL") val paymentUrl: String? = null,
    @SerialName("success") val success: Boolean? = null,
    @SerialName("errorType") val errorType: String? = null,
    @SerialName("errorDesc") val errorDesc: String? = null,
)
