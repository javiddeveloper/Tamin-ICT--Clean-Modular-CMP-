package com.tamin.taminhamrah.model.occurrence

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WorkshopItemDTO(
    @SerialName("id") val id: String? = null,
    @SerialName("workshopCode") val workshopCode: String? = null,
    @SerialName("branchCode") val branchCode: String? = null,
    @SerialName("workshopName") val name: String? = null,
    @SerialName("employerName") val employerName: String? = null,
    @SerialName("employerPhone") val employerPhone: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("postalCode") val postalCode: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("nation") val nation: NationDTO? = null,
)

@Serializable
data class NationDTO(
    @SerialName("nationCode") val nationCode: String? = null,
    @SerialName("nationDesc") val nationDesc: String? = null,
)
