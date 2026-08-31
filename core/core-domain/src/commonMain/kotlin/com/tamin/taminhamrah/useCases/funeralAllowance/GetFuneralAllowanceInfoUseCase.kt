package com.tamin.taminhamrah.useCases.funeralAllowance

import com.tamin.taminhamrah.model.funeralAllowance.FuneralAllowanceInfoDN
import com.tamin.taminhamrah.repository.funeralAllowance.FuneralAllowanceRepository

class GetFuneralAllowanceInfoUseCase(
    private val repository: FuneralAllowanceRepository,
) {
    suspend operator fun invoke(): FuneralAllowanceInfoDN =
        repository.getFuneralAllowanceInfo()
}
