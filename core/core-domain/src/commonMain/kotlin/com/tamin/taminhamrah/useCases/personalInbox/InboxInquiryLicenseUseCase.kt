package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class InboxInquiryLicenseUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(requestId: String, operation: InboxLicenseOperation, duration: String? = null): Flow<Unit> {
        return personalInboxRepository.inboxInquiryLicense(requestId, operation.value, duration)
    }
}
