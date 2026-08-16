package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.repository.HistoryRepository

class SendToInstitutionUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(
        type1: Boolean,
        type2: Boolean,
        type3: Boolean
    ) {
        repository.sendToInstitution(type1, type2, type3)
    }
}
