package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class DependentCancellationCancelUseCase @Inject constructor() :
    ServiceUseCase {

    override val serviceName =
        ServiceNameEnum.DEPENDENT_CANCELLATION_CANCEL

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "حذف افراد تبعی",
                data = ServiceData.GenerativeForm(
                    schema = buildDependentCancellationSchema(
                        payload = params.payload,
                        step = 3,
                        showCancelButton = false,
                        message = "عملیات لغو شد",
                        errorMessage = "درخواست حذف لغو گردید"
                    ),
                    payload = params.payload?.mapValues {
                        it.value?.toString()
                    }
                )
            )

            ServiceResult.Success(listOf(formResponse))

        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}