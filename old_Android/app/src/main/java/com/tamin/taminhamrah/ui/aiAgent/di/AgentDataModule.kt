package com.tamin.taminhamrah.ui.aiAgent.di

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tamin.taminhamrah.BuildConfig
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.services.AgentApiService
import com.tamin.taminhamrah.data.repository.ai.AgentRepositoryImpl
import com.tamin.taminhamrah.data.repository.ai.ChatRepository
import com.tamin.taminhamrah.data.repository.ai.ChatRepositoryImpl
import com.tamin.taminhamrah.di.interceptor.TokenHolder
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AgentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAgentRepository(
        impl: AgentRepositoryImpl
    ): AgentRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AiChatRepository {
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}

class AiChatTokenInterceptor(
    private val preferenceManager: PreferenceManager,
    private val apiProvider: Provider<AgentApiService>,
    private val gson: Gson
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = TokenHolder.getAccessToken(preferenceManager)

        val newRequest = request.newBuilder()
            .header(Constants.AUTHENTICATION, token)
            .build()

        val response = chain.proceed(newRequest)

        // Avoid recursion and only handle relevant AI endpoints
        val path = request.url.encodedPath
        if (path.contains("chat-allowed") || !path.contains("search/service")) {
            return response
        }

        // Peek body to check for INVALID_OR_EXPIRED_TOKEN
        // The server might return 200 OK even for expired tokens
        val bodyString = if (response.code == 200 || response.code == 400) {
            response.peekBody(1024 * 32).string()
        } else {
            ""
        }

        val isTokenExpired = response.code == 400 || bodyString.contains("INVALID_OR_EXPIRED_TOKEN")

        if (isTokenExpired) {
            synchronized(this) {
                val newToken = runBlocking {
                    try {
                        val res = apiProvider.get().checkChatAllowed()
                        if (res.status == 200 && res.data?.chatToken != null) {
                            preferenceManager.saveChatAllowedData(res.data)
                            res.data.chatToken
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }

                if (newToken != null) {
                    response.close()
                    val requestWithChatToken = rebuildRequestWithNewToken(newRequest, newToken)
                    return chain.proceed(requestWithChatToken)
                } else {
                    response.close()
                    // Tell the user "Server Error" as requested.
                    // This exception will be caught by Repository's safeApiCall.
                    throw RuntimeException("خطای سرور")
                }
            }
        }
        return response
    }

    private fun rebuildRequestWithNewToken(request: Request, newToken: String): Request {
        val body = request.body as? MultipartBody ?: return request
        val newBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (part in body.parts) {
            val contentDisposition = part.headers?.get("Content-Disposition")
            if (contentDisposition?.contains("name=\"data\"") == true) {
                val jsonString = getPartBodyAsString(part.body)
                try {
                    val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)
                    jsonObject.addProperty("chatToken", newToken)
                    val newPartBody = gson.toJson(jsonObject)
                        .toRequestBody("application/json".toMediaTypeOrNull())
                    newBodyBuilder.addPart(part.headers, newPartBody)
                } catch (e: Exception) {
                    newBodyBuilder.addPart(part)
                }
            } else {
                newBodyBuilder.addPart(part)
            }
        }
        return request.newBuilder().post(newBodyBuilder.build()).build()
    }

    private fun getPartBodyAsString(body: RequestBody): String {
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }
}

@Module
@InstallIn(SingletonComponent::class)
object AiApiModule {

    @Provides
    @Singleton
    @Named("AiOkHttpClient")
    fun provideAiOkHttpClient(
        okHttpClient: OkHttpClient,
        preferenceManager: PreferenceManager,
        apiProvider: Provider<AgentApiService>,
        gson: Gson
    ): OkHttpClient {
        return okHttpClient.newBuilder()
            .addInterceptor(AiChatTokenInterceptor(preferenceManager, apiProvider, gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("AiRetrofit")
    fun provideAiRetrofit(
        gson: Gson,
        @Named("AiOkHttpClient") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.AI_BASE_URL)
//            .baseUrl(BuildConfig.AI_BASE_IP.removeSuffix("/") + "/")
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAgentApiService(
        @Named("AiRetrofit") retrofit: Retrofit
    ): AgentApiService = retrofit.create(AgentApiService::class.java)
}

