package com.tamin.taminhamrah.model.workshop

data class WorkshopMemberDN(
    val leavingWorkStatus: String,
    val leavingWorkDate: String,
    val specialSubType: String
)

data class WorkshopMemberListDN(
    val list: List<WorkshopMemberDN>,
    val total: Int
)
