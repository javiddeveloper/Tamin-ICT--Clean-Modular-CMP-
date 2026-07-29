package com.tamin.taminhamrah.data.remote.models.services.payment

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class PaymentResponse(
    var data: Payment? = null
) : BaseResponseNew()

data class Payment(
    var  paymentTicket: String?,
    var  paymentURL: String?,
    var  responseMessage: String?,
    var  succeed: Boolean?
)

