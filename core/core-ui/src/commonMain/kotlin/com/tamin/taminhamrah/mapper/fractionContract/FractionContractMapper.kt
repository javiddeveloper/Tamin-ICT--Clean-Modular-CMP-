package com.tamin.taminhamrah.mapper.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultPR
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityPR

fun FractionEligibilityDN.toPresentation(): FractionEligibilityPR {
    val resolvedProvince = provinceName.orEmpty()
    val resolvedCity = city.orEmpty()
    val resolvedAddress = organizationAddress.orEmpty().replace("-", ",")
    val branchAddress = buildString {
        if (resolvedProvince.isNotBlank()) append(resolvedProvince)
        if (resolvedProvince.isNotBlank() && resolvedCity.isNotBlank()) append(", ")
        if (resolvedCity.isNotBlank()) append(resolvedCity)
        if (resolvedCity.isNotBlank() && resolvedAddress.isNotBlank()) append(", ")
        if (resolvedAddress.isNotBlank()) append(resolvedAddress)
    }
    return FractionEligibilityPR(
        newAge = newAge.orEmpty(),
        city = resolvedCity,
        provinceName = resolvedProvince,
        provinceCode = provinceCode.orEmpty(),
        organizationAddress = resolvedAddress,
        eligibilityStatus = eligibilityStatus ?: -1,
        history = history ?: 0,
        isInsurance = isInsurance,
        checkFractionMonthStatus = checkFractionMonthStatus.orEmpty(),
        insuranceId = insuranceId.orEmpty(),
        branchCode = branchCode.orEmpty(),
        cityCode = cityCode.orEmpty(),
        contractProvinceCode = contractProvinceCode.orEmpty(),
        premiumTypeCode = premiumTypeCode.orEmpty(),
        insuranceTypeCode = insuranceTypeCode.orEmpty(),
        contractNumber = contractNumber.orEmpty(),
        branchAddress = branchAddress,
    )
}

fun FractionContractResultDN.toPresentation(): FractionContractResultPR =
    FractionContractResultPR(
        contractNumber = contractNumber?.toString().orEmpty(),
        contractDate = contractDate ?: 0L,
    )
