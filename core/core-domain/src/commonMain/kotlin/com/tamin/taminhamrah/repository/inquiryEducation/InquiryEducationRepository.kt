package com.tamin.taminhamrah.repository.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import kotlinx.coroutines.flow.Flow

interface InquiryEducationRepository {
    fun getDataForEducation(): Flow<EducationDependentsDN>
    fun inquiryEducationCertificate(
        code: String,
        educationCode: String,
    ): Flow<InquiryEducationCertificateDN>
}
