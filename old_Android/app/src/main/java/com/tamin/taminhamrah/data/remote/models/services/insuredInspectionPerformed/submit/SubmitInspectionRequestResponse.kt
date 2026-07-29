package com.tamin.taminhamrah.data.remote.models.services.insuredInspectionPerformed.submit
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class SubmitInspectionRequestResponse(
    var data: SubmitInspectionRequestModel? = null
) : BaseResponseNew()

data class SubmitInspectionRequestModel(
val request: RequestSubmitInspection? = null,
)

data class RequestSubmitInspection(
    val id: Long? = null
)