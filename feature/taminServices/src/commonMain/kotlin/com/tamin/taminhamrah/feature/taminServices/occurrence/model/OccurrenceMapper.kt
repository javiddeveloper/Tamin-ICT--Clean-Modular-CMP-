package com.tamin.taminhamrah.feature.taminServices.occurrence.model

import com.tamin.taminhamrah.model.history.UserInfoDN
import com.tamin.taminhamrah.model.occurrence.OccurrenceDocTypeDN
import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.model.occurrence.WorkshopItemDN

fun WorkshopItemDN.toPR(): WorkshopItemPR = WorkshopItemPR(
    id = id,
    workshopCode = workshopCode,
    branchCode = branchCode,
    name = name,
    employerName = employerName,
    employerPhone = employerPhone,
    address = address,
    postalCode = postalCode,
    phone = phone,
    nationality = nationality,
)

fun OccurrenceDocTypeDN.toPR(): OccurrenceDocTypePR = OccurrenceDocTypePR(
    id = id,
    title = title,
)

fun OccurrencePersonalInfoDN.toPR(): OccurrencePersonalInfoPR = OccurrencePersonalInfoPR(
    nationalCode = nationalCode,
    firstName = firstName,
    lastName = lastName,
    fatherName = fatherName,
    gender = gender,
    birthDate = birthDate,
    insuranceNumber = insuranceNumber,
    branchCode = branchCode,
    nationality = nationality,
    insuranceType = insuranceType,
)

fun UserInfoDN.toPR(): UserInfoPR = UserInfoPR(
    serial1 = serial1.orEmpty(),
    militaryServiceCode = militaryServiceCode.orEmpty(),
    fatherName = fatherName.orEmpty(),
    lastName = lastName.orEmpty(),
    serial2 = serial2.orEmpty(),
    creationTime = creationTime ?: 0L,
    lastModificationTime = lastModificationTime ?: 0L,
    cityCode = cityCode.orEmpty(),
    socialSecurityNumber = socialSecurityNumber.orEmpty(),
    lastModifiedBy = lastModifiedBy.orEmpty(),
    issueplaceName = issueplaceName.orEmpty(),
    birthDate = birthDate.orEmpty(),
    firstName = firstName.orEmpty(),
    insuranceNumber = insuranceNumber.orEmpty(),
    genderCode = genderCode.orEmpty(),
    nationalID = nationalID.orEmpty(),
    marriageCode = marriageCode.orEmpty(),
    createdBy = createdBy.orEmpty(),
    identityNumber = identityNumber.orEmpty(),
    countryCode = countryCode.orEmpty(),
    id = id.orEmpty(),
    birthDateTimestamp = birthDateTimestamp ?: 0L,
    issueplace = issueplace.orEmpty(),
    nationCode = nationCode.orEmpty(),
)
