package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.common.RecipientDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import kotlinx.coroutines.flow.Flow

interface RecipientRepository {
    fun getRecipientList(filters: List<ApiFilterDN> = emptyList()): Flow<List<RecipientDN>>

}
