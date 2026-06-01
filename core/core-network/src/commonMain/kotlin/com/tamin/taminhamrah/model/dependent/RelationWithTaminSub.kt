package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class RelationWithTaminSub(
    @SerialName("baseAudienceType") val baseAudienceType: BaseAudienceType? = null,
    @SerialName("baseInsuranceType") val baseInsuranceType: Any? = null,
    @SerialName("baseRelationType") val baseRelationType: BaseRelationType? = null,
    @SerialName("baseServiceType") val baseServiceType: Any? = null,
    @SerialName("baseTendency") val baseTendency: BaseTendency? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("creationTime") val creationTime: Any? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Any? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: Any? = null,
    @SerialName("relationDescription") val relationDescription: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("statusDate") val statusDate: Any? = null,
)
