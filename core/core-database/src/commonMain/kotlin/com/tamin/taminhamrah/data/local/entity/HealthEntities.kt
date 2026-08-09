package com.tamin.taminhamrah.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entities backing the offline-first cache for the patient-health reads.
 * Single-record lookups are keyed by the queried [natCode]; per-patient lists
 * carry a [natCode] column and an auto-generated id, and are replaced wholesale
 * per patient on each successful refresh.
 */

@Entity(tableName = "patient_general")
data class PatientGeneralEntity(
    @PrimaryKey val natCode: String,
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

@Entity(tableName = "patient_self_declarative")
data class PatientSelfDeclarativeEntity(
    @PrimaryKey val natCode: String,
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

@Entity(tableName = "patient_drug_allergies")
data class DrugAllergyEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val natCode: String,
    val allergyComments: String?,
    val drugId: Int?,
    val drugName: String?
)

@Entity(tableName = "patient_hospitalizations")
data class HospitalizationEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val natCode: String,
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

@Entity(tableName = "patient_visits")
data class PatientVisitEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val natCode: String,
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

@Entity(tableName = "patient_labs")
data class PatientLabEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val natCode: String,
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

@Entity(tableName = "patient_imaging")
data class PatientImagingEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val natCode: String,
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
