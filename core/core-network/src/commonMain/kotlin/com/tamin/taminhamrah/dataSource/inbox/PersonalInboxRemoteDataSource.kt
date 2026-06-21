package com.tamin.taminhamrah.dataSource.inbox

import com.tamin.taminhamrah.model.inbox.PersonalInboxListDTO
import com.tamin.taminhamrah.model.inbox.PersonalInboxSizeDTO
import com.tamin.taminhamrah.model.request.ApiQueryParamDN

interface PersonalInboxRemoteDataSource {
    suspend fun getInboxItems(query: ApiQueryParamDN): PersonalInboxListDTO
    suspend fun getInboxSize(): PersonalInboxSizeDTO
}
