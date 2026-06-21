package com.tamin.taminhamrah.repository.personalInbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxItemDN
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDN
import com.tamin.taminhamrah.model.request.ApiQueryParamDN
import kotlinx.coroutines.flow.Flow

interface PersonalInboxRepository {
    fun getInboxItems(query: ApiQueryParamDN?): Flow<List<PersonalInboxItemDN>>
    fun getInboxSize(): Flow<PersonalInboxSizeDN>
}
