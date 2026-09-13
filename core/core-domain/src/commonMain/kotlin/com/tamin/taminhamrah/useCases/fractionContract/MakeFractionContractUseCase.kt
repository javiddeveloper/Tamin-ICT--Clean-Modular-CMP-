package com.tamin.taminhamrah.useCases.fractionContract

import com.tamin.taminhamrah.model.fractionContract.FractionContractResultDN
import com.tamin.taminhamrah.repository.fractionContract.FractionContractRepository
import kotlinx.coroutines.flow.Flow

class MakeFractionContractUseCase(
    private val fractionContractRepository: FractionContractRepository,
) {
    operator fun invoke(premium: String): Flow<FractionContractResultDN> =
        fractionContractRepository.makeFractionContract(premium)
}
