package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository

class DeleteMyRequestUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    suspend operator fun invoke(requestId: String) {
        personalInboxRepository.deleteMyRequest(requestId)
    }
}
