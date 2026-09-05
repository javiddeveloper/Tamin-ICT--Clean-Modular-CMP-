package com.tamin.taminhamrah.model.orotezProtez

import com.tamin.taminhamrah.tools.ErrorCarrier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InsuredPersonListDTO(
    @SerialName("total") val total: String? = null,
    @SerialName("list") val list: List<InsuredPersonDTO>? = null,
    @SerialName("message") override val message: String? = null,
    @SerialName("cause") override val cause: String? = null,
) : ErrorCarrier

@Serializable
data class InsuredPersonDTO(
    @SerialName("risuId") val risuId: String? = null,
    @SerialName("risuLName") val lastName: String? = null,
    @SerialName("risuFname") val firstName: String? = null,
    @SerialName("nationCode") val nationCode: String? = null,
    @SerialName("risuIdNo") val birthCertificateNumber: String? = null,
    @SerialName("cityName") val cityName: String? = null,
    @SerialName("brithDate") val birthDate: String? = null,
    @SerialName("relationShip") val relationship: String? = null,
    @SerialName("relationShipCode") val relationshipCode: String? = null,
    @SerialName("bletenddate") val bookletValidUntil: String? = null,
)
