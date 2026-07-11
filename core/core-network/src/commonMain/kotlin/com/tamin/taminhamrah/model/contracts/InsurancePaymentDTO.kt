package com.tamin.taminhamrah.model.contracts

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsurancePaymentDTO(
    @SerialName("paymentTicket") val paymentTicket: String?,
    @SerialName("paymentURL") val paymentUrl: String?,
    @SerialName("responseMessage") val responseMessage: String?,
    @SerialName("succeed") val succeed: Boolean?,
)
