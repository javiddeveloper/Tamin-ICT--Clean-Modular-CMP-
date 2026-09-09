package com.tamin.taminhamrah.mapper.common

import com.tamin.taminhamrah.model.common.CityDN
import com.tamin.taminhamrah.model.common.CityPR
import com.tamin.taminhamrah.model.common.InsuranceTypeDN
import com.tamin.taminhamrah.model.common.InsuranceTypePR
import com.tamin.taminhamrah.model.common.ProvinceDN
import com.tamin.taminhamrah.model.common.ProvincePR

fun CityDN.toPresentation(): CityPR = CityPR(
    cityCode = cityCode,
    cityName = cityName.orEmpty(),
    provinceCode = provinceCode,
)

fun List<CityDN>.toCityPresentation(): List<CityPR> = map { it.toPresentation() }

fun List<CityPR>.filterByProvinceCode(provinceCode: String): List<CityPR> =
    filter { it.matchesProvinceCode(provinceCode) }

fun CityPR.matchesProvinceCode(selectedProvinceCode: String): Boolean {
    val cityProvinceCode = provinceCode ?: return false
    if (cityProvinceCode == selectedProvinceCode) return true
    return cityProvinceCode.trimStart('0') == selectedProvinceCode.trimStart('0')
}

fun ProvinceDN.toPresentation(): ProvincePR = ProvincePR(
    provinceCode = provinceCode,
    provinceName = provinceName.orEmpty(),
)

fun List<ProvinceDN>.toProvincePresentation(): List<ProvincePR> = map { it.toPresentation() }

fun InsuranceTypeDN.toPresentation(): InsuranceTypePR = InsuranceTypePR(
    insuranceTypeCode = insuranceTypeCode.orEmpty(),
    insuranceTypeDesc = insuranceTypeDesc.orEmpty(),
)

fun List<InsuranceTypeDN>.toInsuranceTypePresentation(): List<InsuranceTypePR> = map { it.toPresentation() }
