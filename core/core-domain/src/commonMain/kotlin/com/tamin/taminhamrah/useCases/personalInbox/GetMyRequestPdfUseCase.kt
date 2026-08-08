package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository

class GetMyRequestPdfUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    suspend operator fun invoke(requestId: String): PersonalInboxItemDN {
        return personalInboxRepository.getMyRequestPDF(requestId)
    }
}
