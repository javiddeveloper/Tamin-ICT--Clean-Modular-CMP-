package com.tamin.taminhamrah.mapper.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentItemPR
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsPR
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificatePR

fun EducationDependentsDN.toPresentation(): EducationDependentsPR =
    EducationDependentsPR(
        list = list.map { it.toPresentation() },
        total = total,
    )

fun EducationDependentItemDN.toPresentation(): EducationDependentItemPR {
    val resolvedFullName = fullName
        ?: listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { null }
        ?: ""
    return EducationDependentItemPR(
        nationalId = nationalId.orEmpty(),
        firstName = firstName.orEmpty(),
        lastName = lastName.orEmpty(),
        fullName = resolvedFullName,
        relationCode = relationCode.orEmpty(),
        relationDescription = relationDescription.orEmpty(),
        dateOfExpire = dateOfExpire.orEmpty(),
    )
}

fun InquiryEducationCertificateDN.toPresentation(): InquiryEducationCertificatePR =
    InquiryEducationCertificatePR(message = message.orEmpty())
