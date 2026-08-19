package com.tamin.taminhamrah.useCases.occurrence

import com.tamin.taminhamrah.model.occurrence.OccurrencePersonalInfoDN
import com.tamin.taminhamrah.repository.occurrence.OccurrenceRepository

class GetOccurrencePersonalInfoUseCase(
    private val repository: OccurrenceRepository
) {
    suspend operator fun invoke(nationalCode: String, birthDate: String): OccurrencePersonalInfoDN =
        repository.getPersonalInfo(nationalCode, birthDate)
}
