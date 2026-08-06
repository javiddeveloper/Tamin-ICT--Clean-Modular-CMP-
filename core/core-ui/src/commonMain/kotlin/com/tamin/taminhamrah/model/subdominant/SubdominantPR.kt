package com.tamin.taminhamrah.model.subdominant

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class SubdominantPR(
    val list: List<SubdominantItemPR> = emptyList(),
    val total: String = ""
)

@Immutable
@Serializable
data class SubdominantItemPR(
    val id: Long = 0L,
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val fatherName: String = "",
    val nationalCode: String = "",
    /** Jalali, e.g. "۱۳۳۰/۰۴/۰۱" — already formatted for display. */
    val birthDate: String = "",
    val relationDescription: String = "",
    val status: String = "",
    val insuranceId: String = ""
)
