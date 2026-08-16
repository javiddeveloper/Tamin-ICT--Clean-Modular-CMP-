package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.core.data.mapper.userRequestDetail.mapToDetail
import com.tamin.taminhamrah.model.request.ListDataResponse
import com.tamin.taminhamrah.model.request.UserRequestDetailDTO.*
import com.tamin.taminhamrah.repository.userRequest.UserRequestDetailRepository
import com.tamin.taminhamrah.remoteDataSource.UserRequestDetailRemoteDataSource
import io.ktor.http.HttpStatusCode

class UserRequestDetailRepositoryImpl(
    private val remoteDataSource: UserRequestDetailRemoteDataSource
) : UserRequestDetailRepository {

    override suspend fun getShortTermRequestDetails(
        referenceId: String
    ): UserRequestDetailDN = remoteDataSource.runCatching {
        val statusResponse = getShortTermRequestStatus(referenceId)
        val infoResponse = getShortTermRequestLoadData(referenceId)

        if (statusResponse.baseStatus.serviceStatus != "SUCCESS" ||
            infoResponse.baseStatus.serviceStatus != "SUCCESS") {
            throw Exception("خطای درخواست: رشته پایگاه داده")
        }

        UserRequestDetailDN(
            statusModel = statusResponse.ListDataResponse.list.firstOrNull(),
            infoModel = infoResponse.ListDataResponse.list.firstOrNull(),
            pregnancyStatusList = emptyList(),
            pregnancyTypeList = emptyList(),
            article16Detail = null,
            deferredInstallmentDetail = null,
            followUpObjectionHistory = emptyList()
        )
    }.getOrElse {
        when (it) {
            is io.ktor.http.HttpException -> {
                if (it.response.status == HttpStatusCode.NotFound) {
                    throw Exception("درخواست مورد نظر یافت نشد")
                }
                throw Exception("خطای شبکه: ${it.response.status}")
            }
            else -> Exception("خطای نامشخص: ${it.message}")
        }
    }
}