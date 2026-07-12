package com.tamin.taminhamrah.data.mapper.health

import com.tamin.taminhamrah.data.local.entity.DrugAllergyEntity
import com.tamin.taminhamrah.data.local.entity.HospitalizationEntity
import com.tamin.taminhamrah.data.local.entity.PatientGeneralEntity
import com.tamin.taminhamrah.data.local.entity.PatientImagingEntity
import com.tamin.taminhamrah.data.local.entity.PatientLabEntity
import com.tamin.taminhamrah.data.local.entity.PatientSelfDeclarativeEntity
import com.tamin.taminhamrah.data.local.entity.PatientVisitEntity
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientHospitalizationsDN
import com.tamin.taminhamrah.model.health.PatientImagingDN
import com.tamin.taminhamrah.model.health.PatientLabDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN
import com.tamin.taminhamrah.model.health.PatientVisitDN

fun PatientGeneralDN.toEntity(natCode: String) = PatientGeneralEntity(
    natCode = natCode,
    ptientID = ptientID, patientName = patientName, patientFamily = patientFamily,
    patientNatCode = patientNatCode, patientAge = patientAge, patientGender = patientGender,
    patientBirthDate = patientBirthDate, patientMobile = patientMobile,
    patientAddress = patientAddress, patientFather = patientFather
)

fun PatientGeneralEntity.toDomain() = PatientGeneralDN(
    ptientID = ptientID, patientName = patientName, patientFamily = patientFamily,
    patientNatCode = patientNatCode, patientAge = patientAge, patientGender = patientGender,
    patientBirthDate = patientBirthDate, patientMobile = patientMobile,
    patientAddress = patientAddress, patientFather = patientFather
)

fun PatientSelfDeclarativeDN.toEntity(natCode: String) = PatientSelfDeclarativeEntity(
    natCode = natCode,
    alcoholDesc = alcoholDesc, alcoholUsage = alcoholUsage, alcoholUsageTitle = alcoholUsageTitle,
    exerciseDesc = exerciseDesc, exerciseFreq = exerciseFreq, exerciseFreqTitle = exerciseFreqTitle,
    lastUpdateDate = lastUpdateDate, objectID = objectID, smokingDesc = smokingDesc,
    smokingStatus = smokingStatus, smokingStatusTitle = smokingStatusTitle,
    substanceDesc = substanceDesc, substanceUsage = substanceUsage, substanceUsageTitle = substanceUsageTitle
)

fun PatientSelfDeclarativeEntity.toDomain() = PatientSelfDeclarativeDN(
    alcoholDesc = alcoholDesc, alcoholUsage = alcoholUsage, alcoholUsageTitle = alcoholUsageTitle,
    exerciseDesc = exerciseDesc, exerciseFreq = exerciseFreq, exerciseFreqTitle = exerciseFreqTitle,
    lastUpdateDate = lastUpdateDate, objectID = objectID, smokingDesc = smokingDesc,
    smokingStatus = smokingStatus, smokingStatusTitle = smokingStatusTitle,
    substanceDesc = substanceDesc, substanceUsage = substanceUsage, substanceUsageTitle = substanceUsageTitle
)

fun DrugItemAllergiesDN.toEntity(natCode: String) = DrugAllergyEntity(
    natCode = natCode, allergyComments = allergyComments, drugId = drugId, drugName = drugName
)

fun DrugAllergyEntity.toDomain() = DrugItemAllergiesDN(
    allergyComments = allergyComments, drugId = drugId, drugName = drugName
)

fun PatientHospitalizationsDN.toEntity(natCode: String) = HospitalizationEntity(
    natCode = natCode,
    admId = admId, admSource = admSource, admType = admType, comments = comments, docID = docID,
    docSpeciality = docSpeciality, doctorName = doctorName, finalDiagCode = finalDiagCode,
    finalDiagDesc = finalDiagDesc, firstDiagCode = firstDiagCode, firstDiagDesc = firstDiagDesc,
    healthcareProvider = healthcareProvider, hospitalizedDays = hospitalizedDays,
    hospitalizedEndDate = hospitalizedEndDate, hospitalizedStartDate = hospitalizedStartDate,
    outcomeDesc = outcomeDesc, referDocId = referDocId, referDocName = referDocName,
    referDocSpeciality = referDocSpeciality, referHealthcareProvider = referHealthcareProvider
)

