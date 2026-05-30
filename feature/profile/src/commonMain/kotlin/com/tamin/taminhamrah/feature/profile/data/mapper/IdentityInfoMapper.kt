package com.tamin.taminhamrah.feature.profile.data.mapper

import com.tamin.core.network.model.user.IdentityInfoDto
import com.tamin.taminhamrah.core.model.common.IdentityInfoDN
import com.tamin.taminhamrah.data.local.entity.IdentityInfoEntity

internal fun IdentityInfoDto.toEntity(): IdentityInfoEntity = IdentityInfoEntity(
    id = id,
    cityOfBirthId = cityOfBirthId,
    cityOfIssueId = cityOfIssueId,
    countryId = countryId,
    dateOfBirth = dateOfBirth,
    fatherName = fatherName,
    firstName = firstName,
    gender = gender,
    idCardNumber = idCardNumber,
    idCardSerial1 = idCardSerial1,
    idCardSerial2 = idCardSerial2,
    lastName = lastName,
    nationalId = nationalId,
    ssn = ssn,
)

internal fun IdentityInfoDto.toDomain(): IdentityInfoDN = IdentityInfoDN(
    cityOfBirthId = cityOfBirthId,
    cityOfIssueId = cityOfIssueId,
    countryId = countryId,
    dateOfBirth = dateOfBirth,
    fatherName = fatherName,
    firstName = firstName,
    gender = gender,
    id = id,
    idCardNumber = idCardNumber,
    idCardSerial1 = idCardSerial1,
    idCardSerial2 = idCardSerial2,
    lastName = lastName,
    nationalId = nationalId,
    ssn = ssn,
)

internal fun IdentityInfoEntity.toDomain(): IdentityInfoDN = IdentityInfoDN(
    cityOfBirthId = cityOfBirthId,
    cityOfIssueId = cityOfIssueId,
    countryId = countryId,
    dateOfBirth = dateOfBirth,
    fatherName = fatherName,
    firstName = firstName,
    gender = gender,
    id = id,
    idCardNumber = idCardNumber,
    idCardSerial1 = idCardSerial1,
    idCardSerial2 = idCardSerial2,
    lastName = lastName,
    nationalId = nationalId,
    ssn = ssn,
)
