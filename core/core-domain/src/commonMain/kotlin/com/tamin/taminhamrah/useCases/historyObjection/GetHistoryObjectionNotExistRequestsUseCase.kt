package com.tamin.taminhamrah.useCases.historyObjection

import com.tamin.taminhamrah.model.historyObjection.NotExistRequestDN
import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow

class GetHistoryObjectionNotExistRequestsUseCase(
    private val historyObjectionRepository: HistoryObjectionRepository
) {
    operator fun invoke(): Flow<List<NotExistRequestDN>> = historyObjectionRepository.getNotExistRequests()
}
