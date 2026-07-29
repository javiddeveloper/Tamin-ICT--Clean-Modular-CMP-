package com.tamin.taminhamrah.data.repository.ai

import com.tamin.taminhamrah.data.local.preference.PreferenceManager
import com.tamin.taminhamrah.data.remote.models.ai.agent.AgentRequest
import com.tamin.taminhamrah.data.remote.models.ai.agent.ChatAllowedData
import com.tamin.taminhamrah.data.remote.models.ai.agent.PollingResponseDTO
import com.tamin.taminhamrah.data.remote.models.ai.agent.toDomain
import com.tamin.taminhamrah.data.remote.services.AgentApiService
import com.tamin.taminhamrah.data.repository.base.BaseRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.AgentResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.repository.AgentRepository
import com.tamin.taminhamrah.utils.FileUploadUtils
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject


const val MAX_POLLS = 5

class AgentRepositoryImpl @Inject constructor(
    private val api: AgentApiService,
    private val preferenceManager: PreferenceManager
) : BaseRepository(), AgentRepository {

    val isMockResponse = false

    override suspend fun checkChatAllowed(): Result<ChatAllowedData?> {
        return safeApiCall {
            val response = api.checkChatAllowed()
            Timber.tag("AgentRepository").d(response.data.toString())
            if (response.data?.canStartChat == false && !response.data.errorMessage.isNullOrBlank()) {
                throw Exception(response.data.errorMessage)
            }
            response.data
        }
    }

    private suspend fun pollForResult(
        initialResponse: PollingResponseDTO,
    ): AgentResponse {
        var currentResponse = initialResponse
        var requestId: String? = currentResponse.data?.id
        var pollCount = 0
        val maxPolls = MAX_POLLS

        try {
            while (currentResponse.data?.status == "PENDING" && pollCount < maxPolls) {
                val eta = currentResponse.data.eta ?: 5
                delay(eta * 1000L)
                requestId = currentResponse.data.id ?: break

                currentResponse = if (isMockResponse) {
                    delay(2000)
                    preferenceManager.getAgentOneServices()
                } else {
                    api.trackRequest(requestId)
                }
                pollCount++
            }
        } catch (e: CancellationException) {
            requestId?.let { id ->
                try {
                    api.cancelRequest(id)
                } catch (_: Exception) {
                }
            }
            throw e
        }

        return when (currentResponse.data?.status) {
            "DONE" -> {
                currentResponse.data.result?.toDomain(/*currentResponse.data.message*/)
                    ?: throw Exception("موردی یافت نشد!")
            }

            "FAILED" -> {
                throw Exception("درخواست با خطا مواجه شد!")
            }
            else -> {
                if (currentResponse.data?.status == "PENDING") {
                    throw Exception("زمان پاسخگویی بیش از حد طولانی شد. لطفا دوباره تلاش کنید.")
                }
                currentResponse.data?.result?.toDomain(/*currentResponse.data?.message*/) ?: throw Exception("خطا در دریافت پاسخ!")
            }
        }
    }

    override suspend fun sendLawPrompt(data: AgentRequest): Result<AgentResponse> {
        return safeApiCall {
            data.chatToken = getChatAllowedData()?.chatToken ?: ""
            val responseDto = api.getAiLawsSearch(data)
            pollForResult(responseDto)
        }
    }

    override suspend fun searchLawVoiceService(
        voicePath: String,
        data: AgentRequest
    ): Result<AgentResponse> {
        return safeApiCall {
            data.chatToken = getChatAllowedData()?.chatToken ?: ""
            val file = FileUploadUtils.getFileBody(voicePath)
            val response = api.getAiLawsVoiceSearch(file, data)
            pollForResult(response)
        }
    }

   override suspend fun sendPrompt(data: AgentRequest): Result<AgentResponse> {
       return safeApiCall {
           data.chatToken = getChatAllowedData()?.chatToken ?: ""
           val responseDto = api.sentPrompt(data)
           pollForResult(responseDto)
       }
   }

    override suspend fun searchVoiceService(
        voicePath: String,
        data: AgentRequest
    ): Result<AgentResponse> {
        return safeApiCall {
            data.chatToken = getChatAllowedData()?.chatToken ?: ""
            val file = FileUploadUtils.getFileBody(voicePath)
            val response = api.searchVoiceService(file, data)
            pollForResult(response)
        }
    }


    override fun saveChatAllowedData(data: ChatAllowedData) {
        preferenceManager.saveChatAllowedData(data)
    }

    override fun getChatAllowedData(): ChatAllowedData? {
        return preferenceManager.getChatAllowedData()
    }

    override fun isChatAllowed(): Boolean {
        return preferenceManager.isChatAllowed()
    }
}
