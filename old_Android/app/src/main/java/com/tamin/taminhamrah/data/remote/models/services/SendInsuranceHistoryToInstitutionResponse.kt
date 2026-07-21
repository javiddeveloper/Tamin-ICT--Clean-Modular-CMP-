package com.tamin.taminhamrah.data.remote.models.services
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class SendInsuranceHistoryToInstitutionResponse(
    var data : SendInsuranceHistoryToInstitutionModel?= null
): BaseResponseNew()


data class SendInsuranceHistoryToInstitutionModel(
    val pdf :String? = null,
    val text: String? = null)
