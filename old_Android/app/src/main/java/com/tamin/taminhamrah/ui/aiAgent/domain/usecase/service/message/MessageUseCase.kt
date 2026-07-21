package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.message

import android.content.Context
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessageUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.MESSAGE

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val data = params.data
            val itemsWithData = data?.map { it.toServiceResponse(params.serviceName, params.message) } ?: emptyList()
            val itemsWithPromptInList = itemsWithData.collectAllPromptToList()
//            val serviceResponse =
//                data?.map { it.toServiceResponse(serviceName, message = params.message) }

            ServiceResult.Success(itemsWithPromptInList )
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}