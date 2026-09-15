package com.tamin.taminhamrah.useCases.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import kotlinx.coroutines.flow.Flow

class SubmitWeddingPresentUseCase(
    private val weddingPresentRepository: WeddingPresentRepository,
) {
    operator fun invoke(request: WeddingPresentSubmitRequestDN): Flow<Unit> {
        return weddingPresentRepository.submitWeddingPresent(request)
    }
}
