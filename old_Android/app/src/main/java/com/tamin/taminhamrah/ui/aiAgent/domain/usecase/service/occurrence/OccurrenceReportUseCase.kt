package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.remote.models.ai.agent.PromptModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class OccurrenceReportUseCase @Inject constructor(
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val prompts = params.data
            var buttonText = "ثبت اعلام حادثه"
            if (!prompts.isNullOrEmpty()) {
                for (item in prompts) {
                    if (item is PromptModel) {
                        buttonText = item.prompt ?: "ثبت اعلام حادثه"
                        break
                    }
                }
            }

            val response = ServiceResponse(
                action = params.serviceName,
                title = params.message ?: "اعلام حادثه",
                data = ServiceData.Clickable(
                    message = listOf(
                        KeyValueModel("توضیحات", "برای ثبت گزارش حادثه ناشی از کار، لطفاً بر روی دکمه زیر کلیک نمایید.")
                    ),
                    actionType = AgentActionContent.OccurrenceReportGet(buttonText)
                )
            )

            ServiceResult.Success(listOf(response))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
