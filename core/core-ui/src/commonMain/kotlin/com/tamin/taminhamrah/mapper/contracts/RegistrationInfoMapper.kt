package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun RegistrationInfoDN.toPresentation(): RegistrationInfoPR {
    val personal = personalInfo
    return RegistrationInfoPR(
        fullName = listOfNotNull(personal?.firstName, personal?.lastName)
            .joinToString(" ")
            .ifBlank { "-" },
        nationalId = personal?.nationalId ?: "",
        birthDateFormatted = PersianDateFormatter.formatTimestamp(personal?.dateOfBirth),
        insuranceId = insuranceId ?: "",
        genderTitle = if (personal?.genderCode == "02") "خانم" else "آقای",
    )
}
