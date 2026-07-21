package com.tamin.taminhamrah.data.remote.models.services.requestPaymentForillDay

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class RequestPaymentForIllDayResponse (var data : RequestPaymentForIllDayModel? = null):BaseResponseNew()

data class RequestPaymentForIllDayModel(
    val shorttermRequest: ShorttermRequestResponse? = null,
)

data class ShorttermRequestResponse(
    val resultMessage: String? = null
)