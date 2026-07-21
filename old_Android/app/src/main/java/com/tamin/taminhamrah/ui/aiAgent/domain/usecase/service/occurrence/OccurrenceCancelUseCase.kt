package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

/**
 * Cancels the accident-report flow from any step and renders the terminal result step
 * with a cancellation notice. Mirrors the native dialog's "back/cancel" behavior.
 */
class OccurrenceCancelUseCase @Inject constructor() : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT_CANCEL

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val payload = params.payload ?: emptyMap()
        val formResponse = ServiceResponse(
            action = params.serviceName,
            title = "اعلام حادثه",
            data = ServiceData.GenerativeForm(
                schema = buildOccurrenceSchema(
                    payload = payload,
                    step = 5,
                    showCancelButton = false,
                    message = "درخواست اعلام حادثه لغو شد"
                ),
                payload = payload.mapValues { it.value?.toString() }
            )
        )
        return ServiceResult.Success(listOf(formResponse))
    }
}
