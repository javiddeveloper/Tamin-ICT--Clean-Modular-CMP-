package com.tamin.taminhamrah.model.personal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationWithTaminDTO(
    @SerialName("id") val id: Int? = null,
    @SerialName("relationDescription") val relationDescription: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("statusDate") val statusDate: String? = null,
    @SerialName("baseRelationType") val baseRelationType: BaseTypeDTO? = null,
    @SerialName("baseServiceType") val baseServiceType: BaseTypeDTO? = null,
    @SerialName("baseTendency") val baseTendency: BaseTypeDTO? = null,
    @SerialName("baseAudienceType") val baseAudienceType: BaseTypeDTO? = null,
    @SerialName("baseInsuranceType") val baseInsuranceType: BaseTypeDTO? = null,
)
