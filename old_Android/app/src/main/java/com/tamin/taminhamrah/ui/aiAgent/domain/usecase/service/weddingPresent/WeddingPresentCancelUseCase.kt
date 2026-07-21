package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class WeddingPresentCancelUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WEDDING_PRESENT_CANCEL

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val payload = params.payload ?: emptyMap()
            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = "درخواست هدیه ازدواج",
                        data = ServiceData.GenerativeForm(
                            schema = buildWeddingPresentSchema(
                                step = 4,
                                showCancelButton = false,
                                errorMessage = "درخواست لغو شد"
                            ),
                            payload = payload.mapValues { it.value?.toString() }
                        )
                    )
                )
            )
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
