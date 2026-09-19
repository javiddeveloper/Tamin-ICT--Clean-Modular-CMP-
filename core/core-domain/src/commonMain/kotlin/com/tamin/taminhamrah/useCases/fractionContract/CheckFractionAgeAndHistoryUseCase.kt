package com.tamin.taminhamrah.useCases.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionEligibilityDN
import com.tamin.taminhamrah.repository.fractionContract.FractionContractRepository
import kotlinx.coroutines.flow.Flow

class CheckFractionAgeAndHistoryUseCase(
    private val fractionContractRepository: FractionContractRepository,
) {
    operator fun invoke(): Flow<FractionEligibilityDN?> =
        fractionContractRepository.checkAgeAndHistory()
}
