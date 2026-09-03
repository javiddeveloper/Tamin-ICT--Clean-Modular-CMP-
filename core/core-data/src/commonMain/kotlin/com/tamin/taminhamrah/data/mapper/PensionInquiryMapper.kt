package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.pension.*
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDN
import com.tamin.taminhamrah.model.pension.authenticationTicket.AuthenticationTicketDTO
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.*
import com.tamin.taminhamrah.model.pension.disabilityRequest.*
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.CommitteeDemandInfoDocumentDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.CommitteeDemandInfoDocumentDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.CommitteeRequestInfoDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.CommitteeRequestInfoDTO
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDN
import com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission.RegisteredMedicalCommissionDTO
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.*
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO
import com.tamin.taminhamrah.model.pension.retirement.*
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementDocumentDTO
import com.tamin.taminhamrah.model.pension.sendRetirementDocument.RetirementSaveDocumentRequest

fun PensionInquiryDTO.toDomain(): PensionInquiryDN {
    return PensionInquiryDN(
        branchCode = branchCode,
        insuranceNumber = insuranceNumber,
        pensionerRisUid = pensionerRisUid,
        pensionerType = pensionerType,
        paymentDate = paymentDate,
        pensionerBaseDate = pensionerBaseDate,
        fullName = fullName,
        statusDesc = statusDesc,
        sexDesc = sexDesc,
        branchName = branchName,
        pensionEndDate = pensionEndDate,
        nationalId = nationalId,
        paymentAmount = paymentAmount,
        pensionerId = pensionerId,
        pensionerTypeDesc = pensionerTypeDesc,
    )
}

fun AuthenticationTicketDTO.toDomain(): AuthenticationTicketDN {
    return AuthenticationTicketDN(
        mobileNumber = mobileNumber
    )
}

fun PensionIdDTO.toDomain(): PensionIdDN {
    return PensionIdDN(
        pensionerId = pensionerId
    )
}

fun EdictPensionerDTO.toDomain(): EdictPensionerDN {
    return EdictPensionerDN(
        lastName = lastName,
        branchName = branchName,
        insuranceId = insuranceId,
        title = title,
        edictYear = edictYear,
        edictMonth = edictMonth,
        edictInfo = edictInfo?.toDomain(),
        survivorInfo = survivorInfo?.map { it.toDomain() },
        detail = detail?.map { it.toDomain() }
    )
}

fun EdictInfoDTO.toDomain(): EdictInfoDN {
    return EdictInfoDN(
        pensionerId = pensionerId,
        nationalCode = nationalCode,
        firstName = firstName,
        lastName = lastName,
        fatherName = fatherName,
        birthDate = birthDate,
        idNumber = idNumber,
        gender = gender,
        insuranceType = insuranceType,
        pensionStartDate = pensionStartDate,
        originalHistoryYear = originalHistoryYear,
        originalHistoryMonth = originalHistoryMonth,
        originalHistoryDay = originalHistoryDay,
        additionalYear = additionalYear,
        additionalMonth = additionalMonth,
        additionalDay = additionalDay,
        basisImplementation = basisImplementation,
        pensionBeforeIncrease = pensionBeforeIncrease,
        pensionAfterIncrease = pensionAfterIncrease,
        edictDescription = edictDescription,
        id = id,
        firstStageTotalPensionAndProportional = firstStageTotalPensionAndProportional,
        totalPensionBeforeIncrease = totalPensionBeforeIncrease,
        firstStageTotalProportional = firstStageTotalProportional,
        totalAmount = totalAmount,
        payableMonthly = payableMonthly,
        lettersPayableMonthly = lettersPayableMonthly
    )
}

fun SurvivorInfoDTO.toDomain(): SurvivorInfoDN {
    return SurvivorInfoDN(
        pensionAfterIncrease = pensionAfterIncrease,
        previousPension = previousPension,
        originalHistoryDay = originalHistoryDay,
        originalHistoryMonth = originalHistoryMonth,
        originalHistoryYear = originalHistoryYear,
        leniencyYear = leniencyYear,
        leniencyMonth = leniencyMonth,
        leniencyDay = leniencyDay,
        edictDescription = edictDescription,
        id = id,
        insuranceType = insuranceType,
        lastName = lastName,
        firstName = firstName,
        firstStageTotalPensionAndProportional = firstStageTotalPensionAndProportional,
        firstStageTotalProportional = firstStageTotalProportional,
        differenceProportionalityBasedHistory = differenceProportionalityBasedHistory,
        nationalCode = nationalCode,
        pensionerId = pensionerId,
        quota = quota ?: "",
        totalAmount = totalAmount
    )
}

