package com.tamin.taminhamrah.feature.healthProfile.ui.mapper

import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientDrugAllergyMock
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientGeneralMock
import com.tamin.taminhamrah.feature.healthProfile.ui.model.PatientSelfDeclarativeMock
import com.tamin.taminhamrah.model.health.DrugItemAllergiesDN
import com.tamin.taminhamrah.model.health.PatientGeneralDN
import com.tamin.taminhamrah.model.health.PatientSelfDeclarativeDN

fun PatientGeneralDN.toUiMock(): PatientGeneralMock {
    return PatientGeneralMock(
        patientID = ptientID?.toLong() ?: 0L,
        patientName = patientName ?: "",
        patientFamily = patientFamily ?: "",
        patientFather = patientFather ?: "",
        patientAge = patientAge ?: "",
        patientBirthDate = patientBirthDate ?: "",
        patientGender = patientGender ?: "",
        patientNatCode = patientNatCode ?: "",
        patientMobile = patientMobile ?: "",
        patientBloodGroup =  "نامشخص",
        patientHeight =  0.0,
        patientWeight =  0.0,
        patientBMI =  0.0,
        patientAddress = patientAddress ?: "",
        emergencyName =  "",
        emergencyFamily =  "",
        emergencyMobile =  "",
        emergencyRelation =  ""
    )
}

fun PatientSelfDeclarativeDN.toUiMock(): PatientSelfDeclarativeMock {
    return PatientSelfDeclarativeMock(
        smokingStatusTitle = smokingStatusTitle ?: "نامشخص",
        smokingDesc = smokingDesc ?: "ندارد",
        alcoholUsageTitle = alcoholUsageTitle ?: "نامشخص",
        alcoholDesc = alcoholDesc ?: "ندارد",
        exerciseFreqTitle = exerciseFreqTitle ?: "نامشخص",
        exerciseDesc = exerciseDesc ?: "ندارد",
        substanceUsageTitle = substanceUsageTitle ?: "نامشخص"
    )
}

fun DrugItemAllergiesDN.toUiMock(): PatientDrugAllergyMock {
    return PatientDrugAllergyMock(
        drugId = drugId?.toLong() ?: 0L,
        drugName = drugName ?: "نامشخص",
        allergyComments = allergyComments ?: "فاقد توضیحات"
    )
}
