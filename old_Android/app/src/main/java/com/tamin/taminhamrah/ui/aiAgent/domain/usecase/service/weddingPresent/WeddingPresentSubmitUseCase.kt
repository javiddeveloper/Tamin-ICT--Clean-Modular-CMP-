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

class WeddingPresentSubmitUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WEDDING_PRESENT_SUBMIT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        if (WeddingPresentMock.ENABLED) return WeddingPresentMock.mockSubmit(params) // MOCK
        return try {
            val isCommitmentAccepted = params.payload?.get("commitmentAccepted")?.toString() == "true"
            if (!isCommitmentAccepted) {
                return formError(
                    params,
                    context.getString(R.string.error_select_check_box),
                    step = 1
                )
            }

            val request = buildMarriageGiftRequest(params.payload)
            if (request.partnerNationalId.isBlank() || request.weddingDateTimeStamp <= 0L) {
                return formError(params, "اطلاعات درخواست ناقص است", step = 4)
            }

            val response = repository.marriageGiftRequest(request)
            val payload = params.payload ?: emptyMap()
            val successMessage = context.getString(R.string.message_success_send_request_wedding_present)

            if (response.isSuccess) {
                ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            action = params.serviceName,
                            title = "درخواست هدیه ازدواج",
                            data = ServiceData.GenerativeForm(
                                schema = buildWeddingPresentSchema(
                                    step = 4,
                                    showCancelButton = false,
                                    message = successMessage
                                ),
                                payload = payload.mapValues { it.value?.toString() }
                            )
                        )
                    )
                )
            } else {
                formError(
                    params,
                    response.getMessage().takeIf { it.isNotBlank() }
                        ?: context.getString(R.string.unable_to_request),
                    step = 4
                )
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun formError(params: ServiceParams, errorMessage: String, step: Int): ServiceResult {
        val payload = params.payload ?: emptyMap()
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = "درخواست هدیه ازدواج",
                    data = ServiceData.GenerativeForm(
                        schema = buildWeddingPresentSchema(
                            step = step,
                            showCancelButton = step == 1,
                            errorMessage = errorMessage
                        ),
                        payload = payload.mapValues { it.value?.toString() }
                    )
                )
            )
        )
    }
}