fun EdictPensionerDetailDTO.toDomain(): EdictPensionerDetailDN {
    return EdictPensionerDetailDN(
        fieldDesc = fieldDesc,
        fieldValue = fieldValue,
        index = index,
        packageName = packageName
    )
}

fun DeferredInstallmentRequestDN.toDTO(): DeferredInstallmentRequest {
    return DeferredInstallmentRequest(
        bankDTO = bank?.toDTO(),
        bankBranch = bankBranch,
        garanteeType = garanteeType,
        guaranteeAmount = guaranteeAmount,
        installmentAmount = installmentAmount,
        installmentCount = installmentCount,
        loanAmount = loanAmount,
        pensionerId = pensionerId,
        birthDate = birthDate,
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId
    )
}

fun BankDN.toDTO(): BankDTO {
    return BankDTO(
        bankCode = bankCode
    )
}

fun DeferredInstallmentCertificateDTO.toDomain(): DeferredInstallmentCertificateDN {
    return DeferredInstallmentCertificateDN(
        request = request?.toDomain()
    )
}

fun RequestCertificateDTO.toDomain(): RequestCertificateDN {
    return RequestCertificateDN(
        refCode = refCode
    )
}

fun RetirementRequestDTO.toDomain(): RetirementRequestDN {
    return RetirementRequestDN(
        activityType = activityType,
        address = address,
        age = age,
        birthDate = birthDate,
        branchCode = branchCode,
        fatherName = fatherName,
        firstName = firstName,
        gender = gender,
        insuranceNumber = insuranceNumber,
        issuePlace = issuePlace,
        idNumber = idNumber,
        lastName = lastName,
        mobileNumber = mobileNumber,
        nationalCode = nationalCode,
        phoneNumber = phoneNumber,
        workshopAddress = workshopAddress,
        workshopCode = workshopCode,
        workshopName = workshopName,
        managerName = managerName
    )
}

fun PayRollDTO.toDomain(): PayRollDN {
    return PayRollDN(
        id = id,
        clpType = clpType,
        tprDesc = tprDesc,
        sumAmount = sumAmount,
        textNumber = textNumber,
        sumPay = sumPay,
        hisYear = hisYear,
        hisMon = hisMon,
        hisDay = hisDay,
        hisYearPlus = hisYearPlus,
        hisMonPlus = hisMonPlus,
        hisDayPlus = hisDayPlus
    )
}

fun RetirementStatusDTO.toDomain(): RetirementStatusDN {
    return RetirementStatusDN(
        requestId = requestId,
        requestStatusCode = requestStatusCode
    )
}

fun RetirementSaveDocumentDN.toDTO(): RetirementSaveDocumentRequest {
    return RetirementSaveDocumentRequest(
        pensionRequestDocList = pensionRequestDocList?.map { it.toDTO() },
        status = status
    )
}

fun RetirementDocumentDN.toDTO(): RetirementDocumentDTO {
    return RetirementDocumentDTO(
        documentType = documentType,
        guid = guid
    )
}

fun String?.toInquirePensionCertificateDomain(): InquirePensionCertificateDN {
    return InquirePensionCertificateDN(
        message = this
    )
}

fun DisabilitySaveInfoDN.toDTO(): DisabilitySaveInfoRequest {
    return DisabilitySaveInfoRequest(
        activityType = activityType,
        address = address,
        age = age,
        birthDate = birthDate,
        branchCode = branchCode,
        fatherName = fatherName,
        firstName = firstName,
        gender = gender,
        idNumber = idNumber,
        insuranceNumber = insuranceNumber,
        issuePlace = issuePlace,
        lastName = lastName,
        managerName = managerName,
        mobileNumber = mobileNumber,
        nationalCode = nationalCode,
        pensionRequestDocList = pensionRequestDocList?.map { it.toDTO() },
        phoneNumber = phoneNumber,
        status = status,
        workshopAddress = workshopAddress,
        workshopCode = workshopCode,
        workshopName = workshopName,
    )
}

fun DisabilityDocumentDN.toDTO(): DisabilityDocumentDTO {
    return DisabilityDocumentDTO(
        documentType = documentType,
        guid = guid
    )
}

