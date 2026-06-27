package com.tamin.taminhamrah.model.workshop

data class WorkshopStackHolderDN(
    val stackId: Int?,
    val mobile: String?,
    val birthDate: Long?,
    val telephon: String?,
    val userId: String?,
    val nationalId: String?,
    val stackType: String?,
    val startDate: Long?,
    val email: String?
)

data class WorkshopStackHolderListDN(
    val list: List<WorkshopStackHolderDN>?,
    val total: Int
)
