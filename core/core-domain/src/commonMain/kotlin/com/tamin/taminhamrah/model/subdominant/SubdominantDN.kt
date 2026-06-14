package com.tamin.taminhamrah.model.subdominant

data class SubdominantDN(
    val list: List<SubdominantItemDN>? = null,
    val total: String? = null
)

data class SubdominantItemDN(
    val id: Long? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fatherName: String? = null,
    val nationalCode: String? = null,
    val relationDescription: String? = null,
    val status: String? = null,
    val insuranceId: String? = null
)
