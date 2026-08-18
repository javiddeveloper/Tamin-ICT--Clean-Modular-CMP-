package com.tamin.taminhamrah.useCases.history

import com.tamin.taminhamrah.model.history.HistoryCertificateType
import com.tamin.taminhamrah.repository.HistoryRepository

class SendToInstitutionUseCase(
    private val repository: HistoryRepository
) {
    suspend operator fun invoke(selectedTypes: Set<HistoryCertificateType>) {
        repository.sendToInstitution(selectedTypes)
    }
}
