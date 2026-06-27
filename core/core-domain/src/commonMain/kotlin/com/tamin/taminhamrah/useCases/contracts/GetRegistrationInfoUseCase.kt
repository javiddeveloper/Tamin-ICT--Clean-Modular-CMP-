package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.model.contracts.RegistrationInfoDN
import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class GetRegistrationInfoUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(): Flow<RegistrationInfoDN> = contractsRepository.getRegistrationInfo()
}
