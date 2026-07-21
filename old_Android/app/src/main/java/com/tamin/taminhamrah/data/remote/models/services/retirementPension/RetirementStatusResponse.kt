package com.tamin.taminhamrah.data.remote.models.services.retirementPension
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class RetirementStatusResponse(val data : RetirementStatusModel? = null) : BaseResponseNew()

data class RetirementStatusModel(
    val requestId: String? = null,
    val requestStatusCode: String? = null
)