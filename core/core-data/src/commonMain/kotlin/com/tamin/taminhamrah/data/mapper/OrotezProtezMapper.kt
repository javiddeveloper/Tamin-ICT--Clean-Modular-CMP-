package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDN
import com.tamin.taminhamrah.model.orotezProtez.BranchWorkshopDTO
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDTO
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDTO
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDN
import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDTO
import com.tamin.taminhamrah.model.orotezProtez.ShortTermOrthosisRequestDTO
import com.tamin.taminhamrah.model.orotezProtez.ShortTermOrthosisRequestFileDN
import com.tamin.taminhamrah.model.orotezProtez.ShortTermOrthosisRequestFileDTO
import com.tamin.taminhamrah.model.orotezProtez.ShortTermOrthosisRequestIdDTO

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

fun InsuredPersonDTO.toDomain(): InsuredPersonDN {
    return InsuredPersonDN(
        insuredId = risuId,
        firstName = firstName,
        lastName = lastName,
        nationalCode = nationCode,
        birthCertificateNumber = birthCertificateNumber,
        cityName = cityName,
        birthDate = birthDate,
        relationship = relationship,
        relationshipCode = relationshipCode,
        bookletValidUntil = bookletValidUntil,
    )
}

/** "04" = short-term-orthosis/prosthesis help type, fixed for this endpoint (01 = sick-leave pay, 02 = maternity). */
private const val SHORT_TERM_ORTHOSIS_HELP_TYPE = "04"

fun SaveShortTermOrthosisRequestDN.toDTO(): SaveShortTermOrthosisRequestDTO {
    return SaveShortTermOrthosisRequestDTO(
        shorttermRequest = ShortTermOrthosisRequestDTO(
            branchCode = branchCode,
            branchName = branchName,
            insuranceFirstName = insuranceFirstName,
            insuranceLastName = insuranceLastName,
            mobileNumber = mobileNumber,
            nationalCode = nationalCode,
            // Always an "empty" reference — the client never fills in an existing request id here.
            request = ShortTermOrthosisRequestIdDTO(id = null),
            requestFileList = requestFileList.map { it.toDTO() },
            requestHelpType = SHORT_TERM_ORTHOSIS_HELP_TYPE,
            risuid = risuid,
            // Always 0 for this endpoint — not the prescription date (that's useTajTimeStamp below).
            serviceDateTimeStamp = 0L,
        ),
        userNationalCode = userNationalCode,
        userRelation = userRelation,
        userRelationship = userRelationship,
        userFirstName = userFirstName,
        userInsuredId = userInsuredId,
        userLastName = userLastName,
        prescriptionDateTimeStamp = prescriptionDateTimeStamp,
    )
}

fun ShortTermOrthosisRequestFileDN.toDTO(): ShortTermOrthosisRequestFileDTO {
    return ShortTermOrthosisRequestFileDTO(
        documentFile = documentFile,
        documentType = documentType,
    )
}
