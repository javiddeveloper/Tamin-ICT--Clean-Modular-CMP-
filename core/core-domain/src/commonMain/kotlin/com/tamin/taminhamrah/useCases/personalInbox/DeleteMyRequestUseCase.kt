package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class DeleteMyRequestUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(requestId: String): Flow<Unit> {
        return personalInboxRepository.deleteMyRequest(requestId)
    }
}
