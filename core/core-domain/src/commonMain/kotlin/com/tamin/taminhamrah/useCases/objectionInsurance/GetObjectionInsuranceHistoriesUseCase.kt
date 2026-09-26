package com.tamin.taminhamrah.useCases.objectionInsurance

import com.tamin.taminhamrah.model.objectionInsurance.ObjectionInsuranceHistoryDN
import com.tamin.taminhamrah.repository.objectionInsurance.ObjectionInsuranceRepository
import kotlinx.coroutines.flow.Flow

class GetObjectionInsuranceHistoriesUseCase(
    private val objectionInsuranceRepository: ObjectionInsuranceRepository,
) {
    operator fun invoke(): Flow<List<ObjectionInsuranceHistoryDN>> =
        objectionInsuranceRepository.getConflictHistories()
}
