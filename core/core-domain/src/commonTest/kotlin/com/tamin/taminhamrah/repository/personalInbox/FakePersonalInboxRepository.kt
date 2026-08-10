package com.tamin.taminhamrah.repository.personalInbox

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
    var deletedRequestId: String? = null
    var lastInquiryParams: Triple<String, String, String?>? = null

    override fun getInboxItems(query: ApiQueryParamDN?): Flow<List<PersonalInboxItemDN>> = flow {
        lastQuery = query
        if (shouldThrowError) throw error
        emit(inboxItemsResult)
    }

    override fun getInboxSize(): Flow<PersonalInboxSizeDN> = flow {
        if (shouldThrowError) throw error
        emit(inboxSizeResult)
    }

    override fun getMyRequestPDF(requestId: String): Flow<PersonalInboxItemDN> = flow {
        if (shouldThrowError) throw error
        emit(inboxItemsResult.firstOrNull { it.id.toString() == requestId } ?: inboxItemsResult.first())
    }

    override fun deleteMyRequest(requestId: String): Flow<Unit> = flow {
        deletedRequestId = requestId
        if (shouldThrowError) throw error
        emit(Unit)
    }

    override fun inboxInquiryLicense(requestId: String, operation: String, duration: String?): Flow<Unit> = flow {
        lastInquiryParams = Triple(requestId, operation, duration)
        if (shouldThrowError) throw error
        emit(Unit)
    }
}
