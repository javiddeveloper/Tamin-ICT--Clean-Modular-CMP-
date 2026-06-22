package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.model.common.CityDN

fun CityDN.toCityOption(): CityOptionPR = CityOptionPR(
    code = cityCode,
    name = cityName.orEmpty(),
)

fun List<CityDN>.toCityOptions(): List<CityOptionPR> = map { it.toCityOption() }
