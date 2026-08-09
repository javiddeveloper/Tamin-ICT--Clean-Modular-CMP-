package com.tamin.taminhamrah.model.health

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class PatientGeneralPR(
    val ptientID: Int? = null,
    val patientName: String? = null,
    val patientFamily: String? = null,
    val patientNatCode: String? = null,
    val patientAge: String? = null,
    val patientGender: String? = null,
    val patientBirthDate: String? = null,
    val patientMobile: String? = null,
    val patientAddress: String? = null,
    val patientFather: String? = null,
    val emergencyAddress: String? = null,
    val emergencyArea: String? = null,
    val emergencyCity: String? = null,
    val emergencyCityCode: Int? = null,
    val emergencyEmail: String? = null,
    val emergencyFamily: String? = null,
    val emergencyMobile: String? = null,
    val emergencyName: String? = null,
    val emergencyProvince: String? = null,
    val emergencyProvinceCode: Int? = null,
    val emergencyRelation: String? = null,
    val emergencyRelationshipCode: Int? = null,
    val lastUpdateDate: String? = null,
    val lastVisitDate: String? = null,
    val patientArea: String? = null,
    val patientBMI: String? = null,
    val patientBloodGroup: String? = null,
    val patientBloodGroupCode: Int? = null,
    val patientCitizenship: String? = null,
    val patientCity: String? = null,
    val patientCityCode: Int? = null,
    val patientEmail: String? = null,
    val patientGenderCode: Int? = null,
    val patientHeight: String? = null,
    val patientInsurance: String? = null,
    val patientInsuranceCode: Int? = null,
    val patientJob: String? = null,
    val patientMarriage: String? = null,
    val patientMarriageCode: Int? = null,
    val patientNationality: String? = null,
    val patientProvince: String? = null,
    val patientProvinceCode: Int? = null,
    val patientWeight: String? = null
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
