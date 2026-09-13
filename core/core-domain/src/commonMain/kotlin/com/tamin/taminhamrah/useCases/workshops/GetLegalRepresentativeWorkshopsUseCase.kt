package com.tamin.taminhamrah.useCases.workshops

import com.tamin.taminhamrah.model.legalRepresentative.LegalRepresentativeWorkshopListDN
import com.tamin.taminhamrah.repository.WorkShopsRepository
import kotlinx.coroutines.flow.Flow

class GetLegalRepresentativeWorkshopsUseCase(private val repository: WorkShopsRepository) {
    operator fun invoke(): Flow<LegalRepresentativeWorkshopListDN?> {
        return repository.getLegalRepresentativeWorkshops()
    }
}
