package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

/**
 * Entry point of the accident-report AI flow (service id 1011). Loads the authenticated
 * user's identity, pre-populates the read-only national code, and renders Step 1.
 *
 * This is the AI-agent equivalent of OccurrenceReportFragment.getData() +
 * the Article-60 warning pre-flow, surfaced here as the step-1 message.
 */
class OccurrenceReportGetUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val response = repository.getUserInfo()
            if (!response.isSuccess || response.data == null) {
                return ServiceResult.Failure(
                    Exception(response.reason?.takeIf { it.isNotBlank() } ?: "خطا در دریافت اطلاعات کاربر")
                )
            }

            val payload = params.payload?.toMutableMap() ?: mutableMapOf()
            payload[OccurrenceFormKeys.NATIONAL_ID] = response.data?.nationalID
            response.data?.birthDate?.let { payload[OccurrenceFormKeys.BIRTH_DATE] = it }

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "اعلام حادثه",
                data = ServiceData.GenerativeForm(
                    schema = buildOccurrenceSchema(
                        payload = payload,
                        step = 1,
                        showCancelButton = true,
                        message = "گزارش حوادث ناشی از کار بر اساس ماده ۶۰ قانون تأمین اجتماعی. " +
                            "لطفاً اطلاعات هویتی خود را تکمیل کنید."
                    ),
                    payload = payload.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
