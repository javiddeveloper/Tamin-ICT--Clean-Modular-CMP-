package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber

import com.tamin.taminhamrah.data.remote.models.user.VerifyMobileReq
import com.tamin.taminhamrah.data.repository.CommonRepository
import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import javax.inject.Inject

class EditPhoneNumberVerifyOtpUseCase @Inject constructor(
    private val repository: CommonRepository,
    private val loginRepository: LoginRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_PHONE_NUMBER_VERIFY_OTP

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val code = params.payload?.get("code")?.toString()
            val editMobileHash = params.payload?.get("editMobileHash")?.toString()
            val newPhone = params.payload?.get("newPhone")?.toString()
            val request = VerifyMobileReq(
                newPhone ?: "",
                code ?: "",
                editMobileHash ?: ""
            )

            val result = loginRepository.verifyChangeMobileCode(request)

            if (result.isSuccess) {
                repository.setPhoneNumber(newPhone)
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "ویرایش شماره همراه",
                    data = ServiceData.GenerativeForm(
                        schema = buildEditPhoneSchema(
                            currentPhone = repository.getUserPhoneNumber() ?: "",
                            payload = params.payload,
                            step = 3,
                            showCancelButton = false,
                            message = result.getMessage().takeIf { it.isNotBlank() } ?: "شماره موبایل شما با موفقیت تغییر یافت."
                        ),
                        payload = params.payload?.mapValues { it.value?.toString() }
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            } else {
                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "ویرایش شماره همراه",
                    data = ServiceData.GenerativeForm(
                        schema = buildEditPhoneSchema(
                            currentPhone = repository.getUserPhoneNumber() ?: "",
                            payload = params.payload,
                            step = 2,
                            showCancelButton = true,
                            errorMessage = result.getMessage()
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
