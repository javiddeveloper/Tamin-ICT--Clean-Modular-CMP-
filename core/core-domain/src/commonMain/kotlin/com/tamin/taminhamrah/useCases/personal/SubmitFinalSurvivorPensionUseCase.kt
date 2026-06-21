package com.tamin.taminhamrah.useCases.personal

import com.tamin.taminhamrah.model.personal.SubmitFinalSurvivorPensionDN
import com.tamin.taminhamrah.repository.personal.PersonalRepository
import kotlinx.coroutines.flow.Flow

class SubmitFinalSurvivorPensionUseCase(
    private val personalRepository: PersonalRepository
) {
    operator fun invoke(requestId: Int, body: SubmitFinalSurvivorPensionDN): Flow<String?> {
        return personalRepository.submitFinalSurvivorPension(requestId, body)
    }
}
