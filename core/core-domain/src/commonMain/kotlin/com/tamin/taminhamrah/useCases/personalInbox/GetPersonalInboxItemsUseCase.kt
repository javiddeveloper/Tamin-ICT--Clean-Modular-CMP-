package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class GetPersonalInboxItemsUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(
        query: ApiQueryParamDN? = null,
    ): Flow<List<PersonalInboxItemDN>> {
        return personalInboxRepository.getInboxItems(query)
    }
}
