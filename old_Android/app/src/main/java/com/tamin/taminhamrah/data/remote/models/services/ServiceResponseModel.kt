package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.entity.ServiceMainModel
import com.tamin.taminhamrah.data.remote.models.BaseResponseNew


class ServiceResponseModel(
    var data: ServiceData? = null,
) : BaseResponseNew()

class ServiceData(var menu: MutableList<ServiceMenuModel>?=null,
                  var tracks: List<Any>? = null)

class ServiceMenuModel(
    var groups: MutableList<ServiceMainModel>? = null
)
