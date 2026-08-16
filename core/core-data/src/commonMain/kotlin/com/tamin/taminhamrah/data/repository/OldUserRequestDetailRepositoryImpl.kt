package com.tamin.taminhamrah.data.repository

import com.tamin.taminhamrah.core.data.mapper.userRequestDetail.mapToDetail
import com.tamin.taminhamrah.model.userRequest.UserRequestDetailRepository
import com.tamin.taminhamrah.remoteDataSource.UserRequestDetailRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class UserRequestDetailRepository(
    private val remoteDataSource: UserRequestDetailRemoteDataSource
) : UserRequestDetailRepository {

    override suspend fun getShortTermRequestDetails(
        referenceId: String
    ): Any {
        try {
            val statusResponse = remoteDataSource.getShortTermRequestStatus(referenceId)
            val infoResponse = remoteDataSource.getShortTermRequestLoadData(referenceId)

            if (statusResponse.baseStatus.serviceStatus != "SUCCESS" ||
                infoResponse.baseStatus.serviceStatus != "SUCCESS") {
                throw Exception("خطای درخواست: رشته پایگاه داده")
            }

            return mapToDetail(
                statusResponse = statusResponse,
                infoResponse = infoResponse,
                pregnancyStatusList = emptyList(),
                pregnancyTypeList = emptyList(),
                article16Response = null,
                deferredInstallmentResponse = null,
                followUpResponse = remoteDataSource.getFollowUpObjectionHistory(referenceId),
                documentList = emptyList(),
                isPregnancy = false
            )
        } catch (e: Exception) {
            throw Exception("خطا در دریافت اطلاعات پردخواست: ${e.message}")
        }
    }
}