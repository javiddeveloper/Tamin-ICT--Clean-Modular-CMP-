package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OccurrenceRequestDTO(
    @SerialName("birthDate") val birthDate: String,
    @SerialName("workshopId") val workshopId: String,
    @SerialName("employerName") val employerName: String,
    @SerialName("employerPhone") val employerPhone: String,
    @SerialName("workshopAddress") val workshopAddress: String,
    @SerialName("workshopPostalCode") val workshopPostalCode: String,
    @SerialName("workshopPhone") val workshopPhone: String,
    @SerialName("employmentDate") val employmentDate: String,
    @SerialName("maritalStatus") val maritalStatus: String,
    @SerialName("jobTitle") val jobTitle: String,
    @SerialName("workLocation") val workLocation: String,
    @SerialName("transportation") val transportation: String,
    @SerialName("workStartTime") val workStartTime: String,
    @SerialName("workEndTime") val workEndTime: String,
    @SerialName("homeAddress") val homeAddress: String,
    @SerialName("homePhone") val homePhone: String,
    @SerialName("homePostalCode") val homePostalCode: String,
    @SerialName("accidentDate") val accidentDate: String,
    @SerialName("accidentTime") val accidentTime: String,
    @SerialName("accidentOutcomeId") val accidentOutcomeId: String,
    @SerialName("exactLocation") val exactLocation: String,
    @SerialName("description") val description: String,
    @SerialName("documents") val documents: List<OccurrenceDocumentDTO>,
)