fun DisabilitySaveInfoResponseDTO.toDomain(): DisabilityRequestRefDN? {
    return request?.toDomain()
}

fun DisabilityRequestRefDTO.toDomain(): DisabilityRequestRefDN {
    return DisabilityRequestRefDN(
        id = id,
        refCode = refCode
    )
}

fun DisabilityFinalConfirmDN.toDTO(): DisabilityFinalConfirmRequest {
    return DisabilityFinalConfirmRequest(
        id = id,
        status = status
    )
}

fun DisabilitySaveDocumentDN.toDTO(): DisabilitySaveDocumentRequest {
    return DisabilitySaveDocumentRequest(
        pensionRequestDocList = pensionRequestDocList?.map { it.toDTO() },
        status = status
    )
}

fun RegisteredMedicalCommissionDTO.toDomain(): RegisteredMedicalCommissionDN {
    return RegisteredMedicalCommissionDN(
        demandInfoId = demandInfoId,
        demandTypeCode = demandTypeCode,
        demandSaveDate = demandSaveDate,
        commFirstName = commFirstName,
        commLastName = commLastName,
        commFatherName = commFatherName,
        commBirthDate = commBirthDate,
        commGender = commGender,
        commMarriageStatus = commMarriageStatus,
        commIdNumber = commIdNumber,
        commExpCityCode = commExpCityCode,
        commRelationTypeCode = commRelationTypeCode,
        commNationalCode = commNationalCode,
        guardianNationalCode = guardianNationalCode,
        commInsuredTypeCode = commInsuredTypeCode,
        commResidenceCityCode = commResidenceCityCode,
        commAddress = commAddress,
        commMobileNumber = commMobileNumber,
        commTelephoneNumber = commTelephoneNumber,
        commNationality = commNationality,
        deadDate = deadDate,
        insuranceNumber = insuranceNumber,
        pensionerCode = pensionerCode,
        isuTypeCode = isuTypeCode,
        nationalCode = nationalCode,
        referBadviCode = referBadviCode,
        status = status,
        referTypeCode = referTypeCode,
        lastJobDesc = lastJobDesc,
        lastJobCode = lastJobCode,
        jobHistoryDesc = jobHistoryDesc,
        hasDrivingCertificate = hasDrivingCertificate,
        hasVisitBeforeJob = hasVisitBeforeJob,
        hasVisitInJob = hasVisitInJob,
        hasHealthyCertificate = hasHealthyCertificate,
        hasContract = hasContract,
        hasExpertJob = hasExpertJob,
        historyConfirm = historyConfirm,
        militaryStatusCode = militaryStatusCode,
        commissionInResidenceCity = commissionInResidenceCity,
        dependencyTypeCode = dependencyTypeCode,
        branchCode = branchCode,
        divan = divan,
        isConfirmed = isConfirmed,
        refId = refId,
        commPostalCode = commPostalCode,
        commCaseTypeCode = commCaseTypeCode,
        isuStatusTypeCode = isuStatusTypeCode,
        demandStage = demandStage,
        sendCentralCommittee = sendCentralCommittee,
        commissionCentralId = commissionCentralId,
        commissionPollDesc = commissionPollDesc,
        committeeRequestInfoList = committeeRequestInfoList?.map { it.toDomain() },
        committeeDemandInfoDocumentList = committeeDemandInfoDocumentList?.map { it.toDomain() },
    )
}

fun CommitteeRequestInfoDTO.toDomain(): CommitteeRequestInfoDN {
    return CommitteeRequestInfoDN(
        id = id,
        demandInfoId = demandInfoId,
        lastJobDesc = lastJobDesc,
        lastJobCode = lastJobCode,
        jobHistoryDesc = jobHistoryDesc,
        hasDrivingCertificate = hasDrivingCertificate,
        hasVisitBeforeJob = hasVisitBeforeJob,
        hasVisitInJob = hasVisitInJob,
        hasHealthyCertificate = hasHealthyCertificate,
        hasContract = hasContract,
        hasExpertJob = hasExpertJob,
        historyConfirm = historyConfirm,
        militaryStatusCode = militaryStatusCode
    )
}

fun CommitteeDemandInfoDocumentDTO.toDomain(): CommitteeDemandInfoDocumentDN {
    return CommitteeDemandInfoDocumentDN(
        documentId = documentId,
        ownerId = ownerId,
        documentTypeId = documentTypeId,
        documentFileId = documentFileId
    )
}
