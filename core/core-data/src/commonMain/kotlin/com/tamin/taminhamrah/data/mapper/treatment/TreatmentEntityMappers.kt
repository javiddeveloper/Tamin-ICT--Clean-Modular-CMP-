package com.tamin.taminhamrah.data.mapper.treatment

import com.tamin.taminhamrah.data.local.entity.DeservedTreatmentEntity
import com.tamin.taminhamrah.data.local.entity.ElectronicPrescriptionEntity
import com.tamin.taminhamrah.data.local.entity.ElectronicPrescriptionDetailEntity
import com.tamin.taminhamrah.data.local.entity.ElectronicPrescriptionPriceEntity
import com.tamin.taminhamrah.data.local.entity.DependantUserUnderEighteenEntity
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.data.local.entity.TreatmentCostEntity
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
    finalDesc = null,
    illness = illness,
    trackingCode = trackingCode
)

// --- Electronic Prescription ---
fun ElectronicPrescriptionDN.toEntity(patientNationalCode: String) = ElectronicPrescriptionEntity(
    patientNationalCode = patientNationalCode,
    id = id,
    docId = docId,
    docName = docName,
    flagSata = flagSata,
    location = location,
    noteHeadEprescID = noteHeadEprescID,
    patientID = patientID,
    patientName = patientName,
    prescDate = prescDate,
    prescName = prescName,
    specDesc = specDesc,
    prescType = prescType,
    trackingCode = trackingCode
)

fun ElectronicPrescriptionEntity.toDomain() = ElectronicPrescriptionDN(
    id = id,
    docId = docId,
    docName = docName,
    flagSata = flagSata,
    location = location,
    noteHeadEprescID = noteHeadEprescID,
    patientID = patientID,
    patientName = patientName,
    prescDate = prescDate,
    prescName = prescName,
    specDesc = specDesc,
    prescType = prescType,
    trackingCode = trackingCode
)

// --- Electronic Prescription Detail ---
fun ElectronicPrescriptionDetailDN.toEntity(noteHeadId: String) = ElectronicPrescriptionDetailEntity(
    noteHeadId = noteHeadId,
    sumPriceItem = sumPriceItem,
    ssoPayment = ssoPayment,
    insurancePayment = insurancePayment,
    serviceQuantity = serviceQuantity,
    noteHeadEprescID = noteHeadEprescID,
    serverCode = serverCode,
    serverName = serverName,
    serviceName = serviceName,
    drugInst = drugInst,
    registerDate = registerDate,
    drugInstruction = drugInstruction,
    deliveredNo = deliveredNo,
    drugAmount = drugAmount
)

fun ElectronicPrescriptionDetailEntity.toDomain() = ElectronicPrescriptionDetailDN(
    sumPriceItem = sumPriceItem,
    ssoPayment = ssoPayment,
    insurancePayment = insurancePayment,
    serviceQuantity = serviceQuantity,
    noteHeadEprescID = noteHeadEprescID,
    serverCode = serverCode,
    serverName = serverName,
    serviceName = serviceName,
    drugInst = drugInst,
    registerDate = registerDate,
    drugInstruction = drugInstruction,
    deliveredNo = deliveredNo,
    drugAmount = drugAmount
)

// --- Electronic Prescription Price ---
fun ElectronicPrescriptionPriceDN.toEntity(noteHeadId: String) = ElectronicPrescriptionPriceEntity(
    noteHeadId = noteHeadId,
    headInsuPayment = headInsuPayment,
    headSsoPayment = headSsoPayment,
    noteHeadEprescID = noteHeadEprescID,
    requestPrice = requestPrice
)

fun ElectronicPrescriptionPriceEntity.toDomain() = ElectronicPrescriptionPriceDN(
    headInsuPayment = headInsuPayment,
    headSsoPayment = headSsoPayment,
    noteHeadEprescID = noteHeadEprescID,
    requestPrice = requestPrice
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
    estimatePayDate = estimatePayDate,
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
    estimatePayDate = estimatePayDate,
    returnReason = returnReason
)

