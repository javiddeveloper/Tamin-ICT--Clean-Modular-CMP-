package com.tamin.taminhamrah.feature.studentInsuranceContract.ui.mapper

import com.tamin.taminhamrah.feature.studentInsuranceContract.ui.model.CityOptionPR
import com.tamin.taminhamrah.model.common.CityDN

fun CityDN.toCityOption(): CityOptionPR = CityOptionPR(
    code = cityCode,
    name = cityName?:"",
    provinceCode = provinceCode,
)

fun List<CityDN>.toCityOptions(): List<CityOptionPR> = map { it.toCityOption() }

fun List<CityOptionPR>.filterByProvinceCode(provinceCode: String): List<CityOptionPR> =
    filter { it.matchesProvinceCode(provinceCode) }

fun CityOptionPR.matchesProvinceCode(selectedProvinceCode: String): Boolean {
    val cityProvinceCode = provinceCode ?: return false
    if (cityProvinceCode == selectedProvinceCode) return true
    return cityProvinceCode.trimStart('0') == selectedProvinceCode.trimStart('0')
}
