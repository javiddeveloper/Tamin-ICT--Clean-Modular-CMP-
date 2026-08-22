package com.tamin.taminhamrah.model.personal

data class PersonalDN(
    val firstName: String?,
    val lastName: String?,
    val fatherName: String?,
    val nationalId: String?,
    val ssn: String?,
    val genderDesc: String?,
    val genderCode: String? = null,
    val dateOfBirth: Long?,
    val idCardNumber: String? = null,
    val dateOfDead: Long? = null,
    val contactAddress: String? = null,
    val contactZipCode: String? = null,
    val contactPhoneNumber: String? = null,
)
