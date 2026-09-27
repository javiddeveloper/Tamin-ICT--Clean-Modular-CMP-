package com.tamin.taminhamrah.useCases.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.SubmitFuneralAllowanceParamsDN
import com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository

class SubmitFuneralAllowanceRequestUseCase(
    private val repository: FuneralAllowanceRepository,
) {
    suspend operator fun invoke(params: SubmitFuneralAllowanceParamsDN): String =
        repository.submitFuneralAllowanceRequest(params)
}
