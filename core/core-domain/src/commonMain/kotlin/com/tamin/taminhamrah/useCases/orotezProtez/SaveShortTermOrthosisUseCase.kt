package com.tamin.taminhamrah.useCases.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.SaveShortTermOrthosisRequestDN
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import kotlinx.coroutines.flow.Flow

class SaveShortTermOrthosisUseCase(
    private val orotezProtezRepository: OrotezProtezRepository
) {
    operator fun invoke(request: SaveShortTermOrthosisRequestDN): Flow<String?> {
        return orotezProtezRepository.saveShortTermOrthosis(request)
    }
}
