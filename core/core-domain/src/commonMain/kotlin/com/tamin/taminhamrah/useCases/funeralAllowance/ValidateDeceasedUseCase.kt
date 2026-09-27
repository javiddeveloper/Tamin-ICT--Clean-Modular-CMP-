package com.tamin.taminhamrah.useCases.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.DeceasedValidationDN
import com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository

class ValidateDeceasedUseCase(
    private val repository: FuneralAllowanceRepository,
) {
    suspend operator fun invoke(nationalCode: String): DeceasedValidationDN =
        repository.validateDeceased(nationalCode)
}
