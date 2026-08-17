package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.HistoryJobInfoDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryJobInfosUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(
        filters: List<ApiFilterDN> = emptyList()
    ): Flow<HistoryJobInfoDN> {
        return repository.getHistoryJobInfos(filters)
    }
}
