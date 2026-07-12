package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.pension.*
import com.tamin.taminhamrah.model.pension.checkRetirementStatus.*
import com.tamin.taminhamrah.model.pension.fish.PayRollDTO
import com.tamin.taminhamrah.model.pension.installment.*
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDN
import com.tamin.taminhamrah.model.pension.retirementInfo.RetirementRequestDTO

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
        paymentAmount = paymentAmount
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
        quota = quota,
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
