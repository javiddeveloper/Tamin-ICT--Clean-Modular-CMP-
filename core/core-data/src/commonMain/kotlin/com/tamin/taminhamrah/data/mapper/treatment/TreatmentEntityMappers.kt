package com.tamin.taminhamrah.data.mapper.treatment

import com.tamin.taminhamrah.data.local.entity.DeservedTreatmentEntity
import com.tamin.taminhamrah.data.local.entity.DependantUserUnderEighteenEntity
import com.tamin.taminhamrah.data.local.entity.TreatmentCostEntity
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.TreatmentCostDN

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

// --- Treatment Costs ---
fun TreatmentCostDN.toEntity() = TreatmentCostEntity(
    accountNumber = accountNumber,
    bimeCode = bimeCode,
    datePaz = datePaz,
    famil = famil,
    healthcenterName = healthcenterName,
    mainNational = mainNational,
    maliCode = maliCode,
    name = name,
    nameAsli = nameAsli,
    nameFamil = nameFamil,
    noPazir = noPazir,
    payNatCode = payNatCode,
    payOtherService = payOtherService,
    payPrice = payPrice,
    payService = payService,
    payStatus = payStatus,
    payType = payType,
    province = province,
    rahgiriCode = rahgiriCode,
    releaseDate = releaseDate,
    repId = repId,
    serviceDate = serviceDate,
    status = status,
    statusDesc = statusDesc,
    payStatusDesc = payStatusDesc,
    returnReason = returnReason
)

fun TreatmentCostEntity.toDomain() = TreatmentCostDN(
    accountNumber = accountNumber,
    bimeCode = bimeCode,
    datePaz = datePaz,
    famil = famil,
    healthcenterName = healthcenterName,
    mainNational = mainNational,
    maliCode = maliCode,
    name = name,
    nameAsli = nameAsli,
    nameFamil = nameFamil,
    noPazir = noPazir,
    payNatCode = payNatCode,
    payOtherService = payOtherService,
    payPrice = payPrice,
    payService = payService,
    payStatus = payStatus,
    payType = payType,
    province = province,
    rahgiriCode = rahgiriCode,
    releaseDate = releaseDate,
    repId = repId,
    serviceDate = serviceDate,
    status = status,
    statusDesc = statusDesc,
    payStatusDesc = payStatusDesc,
    returnReason = returnReason
)
