package com.tamin.taminhamrah.model.treatment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeservedTreatmentDTO(
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("brhCode") val brhCode: String? = null,
    @SerialName("brhName") val brhName: String? = null,
    @SerialName("dependenceType") val dependenceType: String? = null,
    @SerialName("fatherName") val fatherName: String? = null,
    @SerialName("feranshiz") val feranshiz: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("gender") val gender: String? = null,
    @SerialName("healthBookletDate") val healthBookletDate: Long? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("idNumber") val idNumber: String? = null,
    @SerialName("insuranceType") val insuranceType: String? = null,
    @SerialName("lastBookletDate") val lastBookletDate: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("natCode") val natCode: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("parentRisuid") val parentRisuid: String? = null,
    @SerialName("provinceCode") val provinceCode: String? = null,
    @SerialName("provinceName") val provinceName: String? = null,
    @SerialName("regWorkshopId") val regWorkshopId: String? = null,
    @SerialName("regWorkshopName") val regWorkshopName: String? = null,
    @SerialName("risuid") val risuid: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("finalDesc") val finalDesc: String? = null,
    @SerialName("illness") val illness: String? = null,
    @SerialName("trackingCode") val trackingCode: String? = null
)
