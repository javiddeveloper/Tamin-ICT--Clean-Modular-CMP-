package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.ConstructionBeneficiaryPageEntity
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

// ---- Offline page cache (construction_beneficiary_pages) ----

internal fun BeneficiaryConstructionDN.toPageEntity(listKey: String, position: Int) = ConstructionBeneficiaryPageEntity(
    listKey = listKey,
    position = position,
    nationalCode = nationalCode,
    ownerType = ownerType,
    requestNumber = requestNumber,
    fileNumber = fileNumber,
    requestDate = requestDate,
    name = name,
    lastName = lastName,
    mobile = mobile,
)

internal fun ConstructionBeneficiaryPageEntity.toDomain() = BeneficiaryConstructionDN(
    nationalCode = nationalCode,
    ownerType = ownerType,
    requestNumber = requestNumber,
    fileNumber = fileNumber,
    requestDate = requestDate,
    name = name,
    lastName = lastName,
    mobile = mobile,
)
