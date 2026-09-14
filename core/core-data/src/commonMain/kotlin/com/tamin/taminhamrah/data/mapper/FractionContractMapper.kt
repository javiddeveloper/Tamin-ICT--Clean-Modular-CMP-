package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDTO
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDTO

fun FractionEligibilityDTO.toDomain(): FractionEligibilityDN =
    FractionEligibilityDN(
        newAge = newAge,
        city = city,
        provinceName = provinceName,
        provinceCode = provinceCode,
        organizationAddress = organizationAddress,
        eligibilityStatus = eligibilityStatus,
        history = history,
        isInsurance = isInsurance ?: false,
        checkFractionMonthStatus = checkFractionMonthStatus,
        insuranceId = contract?.insuranceId,
        branchCode = contract?.branchCode,
        cityCode = contract?.cityCode,
        contractProvinceCode = contract?.provinceCode,
        premiumTypeCode = contract?.premiumTypeCode,
        insuranceTypeCode = contract?.premiumType?.insuranceTypeCode,
        contractNumber = contract?.contractNumber,
    )

fun FractionContractResultDTO.toDomain(): FractionContractResultDN =
    FractionContractResultDN(
        contractNumber = contractNumber,
        contractDate = contractDate,
    )
