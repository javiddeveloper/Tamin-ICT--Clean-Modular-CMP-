package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.deceasedInfo.DeceasedInfoDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class GetDeceasedInfoUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(nationalId: String): Flow<DeceasedInfoDN> {
        return personalRepository.getDeceasedInfo(nationalId)
    }
}
