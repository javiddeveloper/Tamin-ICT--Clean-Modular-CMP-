package com.tamin.taminhamrah.useCases.historyObjection

import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow

class ConfirmHistoryObjectionNotExistUseCase(
    private val historyObjectionRepository: HistoryObjectionRepository
) {
    operator fun invoke(description: String?): Flow<Boolean> =
        historyObjectionRepository.confirmNotExist(description)
}
