package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.data.remote.models.ai.agent.PromptModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class InquiryEducationUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EXTEND_EDUCATION

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val introResponse = ServiceResponse(
                action = params.serviceName,
                title = params.message ?: "استعلام کد رهگیری تحصیلی",
                data = ServiceData.Clickable(
                    message = listOf(
                        KeyValueModel("توضیحات", "جهت تمدید دفترچه یا پوشش بیمه‌ای فرزندان دانشجو/دانش‌آموز، لطفا اطلاعات تحصیلی را استعلام نمایید.")
                    ),
                    actionType = AgentActionContent.InquiryEducation(
                        actionText = (params.data?.firstOrNull() as? PromptModel)?.prompt ?: "شروع استعلام تحصیلی"
                    )
                )
            )
            ServiceResult.Success(listOf(introResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}