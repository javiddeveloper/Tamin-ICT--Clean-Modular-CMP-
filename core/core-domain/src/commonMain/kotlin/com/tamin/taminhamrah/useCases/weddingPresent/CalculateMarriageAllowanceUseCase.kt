package com.tamin.taminhamrah.useCases.weddingPresent

import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import kotlinx.coroutines.flow.Flow

class CalculateMarriageAllowanceUseCase(
    private val weddingPresentRepository: WeddingPresentRepository,
) {
    operator fun invoke(timeStamp: String): Flow<List<String>> {
        return weddingPresentRepository.calculateMarriageAllowance(timeStamp)
    }
}
