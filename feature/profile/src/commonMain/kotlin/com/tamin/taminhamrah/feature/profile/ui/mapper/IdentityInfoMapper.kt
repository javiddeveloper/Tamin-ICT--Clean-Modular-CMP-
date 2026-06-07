package com.tamin.taminhamrah.feature.profile.ui.mapper

import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.feature.profile.ui.model.IdentityInfoPR

fun IdentityInfoDN.toPresentation(): IdentityInfoPR {
    val fName = this.firstName ?: ""
    val lName = this.lastName ?: ""
    val resolvedFullName = "$fName $lName".trim()
    val resolvedGenderDisplay = when (this.gender?.uppercase()) {
        "M", "MALE" -> "مرد"
        "F", "FEMALE" -> "زن"
        else -> this.gender ?: "نامشخص"
    }
    val combinedSerial = when {
        !this.idCardSerial1.isNullOrEmpty() && !this.idCardSerial2.isNullOrEmpty() -> {
            "${this.idCardSerial1}/${this.idCardSerial2}"
        }
        else -> this.idCardSerial1 ?: this.idCardSerial2 ?: ""
    }

    return IdentityInfoPR(
        cityOfBirthId = this.cityOfBirthId ?: "",
        cityOfIssueId = this.cityOfIssueId ?: "",
        countryId = this.countryId ?: "",
        dateOfBirth = this.dateOfBirth ?: 0L,
        dateOfBirthFormatted = this.dateOfBirth?.toString() ?: "",
        fatherName = this.fatherName ?: "",
        firstName = fName,
        lastName = lName,
        fullName = resolvedFullName.ifEmpty { "نامشخص" },
        gender = this.gender ?: "",
        genderDisplay = resolvedGenderDisplay,
        id = this.id ?: 0,
        idCardNumber = this.idCardNumber ?: "",
        idCardSerial = combinedSerial,
        nationalId = this.nationalId ?: "",
        ssn = this.ssn ?: "",
        cityOfBirthName = this.cityOfBirthName ?: "",
        cityOfIssueName = this.cityOfIssueName ?: ""
    )
}
