package com.tamin.taminhamrah.data.remote.models.services.retirementPension
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class RetirementConfirmIdentityInfoResponse(
    var data : RetirementConfirmIdentityInfoModel?=null
):BaseResponseNew()

data class RetirementConfirmIdentityInfoModel(
    var request :RequestData?= null
)

data class RequestData(
    var id:Long?=0L
)
