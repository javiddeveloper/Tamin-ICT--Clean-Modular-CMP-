package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.editPhoneNumber

import com.tamin.taminhamrah.data.repository.LoginRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import kotlinx.coroutines.delay
import javax.inject.Inject

class EditPhoneNumberSendOtpUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.EDIT_PHONE_NUMBER_SEND_OTP
    private val isMock = false

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            var newPhone = params.payload?.get("newPhone")?.toString()

            if (!newPhone.isNullOrEmpty()) {
                newPhone = newPhone.filter { it.isDigit() }
                if (newPhone.startsWith("98")) {
                    newPhone = "0" + newPhone.substring(2)
                }
                if (!newPhone.startsWith("09")) {
                    if (newPhone.length == 10 && newPhone.startsWith("9")) {
                        newPhone = "0$newPhone"
                    }
                }
            }

            if (newPhone.isNullOrEmpty()) {
                return ServiceResult.Failure(Exception("شماره تلفن همراه الزامی است"))
            }

            if (isMock) {
                delay(1500)
                val mockHash = "mock_hash_${System.currentTimeMillis()}"
                val newData = params.payload?.toMutableMap() ?: mutableMapOf()
                newData["newPhone"] = newPhone
                newData["expirationDuration"] = "120000"
                newData["targetTime"] = (System.currentTimeMillis() + 120000).toString()
                newData["editMobileHash"] = mockHash

                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "ویرایش شماره همراه",
                    data = ServiceData.GenerativeForm(
                        schema = buildEditPhoneSchema(
                            currentPhone = params.payload?.get("currentPhone")?.toString() ?: "",
                            payload = newData,
                            step = 2,
                            showCancelButton = true,
                            message = "کد تایید با موفقیت به شماره $newPhone ارسال گردید."
                        ),
                        payload = newData.mapValues { it.value?.toString() }
                    )
                )
                ServiceResult.Success(listOf(formResponse))
            } else {
                val result = loginRepository.editMobile(newPhone)

                if (result.isSuccess) {
                    val hash = result.data?.hash
                    val duration = getDurationFromExpiration(result.data?.expirationTime)
                    val newData = params.payload?.toMutableMap() ?: mutableMapOf()
                    newData["newPhone"] = newPhone
                    newData["expirationDuration"] = duration.toString()
                    newData["targetTime"] = (System.currentTimeMillis() + duration).toString()
                    newData["editMobileHash"] = hash

                    val formResponse = ServiceResponse(
                        action = params.serviceName,
                        title = "ویرایش شماره همراه",
                        data = ServiceData.GenerativeForm(
                            schema = buildEditPhoneSchema(
                                currentPhone = params.payload?.get("currentPhone")?.toString() ?: "",
                                payload = newData,
                                step = 2,
                                showCancelButton = true,
                                message = result.getMessage()
                            ),
                            payload = newData.mapValues { it.value?.toString() }
                        )
                    )
                    ServiceResult.Success(listOf(formResponse))
                } else {
                    val formResponse = ServiceResponse(
                        action = params.serviceName,
                        title = "ویرایش شماره همراه",
                        data = ServiceData.GenerativeForm(
                            schema = buildEditPhoneSchema(
                                currentPhone = params.payload?.get("currentPhone")?.toString() ?: "",
                                payload = params.payload,
                                step = 1,
                                showCancelButton = true,
                                errorMessage = result.getMessage()
                            ),
                            payload = params.payload?.mapValues { it.value?.toString() }
                        )
                    )
                    ServiceResult.Success(listOf(formResponse))
                }
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun getDurationFromExpiration(expiration: Long?): Long {
        if (expiration == null) return 120000L
        val diff = expiration - System.currentTimeMillis()
        return if (diff > 0) diff else 120000L
    }
}
