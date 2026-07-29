package com.tamin.taminhamrah.data.remote.services

import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentRequest
import com.tamin.taminhamrah.data.remote.models.ai.agent.CancelResponseDTO
import com.tamin.taminhamrah.data.remote.models.ai.agent.ChatAllowedResponseDTO
import com.tamin.taminhamrah.data.remote.models.ai.agent.PollingResponseDTO
import retrofit2.http.*

interface AgentApiService {

    @GET("chat-allowed")
    suspend fun checkChatAllowed(): ChatAllowedResponseDTO

    @Multipart
    @POST("search/rule")
    suspend fun getAiLawsSearch(
        @Part("data") data: AgentRequest
    ): PollingResponseDTO

    @Multipart
    @POST("search/rule")
    suspend fun getAiLawsVoiceSearch(
        @Part file: okhttp3.MultipartBody.Part,
        @Part("data") data: AgentRequest
    ): PollingResponseDTO

    @Multipart
    @POST("search/service")
    suspend fun sentPrompt(
        @Part("data") data: AgentRequest
    ): PollingResponseDTO

    @Multipart
    @POST("search/service")
    suspend fun searchVoiceService(
        @Part file: okhttp3.MultipartBody.Part,
        @Part("data") data: AgentRequest
    ): PollingResponseDTO

    @GET("request/track/{id}")
    suspend fun trackRequest(
        @Path("id") id: String
    ): PollingResponseDTO

    @GET("request/cancel/{id}")
    suspend fun cancelRequest(
        @Path("id") id: String
    ): CancelResponseDTO
}

