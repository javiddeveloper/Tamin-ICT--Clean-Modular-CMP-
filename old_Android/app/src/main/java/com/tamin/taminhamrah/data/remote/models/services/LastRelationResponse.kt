package com.tamin.taminhamrah.data.remote.models.services
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class LastRelationResponse(
    val data: LastRelationModel?=null
) : BaseResponseNew()

data class LastRelationModel(
    val organizationId: String
)