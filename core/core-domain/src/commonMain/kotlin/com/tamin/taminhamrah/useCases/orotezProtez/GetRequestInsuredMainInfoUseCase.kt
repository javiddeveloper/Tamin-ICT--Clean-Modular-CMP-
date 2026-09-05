package com.tamin.taminhamrah.useCases.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.RequestInsuredMainInfoDN
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import kotlinx.coroutines.flow.Flow

class GetRequestInsuredMainInfoUseCase(
    private val orotezProtezRepository: OrotezProtezRepository
) {
    operator fun invoke(): Flow<RequestInsuredMainInfoDN?> {
        return orotezProtezRepository.getRequestInsuredMainInfo()
    }
}
