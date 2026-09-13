package com.tamin.taminhamrah.useCases.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import kotlinx.coroutines.flow.Flow

class GetWeddingPresentInfoUseCase(
    private val weddingPresentRepository: WeddingPresentRepository,
) {
    operator fun invoke(): Flow<WeddingPresentInfoDN> {
        return weddingPresentRepository.getWeddingPresentInfo()
    }
}
