package com.tamin.taminhamrah.model.treatment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DependantUserUnderEighteenDTO(
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTaminDTO? = null,
    @SerialName("id") val id: Long? = null
)

@Serializable
data class RelationWithTaminDTO(
    @SerialName("personal") val personal: PersonalDTO? = null
)

@Serializable
data class PersonalDTO(
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null
)

@Serializable
data class TreatmentCostDTO(
    @SerialName("accountNumber") val accountNumber: String? = null,
    @SerialName("bimeCode") val bimeCode: String? = null,
    @SerialName("datePaz") val datePaz: String? = null,
    @SerialName("famil") val famil: String? = null,
    @SerialName("healthcenterName") val healthcenterName: String? = null,
    @SerialName("mainNational") val mainNational: String? = null,
    @SerialName("maliCode") val maliCode: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("nameAsli") val nameAsli: String? = null,
    @SerialName("nameFamil") val nameFamil: String? = null,
    @SerialName("noPazir") val noPazir: String? = null,
    @SerialName("payNatCode") val payNatCode: String? = null,
    @SerialName("payOtherService") val payOtherService: String? = null,
    @SerialName("payPrice") val payPrice: String? = null,
    @SerialName("payService") val payService: String? = null,
    @SerialName("payStatus") val payStatus: String? = null,
    @SerialName("payType") val payType: String? = null,
    @SerialName("province") val province: String? = null,
    @SerialName("rahgiriCode") val rahgiriCode: String? = null,
    @SerialName("releaseDate") val releaseDate: String? = null,
    @SerialName("repId") val repId: Int? = null,
    @SerialName("serviceDate") val serviceDate: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDesc") val statusDesc: String? = null,
    @SerialName("payStatusDesc") val payStatusDesc: String? = null,
    @SerialName("returnReason") val returnReason: String? = null
)
