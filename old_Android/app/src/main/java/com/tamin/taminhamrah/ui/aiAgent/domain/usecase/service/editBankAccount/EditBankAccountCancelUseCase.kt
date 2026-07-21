package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount

import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class EditBankAccountCancelUseCase @Inject constructor() : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_BANK_ACCOUNT_CANCEL

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "ویرایش شماره حساب",
                data = ServiceData.GenerativeForm(
                    schema = buildEditBankAccountSchema(
                        payload = params.payload,
                        step = 2,
                        showCancelButton = false,
                        message = "عملیات با موفقیت لغو شد.",
                        errorMessage = "عملیات لغو گردید."
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
