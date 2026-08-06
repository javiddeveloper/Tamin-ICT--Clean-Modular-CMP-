package com.tamin.taminhamrah.useCases.addDependent

import com.tamin.taminhamrah.repository.AddDependentRepository
import kotlinx.coroutines.flow.Flow

class InquiryEducationCodeUseCase(
    private val repository: AddDependentRepository
) {
    operator fun invoke(
        nationalId: String,
        educationCode: String
    ): Flow<String> = repository.inquiryEducationCode(
        nationalId = nationalId,
        educationCode = educationCode
    )
}
