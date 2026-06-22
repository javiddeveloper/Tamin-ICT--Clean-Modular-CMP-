package com.tamin.taminhamrah.model.contracts

data class RegistrationInfoDN(
    val personalInfo: RegistrationPersonalInfoDN?,
    val insuranceIdValidity: Boolean,
    val mobileNumber: String?,
    val insuranceId: String?,
    val lastContact: RegistrationContactDN?,
)

data class RegistrationPersonalInfoDN(
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val dateOfBirth: Long?,
    val genderCode: String?,
    val genderDesc: String?,
    val ssn: String?,
)

data class RegistrationContactDN(
    val address: String?,
    val zipCode: String?,
    val mobile: String?,
    val phoneNumber: String?,
)
