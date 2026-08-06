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
    // Raw epoch-millis timestamp from the backend — format with PersianDateFormatter at the
    // presentation layer rather than here, so the domain model stays free of display concerns.
    val dateOfBirth: Long? = null,
    val relationDescription: String? = null,
    val status: String? = null,
    val insuranceId: String? = null
)
