package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.model.request.ApiFilterDN

class GetDastmozdInfosUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): DastmozdInfoDN {
        return repository.getDastmozdInfos(filters)
    }
}
