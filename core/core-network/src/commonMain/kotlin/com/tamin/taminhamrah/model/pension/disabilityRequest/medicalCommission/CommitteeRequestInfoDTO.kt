package com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommitteeRequestInfoDTO(
    @SerialName("id") val id: Long? = null,
    @SerialName("demandInfoId") val demandInfoId: Long? = null,
    @SerialName("lastJobDesc") val lastJobDesc: String? = null,
    @SerialName("lastJobCode") val lastJobCode: String? = null,
    @SerialName("jobHistoryDesc") val jobHistoryDesc: String? = null,
    @SerialName("hasDrivingCertificate") val hasDrivingCertificate: Boolean? = null,
    @SerialName("hasVisitBeforeJob") val hasVisitBeforeJob: Boolean? = null,
    @SerialName("hasVisitInJob") val hasVisitInJob: Boolean? = null,
    @SerialName("hasHealthyCertificate") val hasHealthyCertificate: Boolean? = null,
    @SerialName("hasContract") val hasContract: Boolean? = null,
    @SerialName("hasExpertJob") val hasExpertJob: Boolean? = null,
    @SerialName("historyConfirm") val historyConfirm: Boolean? = null,
    @SerialName("militaryStatusCode") val militaryStatusCode: String? = null,
)
