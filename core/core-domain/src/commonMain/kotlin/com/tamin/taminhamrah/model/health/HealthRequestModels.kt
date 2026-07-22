package com.tamin.taminhamrah.model.health

/**
 * Domain-layer request models for POST/mutation operations.
 * Decoupled from network DTOs so the domain layer stays network-agnostic.
 */

data class UpdatePatientRequest(
    val patientID: Int?,
    val patientNatCode: String?,
    val patientMobile: String?,
    val patientEmail: String?,
    val patientAddress: String?,
    val patientArea: String?,
    val patientCityID: Int?,
    val patientBloodGroup: Int?,
    val patientMarriage: Int?,
    val patientJob: String?,
    val patientHeight: Int?,
    val patientWeight: Int?,
    val patientCitizenship: String?,
    val patientNationality: String?,
    val patientInsurance: Int?,
    val emergencyName: String?,
    val emergencyFamily: String?,
    val emergencyMobile: String?,
    val emergencyEmail: String?,
    val emergencyRelation: Int?,
    val emergencyAddress: String?,
    val emergencyArea: String?,
    val emergencyCityID: Int?
)

data class AddSelfDeclarativeRequest(
    val natCode: String?,
    val patientID: Int?,
    val smoking: Int?,
    val smokeDesc: String?,
    val alcoholUse: Int?,
    val alcoholUseDesc: String?,
    val substanceUse: Int?,
    val substanceUseDesc: String?,
    val exerciseFrequency: Int?,
    val exerciseDesc: String?
)

data class UpdateSelfDeclarativeRequest(
    val patientID: Int?,
    val objectID: Int?,
    val smoking: Int?,
    val smokeDesc: String?,
    val alcoholUse: Int?,
    val alcoholUseDesc: String?,
    val substanceUse: Int?,
    val substanceUseDesc: String?,
    val exerciseFrequency: Int?,
    val exerciseDesc: String?
)

data class SyncIllnessSelfDeclarativesRequest(
    val natCode: String?,
    val patientID: Int?,
    val illnessSelfDeclareList: List<IllnessSelfDeclareRequest>?
)

data class IllnessSelfDeclareRequest(
    val illnessID: Int?,
    val relation: Int?,
    val illnessComments: String?
)

data class SyncDrugAllergiesRequest(
    val natCode: String?,
    val patientID: Int?,
    val drugAllergyList: List<DrugAllergyRequest>?
)

data class DrugAllergyRequest(
    val drugId: Int?,
    val allergyComments: String?
)
