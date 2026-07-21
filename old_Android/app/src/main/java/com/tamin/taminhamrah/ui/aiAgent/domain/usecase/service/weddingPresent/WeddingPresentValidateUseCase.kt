package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.weddingPresent

import android.content.Context
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeddingPresentValidateUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WEDDING_PRESENT_VALIDATE

    override suspend fun execute(params: ServiceParams): ServiceResult {
        if (WeddingPresentMock.ENABLED) return WeddingPresentMock.mockValidate(params) // MOCK
        return try {
            val isCommitmentAccepted = params.payload?.get("commitmentAccepted")?.toString() == "true"
            if (!isCommitmentAccepted) {
                return formError(params, context.getString(R.string.error_select_check_box))
            }

            val weddingDateTimestamp = params.payload?.get("weddingDateTimestamp")?.toString()?.toLongOrNull()
            val partnerNationalCode = params.payload?.get("partnerNationalCode")?.toString()?.trim()

            if (weddingDateTimestamp == null || weddingDateTimestamp <= 0L) {
                return formError(params, context.getString(R.string.message_selecte_marriage_date))
            }
            if (partnerNationalCode.isNullOrBlank() || partnerNationalCode.length != 10) {
                return formError(params, context.getString(R.string.error_not_valid_national_id))
            }

            val response = repository.validateMarriageGift(
                weddingDateTimestamp.toString(),
                partnerNationalCode
            )
            val payload = params.payload ?: emptyMap()

            if (response.isSuccess) {
                val errorFromData = response.data?.takeIf { !it.isNullOrBlank() }?.trim()
                if (!errorFromData.isNullOrBlank()) {
                    return formError(params, errorFromData)
                }

                val validateMessage = "اعتبارسنجی تاریخ عقد با موفقیت انجام شد"
                val resultPayload = payload.mergePayload(
                    mapOf(
                        "validateMessage" to validateMessage,
                        "calcDateTimestamp" to weddingDateTimestamp.toString(),
                        "calcDateJalali" to payload["weddingDateJalali"],
                    )
                )
                ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            action = params.serviceName,
                            title = "درخواست هدیه ازدواج",
                            data = ServiceData.GenerativeForm(
                                schema = buildWeddingPresentSchema(
                                    step = 2,
                                    showCancelButton = true,
                                    message = validateMessage
                                ),
                                payload = resultPayload.mapValues { it.value?.toString() }
                            )
                        )
                    )
                )
            } else {
                formError(
                    params,
                    response.message?.message?.takeIf { it.isNotBlank() }
                        ?: context.getString(R.string.unable_to_request)
                )
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun formError(params: ServiceParams, errorMessage: String): ServiceResult {
        val payload = params.payload ?: emptyMap()
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = "درخواست هدیه ازدواج",
                    data = ServiceData.GenerativeForm(
                        schema = buildWeddingPresentSchema(
                            step = 1,
                            showCancelButton = true,
                            errorMessage = errorMessage
                        ),
                        payload = payload.mapValues { it.value?.toString() }
                    )
                )
            )
        )
    }
}
