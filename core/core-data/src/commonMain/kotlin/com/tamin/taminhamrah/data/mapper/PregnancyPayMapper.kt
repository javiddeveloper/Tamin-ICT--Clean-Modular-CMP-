package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.pregnancyPay.PregnancyBranchWorkshopDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyBranchWorkshopDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyPayEstimateDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyRequestFileDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyRequestFileDTO
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyShorttermRequestDTO
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDTO

private const val PREGNANCY_PAY_HELP_TYPE = "02"

fun PregnancyMainInfoDTO.toDomain(): PregnancyMainInfoDN {
    return PregnancyMainInfoDN(
        risuid = risuid,
        nationalCode = nationalCode,
        firstName = insuranceFirstName,
        lastName = insuranceLastName,
        mobileNumber = mobileNumber,
        genderCode = genderCode,
        serviceDateTimeStamp = serviceDateTimeStamp,
        branchWorkshops = branchWorkshop?.map { it.toDomain() } ?: emptyList(),
        bankAccount = bankAccount,
        bankName = bankName,
        insuranceTypeDesc = insuranceTypeDesc,
        insuranceStatusDesc = insuranceStatusDesc,
    )
}

fun PregnancyBranchWorkshopDTO.toDomain(): PregnancyBranchWorkshopDN {
    return PregnancyBranchWorkshopDN(
        branchCode = branchCode,
        branchName = branchName,
        workshopName = workshopName,
    )
}

fun PregnancyOptionDTO.toDomain(): PregnancyOptionDN? {
    val code = code ?: return null
    val label = name ?: return null
    return PregnancyOptionDN(code = code, name = label)
}

fun SendPregnancyPayRequestDN.toDTO(): SendPregnancyPayRequestDTO {
    return SendPregnancyPayRequestDTO(
        barChild = pregnancyTypeCode,
        doctorCode = doctorCode,
        restDaysCount = restDaysCount,
        babyBirthDateTimeStamp = babyBirthDateTimeStamp,
        doctorName = doctorName,
        restEndDateTimeStamp = restEndDateTimeStamp,
        restStartDateTimeStamp = restStartDateTimeStamp,
        barType = pregnancyStatusCode,
        childNationalId = childNationalId,
        childNationalId2 = childNationalId2,
        childNationalId3 = childNationalId3,
        requestTypeCode = requestTypeCode,
        shorttermRequest = PregnancyShorttermRequestDTO(
            branchCode = branchCode,
            branchName = branchName,
            insuranceFirstName = insuranceFirstName,
            insuranceLastName = insuranceLastName,
            mobileNumber = mobileNumber,
            nationalCode = nationalCode,
            requestFileList = requestFileList.map { it.toDTO() },
            requestHelpType = PREGNANCY_PAY_HELP_TYPE,
            risuid = risuid,
            serviceDateTimeStamp = serviceDateTimeStamp,
        ),
    )
}

fun PregnancyRequestFileDN.toDTO(): PregnancyRequestFileDTO {
    return PregnancyRequestFileDTO(
        documentFile = documentFile,
        documentType = documentType,
    )
}

fun List<String?>.toPregnancyPayEstimateDomain(): PregnancyPayEstimateDN {
    return PregnancyPayEstimateDN(
        averageSalaryLast90Days = getOrNull(0).orEmpty(),
        amountPayable = getOrNull(1).orEmpty(),
    )
}
