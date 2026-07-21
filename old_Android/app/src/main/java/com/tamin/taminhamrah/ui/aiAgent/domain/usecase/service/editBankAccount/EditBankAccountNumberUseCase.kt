package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount

import com.tamin.taminhamrah.data.remote.models.ai.agent.PromptModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class EditBankAccountNumberUseCase @Inject constructor(
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_BANK_ACCOUNT_NUMBER

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val prompts = params.data
            var buttonText = "تغییر شماره حساب"
            if (!prompts.isNullOrEmpty()) {
                // Try to find the first valid prompt
                for (item in prompts) {
                    if (item is PromptModel) {
                        buttonText = item.prompt ?: "تغییر شماره حساب"
                        break
                    }
                }
            }

            val response = ServiceResponse(
                action = params.serviceName,
                title = params.message ?: "ویرایش شماره حساب",
                data = ServiceData.Clickable(
                    message = emptyList(),
                    actionType = AgentActionContent.AddAccountNumber(buttonText)                )
            )

            ServiceResult.Success(listOf(response))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}