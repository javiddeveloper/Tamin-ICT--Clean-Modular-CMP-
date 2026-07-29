package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance

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

class FuneralAllowanceValidateUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.FUNERAL_ALLOWANCE_VALIDATE

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val nationalCode = params.payload?.get("nationalCode")?.toString()
            if (nationalCode.isNullOrEmpty()) {
                return ServiceResult.Failure(Exception("کد ملی متوفی نامعتبر است"))
            }

            val result = repository.inquiryDeceasedInfo(nationalCode)
            if (result.isSuccess) {
                val infoList = result.data
                
                if (infoList.size >= 8) {
                    if (infoList[6] == "1") {
                        val newData = mutableMapOf<String?, String?>()
                        params.payload.forEach { (k, v) ->
                            newData[k] = v?.toString()
                        }
                        newData["deceasedFullName"] = infoList[4]
                        newData["deceasedRelation"] = infoList[5]

                        val formResponse = ServiceResponse(
                            action = params.serviceName,
                            title = "کمک هزینه مراسم ترحیم",
                            data = ServiceData.GenerativeForm(
                                schema = buildStep2Schema(result.getMessage()),
                                payload = newData
                            )
                        )
                        return ServiceResult.Success(listOf(formResponse))
                    } else {
                        val errorMessage = infoList.getOrNull(7)?.takeIf { it.isNotEmpty() } ?: "شما دارای شرایط دریافت کمک هزینه مراسم ترحیم نمی باشید"
                        val formResponse = ServiceResponse(
                            action = params.serviceName,
                            title = "کمک هزینه مراسم ترحیم",
                            data = ServiceData.GenerativeForm(
                                schema = buildStep1ErrorSchema(errorMessage),
                                payload = params.payload.entries.associate { it.key as String? to it.value?.toString() }
                            )
                        )
                        return ServiceResult.Success(listOf(formResponse))
                    }
                } else {
                    val formResponse = ServiceResponse(
                        action = params.serviceName,
                        title = "کمک هزینه مراسم ترحیم",
                        data = ServiceData.GenerativeForm(
                            schema = buildStep1ErrorSchema("خطا در دریافت اطلاعات متوفی"),
                            payload = params.payload.entries.associate { it.key as String? to it.value?.toString() }
                        )
                    )
                    return ServiceResult.Success(listOf(formResponse))
                }
            } else {
                val errorMessage = result.getMessage().ifBlank { "خطا در دریافت اطلاعات متوفی" }
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "کمک هزینه مراسم ترحیم",
                    data = ServiceData.GenerativeForm(
                        schema = buildStep1ErrorSchema(errorMessage),
                        payload = params.payload.entries.associate { it.key as String? to it.value?.toString() }
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun buildStep2Schema(message: String?): FormSchema {
        return FormSchema(
            key = ServiceNameEnum.FUNERAL_ALLOWANCE_VALIDATE.key,
            currentStep = 2,
            message = message,
            steps = listOf(
                FormStep(index = 1, title = "اطلاعات پایه"),
                FormStep(index = 2, title = "اطلاعات متوفی")
            )
        )
    }

    private fun buildStep1ErrorSchema(errorMessage: String?): FormSchema {
        return FormSchema(
            key = ServiceNameEnum.FUNERAL_ALLOWANCE_GET.key,
            currentStep = 1,
            errorMessage = errorMessage,
            steps = listOf(
                FormStep(index = 1, title = "اطلاعات پایه")
            )
        )
    }
}
