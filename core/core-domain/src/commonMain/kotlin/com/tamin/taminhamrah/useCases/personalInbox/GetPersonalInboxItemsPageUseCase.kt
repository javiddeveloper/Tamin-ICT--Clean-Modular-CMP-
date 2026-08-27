package com.tamin.taminhamrah.useCases.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.paging.PageDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.personalInbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class GetPersonalInboxItemsPageUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(
        query: ApiQueryParamDN,
    ): Flow<PageDN<PersonalInboxItemDN>> {
        return personalInboxRepository.getInboxItemsPage(query)
    }
}
