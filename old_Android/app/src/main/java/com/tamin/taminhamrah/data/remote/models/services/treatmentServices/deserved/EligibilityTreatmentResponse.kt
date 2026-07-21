package com.tamin.taminhamrah.data.remote.models.services.treatmentServices.deserved

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class EligibilityTreatmentResponse(val data: EligibilityStatusModel?= null):BaseResponseNew()

data class EligibilityStatusModel(
    val nationalId: String? = null,
    @SerializedName("refrenceCode")
    val referenceCode: String? =null,
    val result: Boolean? = null
)
