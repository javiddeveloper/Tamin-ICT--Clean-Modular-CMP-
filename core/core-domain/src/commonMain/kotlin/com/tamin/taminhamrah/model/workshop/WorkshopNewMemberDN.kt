package com.tamin.taminhamrah.model.workshop

data class WorkshopNewMemberDN(
    val id: Long?,
    val dateOfStart: Long?,
    val insuranceId: String?,
    val relationWithTamin: Int?,
    val organizationId: String?,
    val workshopId: String?,
    val job: String?
)

data class WorkshopNewMemberListDN(
    val list: List<WorkshopNewMemberDN>?,
    val total: Int
)
