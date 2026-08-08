package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository

class InboxInquiryLicenseUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    suspend operator fun invoke(requestId: String, operation: String, duration: String? = null) {
        personalInboxRepository.inboxInquiryLicense(requestId, operation, duration)
    }
}
