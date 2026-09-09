package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.payment.PayerType
import com.tamin.taminhamrah.model.payment.PaymentInfoDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDTO
import com.tamin.taminhamrah.model.payment.PaymentLinkDN
import com.tamin.taminhamrah.model.payment.PaymentLinkRequestDTO
import com.tamin.taminhamrah.model.payment.PaymentPreviewDN
import com.tamin.taminhamrah.model.payment.PaymentStatus

/**
 * The ticket the gateway echoes back can be blank on some responses, so the ticket that was asked
 * about is passed in and used as the fallback — a preview with no ticket cannot be cancelled or
 * re-checked, which is how the old client ended up with tickets it could not release.
 */
fun PaymentInfoDTO.toDomain(requestedTicket: String): PaymentPreviewDN = PaymentPreviewDN(
    ticket = ticket?.takeIf { it.isNotBlank() } ?: requestedTicket,
    paymentId = paymentId.orEmpty(),
    amount = paymentAmount ?: 0L,
    settledAmount = affectiveAmount ?: 0L,
    description = paymentDesc.orEmpty(),
    millisToExpire = milliSecondsToExpire ?: 0L,
    status = PaymentStatus.fromCode(paymentStatus),
    referenceNumber = refNum.orEmpty(),
    traceNumber = traceNo.orEmpty(),
    message = transactionResultDesc.orEmpty(),
)

/**
 * A refusal carries its reason in [PaymentLinkDTO.errorDesc]; [PaymentLinkDTO.errorType] is the
 * gateway's internal code and is deliberately not surfaced, since it is not written for a reader.
 */
fun PaymentLinkDTO.toDomain(): PaymentLinkDN = PaymentLinkDN(
    succeeded = success == true,
    paymentUrl = paymentUrl.orEmpty(),
    message = errorDesc.orEmpty(),
)

fun buildPaymentLinkRequest(
    payerType: PayerType,
    payerIdentifier: String,
): PaymentLinkRequestDTO = PaymentLinkRequestDTO(
    enteredNationalCode = payerIdentifier,
    personType = payerType.code.toString(),
)
