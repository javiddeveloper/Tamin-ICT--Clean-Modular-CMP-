package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


class AcraConfigResponse(
    var data: AcraConfigModel? = null
) : BaseResponseNew()

data class AcraConfigModel(
    var basicAuthLogin: String = "",
    val basicAuthPassword: String = "",
    val uri: String = "",
    val httpMethod: String = ""
)
