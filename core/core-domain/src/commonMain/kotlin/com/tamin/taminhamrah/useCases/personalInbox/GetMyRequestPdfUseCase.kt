package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class GetMyRequestPdfUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(requestId: String): Flow<PersonalInboxItemDN> {
        return personalInboxRepository.getMyRequestPDF(requestId)
    }
}
