package com.tamin.taminhamrah.data.remote.services


import com.tamin.taminhamrah.data.remote.BaseRemoteDataSource
import com.tamin.taminhamrah.data.remote.models.services.SentryConfig
import retrofit2.Response
import javax.inject.Inject

class SentryRemoteDataSourceImpl @Inject constructor(private val service: ServicesService) :
    SentryRemoteDataSource,
    BaseRemoteDataSource() {
    override suspend fun getSentryConfig(token: String): Response<SentryConfig?>? {
        return service.getSentryConfig(token)

    }
}