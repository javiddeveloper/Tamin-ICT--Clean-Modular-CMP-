package com.tamin.taminhamrah.mapper.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoPR

fun WeddingPresentInfoDN.toPresentation(): WeddingPresentInfoPR {
    val resolvedFullName = listOfNotNull(insuranceFirstName, insuranceLastName)
        .joinToString(" ")
        .ifBlank { "" }
    return WeddingPresentInfoPR(
        risuid = risuid.orEmpty(),
        nationalCode = nationalCode.orEmpty(),
        insuranceFirstName = insuranceFirstName.orEmpty(),
        insuranceLastName = insuranceLastName.orEmpty(),
        fullName = resolvedFullName,
        mobileNumber = mobileNumber.orEmpty(),
        insuranceTypeDesc = insuranceTypeDesc.orEmpty(),
        insuranceStatusDesc = insuranceStatusDesc.orEmpty(),
        bankAccount = bankAccount.orEmpty(),
        bankName = bankName.orEmpty(),
        branchCode = branchCode.orEmpty(),
        branchName = branchName.orEmpty(),
        requestHelpType = requestHelpType.orEmpty(),
        serviceDateTimeStamp = serviceDateTimeStamp.orEmpty(),
    )
}
