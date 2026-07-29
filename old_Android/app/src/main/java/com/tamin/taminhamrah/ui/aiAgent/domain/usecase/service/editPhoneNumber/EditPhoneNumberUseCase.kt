package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber

import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.data.remote.models.ai.agent.PromptModel
import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class EditPhoneNumberUseCase @Inject constructor(
    private val repository: CommonRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_PHONE_NUMBER

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val introResponse = ServiceResponse(
                action = params.serviceName,
                title = params.message,
                data = ServiceData.Clickable(
                    message = listOf(
                        KeyValueModel("توضیحات", "برای ویرایش شماره تلفن همراه، لطفا بر روی دکمه زیر کلیک نمایید.")
                    ),
                    actionType = AgentActionContent.EditMobile(
                        actionText = (params.data?.firstOrNull() as? PromptModel)?.prompt ?: "ویرایش شماره همراه"
                    )
                )
            )
            ServiceResult.Success(listOf(introResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
