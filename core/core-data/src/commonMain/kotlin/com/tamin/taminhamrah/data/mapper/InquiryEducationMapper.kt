package com.tamin.taminhamrah.data.mapper

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDTO
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsListDTO
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN

fun EducationDependentsListDTO.toDomain(): EducationDependentsDN {
    return EducationDependentsDN(
        list = list?.map { it.toDomain() } ?: emptyList(),
        total = total ?: list?.size ?: 0,
    )
}

fun EducationDependentItemDTO.toDomain(): EducationDependentItemDN {
    val personal = relationWithTamin?.personal
    val firstName = personal?.firstName
    val lastName = personal?.lastName
    val fullName = listOfNotNull(firstName, lastName)
        .joinToString(" ")
        .ifBlank { null }
    val baseTendency = relationWithTamin?.relationWithTamin?.baseTendency
    return EducationDependentItemDN(
        nationalId = personal?.nationalId,
        firstName = firstName,
        lastName = lastName,
        fullName = fullName,
        relationCode = baseTendency?.tendencyCode,
        relationDescription = baseTendency?.tendencyDescription,
        dateOfExpire = personal?.subDominant?.dateOfExpire,
    )
}

fun String?.toInquiryEducationCertificateDN(): InquiryEducationCertificateDN =
    InquiryEducationCertificateDN(message = this)
