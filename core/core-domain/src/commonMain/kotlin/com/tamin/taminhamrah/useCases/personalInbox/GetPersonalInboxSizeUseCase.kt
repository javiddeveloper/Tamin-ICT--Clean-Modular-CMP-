package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class GetPersonalInboxSizeUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(): Flow<PersonalInboxSizeDN> {
        return personalInboxRepository.getInboxSize()
    }
}
