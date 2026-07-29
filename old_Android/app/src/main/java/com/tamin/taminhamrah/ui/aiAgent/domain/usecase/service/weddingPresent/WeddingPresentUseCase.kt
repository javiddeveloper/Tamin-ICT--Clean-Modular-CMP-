package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class WeddingPresentUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WEDDING_PRESENT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = params.message,
                        data = ServiceData.Clickable(
                            message = listOf(
                                KeyValueModel(
                                    "توضیحات",
                                    "برای ثبت درخواست هدیه ازدواج، روی دکمه زیر کلیک کنید."
                                )
                            ),
                            actionType = AgentActionContent.WeddingPresent(
                                actionText = "درخواست هدیه ازدواج"
                            )
                        )
                    )
                )
            )
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
