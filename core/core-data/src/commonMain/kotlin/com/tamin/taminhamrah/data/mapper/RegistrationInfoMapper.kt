package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.data.local.entity.RegistrationInfoEntity
import com.tamin.taminhamrah.model.contracts.RegistrationContactDN
import com.tamin.taminhamrah.model.contracts.RegistrationContactDTO
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationInfoDTO
import com.tamin.taminhamrah.model.contracts.RegistrationPersonalInfoDN
import com.tamin.taminhamrah.model.contracts.RegistrationPersonalInfoDTO

internal fun RegistrationInfoDTO.toDomain(): RegistrationInfoDN = RegistrationInfoDN(
    personalInfo = personalInfo?.toDomain(),
    insuranceIdValidity = insuranceIdValidity ?: false,
    mobileNumber = mobileNumber,
    insuranceId = insuranceId,
    lastContact = lastContact?.toDomain(),
)

internal fun RegistrationPersonalInfoDTO.toDomain(): RegistrationPersonalInfoDN =
    RegistrationPersonalInfoDN(
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        dateOfBirth = dateOfBirth,
        genderCode = gender?.genderCode,
        genderDesc = gender?.genderDesc,
        ssn = ssn,
    )

internal fun RegistrationContactDTO.toDomain(): RegistrationContactDN = RegistrationContactDN(
    address = address,
    zipCode = zipCode,
    mobile = mobile,
    phoneNumber = phoneNumber,
)

internal fun RegistrationInfoDN.toEntity(): RegistrationInfoEntity {
    val personal = personalInfo
    val contact = lastContact
    return RegistrationInfoEntity(
        insuranceId = insuranceId,
        insuranceIdValidity = insuranceIdValidity,
        mobileNumber = mobileNumber,
        firstName = personal?.firstName,
        lastName = personal?.lastName,
        nationalId = personal?.nationalId,
        dateOfBirth = personal?.dateOfBirth,
        genderCode = personal?.genderCode,
        genderDesc = personal?.genderDesc,
        ssn = personal?.ssn,
        contactAddress = contact?.address,
        contactZipCode = contact?.zipCode,
        contactMobile = contact?.mobile,
        contactPhoneNumber = contact?.phoneNumber,
    )
}

internal fun RegistrationInfoEntity.toDomain(): RegistrationInfoDN = RegistrationInfoDN(
    personalInfo = RegistrationPersonalInfoDN(
        firstName = firstName,
        lastName = lastName,
        nationalId = nationalId,
        dateOfBirth = dateOfBirth,
        genderCode = genderCode,
        genderDesc = genderDesc,
        ssn = ssn,
    ),
    insuranceIdValidity = insuranceIdValidity,
    mobileNumber = mobileNumber,
    insuranceId = insuranceId,
    lastContact = RegistrationContactDN(
        address = contactAddress,
        zipCode = contactZipCode,
        mobile = contactMobile,
        phoneNumber = contactPhoneNumber,
    ),
)
