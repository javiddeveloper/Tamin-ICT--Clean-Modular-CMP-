package com.tamin.taminhamrah.mapper.health

import com.tamin.taminhamrah.model.health.*
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN

fun PatientGeneralDN.toPresentation() = PatientGeneralPR(
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

fun PatientSelfDeclarativeDN.toPresentation() = PatientSelfDeclarativePR(
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

fun DrugItemAllergiesDN.toPresentation() = DrugItemAllergiesPR(
    allergyComments = allergyComments,
    drugId = drugId,
    drugName = drugName
)

fun PatientHospitalizationsDN.toPresentation() = PatientHospitalizationsPR(
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

fun PatientVisitDN.toPresentation() = PatientVisitPR(
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

fun PatientLabDN.toPresentation() = PatientLabPR(
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

fun PatientImagingDN.toPresentation() = PatientImagingPR(
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
