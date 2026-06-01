package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class SubDominant(
    @SerialName("id") val id: Int? = null,
    @SerialName("bailExpireControl") val bailExpireControl: Any? = null,
    @SerialName("bailType") val bailType: BailType? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("dateOfExpire") val dateOfExpire: Any? = null,
    @SerialName("dependentType") val dependentType: Any? = null,
    @SerialName("howManyChildrenBefore") val howManyChildrenBefore: Any? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("officeRelation") val officeRelation: Boolean? = null,
    @SerialName("parentInsuranceId") val parentInsuranceId: String? = null,
    @SerialName("personalOfMainAudience") val personalOfMainAudience: Any? = null,
    @SerialName("relationWithTamin") val relationWithTamin: Int? = null,
)
