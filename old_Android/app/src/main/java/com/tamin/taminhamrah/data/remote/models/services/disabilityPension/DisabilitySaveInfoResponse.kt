package com.tamin.taminhamrah.data.remote.models.services.disabilityPension

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class DisabilitySaveInfoResponse(val data: DisabilitySaveInfoModel = DisabilitySaveInfoModel() ):BaseResponseNew()

data class DisabilitySaveInfoModel(
    val request: Request? = Request(),
)

data class Request(
    val id: Long = 0,
    val refCode: String = ""
)