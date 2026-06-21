package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.GirlSurvivorConditionDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class CheckGirlSurvivorConditionsUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(nationalCode: String, pensionerId: String): Flow<GirlSurvivorConditionDN> {
        return personalRepository.checkGirlSurvivorConditions(nationalCode, pensionerId)
    }
}
