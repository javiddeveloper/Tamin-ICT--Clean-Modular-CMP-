package com.tamin.taminhamrah.data.remote.models.services.payment

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class PaymentLinkResponse(
    var data: PaymentLink? = null
) : BaseResponseNew()

data class PaymentLink(
    var paymentURL: String? = "",
    var success: Boolean?,
    var errorType: String? = "",
    var errorDesc: String? = ""
)