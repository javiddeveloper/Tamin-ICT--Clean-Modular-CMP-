package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.funeralAllowance
    
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.data.repository.ai.model.FormStep
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject
    
class FuneralAllowanceCancelUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.FUNERAL_ALLOWANCE_CANCEL
    
    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "کمک هزینه مراسم ترحیم",
                data = ServiceData.GenerativeForm(
                    schema = FormSchema(
                        key = ServiceNameEnum.FUNERAL_ALLOWANCE_CANCEL.key,
                        currentStep = 3,
                        message = "عملیات لغو شد",
                        errorMessage = "عملیات توسط کاربر لغو گردید.",
                        steps = listOf(
                            FormStep(index = 1, title = "اطلاعات پایه"),
                            FormStep(index = 2, title = "اطلاعات متوفی"),
                            FormStep(index = 3, title = "لغو درخواست")
                        )
                    ),
                    payload = params.payload?.entries?.associate { it.key as String? to it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
