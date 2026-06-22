package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun RegistrationInfoDN.toPresentation(): RegistrationInfoPR {
    val personal = personalInfo
    val contact = lastContact
    val mobile = contact?.mobile?.takeIf { it.isNotBlank() }
        ?: mobileNumber.orEmpty()
    return RegistrationInfoPR(
        fullName = listOfNotNull(personal?.firstName, personal?.lastName)
            .joinToString(" ")
            .ifBlank { "-" },
        nationalId = personal?.nationalId ?: "",
        birthDateFormatted = PersianDateFormatter.formatTimestamp(personal?.dateOfBirth),
        insuranceId = insuranceId ?: "",
        genderTitle = if (personal?.genderCode == "02") "خانم" else "آقای",
        address = contact?.address.orEmpty(),
        zipCode = contact?.zipCode.orEmpty(),
        phoneNumber = contact?.phoneNumber.orEmpty(),
        mobileNumber = mobile,
        hasMobile = mobile.isNotBlank(),
    )
}
