package com.tamin.taminhamrah.useCases.objectionInsurance

import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class ConfirmObjectionInsuranceConflictUseCase(
    private val objectionInsuranceRepository: ObjectionInsuranceRepository,
) {
    operator fun invoke(description: String?): Flow<Boolean> =
        objectionInsuranceRepository.confirmConflict(description)
}
