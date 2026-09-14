package com.tamin.taminhamrah.data.repository.weddingPresent

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.dataSource.weddingPresent.WeddingPresentRemoteDataSource
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentInfoDN
import com.tamin.taminhamrah.model.weddingPresent.WeddingPresentSubmitRequestDN
import com.tamin.taminhamrah.repository.weddingPresent.WeddingPresentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeddingPresentRepositoryImpl(
    private val weddingPresentRemoteDataSource: WeddingPresentRemoteDataSource,
) : WeddingPresentRepository {

    override fun getWeddingPresentInfo(): Flow<WeddingPresentInfoDN> = flow {
        val response = weddingPresentRemoteDataSource.getWeddingPresentInfo()
        emit(response?.toDomain() ?: WeddingPresentInfoDN())
    }

    override fun submitWeddingPresent(request: WeddingPresentSubmitRequestDN): Flow<Unit> = flow {
        weddingPresentRemoteDataSource.submitWeddingPresent(request.toDTO())
        emit(Unit)
    }

    override fun calculateMarriageAllowance(timeStamp: String): Flow<List<String>> = flow {
        emit(
            weddingPresentRemoteDataSource.calculateMarriageAllowance(timeStamp).orEmpty(),
        )
    }
}
