package com.tamin.taminhamrah.model.health

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddSelfDeclarativeRequestDTO(
    @SerialName("alcoholUse") val alcoholUse: Int? = null,
    @SerialName("alcoholUseDesc") val alcoholUseDesc: String? = null,
    @SerialName("exerciseDesc") val exerciseDesc: String? = null,
    @SerialName("exerciseFrequency") val exerciseFrequency: Int? = null,
    @SerialName("natCode") val natCode: String? = null,
    @SerialName("patientID") val patientID: Int? = null,
    @SerialName("smokeDesc") val smokeDesc: String? = null,
    @SerialName("smoking") val smoking: Int? = null,
    @SerialName("substanceUse") val substanceUse: Int? = null,
    @SerialName("substanceUseDesc") val substanceUseDesc: String? = null
)

@Serializable
data class AddSelfDeclarativeDTO(
    @SerialName("alcoholDesc") val alcoholDesc: String? = null,
    @SerialName("alcoholUsage") val alcoholUsage: Int? = null,
    @SerialName("alcoholUsageTitle") val alcoholUsageTitle: String? = null,
    @SerialName("exerciseDesc") val exerciseDesc: String? = null,
    @SerialName("exerciseFreq") val exerciseFreq: Int? = null,
    @SerialName("exerciseFreqTitle") val exerciseFreqTitle: String? = null,
    @SerialName("lastUpdateDate") val lastUpdateDate: String? = null,
    @SerialName("objectID") val objectID: Int? = null,
    @SerialName("smokingDesc") val smokingDesc: String? = null,
    @SerialName("smokingStatus") val smokingStatus: Int? = null,
    @SerialName("smokingStatusTitle") val smokingStatusTitle: String? = null,
    @SerialName("substanceDesc") val substanceDesc: String? = null,
    @SerialName("substanceUsage") val substanceUsage: Int? = null,
    @SerialName("substanceUsageTitle") val substanceUsageTitle: String? = null
)

