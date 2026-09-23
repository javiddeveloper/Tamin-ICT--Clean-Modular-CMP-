package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDTO

fun BeneficiaryConstructionDTO.toDomain(): BeneficiaryConstructionDN {
    return BeneficiaryConstructionDN(
        nationalCode = nationalCode,
        ownerType = ownerType,
        requestNumber = requestNumber,
        fileNumber = fileNumber,
        requestDate = requestDate,
        name = name,
        lastName = lastName,
        mobile = mobile,
    )
}
