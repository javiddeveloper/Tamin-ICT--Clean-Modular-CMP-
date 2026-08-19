package com.tamin.taminhamrah.data.mapper.calculateWagePension

import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopPersonalInfoDTO
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDN
import com.tamin.taminhamrah.model.calculateWagePension.MultipleWorkshopResultDTO

fun MultipleWorkshopPersonalInfoDTO.toDomain(): MultipleWorkshopPersonalInfoDN {
    return MultipleWorkshopPersonalInfoDN(
        branchCode = organizationId.orEmpty(),
        insuranceNumber = insuranceId.orEmpty(),
        branch = branch
    )
}

fun MultipleWorkshopResultDTO.toDomain(): MultipleWorkshopResultDN {
    return MultipleWorkshopResultDN(
        result = result ?: 0
    )
}
