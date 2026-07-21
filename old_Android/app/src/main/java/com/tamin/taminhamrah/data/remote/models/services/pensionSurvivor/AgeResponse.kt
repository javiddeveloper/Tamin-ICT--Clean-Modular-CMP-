package com.tamin.taminhamrah.data.remote.models.services.pensionSurvivor

import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class AgeResponse(var data: AgeModel = AgeModel()) : BaseResponseNew()

data class AgeModel(
    val age: String? = null,
    val birthDate: Long? = null
):BaseResponseNew()