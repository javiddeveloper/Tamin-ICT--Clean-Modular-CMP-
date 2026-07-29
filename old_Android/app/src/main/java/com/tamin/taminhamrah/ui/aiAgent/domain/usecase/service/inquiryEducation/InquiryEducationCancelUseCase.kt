package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.inquiryEducation

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class InquiryEducationCancelUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EXTEND_EDUCATION_CANCEL

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "استعلام کد تحصیلی",
                data = ServiceData.GenerativeForm(
                    schema = buildInquiryEducationSchema(
                        payload = params.payload,
                        step = 2,
                        showCancelButton = false,
                        errorMessage = "عملیات استعلام کد تحصیلی لغو شد."
                    ),
                    payload = params.payload?.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
