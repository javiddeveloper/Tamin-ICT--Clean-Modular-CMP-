package com.tamin.taminhamrah.useCases.orotezProtez

import com.tamin.taminhamrah.model.orotezProtez.InsuredPersonDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.orotezProtez.OrotezProtezRepository
import kotlinx.coroutines.flow.Flow

class GetInsuredPersonsUseCase(
    private val orotezProtezRepository: OrotezProtezRepository
) {
    operator fun invoke(query: ApiQueryParamDN? = null): Flow<List<InsuredPersonDN>> {
        return orotezProtezRepository.getInsuredPersons(query)
    }
}
