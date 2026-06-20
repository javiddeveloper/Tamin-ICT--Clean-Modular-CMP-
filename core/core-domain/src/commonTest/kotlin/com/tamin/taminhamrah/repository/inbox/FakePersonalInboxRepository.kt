package com.tamin.taminhamrah.repository.inbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePersonalInboxRepository : PersonalInboxRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var inboxItemsResult: List<PersonalInboxItemDN> = emptyList()
    var inboxSizeResult: PersonalInboxSizeDN = PersonalInboxSizeDN(usage = "0", total = "10")
    var lastQuery: ApiQueryParamDN? = null

    override fun getInboxItems(query: ApiQueryParamDN): Flow<List<PersonalInboxItemDN>> = flow {
        lastQuery = query
        if (shouldThrowError) throw error
        emit(inboxItemsResult)
    }

    override fun getInboxSize(): Flow<PersonalInboxSizeDN> = flow {
        if (shouldThrowError) throw error
        emit(inboxSizeResult)
    }
}
