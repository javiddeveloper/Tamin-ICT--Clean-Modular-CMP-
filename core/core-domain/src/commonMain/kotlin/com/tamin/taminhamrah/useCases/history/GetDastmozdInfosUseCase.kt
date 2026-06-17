package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.repository.HistoryRepository

class GetDastmozdInfosUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(
        page: Int = 1,
        limit: Int = 10,
        start: Int = 0
    ): DastmozdInfoDN {
        return repository.getDastmozdInfos(page = page, limit = limit, start = start)
    }
}
