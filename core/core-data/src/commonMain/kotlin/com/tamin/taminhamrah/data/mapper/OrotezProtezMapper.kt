package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDTO
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO

fun RequestInsuredMainInfoDTO.toDomain(): RequestInsuredMainInfoDN {
    return RequestInsuredMainInfoDN(
        risuid = risuid,
        nationalCode = nationalCode,
        firstName = insuranceFirstName,
        lastName = insuranceLastName,
        mobileNumber = mobileNumber,
        genderCode = genderCode,
        branchCode = branchCode,
        branchName = branchName,
        bankAccount = bankAccount,
        bankName = bankName,
        insuranceTypeDesc = insuranceTypeDesc,
        insuranceStatusDesc = insuranceStatusDesc,
        branchWorkshops = branchWorkshop?.map { it.toDomain() } ?: emptyList(),
    )
}

fun BranchWorkshopDTO.toDomain(): BranchWorkshopDN {
    return BranchWorkshopDN(
        branchCode = branchCode,
        branchName = branchName,
        workshopCode = workshopCode,
        workshopName = workshopName,
    )
}
