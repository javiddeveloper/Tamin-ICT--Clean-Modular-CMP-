package com.tamin.taminhamrah.model.occurrence

data class OccurrenceSubmitRequestDN(
    val birthDate: String,
    val workshopId: String,
    val employerName: String,
    val employerPhone: String,
    val workshopAddress: String,
    val workshopPostalCode: String,
    val workshopPhone: String,
    val employmentDate: String,
    val maritalStatus: String,
    val jobTitle: String,
    val workLocation: String,
    val transportation: String,
    val workStartTime: String,
    val workEndTime: String,
    val homeAddress: String,
    val homePhone: String,
    val homePostalCode: String,
    val accidentDate: String,
    val accidentTime: String,
    val accidentOutcomeId: String,
    val exactLocation: String,
    val description: String,
    val documents: List<OccurrenceUploadedDocDN>,
)

data class OccurrenceUploadedDocDN(
    val typeId: Int,
    val typeName: String,
    val fileName: String,
    val guid: String,
)
