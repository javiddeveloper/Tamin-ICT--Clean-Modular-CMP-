package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeGetRecipientRepository : RecipientRepository {
    var shouldThrowError = false
    var error: Throwable = RuntimeException("Error")
    var recipientListResult: List<RecipientDN> = emptyList()

    override  fun getRecipientList(filters: List<ApiFilterDN>): Flow<List<RecipientDN>> = flow {
        if (shouldThrowError) throw error
        emit(recipientListResult)
    }
}
