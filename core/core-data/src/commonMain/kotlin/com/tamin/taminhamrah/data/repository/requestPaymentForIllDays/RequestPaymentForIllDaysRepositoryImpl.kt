package com.tamin.taminhamrah.data.repository.requestPaymentForIllDays

import com.tamin.taminhamrah.data.mapper.requestPaymentForIllDays.toDTO
import com.tamin.taminhamrah.data.mapper.requestPaymentForIllDays.toDomain
import com.tamin.taminhamrah.dataSource.requestPaymentForIllDays.RequestPaymentForIllDaysRemoteDataSource
import com.tamin.taminhamrah.model.requestPaymentForIllDays.CovidResultDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.IllDaysInsuredMainInfoDN
import com.tamin.taminhamrah.model.requestPaymentForIllDays.SaveShortTermIllnessRequestDN
import com.tamin.taminhamrah.repository.requestPaymentForIllDays.RequestPaymentForIllDaysRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RequestPaymentForIllDaysRepositoryImpl(
    private val remoteDataSource: RequestPaymentForIllDaysRemoteDataSource,
) : RequestPaymentForIllDaysRepository {

    override fun getLatestInsuranceInfo(): Flow<IllDaysInsuredMainInfoDN?> = flow {
        emit(remoteDataSource.getLatestInsuranceInfo()?.toDomain())
    }

    override fun getCovidResult(): Flow<CovidResultDN> = flow {
        val result = remoteDataSource.getCovidResult()?.toDomain()
            ?: CovidResultDN(
                startDateTimeStamp = null,
                endDateTimeStamp = null,
                timestamps = emptyList(),
            )
        emit(result)
    }

    override fun calcIllness(
        startDateTimeStamp: String,
        endDateTimeStamp: String,
        maritalStatus: String,
    ): Flow<List<String>> = flow {
        emit(
            remoteDataSource.calcIllness(
                startDateTimeStamp = startDateTimeStamp,
                endDateTimeStamp = endDateTimeStamp,
                maritalStatus = maritalStatus,
            ).orEmpty()
        )
    }

    override fun sendRequestForIllDay(
        request: SaveShortTermIllnessRequestDN
    ): Flow<String?> = flow {
        emit(
            remoteDataSource.sendRequestForIllDay(request.toDTO())
                ?.shorttermRequest
                ?.resultMessage
        )
    }
}
