package com.tamin.taminhamrah.model.treatment

data class MedicalAuthoritiesDN(
    val supportType: String?,
    val treatmentCenter: String?,
    val confirmInBranch: String?,
    val confirmStatus: String?,
    val insuranceNumber: String?,
    val nationalCode: String?,
    val firstName: String?,
    val lastName: String?,
    val outpatientRestStartDate: String?,
    val outpatientRestEndDate: String?,
    val numberOfOutpatientDays: String?,
    val hospitalizationStartDate: String?,
    val hospitalizationEndDate: String?,
    val numberOfHospitalizationDays: String?,
    val description: String?,
    val branch: String?,
    val fromDateNotConfirm: String?,
    val toDateNotConfirm: String?
)
