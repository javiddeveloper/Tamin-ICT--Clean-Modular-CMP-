package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel

class CityNameListResponse : ListDataModel<CityNameModel>()

data class CityNameModel(
    var cityCode: String = "",
    var provincecode: String = "",
    var cityName: String = ""
)
