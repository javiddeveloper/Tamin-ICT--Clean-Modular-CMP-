package com.tamin.taminhamrah.model.inquiryEducation

data class EducationDependentsDN(
    val list: List<EducationDependentItemDN> = emptyList(),
    val total: Int = 0,
)

data class EducationDependentItemDN(
    val nationalId: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val relationCode: String? = null,
    val relationDescription: String? = null,
    val dateOfExpire: String? = null,
)

data class InquiryEducationCertificateDN(
    val message: String? = null,
)
