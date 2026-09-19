package com.tamin.taminhamrah.mapper.requestPaymentForIllDays

import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysBranchWorkshopPR
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoPR

fun IllDaysBranchWorkshopDN.toPresentation(): IllDaysBranchWorkshopPR {
    return IllDaysBranchWorkshopPR(
        id = "$branchCode-$workshopCode",
        label = listOfNotNull(branchName, workshopName).joinToString(" - "),
        branchCode = branchCode.orEmpty(),
        branchName = branchName.orEmpty(),
        workshopCode = workshopCode.orEmpty(),
        workshopName = workshopName.orEmpty(),
    )
}

fun IllDaysInsuredMainInfoDN.toPresentation(): IllDaysInsuredMainInfoPR {
    val fullName = listOfNotNull(firstName, lastName).joinToString(" ")
    return IllDaysInsuredMainInfoPR(
        risuid = risuid.orEmpty(),
        nationalCode = nationalCode.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        fullName = fullName,
        mobileNumber = mobileNumber.orEmpty(),
        genderCode = genderCode.orEmpty(),
        branchCode = branchCode.orEmpty(),
        branchName = branchName.orEmpty(),
        bankAccount = bankAccount.orEmpty(),
        bankName = bankName.orEmpty(),
        insuranceTypeDesc = insuranceTypeDesc.orEmpty(),
        insuranceStatusDesc = insuranceStatusDesc.orEmpty(),
        serviceDateTimeStamp = serviceDateTimeStamp,
        branchWorkshops = branchWorkshops.map { it.toPresentation() },
    )
}

fun CovidResultDN.toPresentation(): CovidResultPR {
    return CovidResultPR(
        startDateTimeStamp = startDateTimeStamp.orEmpty(),
        endDateTimeStamp = endDateTimeStamp.orEmpty(),
        timestamps = timestamps,
    )
}
