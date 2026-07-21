package com.tamin.taminhamrah.data.remote.models.services

import com.tamin.taminhamrah.data.remote.models.ListDataModel


class CityResponse : ListDataModel<CityModel>()

data class CityModel(
    val cityCode: String = "-",
    val cityName: String = "-",
    val provincecode: String = "0"
)