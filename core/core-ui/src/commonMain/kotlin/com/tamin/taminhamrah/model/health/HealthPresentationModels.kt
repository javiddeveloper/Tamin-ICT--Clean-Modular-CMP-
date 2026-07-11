package com.tamin.taminhamrah.model.health

data class PatientGeneralPR(
    val ptientID: Int?,
    val patientName: String?,
    val patientFamily: String?,
    val patientNatCode: String?,
    val patientAge: String?,
    val patientGender: String?,
    val patientBirthDate: String?,
    val patientMobile: String?,
    val patientAddress: String?,
    val patientFather: String?
)

data class PatientSelfDeclarativePR(
    val alcoholDesc: String?,
    val alcoholUsage: Int?,
    val alcoholUsageTitle: String?,
    val exerciseDesc: String?,
    val exerciseFreq: Int?,
    val exerciseFreqTitle: String?,
    val lastUpdateDate: String?,
    val objectID: Int?,
    val smokingDesc: String?,
    val smokingStatus: Int?,
    val smokingStatusTitle: String?,
    val substanceDesc: String?,
    val substanceUsage: Int?,
    val substanceUsageTitle: String?
)

data class DrugItemAllergiesPR(
    val allergyComments: String?,
    val drugId: Int?,
    val drugName: String?
)
