package com.tamin.taminhamrah.model.dependent

import kotlinx.serialization.SerialName

data class RelationWithTamin(
    @SerialName("id") val id: Int? = null,
    @SerialName("branch") val branch: Any? = null,
    @SerialName("confirmed") val confirmed: Boolean? = null,
    @SerialName("createdBy") val createdBy: String? = null,
    @SerialName("creationTime") val creationTime: Long? = null,
    @SerialName("dateOfStart") val dateOfStart: Long? = null,
    @SerialName("deleted") val deleted: Boolean? = null,
    @SerialName("edited") val edited: Boolean? = null,
    @SerialName("endConfirmed") val endConfirmed: Boolean? = null,
    @SerialName("endDate") val endDate: Any? = null,
    @SerialName("endReasonType") val endReasonType: Any? = null,
    @SerialName("inspectionId") val inspectionId: Any? = null,
    @SerialName("insuranceId") val insuranceId: String? = null,
    @SerialName("is_first") val isFirst: Boolean? = null,
    @SerialName("lastModificationTime") val lastModificationTime: Long? = null,
    @SerialName("lastModifiedBy") val lastModifiedBy: String? = null,
    @SerialName("logicalControlStatus") val logicalControlStatus: Any? = null,
    @SerialName("newInsuranceId") val newInsuranceId: Boolean? = null,
    @SerialName("organizationId") val organizationId: String? = null,
    @SerialName("personal") val personal: Personal? = null,
    @SerialName("printed") val printed: Boolean? = null,
    @SerialName("recognizeDate") val recognizeDate: Long? = null,
    @SerialName("recognizeMethod") val recognizeMethod: Any? = null,
    @SerialName("relationWithTamin") val relationWithTamin: RelationWithTaminSub? = null,
    @SerialName("request") val request: Any? = null,
    @SerialName("subDominant") val subDominant: SubDominant? = null,
    @SerialName("work") val work: Any? = null,
)
