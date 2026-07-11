package com.tamin.taminhamrah.data.mapper.treatment

import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDN
import com.tamin.taminhamrah.model.treatment.DependantUserUnderEighteenDTO
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDN
import com.tamin.taminhamrah.model.treatment.DeservedTreatmentDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDetailDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionDTO
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDN
import com.tamin.taminhamrah.model.treatment.ElectronicPrescriptionPriceDTO

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

fun ElectronicPrescriptionDTO.toDomain() = ElectronicPrescriptionDN(
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

fun ElectronicPrescriptionDetailDTO.toDomain() = ElectronicPrescriptionDetailDN(
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

fun ElectronicPrescriptionPriceDTO.toDomain() = ElectronicPrescriptionPriceDN(
    headInsuPayment = headInsuPayment,
    headSsoPayment = headSsoPayment,
    noteHeadEprescID = noteHeadEprescID,
    requestPrice = requestPrice
)

fun DependantUserUnderEighteenDTO.toDomain() = DependantUserUnderEighteenDN(
    firstName = relationWithTamin?.personal?.firstName,
    lastName = relationWithTamin?.personal?.lastName,
    nationalId = relationWithTamin?.personal?.nationalId,
    id = id
)
