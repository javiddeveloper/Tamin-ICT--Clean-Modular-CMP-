package com.tamin.taminhamrah.feature.healthProfile.ui.model

data class PatientGeneralMock(
    val patientID: Long,
    val patientName: String,
    val patientFamily: String,
    val patientFather: String,
    val patientAge: String,
    val patientBirthDate: String,
    val patientGender: String,
    val patientNatCode: String,
    val patientMobile: String,
    val patientBloodGroup: String,
    val patientHeight: Double,
    val patientWeight: Double,
    val patientBMI: Double,
    val patientAddress: String,
    val emergencyName: String,
    val emergencyFamily: String,
    val emergencyMobile: String,
    val emergencyRelation: String
)

data class PatientSelfDeclarativeMock(
    val smokingStatusTitle: String,
    val smokingDesc: String,
    val alcoholUsageTitle: String,
    val alcoholDesc: String,
    val exerciseFreqTitle: String,
    val exerciseDesc: String,
    val substanceUsageTitle: String
)

data class PatientDrugAllergyMock(
    val drugId: Long,
    val drugName: String,
    val allergyComments: String
)
