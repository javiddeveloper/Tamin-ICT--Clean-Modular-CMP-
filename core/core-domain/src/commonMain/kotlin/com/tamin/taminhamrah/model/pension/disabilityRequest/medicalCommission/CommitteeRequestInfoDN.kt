package com.tamin.taminhamrah.model.pension.disabilityRequest.medicalCommission

data class CommitteeRequestInfoDN(
    val id: Long? = null,
    val demandInfoId: Long? = null,
    val lastJobDesc: String? = null,
    val lastJobCode: String? = null,
    val jobHistoryDesc: String? = null,
    val hasDrivingCertificate: Boolean? = null,
    val hasVisitBeforeJob: Boolean? = null,
    val hasVisitInJob: Boolean? = null,
    val hasHealthyCertificate: Boolean? = null,
    val hasContract: Boolean? = null,
    val hasExpertJob: Boolean? = null,
    val historyConfirm: Boolean? = null,
    val militaryStatusCode: String? = null,
)
