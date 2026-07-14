package com.tamin.taminhamrah.data.mapper.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO

fun DeservedTreatmentDTO.toDomain() = DeservedTreatmentDN(
    birthDate = birthDate,
    brhCode = brhCode,
    brhName = brhName,
    dependenceType = dependenceType,
    fatherName = fatherName,
    feranshiz = feranshiz,
    firstName = firstName,
    gender = gender,
    healthBookletDate = healthBookletDate,
    id = id,
    idNumber = idNumber,
    insuranceType = insuranceType,
    lastBookletDate = lastBookletDate,
    lastName = lastName,
    natCode = natCode,
    nationalId = nationalId,
    parentRisuid = parentRisuid,
    provinceCode = provinceCode,
    provinceName = provinceName,
    regWorkshopId = regWorkshopId,
    regWorkshopName = regWorkshopName,
    risuid = risuid,
    message = message,
    illness = illness,
    trackingCode = trackingCode
)

fun DependantUserUnderEighteenDTO.toDomain() = DependantUserUnderEighteenDN(
    firstName = relationWithTamin?.personal?.firstName,
    lastName = relationWithTamin?.personal?.lastName,
    nationalId = relationWithTamin?.personal?.nationalId,
    id = id
)
