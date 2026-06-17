package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.pension.*

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
