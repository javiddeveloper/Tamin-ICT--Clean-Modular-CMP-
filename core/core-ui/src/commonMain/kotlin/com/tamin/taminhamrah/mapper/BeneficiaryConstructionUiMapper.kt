package com.tamin.taminhamrah.mapper

import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionDN
import com.tamin.taminhamrah.model.constructionInsurance.BeneficiaryConstructionPR

fun BeneficiaryConstructionDN.toPR(): BeneficiaryConstructionPR {
    return BeneficiaryConstructionPR(
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
