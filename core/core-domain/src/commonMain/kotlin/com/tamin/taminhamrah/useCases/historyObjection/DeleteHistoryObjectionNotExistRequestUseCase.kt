package com.tamin.taminhamrah.useCases.historyObjection

import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow

class DeleteHistoryObjectionNotExistRequestUseCase(
    private val historyObjectionRepository: HistoryObjectionRepository
) {
    operator fun invoke(requestNumber: String, rowIndex: String): Flow<Boolean> =
        historyObjectionRepository.deleteNotExist(requestNumber, rowIndex)
}
