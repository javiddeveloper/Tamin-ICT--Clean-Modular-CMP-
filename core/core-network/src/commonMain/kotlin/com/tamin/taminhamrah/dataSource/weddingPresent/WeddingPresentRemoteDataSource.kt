package com.tamin.taminhamrah.dataSource.weddingPresent

import com.tamin.taminhamrah.model.weddingPresent.ShortTermMarriageRequestDTO
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDTO

interface WeddingPresentRemoteDataSource {
    suspend fun getWeddingPresentInfo(): WeddingPresentInfoDTO?
    suspend fun submitWeddingPresent(request: ShortTermMarriageRequestDTO)
    suspend fun calculateMarriageAllowance(timeStamp: String): List<String>?
}
