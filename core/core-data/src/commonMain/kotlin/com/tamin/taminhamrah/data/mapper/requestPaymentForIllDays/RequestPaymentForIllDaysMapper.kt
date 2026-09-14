package com.tamin.taminhamrah.data.mapper.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultListDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestFileDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestFileDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysRequestPlaceholderDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysShortTermRequestDTO
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDTO

private const val ILL_DAYS_HELP_TYPE = "01"

fun IllDaysInsuredMainInfoDTO.toDomain(): IllDaysInsuredMainInfoDN {
    return IllDaysInsuredMainInfoDN(
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
        serviceDateTimeStamp = serviceDateTimeStamp,
        branchWorkshops = branchWorkshop?.map { it.toDomain() } ?: emptyList(),
    )
}

fun IllDaysBranchWorkshopDTO.toDomain(): IllDaysBranchWorkshopDN {
    return IllDaysBranchWorkshopDN(
        branchCode = branchCode,
        branchName = branchName,
        workshopCode = workshopCode,
        workshopName = workshopName,
    )
}

fun CovidResultListDTO.toDomain(): CovidResultDN {
    val timestamps = list.orEmpty()
    return CovidResultDN(
        startDateTimeStamp = timestamps.getOrNull(0),
        endDateTimeStamp = timestamps.getOrNull(1),
        timestamps = timestamps,
    )
}

fun SaveShortTermIllnessRequestDN.toDTO(): SaveShortTermIllnessRequestDTO {
    return SaveShortTermIllnessRequestDTO(
        doctorId = doctorId,
        doctorName = doctorName,
        endDateTimeStamp = endDateTimeStamp,
        illnessKind = illnessKind,
        startDateTimeStamp = startDateTimeStamp,
        workStatus = workStatus,
        provinceCode = provinceCode,
        cityCode = cityCode,
        shorttermRequest = IllDaysShortTermRequestDTO(
            branchCode = branchCode,
            branchName = branchName,
            insuranceFirstName = insuranceFirstName,
            insuranceLastName = insuranceLastName,
            mobileNumber = mobileNumber,
            nationalCode = nationalCode,
            request = IllDaysRequestPlaceholderDTO(id = null),
            requestFileList = requestFileList.map { it.toDTO() },
            requestHelpType = ILL_DAYS_HELP_TYPE,
            risuid = risuid,
            serviceDateTimeStamp = serviceDateTimeStamp,
        ),
    )
}

fun IllDaysRequestFileDN.toDTO(): IllDaysRequestFileDTO {
    return IllDaysRequestFileDTO(
        documentFile = documentFile,
        documentType = documentType,
    )
}
