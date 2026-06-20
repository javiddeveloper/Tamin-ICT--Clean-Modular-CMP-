package com.tamin.taminhamrah.useCases.inbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import com.tamin.taminhamrah.repository.inbox.PersonalInboxRepository
import kotlinx.coroutines.flow.Flow

class GetPersonalInboxItemsUseCase(
    private val personalInboxRepository: PersonalInboxRepository,
) {
    operator fun invoke(
        query: ApiQueryParamDN = defaultQuery(),
    ): Flow<List<PersonalInboxItemDN>> {
        return personalInboxRepository.getInboxItems(query)
    }

    companion object {
        fun defaultQuery(): ApiQueryParamDN = ApiQueryParamDN(
            page = 0,
            start = 0,
            limit = 10,
        )
    }
}
