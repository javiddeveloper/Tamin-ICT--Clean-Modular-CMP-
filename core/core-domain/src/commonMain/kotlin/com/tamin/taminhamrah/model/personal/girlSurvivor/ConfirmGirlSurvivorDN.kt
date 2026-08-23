package com.tamin.taminhamrah.model.personal.girlSurvivor

import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.DependencyTypeDN

data class ConfirmGirlSurvivorDN(
    val address: String? = null,
    val age: String? = null,
    val birthDate: Long? = null,
    val childInsuranceId: String? = null,
    val childNationalId: String? = null,
    val deathDate: Long? = null,
    val deathType: String? = null,
    val dependencyType: DependencyTypeDN? = null,
    val firstName: String? = null,
    val gender: String? = null,
    val idNumber: String? = null,
    val insuranceNumber: String? = null,
    val lastName: String? = null,
    val mobileNumber: String? = null,
    val nationalCode: String? = null,
    val pensionId: String? = null,
    val phoneNumber: String? = null,
    val status: String? = null,
)
