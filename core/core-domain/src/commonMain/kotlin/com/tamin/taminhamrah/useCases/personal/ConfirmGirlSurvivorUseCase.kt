package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.girlSurvivor.ConfirmGirlSurvivorDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class ConfirmGirlSurvivorUseCase(
    private val personalRepository: PersonalRepository,
) {
    operator fun invoke(body: ConfirmGirlSurvivorDN): Flow<String?> {
        return personalRepository.confirmGirlSurvivor(body)
    }
}
