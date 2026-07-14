package com.tamin.taminhamrah.model.treatment

data class DeservedTreatmentDN(
    val birthDate: String?,
    val brhCode: String?,
    val brhName: String?,
    val dependenceType: String?,
    val fatherName: String?,
    val feranshiz: String?,
    val firstName: String?,
    val gender: String?,
    val healthBookletDate: Long?,
    val id: Int?,
    val idNumber: String?,
    val insuranceType: String?,
    val lastBookletDate: String?,
    val lastName: String?,
    val natCode: String?,
    val nationalId: String?,
    val parentRisuid: String?,
    val provinceCode: String?,
    val provinceName: String?,
    val regWorkshopId: String?,
    val regWorkshopName: String?,
    val risuid: String?,
    val message: String?,
    val illness: String?,
    val trackingCode: String?
)

data class DependantUserUnderEighteenDN(
    val firstName: String?,
    val lastName: String?,
    val nationalId: String?,
    val id: Long?
)
