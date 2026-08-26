package com.tamin.taminhamrah.data.repository.pregnancyPay

import com.tamin.taminhamrah.data.mapper.toDTO
import com.tamin.taminhamrah.data.mapper.toDomain
import com.tamin.taminhamrah.data.mapper.toPregnancyPayEstimateDomain
import com.tamin.taminhamrah.dataSource.pregnancyPay.PregnancyPayRemoteDataSource
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyMainInfoDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyOptionDN
import com.tamin.taminhamrah.model.pregnancyPay.PregnancyPayEstimateDN
import com.tamin.taminhamrah.model.pregnancyPay.SendPregnancyPayRequestDN
import com.tamin.taminhamrah.repository.pregnancyPay.PregnancyPayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PregnancyPayRepositoryImpl(
    private val pregnancyPayRemoteDataSource: PregnancyPayRemoteDataSource,
) : PregnancyPayRepository {

    override fun getMainInfo(): Flow<PregnancyMainInfoDN?> = flow {
        emit(pregnancyPayRemoteDataSource.getMainInfo()?.toDomain())
    }

    override fun getPregnancyStatusList(): Flow<List<PregnancyOptionDN>> = flow {
        val response = pregnancyPayRemoteDataSource.getPregnancyStatusList()
        emit(response?.list?.mapNotNull { it.toDomain() } ?: emptyList())
    }

    override fun getPregnancyTypeList(): Flow<List<PregnancyOptionDN>> = flow {
        val response = pregnancyPayRemoteDataSource.getPregnancyTypeList()
        emit(response?.list?.mapNotNull { it.toDomain() } ?: emptyList())
    }

    override fun sendPregnancyPayRequest(request: SendPregnancyPayRequestDN): Flow<String?> = flow {
        emit(pregnancyPayRemoteDataSource.sendPregnancyPayRequest(request.toDTO())?.shorttermRequest?.resultMessage)
    }

    override fun calculateEstimate(startDateTimeStamp: Long, endDateTimeStamp: Long): Flow<PregnancyPayEstimateDN> = flow {
        val result = pregnancyPayRemoteDataSource.calculateEstimate(
            startDateTimeStamp.toString(),
            endDateTimeStamp.toString(),
        )
        emit(result.orEmpty().toPregnancyPayEstimateDomain())
    }
}
