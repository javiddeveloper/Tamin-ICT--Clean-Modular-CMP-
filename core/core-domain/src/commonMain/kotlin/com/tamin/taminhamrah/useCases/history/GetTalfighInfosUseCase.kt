package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.TalfighInfoDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.model.request.ApiFilterDN

class GetTalfighInfosUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): TalfighInfoDN {
        return repository.getTalfighInfos(filters)
    }
}
