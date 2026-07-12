package com.tamin.taminhamrah.data.mapper.treatment

import com.tamin.taminhamrah.data.local.entity.DeservedTreatmentEntity
import com.tamin.taminhamrah.data.local.entity.DependantUserUnderEighteenEntity
import com.tamin.taminhamrah.data.local.entity.MedicalAuthoritiesEntity
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.MedicalAuthoritiesDN

// --- Deserved Treatment ---
fun DeservedTreatmentDN.toEntity(nationalCode: String) = DeservedTreatmentEntity(
    nationalCode = nationalCode,
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

fun DeservedTreatmentEntity.toDomain() = DeservedTreatmentDN(
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

// --- Dependant Under Eighteen ---
fun DependantUserUnderEighteenDN.toEntity(nationalCode: String) = DependantUserUnderEighteenEntity(
    nationalCode = nationalCode,
    firstName = firstName,
    lastName = lastName,
    nationalId = nationalId,
    id = id
)

fun DependantUserUnderEighteenEntity.toDomain() = DependantUserUnderEighteenDN(
    firstName = firstName,
    lastName = lastName,
    nationalId = nationalId,
    id = id
)

// --- Medical Confirmations ---
fun MedicalAuthoritiesDN.toEntity() = MedicalAuthoritiesEntity(
    supportType = supportType,
    treatmentCenter = treatmentCenter,
    confirmInBranch = confirmInBranch,
    confirmStatus = confirmStatus,
    insuranceNumber = insuranceNumber,
    nationalCode = nationalCode,
    firstName = firstName,
    lastName = lastName,
    outpatientRestStartDate = outpatientRestStartDate,
    outpatientRestEndDate = outpatientRestEndDate,
    numberOfOutpatientDays = numberOfOutpatientDays,
    hospitalizationStartDate = hospitalizationStartDate,
    hospitalizationEndDate = hospitalizationEndDate,
    numberOfHospitalizationDays = numberOfHospitalizationDays,
    description = description,
    branch = branch,
    fromDateNotConfirm = fromDateNotConfirm,
    toDateNotConfirm = toDateNotConfirm
)

fun MedicalAuthoritiesEntity.toDomain() = MedicalAuthoritiesDN(
    supportType = supportType,
    treatmentCenter = treatmentCenter,
    confirmInBranch = confirmInBranch,
    confirmStatus = confirmStatus,
    insuranceNumber = insuranceNumber,
    nationalCode = nationalCode,
    firstName = firstName,
    lastName = lastName,
    outpatientRestStartDate = outpatientRestStartDate,
    outpatientRestEndDate = outpatientRestEndDate,
    numberOfOutpatientDays = numberOfOutpatientDays,
    hospitalizationStartDate = hospitalizationStartDate,
    hospitalizationEndDate = hospitalizationEndDate,
    numberOfHospitalizationDays = numberOfHospitalizationDays,
    description = description,
    branch = branch,
    fromDateNotConfirm = fromDateNotConfirm,
    toDateNotConfirm = toDateNotConfirm
)
