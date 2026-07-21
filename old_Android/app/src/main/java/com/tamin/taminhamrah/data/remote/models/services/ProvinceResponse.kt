package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class ProvinceResponse : ListDataModel<ProvinceModel>()
data class ProvinceModel(
    val provinceCode: String? = null,
    val provinceName: String? = null,
    val status: String? = null,
    val statusStartDate: String? = null
)