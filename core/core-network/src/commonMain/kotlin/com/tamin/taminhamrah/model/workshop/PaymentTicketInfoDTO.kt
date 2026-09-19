package com.tamin.taminhamrah.model.workshop

import kotlinx.serialization.Serializable

/**
 * The body of `payment/ticket/current-user/{ticket}`, deliberately without fields.
 *
 * The call is a gate: it binds the ticket to the signed-in user and either succeeds or fails, and
 * nothing it answers with is read. The gateway sends an amount, a status, trace numbers and a
 * redirect address; every one of them is dropped by `ignoreUnknownKeys`. Giving the type properties
 * nobody reads would only invite the next reader to trust values that were never checked.
 */
@Serializable
class PaymentTicketInfoDTO
