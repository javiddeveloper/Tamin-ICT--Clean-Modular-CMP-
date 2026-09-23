package com.tamin.taminhamrah.useCases.objectionInsurance

import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class CheckObjectionInsuranceStatusConflictUseCase(
    private val objectionInsuranceRepository: ObjectionInsuranceRepository,
) {
    operator fun invoke(): Flow<Boolean> = objectionInsuranceRepository.checkStatusConflict()
}
