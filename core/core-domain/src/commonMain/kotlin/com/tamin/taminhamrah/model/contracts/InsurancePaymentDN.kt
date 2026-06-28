package com.tamin.taminhamrah.model.contracts

data class InsurancePaymentParamsDN(
    val systemType: String,
    val redirectUrl: String,
    val startDate: Long,
    val endDate: Long,
    val amount: Long,
    val redirectUri: String,
    val paramPage: String,
    val month: Int,
)

data class InsurancePaymentDN(
    val paymentTicket: String?,
    val paymentUrl: String?,
    val responseMessage: String?,
    val succeed: Boolean?,
)
