package com.tamin.taminhamrah.useCases.historyObjection

import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow

class CheckHistoryObjectionStatusNotExistUseCase(
    private val historyObjectionRepository: HistoryObjectionRepository
) {
    operator fun invoke(): Flow<Boolean> = historyObjectionRepository.checkStatusNotExist()
}
