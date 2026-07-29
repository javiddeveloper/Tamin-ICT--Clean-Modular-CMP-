package com.tamin.taminhamrah.mapper.identity

import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.identity.IdentityInfoPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun IdentityInfoDN.toPresentation(): IdentityInfoPR {
    val fName = this.firstName ?: ""
    val lName = this.lastName ?: ""
    val resolvedFullName = "$fName $lName".trim()
    // The service sends the civil-registry code ("01"/"02"); the letter forms are kept because
    // other callers of this mapper still send them.
    val resolvedGenderDisplay = when (this.gender?.uppercase()?.trimStart('0')) {
        "M", "MALE", "1" -> "مرد"
        "F", "FEMALE", "2" -> "زن"
        else -> "نامشخص"
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
        dateOfBirthFormatted = PersianDateFormatter.formatTimestamp(this.dateOfBirth),
        fatherName = this.fatherName ?: "",
        firstName = fName,
        lastName = lName,
        fullName = resolvedFullName.ifEmpty { "نامشخص" },
        gender = this.gender ?: "",
        genderDisplay = resolvedGenderDisplay,
        id = this.id ?: 0,
        idCardNumber = this.idCardNumber ?: "",
        idCardSerial = combinedSerial,
        idCardSerial1 = this.idCardSerial1 ?: "",
        idCardSerial2 = this.idCardSerial2 ?: "",
        nationalId = this.nationalId ?: "",
        ssn = this.ssn ?: "",
        cityOfBirthName = this.cityOfBirthName ?: "",
        cityOfIssueName = this.cityOfIssueName ?: ""
    )
}
