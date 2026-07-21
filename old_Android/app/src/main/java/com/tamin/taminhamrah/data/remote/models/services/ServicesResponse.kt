package com.tamin.taminhamrah.data.remote.models.services

import com.google.gson.annotations.SerializedName
import com.tamin.taminhamrah.data.entity.ServiceMainModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew

data class ServicesResponse(
    @SerializedName("menu")
    var eservices: List<ServiceMenu>,
    var list: List<Any>,
    var tracks: List<Any>
):BaseResponseNew(){
    fun getServiceList(): ServiceMenu {
        return eservices[0]
    }
}

data class ServiceMenu(
    var date: String? = "",
    var groups: List<ServiceMainModel>? = null
)


