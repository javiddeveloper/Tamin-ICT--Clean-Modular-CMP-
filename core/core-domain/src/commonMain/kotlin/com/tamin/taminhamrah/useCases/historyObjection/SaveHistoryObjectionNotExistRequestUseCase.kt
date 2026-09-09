package com.tamin.taminhamrah.useCases.historyObjection

import com.tamin.taminhamrah.model.historyObjection.SaveNotExistRequestDN
import com.tamin.taminhamrah.repository.historyObjection.HistoryObjectionRepository
import kotlinx.coroutines.flow.Flow

class SaveHistoryObjectionNotExistRequestUseCase(
    private val historyObjectionRepository: HistoryObjectionRepository
) {
    operator fun invoke(request: SaveNotExistRequestDN): Flow<Boolean> =
        historyObjectionRepository.saveNotExist(request)
}
