package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.saveSurvivorInfo.SaveSurvivorInfoDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class SaveSurvivorInfoUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(body: SaveSurvivorInfoDN): Flow<String?> {
        return personalRepository.saveSurvivorInfo(body)
    }
}
