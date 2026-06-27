package com.tamin.taminhamrah.useCases.common

import com.tamin.taminhamrah.repository.common.CommonRepository

class GetRegistrationDeclarationFormUseCase(
    private val repository: CommonRepository
) {
    suspend operator fun invoke(): Any {
        return repository.getRegistrationDeclarationForm()
    }
}
