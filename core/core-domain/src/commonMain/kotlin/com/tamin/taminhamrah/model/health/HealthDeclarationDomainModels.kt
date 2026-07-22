package com.tamin.taminhamrah.model.health

data class PatientGeneralDN(
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

data class PatientSelfDeclarativeDN(
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

data class DrugItemAllergiesDN(
    val allergyComments: String?,
    val drugId: Int?,
    val drugName: String?
)

data class PatientHospitalizationsDN(
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

data class PatientVisitDN(
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

data class PatientLabDN(
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

data class PatientImagingDN(
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

// --- Location ---

data class ProvinceItemDN(
    val id: Int?,
    val name: String?
)

data class ProvinceCityItemDN(
    val id: Int?,
    val name: String?
)

// --- Lookup ---

data class BloodGroupDN(
    val key: Int?,
    val value: String?
)

data class MaritalStatusDN(
    val key: Int?,
    val value: String?
)

data class SmokingStatusDN(
    val key: Int?,
    val value: String?
)

// --- Illness ---

data class IllnessItemDN(
    val illnessID: Int?,
    val illnessDesc: String?
)

data class SelfDeclarableIllnessGroupDN(
    val groupId: Int?,
    val groupTitle: String?,
    val forFamily: Boolean?,
    val illnessList: List<IllnessItemDN>?
)

// --- Drug ---

data class DrugItemDN(
    val drugID: Int?,
    val drugName: String?
)

// --- Mutation results ---

data class UpdatePatientDN(
    val ptientID: Int?,
    val patientNatCode: String?,
    val patientName: String?,
    val patientFamily: String?,
    val patientMobile: String?,
    val patientAddress: String?,
    val patientBloodGroup: String?,
    val patientBloodGroupCode: Int?,
    val patientMarriage: String?,
    val patientMarriageCode: Int?,
    val patientJob: String?,
    val patientHeight: String?,
    val patientWeight: String?,
    val patientBMI: String?,
    val patientCitizenship: String?,
    val patientCity: String?,
    val patientCityCode: Int?,
    val patientProvince: String?,
    val patientProvinceCode: Int?,
    val patientEmail: String?,
    val patientArea: String?,
    val emergencyName: String?,
    val emergencyFamily: String?,
    val emergencyMobile: String?,
    val emergencyRelation: String?,
    val emergencyRelationshipCode: Int?,
    val emergencyAddress: String?,
    val emergencyCity: String?,
    val emergencyCityCode: Int?,
    val emergencyProvince: String?,
    val emergencyProvinceCode: Int?,
    val emergencyEmail: String?,
    val emergencyArea: String?,
    val lastUpdateDate: String?
)

data class AddSelfDeclarativeDN(
    val objectID: Int?,
    val smokingStatus: Int?,
    val smokingStatusTitle: String?,
    val smokingDesc: String?,
    val alcoholUsage: Int?,
    val alcoholUsageTitle: String?,
    val alcoholDesc: String?,
    val substanceUsage: Int?,
    val substanceUsageTitle: String?,
    val substanceDesc: String?,
    val exerciseFreq: Int?,
    val exerciseFreqTitle: String?,
    val exerciseDesc: String?,
    val lastUpdateDate: String?
)

data class UpdateSelfDeclarativeDN(
    val objectID: Int?,
    val smokingStatus: Int?,
    val smokingStatusTitle: String?,
    val smokingDesc: String?,
    val alcoholUsage: Int?,
    val alcoholUsageTitle: String?,
    val alcoholDesc: String?,
    val substanceUsage: Int?,
    val substanceUsageTitle: String?,
    val substanceDesc: String?,
    val exerciseFreq: Int?,
    val exerciseFreqTitle: String?,
    val exerciseDesc: String?,
    val lastUpdateDate: String?
)

data class SyncResultDN(val data: String?)
