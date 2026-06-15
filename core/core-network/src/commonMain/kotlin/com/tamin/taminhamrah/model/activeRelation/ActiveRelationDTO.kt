package com.tamin.taminhamrah.model.activeRelation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActiveRelationDTO (
    @SerialName("lastName") val lastName: String? = null,
    @SerialName("relationWithTaminId") val relationWithTaminId: Int? = null,
    @SerialName("endDate") val endDate: String? = null,
    @SerialName("workshopName") val workshopName: String? = null,
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("birthDate") val birthDate: String? = null,
    @SerialName("parentId") val parentId: String? = null,
    @SerialName("firstName") val firstName: String? = null,
    @SerialName("nationalId") val nationalId: String? = null,
    @SerialName("organization") val organization: Organization? = null,
    @SerialName("relationWithTamin") val relationWithTamin:  RelationWithTaminInfo? = null,
    @SerialName("workshopId") val workshopId: String? = null,
    @SerialName("ageFlag") val ageFlag: Int? = null,
    @SerialName("startDate") val startDate: String? = null

)
