package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDN
import com.tamin.taminhamrah.model.contracts.FreelanceContractResultDTO
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDN
import com.tamin.taminhamrah.model.contracts.FreelanceMakeContractRequestDTO

internal fun FreelanceMakeContractRequestDN.toDto(): FreelanceMakeContractRequestDTO =
    FreelanceMakeContractRequestDTO(
        brchCodeNew = brchCodeNew,
        cityCode = cityCode,
        cntDrmn = cntDrmn,
        cntFreeJobCode = cntFreeJobCode,
        guid = guid,
        guidName = guidName,
        premiumRateCode = premiumRateCode,
        provinceCode = provinceCode,
    )

internal fun FreelanceContractResultDTO.toDomain(): FreelanceContractResultDN =
    FreelanceContractResultDN(
        contractNumber = contractNumber,
        contractDate = contractDate,
    )
