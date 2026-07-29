package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editBankAccount

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.utils.Utility
import javax.inject.Inject

class EditBankAccountSubmitUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_BANK_ACCOUNT_SUBMIT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val accountNumber = params.payload?.get("accountNumber")?.toString() ?: ""
            val bankId = params.payload?.get("bankId")?.toString() ?: ""
            val accountTypeId = params.payload?.get("accountTypeId")?.toString() ?: ""
            var startDate = params.payload?.get("startDate")?.toString() ?: ""

            if (startDate.contains("/")) {
                val gregorianDate = Utility.convertJalaliToGregorianString(startDate)
                if (gregorianDate.isNotEmpty()) {
                    startDate = gregorianDate
                }
            }

            if (accountNumber.isBlank() || bankId.isBlank() || accountTypeId.isBlank() || startDate.isBlank()) {
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "ویرایش شماره حساب",
                    data = ServiceData.GenerativeForm(
                        schema = buildEditBankAccountSchema(
                            payload = params.payload,
                            step = 1,
                            showCancelButton = true,
                            errorMessage = "لطفا تمام فیلدها را پر کنید"
                        ),
                        payload = params.payload?.mapValues { it.value?.toString() }
                    )
                )
                return ServiceResult.Success(listOf(formResponse))
            }

            val result = repository.sendBankAccountInfo(
                accountNumber,
                accountTypeId,
                bankId,
                startDate
            )

            if (result.isSuccess) {
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "ویرایش شماره حساب",
                    data = ServiceData.GenerativeForm(
                        schema = buildEditBankAccountSchema(
                            payload = params.payload,
                            step = 2,
                            showCancelButton = false,
                            message = result.getMessage().takeIf { it.isNotBlank() } ?: "عملیات با موفقیت انجام شد"
                        ),
                        payload = params.payload?.mapValues { it.value?.toString() }
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            } else {
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "ویرایش شماره حساب",
                    data = ServiceData.GenerativeForm(
                        schema = buildEditBankAccountSchema(
                            payload = params.payload,
                            step = 1,
                            showCancelButton = true,
                            errorMessage = result.getMessage() ?: "متاسفانه خطایی رخ داده است"
                        ),
                        payload = params.payload?.mapValues { it.value?.toString() }
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
