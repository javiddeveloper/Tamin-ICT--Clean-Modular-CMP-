package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.weddingPresent.MarriageGiftRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentRequestDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN

fun WeddingPresentInfoDTO.toDomain(): WeddingPresentInfoDN =
    WeddingPresentInfoDN(
        risuid = risuid,
        nationalCode = nationalCode,
        insuranceFirstName = insuranceFirstName,
        insuranceLastName = insuranceLastName,
        mobileNumber = mobileNumber,
        insuranceTypeDesc = insuranceTypeDesc,
        insuranceStatusDesc = insuranceStatusDesc,
        bankAccount = bankAccount,
        bankName = bankName,
        branchCode = branchCode,
        branchName = branchName,
        requestHelpType = requestHelpType,
        serviceDateTimeStamp = serviceDateTimeStamp,
        request = request?.toDomain(),
    )

fun WeddingPresentRequestDTO.toDomain(): WeddingPresentRequestDN =
    WeddingPresentRequestDN(
        id = id,
        systemType = systemType,
        requestDate = requestDate,
        userId = userId,
        status = status,
        requestType = requestType,
        editDate = editDate,
        editUser = editUser,
        referenceCode = referenceCode,
        branchCode = branchCode,
        statusName = statusName,
        statusId = statusId,
    )

fun WeddingPresentSubmitRequestDN.toDTO(): ShortTermMarriageRequestDTO =
    ShortTermMarriageRequestDTO(
        partnerNationalId = partnerNationalId,
        weddingDateTimeStamp = weddingDateTimeStamp,
        shortTermRequest = info.toMarriageGiftRequestDTO(),
    )

fun WeddingPresentInfoDN.toMarriageGiftRequestDTO(): MarriageGiftRequestDTO =
    MarriageGiftRequestDTO(
        request = request?.toDTO(),
        requestFileList = null,
        risuid = risuid,
        insuranceFirstName = insuranceFirstName,
        insuranceLastName = insuranceLastName,
        nationalCode = nationalCode,
        requestHelpType = requestHelpType,
        mobileNumber = mobileNumber,
        serviceDateTimeStamp = serviceDateTimeStamp,
        branchCode = branchCode,
        branchName = branchName,
    )

fun WeddingPresentRequestDN.toDTO(): WeddingPresentRequestDTO =
    WeddingPresentRequestDTO(
        id = id,
        systemType = systemType,
        requestDate = requestDate,
        userId = userId,
        status = status,
        requestType = requestType,
        editDate = editDate,
        editUser = editUser,
        referenceCode = referenceCode,
        branchCode = branchCode,
        statusName = statusName,
        statusId = statusId,
    )
