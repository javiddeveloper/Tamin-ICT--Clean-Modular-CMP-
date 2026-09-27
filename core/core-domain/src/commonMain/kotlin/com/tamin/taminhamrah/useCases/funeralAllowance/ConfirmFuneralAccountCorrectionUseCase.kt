package com.tamin.taminhamrah.useCases.funeralAllowance

import com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository

class ConfirmFuneralAccountCorrectionUseCase(
    private val repository: FuneralAllowanceRepository,
) {
    suspend operator fun invoke(requestId: String): String =
        repository.confirmAccountCorrection(requestId)
}