@Serializable
data class AllergicDrugsDTO(
    @SerialName("list") val list: List<DrugItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class DrugItemDTO(
    @SerialName("drugID") val drugID: Int? = null,
    @SerialName("drugName") val drugName: String? = null
)

@Serializable
data class BloodGroupDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class SelfDeclarableIllnessesByGroupDTO(
    @SerialName("list") val list: List<SelfDeclarableIllnessGroupDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class SelfDeclarableIllnessGroupDTO(
    @SerialName("groupId") val groupId: Int? = null,
    @SerialName("groupTitle") val groupTitle: String? = null,
    @SerialName("forFamilly") val forFamily: Boolean? = null,
    @SerialName("illnessList") val illnessList: List<IllnessItemDTO>? = null
)

@Serializable
data class GenderTypeDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class DrugItemAllergiesDTO(
    @SerialName("allergyComments") val allergyComments: String? = null,
    @SerialName("drugId") val drugId: Int? = null,
    @SerialName("drugName") val drugName: String? = null
)

@Serializable
data class LabDeliveryDTO(
    @SerialName("deliverDate") val deliverDate: String? = null,
    @SerialName("labName") val labName: String? = null,
    @SerialName("testCode") val testCode: String? = null,
    @SerialName("testName") val testName: String? = null
)

@Serializable
data class DrugDeliveryDTO(
    @SerialName("drugStoreName") val drugStoreName: String? = null,
    @SerialName("deliverDate") val deliverDate: String? = null,
    @SerialName("drugCode") val drugCode: String? = null,
    @SerialName("drugName") val drugName: String? = null,
    @SerialName("deliverQuantity") val deliverQuantity: Int? = null
)

@Serializable
data class PatientDrugDTO(
    @SerialName("drugId") val drugId: Int? = null,
    @SerialName("drugName") val drugName: String? = null,
    @SerialName("useDirections") val useDirections: String? = null,
    @SerialName("prescribedQty") val prescribedQty: Int? = null,
    @SerialName("deliveredQty") val deliveredQty: Int? = null,
    @SerialName("itemComments") val itemComments: String? = null,
    @SerialName("visitDate") val visitDate: String? = null,
    @SerialName("visitType") val visitType: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("docSpeciality") val docSpeciality: String? = null,
    @SerialName("diagCode") val diagCode: String? = null,
    @SerialName("diagDesc") val diagDesc: String? = null,
    @SerialName("sourceSystem") val sourceSystem: Int? = null,
    @SerialName("serviceProvideType") val serviceProvideType: Int? = null,
    @SerialName("docSpecialityCode") val docSpecialityCode: String? = null
)

@Serializable
data class PatientHospitalizationsDTO(
    @SerialName("admId") val admId: Int? = null,
    @SerialName("admSource") val admSource: String? = null,
    @SerialName("admType") val admType: String? = null,
    @SerialName("comments") val comments: String? = null,
    @SerialName("docID") val docID: String? = null,
    @SerialName("docSpeciality") val docSpeciality: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("finalDiagCode") val finalDiagCode: String? = null,
    @SerialName("finalDiagDesc") val finalDiagDesc: String? = null,
    @SerialName("firstDiagCode") val firstDiagCode: String? = null,
    @SerialName("firstDiagDesc") val firstDiagDesc: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("hospitalizedDays") val hospitalizedDays: Int? = null,
    @SerialName("hospitalizedEndDate") val hospitalizedEndDate: String? = null,
    @SerialName("hospitalizedStartDate") val hospitalizedStartDate: String? = null,
    @SerialName("outcomeDesc") val outcomeDesc: String? = null,
    @SerialName("referDocId") val referDocId: String? = null,
    @SerialName("referDocName") val referDocName: String? = null,
    @SerialName("referDocSpeciality") val referDocSpeciality: String? = null,
    @SerialName("referHealthcareProvider") val referHealthcareProvider: String? = null
)

@Serializable
data class ImagingDeliveryDTO(
    @SerialName("hcpName") val hcpName: String? = null,
    @SerialName("deliverDate") val deliverDate: String? = null,
    @SerialName("serviceCode") val serviceCode: String? = null,
    @SerialName("serviceName") val serviceName: String? = null
)

@Serializable
data class PatientImagingDTO(
    @SerialName("deliverStatus") val deliverStatus: String? = null,
    @SerialName("diagCode") val diagCode: String? = null,
    @SerialName("diagDesc") val diagDesc: String? = null,
    @SerialName("docSpeciality") val docSpeciality: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("imagingName") val imagingName: String? = null,
    @SerialName("itemComments") val itemComments: String? = null,
    @SerialName("modality") val modality: String? = null,
    @SerialName("objectId") val objectId: Int? = null,
    @SerialName("resultDesc") val resultDesc: String? = null,
    @SerialName("visitDate") val visitDate: String? = null,
    @SerialName("visitType") val visitType: String? = null
)

@Serializable
data class PatientPhysioDTO(
    @SerialName("deliveredSessions") val deliveredSessions: Int? = null,
    @SerialName("diagCode") val diagCode: String? = null,
    @SerialName("diagDesc") val diagDesc: String? = null,
    @SerialName("docSpeciality") val docSpeciality: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("itemComments") val itemComments: String? = null,
    @SerialName("objectId") val objectId: Int? = null,
    @SerialName("requestSessions") val requestSessions: Int? = null,
    @SerialName("serviceName") val serviceName: String? = null,
    @SerialName("visitDate") val visitDate: String? = null,
    @SerialName("visitType") val visitType: String? = null
)

@Serializable
data class PatientSurgeriesDTO(
    @SerialName("anesthesioDocId") val anesthesioDocId: String? = null,
    @SerialName("anesthesioName") val anesthesioName: String? = null,
    @SerialName("anesthesioSpeciality") val anesthesioSpeciality: String? = null,
    @SerialName("bodySite") val bodySite: String? = null,
    @SerialName("diagCode") val diagCode: String? = null,
    @SerialName("diagDesc") val diagDesc: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("itemComments") val itemComments: String? = null,
    @SerialName("objectId") val objectId: Int? = null,
    @SerialName("serviceName") val serviceName: String? = null,
    @SerialName("surgeonDocId") val surgeonDocId: String? = null,
    @SerialName("surgeonName") val surgeonName: String? = null,
    @SerialName("surgeonSpeciality") val surgeonSpeciality: String? = null,
    @SerialName("surgeryDate") val surgeryDate: String? = null,
    @SerialName("surgeryReport") val surgeryReport: String? = null
)

@Serializable
data class PhysioDeliveryDTO(
    @SerialName("deliverDate") val deliverDate: String? = null,
    @SerialName("deliverQty") val deliverQty: Int? = null,
    @SerialName("hcpName") val hcpName: String? = null,
    @SerialName("serviceCode") val serviceCode: String? = null,
    @SerialName("serviceName") val serviceName: String? = null
)

@Serializable
data class PatientSelfDeclarativeDTO(
    @SerialName("alcoholDesc") val alcoholDesc: String? = null,
    @SerialName("alcoholUsage") val alcoholUsage: Int? = null,
    @SerialName("alcoholUsageTitle") val alcoholUsageTitle: String? = null,
    @SerialName("exerciseDesc") val exerciseDesc: String? = null,
    @SerialName("exerciseFreq") val exerciseFreq: Int? = null,
    @SerialName("exerciseFreqTitle") val exerciseFreqTitle: String? = null,
    @SerialName("lastUpdateDate") val lastUpdateDate: String? = null,
    @SerialName("objectID") val objectID: Int? = null,
    @SerialName("smokingDesc") val smokingDesc: String? = null,
    @SerialName("smokingStatus") val smokingStatus: Int? = null,
    @SerialName("smokingStatusTitle") val smokingStatusTitle: String? = null,
    @SerialName("substanceDesc") val substanceDesc: String? = null,
    @SerialName("substanceUsage") val substanceUsage: Int? = null,
    @SerialName("substanceUsageTitle") val substanceUsageTitle: String? = null
)

@Serializable
data class SelfDeclarativeIllnessDTO(
    @SerialName("illnessComments") val illnessComments: String? = null,
    @SerialName("illnessDesc") val illnessDesc: String? = null,
    @SerialName("illnessId") val illnessId: Int? = null,
    @SerialName("lastUpdate") val lastUpdate: String? = null,
    @SerialName("relation") val relation: Int? = null,
    @SerialName("relationDesc") val relationDesc: String? = null
)

@Serializable
data class SmokingStatusDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class IllnessGroupDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class DeclarableIllnessesDTO(
    @SerialName("list") val list: List<IllnessItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class IllnessItemDTO(
    @SerialName("illnessDesc") val illnessDesc: String? = null,
    @SerialName("illnessID") val illnessID: Int? = null
)

@Serializable
data class MaritalStatusDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class PatientCommissionDTO(
    @SerialName("illnessName") val illnessName: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("referDate") val referDate: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("commissionType") val commissionType: String? = null
)

@Serializable
data class PatientGeneralDTO(
    @SerialName("emergencyAddress") val emergencyAddress: String? = null,
    @SerialName("emergencyArea") val emergencyArea: String? = null,
    @SerialName("emergencyCity") val emergencyCity: String? = null,
    @SerialName("emergencyCityCode") val emergencyCityCode: Int? = null,
    @SerialName("emergencyEmail") val emergencyEmail: String? = null,
    @SerialName("emergencyFamily") val emergencyFamily: String? = null,
    @SerialName("emergencyMobile") val emergencyMobile: String? = null,
    @SerialName("emergencyName") val emergencyName: String? = null,
    @SerialName("emergencyProvince") val emergencyProvince: String? = null,
    @SerialName("emergencyProvinceCode") val emergencyProvinceCode: Int? = null,
    @SerialName("emergencyRelation") val emergencyRelation: String? = null,
    @SerialName("emergencyRelationshipCode") val emergencyRelationshipCode: Int? = null,
    @SerialName("lastUpdateDate") val lastUpdateDate: String? = null,
    @SerialName("lastVisitDate") val lastVisitDate: String? = null,
    @SerialName("patientAddress") val patientAddress: String? = null,
    @SerialName("patientAge") val patientAge: String? = null,
    @SerialName("patientArea") val patientArea: String? = null,
    @SerialName("patientBMI") val patientBMI: String? = null,
    @SerialName("patientBirthDate") val patientBirthDate: String? = null,
    @SerialName("patientBloodGroup") val patientBloodGroup: String? = null,
    @SerialName("patientBloodGroupCode") val patientBloodGroupCode: Int? = null,
    @SerialName("patientCitizenship") val patientCitizenship: String? = null,
    @SerialName("patientCity") val patientCity: String? = null,
    @SerialName("patientCityCode") val patientCityCode: Int? = null,
    @SerialName("patientEmail") val patientEmail: String? = null,
    @SerialName("patientFamily") val patientFamily: String? = null,
    @SerialName("patientFather") val patientFather: String? = null,
    @SerialName("patientGender") val patientGender: String? = null,
    @SerialName("patientGenderCode") val patientGenderCode: Int? = null,
    @SerialName("patientHeight") val patientHeight: String? = null,
    @SerialName("patientInsurance") val patientInsurance: String? = null,
    @SerialName("patientInsuranceCode") val patientInsuranceCode: Int? = null,
    @SerialName("patientJob") val patientJob: String? = null,
    @SerialName("patientMarriage") val patientMarriage: String? = null,
    @SerialName("patientMarriageCode") val patientMarriageCode: Int? = null,
    @SerialName("patientMobile") val patientMobile: String? = null,
    @SerialName("patientName") val patientName: String? = null,
    @SerialName("patientNatCode") val patientNatCode: String? = null,
    @SerialName("patientNationality") val patientNationality: String? = null,
    @SerialName("patientProvince") val patientProvince: String? = null,
    @SerialName("patientProvinceCode") val patientProvinceCode: Int? = null,
    @SerialName("patientWeight") val patientWeight: String? = null,
    @SerialName("ptientID") val ptientID: Int? = null
)

@Serializable
data class PatientHealthDataDTO(
    @SerialName("list") val list: List<PatientItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class PatientItemDTO(
    @SerialName("deliverQty") val deliverQty: Int? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("doctorSpecialty") val doctorSpecialty: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("hospitalizedEndDate") val hospitalizedEndDate: String? = null,
    @SerialName("hospitalizedStartDate") val hospitalizedStartDate: String? = null,
    @SerialName("parType") val parType: String? = null,
    @SerialName("prescQty") val prescQty: Int? = null,
    @SerialName("serviceDate") val serviceDate: String? = null,
    @SerialName("serviceName") val serviceName: String? = null,
    @SerialName("serviceProvideType") val serviceProvideType: String? = null,
    @SerialName("serviceType") val serviceType: String? = null,
    @SerialName("surgeryDate") val surgeryDate: String? = null,
    @SerialName("visitDate") val visitDate: String? = null
)

@Serializable
data class PatientLabDTO(
    @SerialName("deliveredQty") val deliveredQty: Int? = null,
    @SerialName("diagCode") val diagCode: String? = null,
    @SerialName("diagDesc") val diagDesc: String? = null,
    @SerialName("docSpeciality") val docSpeciality: String? = null,
    @SerialName("docSpecialityCode") val docSpecialityCode: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("examName") val examName: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("itemComments") val itemComments: String? = null,
    @SerialName("objectId") val objectId: Int? = null,
    @SerialName("prescribedQty") val prescribedQty: Int? = null,
    @SerialName("resultDesc") val resultDesc: String? = null,
    @SerialName("resultValue") val resultValue: String? = null,
    @SerialName("serviceProvideType") val serviceProvideType: Int? = null,
    @SerialName("sourceSystem") val sourceSystem: Int? = null,
    @SerialName("visitDate") val visitDate: String? = null,
    @SerialName("visitType") val visitType: String? = null
)

@Serializable
data class PatientVisitDTO(
    @SerialName("comments") val comments: String? = null,
    @SerialName("diagCode") val diagCode: String? = null,
    @SerialName("diagDesc") val diagDesc: String? = null,
    @SerialName("docID") val docID: String? = null,
    @SerialName("docSpeciality") val docSpeciality: String? = null,
    @SerialName("docSpecialityCode") val docSpecialityCode: String? = null,
    @SerialName("doctorName") val doctorName: String? = null,
    @SerialName("healthcareProvider") val healthcareProvider: String? = null,
    @SerialName("serviceName") val serviceName: String? = null,
    @SerialName("serviceProvideType") val serviceProvideType: String? = null,
    @SerialName("serviceResult") val serviceResult: String? = null,
    @SerialName("sourceSystem") val sourceSystem: Int? = null,
    @SerialName("visitDate") val visitDate: String? = null,
    @SerialName("visitType") val visitType: String? = null
)

@Serializable
data class ProvinceCitiesDTO(
    @SerialName("list") val list: List<ProvinceCitiesItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class ProvinceCitiesItemDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class ProvincesDTO(
    @SerialName("list") val list: List<ProvinceItemDTO>? = null,
    @SerialName("total") val total: Int? = null
)

@Serializable
data class ProvinceItemDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("name") val name: String? = null
)

@Serializable
data class RelationTypeDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)

@Serializable
data class SyncDrugAllergiesRequestDTO(
    @SerialName("drugAllergyList") val drugAllergyList: List<DrugAllergyDTO>? = null,
    @SerialName("natCode") val natCode: String? = null,
    @SerialName("patientID") val patientID: Int? = null
)

@Serializable
data class DrugAllergyDTO(
    @SerialName("allergyComments") val allergyComments: String? = null,
    @SerialName("drugId") val drugId: Int? = null
)

@Serializable
data class SyncDrugAllergiesDTO(
    @SerialName("data") val data: String? = null
)

@Serializable
data class SyncIllnessesSelfDecRequestDTO(
    @SerialName("illnessSelfDeclareList") val illnessSelfDeclareList: List<IllnessSelfDeclareDTO>? = null,
    @SerialName("natCode") val natCode: String? = null,
    @SerialName("patientID") val patientID: Int? = null
)

@Serializable
data class IllnessSelfDeclareDTO(
    @SerialName("illnessComments") val illnessComments: String? = null,
    @SerialName("illnessID") val illnessID: Int? = null,
    @SerialName("relation") val relation: Int? = null
)

@Serializable
data class SyncIllnessSelfDeclarativesDTO(
    @SerialName("data") val data: String? = null
)

@Serializable
data class UpdatePatientRequestDTO(
    @SerialName("emergencyAddress") val emergencyAddress: String? = null,
    @SerialName("emergencyArea") val emergencyArea: String? = null,
    @SerialName("emergencyCityID") val emergencyCityID: Int? = null,
    @SerialName("emergencyEmail") val emergencyEmail: String? = null,
    @SerialName("emergencyFamily") val emergencyFamily: String? = null,
    @SerialName("emergencyMobile") val emergencyMobile: String? = null,
    @SerialName("emergencyName") val emergencyName: String? = null,
    @SerialName("emergencyRelation") val emergencyRelation: Int? = null,
    @SerialName("patientAddress") val patientAddress: String? = null,
    @SerialName("patientArea") val patientArea: String? = null,
    @SerialName("patientBloodGroup") val patientBloodGroup: Int? = null,
    @SerialName("patientCitizenship") val patientCitizenship: String? = null,
    @SerialName("patientCityID") val patientCityID: Int? = null,
    @SerialName("patientEmail") val patientEmail: String? = null,
    @SerialName("patientHeight") val patientHeight: Int? = null,
    @SerialName("patientID") val patientID: Int? = null,
    @SerialName("patientInsurance") val patientInsurance: Int? = null,
    @SerialName("patientJob") val patientJob: String? = null,
    @SerialName("patientMarriage") val patientMarriage: Int? = null,
    @SerialName("patientMobile") val patientMobile: String? = null,
    @SerialName("patientNatCode") val patientNatCode: String? = null,
    @SerialName("patientNationality") val patientNationality: String? = null,
    @SerialName("patientWeight") val patientWeight: Int? = null
)

@Serializable
data class UpdatePatientDTO(
    @SerialName("emergencyAddress") val emergencyAddress: String? = null,
    @SerialName("emergencyArea") val emergencyArea: String? = null,
    @SerialName("emergencyCity") val emergencyCity: String? = null,
    @SerialName("emergencyCityCode") val emergencyCityCode: Int? = null,
    @SerialName("emergencyEmail") val emergencyEmail: String? = null,
    @SerialName("emergencyFamily") val emergencyFamily: String? = null,
    @SerialName("emergencyMobile") val emergencyMobile: String? = null,
    @SerialName("emergencyName") val emergencyName: String? = null,
    @SerialName("emergencyProvince") val emergencyProvince: String? = null,
    @SerialName("emergencyProvinceCode") val emergencyProvinceCode: Int? = null,
    @SerialName("emergencyRelation") val emergencyRelation: String? = null,
    @SerialName("emergencyRelationshipCode") val emergencyRelationshipCode: Int? = null,
    @SerialName("lastUpdateDate") val lastUpdateDate: String? = null,
    @SerialName("lastVisitDate") val lastVisitDate: String? = null,
    @SerialName("patientAddress") val patientAddress: String? = null,
    @SerialName("patientAge") val patientAge: String? = null,
    @SerialName("patientArea") val patientArea: String? = null,
    @SerialName("patientBMI") val patientBMI: String? = null,
    @SerialName("patientBirthDate") val patientBirthDate: String? = null,
    @SerialName("patientBloodGroup") val patientBloodGroup: String? = null,
    @SerialName("patientBloodGroupCode") val patientBloodGroupCode: Int? = null,
    @SerialName("patientCitizenship") val patientCitizenship: String? = null,
    @SerialName("patientCity") val patientCity: String? = null,
    @SerialName("patientCityCode") val patientCityCode: Int? = null,
    @SerialName("patientEmail") val patientEmail: String? = null,
    @SerialName("patientFamily") val patientFamily: String? = null,
    @SerialName("patientFather") val patientFather: String? = null,
    @SerialName("patientGender") val patientGender: String? = null,
    @SerialName("patientGenderCode") val patientGenderCode: Int? = null,
    @SerialName("patientHeight") val patientHeight: String? = null,
    @SerialName("patientInsurance") val patientInsurance: String? = null,
    @SerialName("patientInsuranceCode") val patientInsuranceCode: Int? = null,
    @SerialName("patientJob") val patientJob: String? = null,
    @SerialName("patientMarriage") val patientMarriage: String? = null,
    @SerialName("patientMarriageCode") val patientMarriageCode: Int? = null,
    @SerialName("patientMobile") val patientMobile: String? = null,
    @SerialName("patientName") val patientName: String? = null,
    @SerialName("patientNatCode") val patientNatCode: String? = null,
    @SerialName("patientNationality") val patientNationality: String? = null,
    @SerialName("patientProvince") val patientProvince: String? = null,
    @SerialName("patientProvinceCode") val patientProvinceCode: Int? = null,
    @SerialName("patientWeight") val patientWeight: String? = null,
    @SerialName("ptientID") val ptientID: Int? = null
)

@Serializable
data class UpdateSelfDeclarativeRequestDTO(
    @SerialName("alcoholUse") val alcoholUse: Int? = null,
    @SerialName("alcoholUseDesc") val alcoholUseDesc: String? = null,
    @SerialName("exerciseDesc") val exerciseDesc: String? = null,
    @SerialName("exerciseFrequency") val exerciseFrequency: Int? = null,
    @SerialName("objectID") val objectID: Int? = null,
    @SerialName("patientID") val patientID: Int? = null,
    @SerialName("smokeDesc") val smokeDesc: String? = null,
    @SerialName("smoking") val smoking: Int? = null,
    @SerialName("substanceUse") val substanceUse: Int? = null,
    @SerialName("substanceUseDesc") val substanceUseDesc: String? = null
)

@Serializable
data class UpdateSelfDeclarativeDTO(
    @SerialName("alcoholDesc") val alcoholDesc: String? = null,
    @SerialName("alcoholUsage") val alcoholUsage: Int? = null,
    @SerialName("alcoholUsageTitle") val alcoholUsageTitle: String? = null,
    @SerialName("exerciseDesc") val exerciseDesc: String? = null,
    @SerialName("exerciseFreq") val exerciseFreq: Int? = null,
    @SerialName("exerciseFreqTitle") val exerciseFreqTitle: String? = null,
    @SerialName("lastUpdateDate") val lastUpdateDate: String? = null,
    @SerialName("objectID") val objectID: Int? = null,
    @SerialName("smokingDesc") val smokingDesc: String? = null,
    @SerialName("smokingStatus") val smokingStatus: Int? = null,
    @SerialName("smokingStatusTitle") val smokingStatusTitle: String? = null,
    @SerialName("substanceDesc") val substanceDesc: String? = null,
    @SerialName("substanceUsage") val substanceUsage: Int? = null,
    @SerialName("substanceUsageTitle") val substanceUsageTitle: String? = null
)

@Serializable
data class ActFrequencyDTO(
    @SerialName("key") val key: Int? = null,
    @SerialName("value") val value: String? = null
)
