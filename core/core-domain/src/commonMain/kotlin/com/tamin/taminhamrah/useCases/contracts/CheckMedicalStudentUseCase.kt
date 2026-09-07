package com.tamin.taminhamrah.useCases.contracts

import com.tamin.taminhamrah.repository.contracts.ContractsRepository
import kotlinx.coroutines.flow.Flow

class CheckMedicalStudentUseCase(
    private val contractsRepository: ContractsRepository,
) {
    operator fun invoke(): Flow<String> = contractsRepository.checkMedicalStudent()
}