fun HospitalizationEntity.toDomain() = PatientHospitalizationsDN(
    admId = admId, admSource = admSource, admType = admType, comments = comments, docID = docID,
    docSpeciality = docSpeciality, doctorName = doctorName, finalDiagCode = finalDiagCode,
    finalDiagDesc = finalDiagDesc, firstDiagCode = firstDiagCode, firstDiagDesc = firstDiagDesc,
    healthcareProvider = healthcareProvider, hospitalizedDays = hospitalizedDays,
    hospitalizedEndDate = hospitalizedEndDate, hospitalizedStartDate = hospitalizedStartDate,
    outcomeDesc = outcomeDesc, referDocId = referDocId, referDocName = referDocName,
    referDocSpeciality = referDocSpeciality, referHealthcareProvider = referHealthcareProvider
)

fun PatientVisitDN.toEntity(natCode: String) = PatientVisitEntity(
    natCode = natCode,
    comments = comments, diagCode = diagCode, diagDesc = diagDesc, docID = docID,
    docSpeciality = docSpeciality, docSpecialityCode = docSpecialityCode, doctorName = doctorName,
    healthcareProvider = healthcareProvider, serviceName = serviceName,
    serviceProvideType = serviceProvideType, serviceResult = serviceResult,
    sourceSystem = sourceSystem, visitDate = visitDate, visitType = visitType
)

fun PatientVisitEntity.toDomain() = PatientVisitDN(
    comments = comments, diagCode = diagCode, diagDesc = diagDesc, docID = docID,
    docSpeciality = docSpeciality, docSpecialityCode = docSpecialityCode, doctorName = doctorName,
    healthcareProvider = healthcareProvider, serviceName = serviceName,
    serviceProvideType = serviceProvideType, serviceResult = serviceResult,
    sourceSystem = sourceSystem, visitDate = visitDate, visitType = visitType
)

fun PatientLabDN.toEntity(natCode: String) = PatientLabEntity(
    natCode = natCode,
    deliveredQty = deliveredQty, diagCode = diagCode, diagDesc = diagDesc,
    docSpeciality = docSpeciality, docSpecialityCode = docSpecialityCode, doctorName = doctorName,
    examName = examName, healthcareProvider = healthcareProvider, itemComments = itemComments,
    objectId = objectId, prescribedQty = prescribedQty, resultDesc = resultDesc,
    resultValue = resultValue, serviceProvideType = serviceProvideType,
    sourceSystem = sourceSystem, visitDate = visitDate, visitType = visitType
)

fun PatientLabEntity.toDomain() = PatientLabDN(
    deliveredQty = deliveredQty, diagCode = diagCode, diagDesc = diagDesc,
    docSpeciality = docSpeciality, docSpecialityCode = docSpecialityCode, doctorName = doctorName,
    examName = examName, healthcareProvider = healthcareProvider, itemComments = itemComments,
    objectId = objectId, prescribedQty = prescribedQty, resultDesc = resultDesc,
    resultValue = resultValue, serviceProvideType = serviceProvideType,
    sourceSystem = sourceSystem, visitDate = visitDate, visitType = visitType
)

fun PatientImagingDN.toEntity(natCode: String) = PatientImagingEntity(
    natCode = natCode,
    deliverStatus = deliverStatus, diagCode = diagCode, diagDesc = diagDesc,
    docSpeciality = docSpeciality, doctorName = doctorName, healthcareProvider = healthcareProvider,
    imagingName = imagingName, itemComments = itemComments, modality = modality,
    objectId = objectId, resultDesc = resultDesc, visitDate = visitDate, visitType = visitType
)

fun PatientImagingEntity.toDomain() = PatientImagingDN(
    deliverStatus = deliverStatus, diagCode = diagCode, diagDesc = diagDesc,
    docSpeciality = docSpeciality, doctorName = doctorName, healthcareProvider = healthcareProvider,
    imagingName = imagingName, itemComments = itemComments, modality = modality,
    objectId = objectId, resultDesc = resultDesc, visitDate = visitDate, visitType = visitType
)
