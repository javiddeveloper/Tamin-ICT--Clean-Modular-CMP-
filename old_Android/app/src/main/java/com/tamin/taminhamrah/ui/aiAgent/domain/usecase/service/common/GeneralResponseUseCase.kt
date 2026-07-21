package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.common

import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class GeneralResponseUseCase @Inject constructor(
    private val commonRepository: CommonRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.GENERAL_RESPONSE
    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val userFullName = buildString {
                append(commonRepository.getUserInfo().fullName)
                append(" عزیز ")
            }
            val prompts = params.data
            if (!params.message.isNullOrEmpty()) {
                val messageBuilder = StringBuilder()
                messageBuilder.append(userFullName)
                messageBuilder.append(params.message)
            }
            val itemsWithData = prompts?.map { it.toServiceResponse(params.serviceName,
                "$userFullName ${ params.message }") } ?: emptyList()
            val itemsWithPromptInList = itemsWithData.collectAllPromptToList()

            ServiceResult.Success(itemsWithPromptInList)
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
