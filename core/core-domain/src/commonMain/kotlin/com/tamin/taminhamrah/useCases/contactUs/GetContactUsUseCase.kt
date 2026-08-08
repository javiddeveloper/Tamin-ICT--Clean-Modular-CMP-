package com.tamin.taminhamrah.useCases.contactUs

import com.tamin.taminhamrah.model.contactUs.ContactUsInfoDN
import com.tamin.taminhamrah.repository.ContactUsRepository
import kotlinx.coroutines.flow.Flow

class GetContactUsUseCase(
    private val repository: ContactUsRepository
) {
    operator fun invoke(): Flow<ContactUsInfoDN> {
        return repository.getContactUsInfo()
    }
}
