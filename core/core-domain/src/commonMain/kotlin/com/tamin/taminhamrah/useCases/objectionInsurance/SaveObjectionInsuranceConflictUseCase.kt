package com.tamin.taminhamrah.useCases.objectionInsurance

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class SaveObjectionInsuranceConflictUseCase(
    private val objectionInsuranceRepository: ObjectionInsuranceRepository,
) {
    operator fun invoke(items: List<ObjectionInsuranceHistoryDN>): Flow<String?> =
        objectionInsuranceRepository.saveConflict(items)
}
