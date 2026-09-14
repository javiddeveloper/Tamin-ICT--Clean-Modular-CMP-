package com.tamin.taminhamrah.mapper.contracts

import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun RegistrationInfoDN.toPresentation(): RegistrationInfoPR {
    val personal = personalInfo
    val contact = lastContact
    val mobile = contact?.mobile?.takeIf { it.isNotBlank() }
        ?: (mobileNumber?:"")
    return RegistrationInfoPR(
        fullName = listOfNotNull(personal?.firstName, personal?.lastName)
            .joinToString(" ")
            .ifBlank { "-" },
        nationalId = personal?.nationalId ?: "",
        birthDateFormatted = PersianDateFormatter.formatTimestamp(personal?.dateOfBirth),
        insuranceId = insuranceId ?: "",
        genderCode = personal?.genderCode.orEmpty(),
        address = contact?.address?:"",
        zipCode = contact?.zipCode?:"",
        phoneNumber = contact?.phoneNumber?:"",
        mobileNumber = mobile,
        hasMobile = mobile.isNotBlank(),
        insuranceIdValid = insuranceIdValidity,
        dateOfBirthEpoch = personal?.dateOfBirth,
    )
}
