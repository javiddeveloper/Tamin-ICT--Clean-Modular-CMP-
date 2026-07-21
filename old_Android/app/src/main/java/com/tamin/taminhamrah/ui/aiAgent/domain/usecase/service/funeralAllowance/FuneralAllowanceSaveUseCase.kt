package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance

import com.tamin.taminhamrah.data.remote.models.services.requestFuneralAllowance.FuneralAllowanceRequest
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class FuneralAllowanceSaveUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.FUNERAL_ALLOWANCE_SAVE

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val nationalCode = params.payload?.get("nationalCode")?.toString() ?: ""
            val request = FuneralAllowanceRequest(deadNationalId = nationalCode)

            val result = repository.submitRequestFuneralAllowance(request)
            val payloadData = params.payload?.entries?.associate { it.key as String? to it.value?.toString() }
            if (result.isSuccess && result.data != null) {
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "کمک هزینه مراسم ترحیم",
                    data = ServiceData.GenerativeForm(
                        schema = buildStep3Schema(result.getMessage(), null),
                        payload = payloadData
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            } else {
                // If it fails, maybe due to wrong account number, we show step 3 with error
                // so they can edit the bank account number
                val errorMessage = result.getMessage().ifBlank { "خطا در دریافت اطلاعات" }
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "کمک هزینه مراسم ترحیم",
                    data = ServiceData.GenerativeForm(
                        schema = buildStep3Schema(null, errorMessage),
                        payload = payloadData
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun buildStep3Schema(message: String?, errorMessage: String?): FormSchema {
        return FormSchema(
            key = ServiceNameEnum.FUNERAL_ALLOWANCE_SAVE.key,
            currentStep = 3,
            message = message,
            errorMessage = errorMessage,
            steps = listOf(
                FormStep(index = 1, title = "اطلاعات پایه"),
                FormStep(index = 2, title = "اطلاعات متوفی"),
                FormStep(index = 3, title = "ثبت نهایی")
            )
        )
    }
}
