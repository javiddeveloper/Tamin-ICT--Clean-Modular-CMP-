package com.tamin.taminhamrah.useCases.inquiryEducation

import com.tamin.taminhamrah.model.inquiryEducation.EducationDependentsDN
import com.tamin.taminhamrah.repository.inquiryEducation.InquiryEducationRepository
import kotlinx.coroutines.flow.Flow

class GetDataForEducationUseCase(
    private val inquiryEducationRepository: InquiryEducationRepository,
) {
    operator fun invoke(): Flow<EducationDependentsDN> {
        return inquiryEducationRepository.getDataForEducation()
    }
}
