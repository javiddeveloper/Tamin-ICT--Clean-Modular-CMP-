package com.tamin.taminhamrah.model.activeRelation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RelationWithTaminInfo(
    @SerialName("relationDescription") val relationDescription: String? = null,
    @SerialName("baseRelationType") val baseRelationType: BaseType? = null,
    @SerialName("baseServiceType") val baseServiceType: BaseType? = null,
    @SerialName("baseTendency") val baseTendency: BaseType? = null,
    @SerialName("baseAudienceType") val baseAudienceType: BaseType? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("id") val id: Int? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("baseInsuranceType") val baseInsuranceType: BaseType? = null
)
@Serializable
data class BaseType(
    @SerialName("relationTypeCode") val relationTypeCode: String? = null,
    @SerialName("relationTypeDescription") val relationTypeDescription: String? = null,
    @SerialName("id") val id: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("serviceTypeDescription") val serviceTypeDescription: String? = null,
    @SerialName("serviceTypeCode") val serviceTypeCode: String? = null,
    @SerialName("audienceTypeDescription") val audienceTypeDescription: String? = null,
    @SerialName("audienceTypeCode") val audienceTypeCode: String? = null
)
