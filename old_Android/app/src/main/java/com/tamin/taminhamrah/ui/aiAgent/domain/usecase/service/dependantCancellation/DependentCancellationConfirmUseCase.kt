package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

//second in the flow

class DependentCancellationConfirmUseCase @Inject constructor(private val repository: ServiceRepository) :
    ServiceUseCase {

    override val serviceName =
        ServiceNameEnum.DEPENDENT_CANCELLATION_CONFIRM

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {
            val response = repository.getDependentInfo()
            if (!response.isSuccess || response.data == null) {
                return ServiceResult.Failure(Exception(response.reason ?: "خطا در دریافت اطلاعات"))
            }



            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "حذف افراد تبعی",
                data = ServiceData.GenerativeForm(
                    schema = buildDependentCancellationSchema(
                        payload = params.payload,
                        step = 2,
                        showCancelButton = true
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