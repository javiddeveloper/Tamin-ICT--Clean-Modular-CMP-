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

fun PatientHospitalizationsDTO.toDomain() = PatientHospitalizationsDN(
    admId = admId,
    admSource = admSource,
    admType = admType,
    comments = comments,
    docID = docID,
    docSpeciality = docSpeciality,
    doctorName = doctorName,
    finalDiagCode = finalDiagCode,
    finalDiagDesc = finalDiagDesc,
    firstDiagCode = firstDiagCode,
    firstDiagDesc = firstDiagDesc,
    healthcareProvider = healthcareProvider,
    hospitalizedDays = hospitalizedDays,
    hospitalizedEndDate = hospitalizedEndDate,
    hospitalizedStartDate = hospitalizedStartDate,
    outcomeDesc = outcomeDesc,
    referDocId = referDocId,
    referDocName = referDocName,
    referDocSpeciality = referDocSpeciality,
    referHealthcareProvider = referHealthcareProvider
)

fun PatientVisitDTO.toDomain() = PatientVisitDN(
    comments = comments,
    diagCode = diagCode,
    diagDesc = diagDesc,
    docID = docID,
    docSpeciality = docSpeciality,
    docSpecialityCode = docSpecialityCode,
    doctorName = doctorName,
    healthcareProvider = healthcareProvider,
    serviceName = serviceName,
    serviceProvideType = serviceProvideType,
    serviceResult = serviceResult,
    sourceSystem = sourceSystem,
    visitDate = visitDate,
    visitType = visitType
)

fun PatientLabDTO.toDomain() = PatientLabDN(
    deliveredQty = deliveredQty,
    diagCode = diagCode,
    diagDesc = diagDesc,
    docSpeciality = docSpeciality,
    docSpecialityCode = docSpecialityCode,
    doctorName = doctorName,
    examName = examName,
    healthcareProvider = healthcareProvider,
    itemComments = itemComments,
    objectId = objectId,
    prescribedQty = prescribedQty,
    resultDesc = resultDesc,
    resultValue = resultValue,
    serviceProvideType = serviceProvideType,
    sourceSystem = sourceSystem,
    visitDate = visitDate,
    visitType = visitType
)

fun PatientImagingDTO.toDomain() = PatientImagingDN(
    deliverStatus = deliverStatus,
    diagCode = diagCode,
    diagDesc = diagDesc,
    docSpeciality = docSpeciality,
    doctorName = doctorName,
    healthcareProvider = healthcareProvider,
    imagingName = imagingName,
    itemComments = itemComments,
    modality = modality,
    objectId = objectId,
    resultDesc = resultDesc,
    visitDate = visitDate,
    visitType = visitType
)
