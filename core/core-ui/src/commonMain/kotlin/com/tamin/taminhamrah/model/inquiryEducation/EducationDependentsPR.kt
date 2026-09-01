package com.tamin.taminhamrah.model.inquiryEducation

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class EducationDependentsPR(
    val list: List<EducationDependentItemPR> = emptyList(),
    val total: Int = 0,
)

@Immutable
@Serializable
data class EducationDependentItemPR(
    val nationalId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val fullName: String = "",
    val relationCode: String = "",
    val relationDescription: String = "",
    val dateOfExpire: String = "",
)

@Immutable
@Serializable
data class InquiryEducationCertificatePR(
    val message: String = "",
)
