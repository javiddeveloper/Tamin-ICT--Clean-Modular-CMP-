package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.repository.addDependent.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class InquiryEducationCodeUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        nationalId: String,
        educationCode: String
    ): Flow<String> = repository.inquiryEducationCode(nationalId, educationCode)
}
