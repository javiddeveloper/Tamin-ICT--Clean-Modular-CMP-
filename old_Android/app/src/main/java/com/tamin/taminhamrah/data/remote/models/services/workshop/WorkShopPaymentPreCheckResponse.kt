package com.tamin.taminhamrah.data.remote.models.services.workshop

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class WorkShopPaymentPreCheckResponse(
    var data: WorkShopPaymentPreCheck? = null
) : BaseResponseNew()

data class WorkShopPaymentPreCheck(
    var functionResult: String? = null,
    var days: String? = null,
    var queryResult: Int? = 0
)