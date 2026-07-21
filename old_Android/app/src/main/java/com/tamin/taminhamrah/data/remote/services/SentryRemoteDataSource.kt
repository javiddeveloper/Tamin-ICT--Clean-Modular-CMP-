package com.tamin.taminhamrah.data.remote.services

import com.tamin.taminhamrah.data.remote.models.services.SentryConfig
import retrofit2.Response

interface SentryRemoteDataSource{

    suspend fun getSentryConfig(token: String
    ): Response<SentryConfig?>?

}
