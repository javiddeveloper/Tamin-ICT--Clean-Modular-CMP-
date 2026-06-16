package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.HistoryRepository

class GetTalfighInfosUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(query: ApiQueryParamDN): TalfighInfoDN {
        return repository.getTalfighInfos(query)
    }
}
