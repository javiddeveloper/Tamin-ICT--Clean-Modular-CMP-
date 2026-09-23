package com.tamin.taminhamrah.useCases.objectionInsurance

import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class FinalConfirmObjectionInsuranceConflictUseCase(
    private val objectionInsuranceRepository: ObjectionInsuranceRepository,
) {
    operator fun invoke(): Flow<String> = objectionInsuranceRepository.finalConfirmConflict()
}
