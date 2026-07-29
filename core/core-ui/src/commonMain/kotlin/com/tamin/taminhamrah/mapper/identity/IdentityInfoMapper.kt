package com.tamin.taminhamrah.mapper.identity

import com.tamin.taminhamrah.model.identity.IdentityInfoDN
import com.tamin.taminhamrah.model.identity.IdentityInfoPR
import com.tamin.taminhamrah.util.PersianDateFormatter

fun IdentityInfoDN.toPresentation(): IdentityInfoPR {
    val fName = this.firstName ?: ""
    val lName = this.lastName ?: ""
    val resolvedFullName = "$fName $lName".trim()
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
        fullName = resolvedFullName,
        gender = this.gender ?: "",
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
