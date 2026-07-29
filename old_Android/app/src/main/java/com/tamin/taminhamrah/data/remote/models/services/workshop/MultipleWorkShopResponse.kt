package com.tamin.taminhamrah.data.remote.models.services.workshop
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

class MultipleWorkShopResponse(
    val data: MultipleWorkShopModel?=null
) : BaseResponseNew()

data class MultipleWorkShopModel (
    val result : Int?=null
)