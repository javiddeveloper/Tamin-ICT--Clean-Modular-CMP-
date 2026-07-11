package com.tamin.taminhamrah.data.mapper.health

import com.tamin.taminhamrah.model.health.*

fun PatientGeneralDTO.toDomain() = PatientGeneralDN(
    ptientID = ptientID,
    patientName = patientName,
    patientFamily = patientFamily,
    patientNatCode = patientNatCode,
    patientAge = patientAge,
    patientGender = patientGender,
    patientBirthDate = patientBirthDate,
    patientMobile = patientMobile,
    patientAddress = patientAddress,
    patientFather = patientFather
)

fun PatientSelfDeclarativeDTO.toDomain() = PatientSelfDeclarativeDN(
    alcoholDesc = alcoholDesc,
    alcoholUsage = alcoholUsage,
    alcoholUsageTitle = alcoholUsageTitle,
    exerciseDesc = exerciseDesc,
    exerciseFreq = exerciseFreq,
    exerciseFreqTitle = exerciseFreqTitle,
    lastUpdateDate = lastUpdateDate,
    objectID = objectID,
    smokingDesc = smokingDesc,
    smokingStatus = smokingStatus,
    smokingStatusTitle = smokingStatusTitle,
    substanceDesc = substanceDesc,
    substanceUsage = substanceUsage,
    substanceUsageTitle = substanceUsageTitle
)

fun DrugItemAllergiesDTO.toDomain() = DrugItemAllergiesDN(
    allergyComments = allergyComments,
    drugId = drugId,
    drugName = drugName
)
