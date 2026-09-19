package com.tamin.taminhamrah.useCases.contractAffair

import com.tamin.taminhamrah.model.contractAffair.CancelContractParamsDN
import com.tamin.taminhamrah.repository.contractAffair.ContractAffairRepository
import kotlinx.coroutines.flow.Flow

class CancelContractUseCase(
    private val contractAffairRepository: ContractAffairRepository,
) {
    operator fun invoke(params: CancelContractParamsDN): Flow<Unit> =
        contractAffairRepository.cancelContract(params)
}
