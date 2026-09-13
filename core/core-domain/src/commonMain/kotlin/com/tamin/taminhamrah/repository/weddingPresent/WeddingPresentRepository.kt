package com.tamin.taminhamrah.repository.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import kotlinx.coroutines.flow.Flow

interface WeddingPresentRepository {
    fun getWeddingPresentInfo(): Flow<WeddingPresentInfoDN>
    fun submitWeddingPresent(request: WeddingPresentSubmitRequestDN): Flow<Unit>
    fun calculateMarriageAllowance(timeStamp: String): Flow<List<String>>
}
