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
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount.EditBankAccountNumberUseCase
import javax.inject.Inject

class FuneralAllowanceGetUseCase @Inject constructor(
    private val repository: ServiceRepository,
    private val editBankAccountNumberUseCase: EditBankAccountNumberUseCase
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.FUNERAL_ALLOWANCE_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val result = repository.getInfoFuneral()
            if (result.isSuccess && result.data != null) {
                if (result.data.flag) {
                    val errMessage = "درخواست شما به دلیل مشکل در شماره حساب بانکی امکان تایید توسط شعبه را ندارد، لطفا بعد از اصلاح شماره حساب روی دکمه مشکل شماره حساب خود را برطرف نموده‌ام کلیک نمایید."
                    val payloadData = params.payload?.entries?.associate { it.key as String? to it.value?.toString() }?.toMutableMap() ?: mutableMapOf()
                    payloadData["requestId"] = result.data.request?.id?.toString() ?: ""

                    val formResponse = ServiceResponse(
                        action = params.serviceName,
                        title = "کمک هزینه مراسم ترحیم",
                        data = ServiceData.GenerativeForm(
                            schema = buildErrorSchema(errMessage),
                            payload = payloadData
                        )
                    )
                    ServiceResult.Success(listOf(formResponse))
                } else {
                    val formResponse = ServiceResponse(
                        action = params.serviceName,
                        title = "کمک هزینه مراسم ترحیم",
                        data = ServiceData.GenerativeForm(
                            schema = buildStep1Schema(result.getMessage()),
                            payload = mapOf(
                                "bankAccount" to (result.data.bankAccount ?: ""),
                                "bankName" to (result.data.bankName ?: ""),
                                "insuranceFirstName" to (result.data.insuranceFirstName ?: ""),
                                "insuranceLastName" to (result.data.insuranceLastName ?: ""),
                                "mobilNumber" to (result.data.mobilNumber ?: ""),
                                "branchName" to (result.data.branchName ?: ""),
                                "branchCode" to (result.data.branchCode ?: ""),
                                "nationalCode" to (result.data.nationalCode ?: ""),
                                "risuid" to (result.data.risuid ?: "")
                            )
                        )
                    )
                    ServiceResult.Success(listOf(formResponse))
                }
            } else {
                val message = result.getMessage()
                if (message.contains("شما فاقد شماره حساب بانکی می باشید")) {
                    val errMessage = "شما فاقد شماره حساب بانکی می باشید. لطفا نسبت به ثبت شماره حساب بانکی اقدام نمایید."
                    return editBankAccountNumberUseCase.execute(
                        ServiceParams(
                            serviceName = ServiceNameEnum.EDIT_BANK_ACCOUNT_NUMBER,
                            message = errMessage
                        )
                    )
                } else {
                    ServiceResult.Failure(Exception(message))
                }
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun buildStep1Schema(message: String?): FormSchema {
        return FormSchema(
            key = ServiceNameEnum.FUNERAL_ALLOWANCE_GET.key,
            currentStep = 1,
            message = message,
            steps = listOf(
                FormStep(
                    index = 1,
                    title = "اطلاعات پایه"
                )
            )
        )
    }

    private fun buildErrorSchema(errorMessage: String): FormSchema {
        return FormSchema(
            key = ServiceNameEnum.FUNERAL_ALLOWANCE_GET.key,
            currentStep = 3,
            errorMessage = errorMessage,
            steps = listOf(
                FormStep(index = 1, title = "اطلاعات پایه"),
                FormStep(index = 2, title = "اطلاعات متوفی"),
                FormStep(index = 3, title = "خطا")
            )
        )
    }
}
