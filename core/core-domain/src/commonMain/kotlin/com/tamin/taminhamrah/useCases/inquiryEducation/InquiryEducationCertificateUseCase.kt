package com.tamin.taminhamrah.useCases.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.InquiryEducationCertificateDN
import com.tamin.taminhamrah.repository.inquiryEducation.InquiryEducationRepository
import kotlinx.coroutines.flow.Flow

class InquiryEducationCertificateUseCase(
    private val inquiryEducationRepository: InquiryEducationRepository,
) {
    operator fun invoke(
        code: String,
        educationCode: String,
    ): Flow<InquiryEducationCertificateDN> {
        return inquiryEducationRepository.inquiryEducationCertificate(
            code = code,
            educationCode = educationCode,
        )
    }
}
