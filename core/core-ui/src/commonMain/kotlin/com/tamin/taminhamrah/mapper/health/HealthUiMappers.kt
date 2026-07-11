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
