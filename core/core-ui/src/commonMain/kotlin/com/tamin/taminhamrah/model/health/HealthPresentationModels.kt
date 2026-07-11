package com.tamin.taminhamrah.model.health

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
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

@Immutable
@Serializable
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

@Immutable
@Serializable
data class DrugItemAllergiesPR(
    val allergyComments: String?,
    val drugId: Int?,
    val drugName: String?
)

@Immutable
@Serializable
data class PatientHospitalizationsPR(
    val admId: Int?,
    val admSource: String?,
    val admType: String?,
    val comments: String?,
    val docID: String?,
    val docSpeciality: String?,
    val doctorName: String?,
    val finalDiagCode: String?,
    val finalDiagDesc: String?,
    val firstDiagCode: String?,
    val firstDiagDesc: String?,
    val healthcareProvider: String?,
    val hospitalizedDays: Int?,
    val hospitalizedEndDate: String?,
    val hospitalizedStartDate: String?,
    val outcomeDesc: String?,
    val referDocId: String?,
    val referDocName: String?,
    val referDocSpeciality: String?,
    val referHealthcareProvider: String?
)

@Immutable
@Serializable
data class PatientVisitPR(
    val comments: String?,
    val diagCode: String?,
    val diagDesc: String?,
    val docID: String?,
    val docSpeciality: String?,
    val docSpecialityCode: String?,
    val doctorName: String?,
    val healthcareProvider: String?,
    val serviceName: String?,
    val serviceProvideType: String?,
    val serviceResult: String?,
    val sourceSystem: Int?,
    val visitDate: String?,
    val visitType: String?
)

@Immutable
@Serializable
data class PatientLabPR(
    val deliveredQty: Int?,
    val diagCode: String?,
    val diagDesc: String?,
    val docSpeciality: String?,
    val docSpecialityCode: String?,
    val doctorName: String?,
    val examName: String?,
    val healthcareProvider: String?,
    val itemComments: String?,
    val objectId: Int?,
    val prescribedQty: Int?,
    val resultDesc: String?,
    val resultValue: String?,
    val serviceProvideType: Int?,
    val sourceSystem: Int?,
    val visitDate: String?,
    val visitType: String?
)

@Immutable
@Serializable
data class PatientImagingPR(
    val deliverStatus: String?,
    val diagCode: String?,
    val diagDesc: String?,
    val docSpeciality: String?,
    val doctorName: String?,
    val healthcareProvider: String?,
    val imagingName: String?,
    val itemComments: String?,
    val modality: String?,
    val objectId: Int?,
    val resultDesc: String?,
    val visitDate: String?,
    val visitType: String?
)
