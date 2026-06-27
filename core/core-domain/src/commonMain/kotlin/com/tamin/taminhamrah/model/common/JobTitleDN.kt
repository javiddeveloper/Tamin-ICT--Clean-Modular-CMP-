package com.tamin.taminhamrah.model.common

data class JobTitleDN(
    val jobCode: String?,
    val jobDescription: String?,
    val status: String?,
    val statusDate: String?
)

data class JobTitleListDN(
    val list: List<JobTitleDN>?,
    val total: Int
)
