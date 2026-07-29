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
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WeddingPresentCalculateUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WEDDING_PRESENT_CALCULATE

    override suspend fun execute(params: ServiceParams): ServiceResult {
        if (WeddingPresentMock.ENABLED) return WeddingPresentMock.mockCalculate(params) // MOCK
        return try {
            val calcDateTimestamp = params.payload?.get("calcDateTimestamp")?.toString()?.toLongOrNull()
            if (calcDateTimestamp == null || calcDateTimestamp <= 0L) {
                return formError(
                    params,
                    context.getString(R.string.message_selecte_marriage_date)
                )
            }

            val calculateResult = repository.calculateMarriageAllowance(calcDateTimestamp.toString())
            if (!calculateResult.isSuccess) {
                return formError(
                    params,
                    calculateResult.getMessage().takeIf { it.isNotBlank() }
                        ?: context.getString(R.string.unable_to_request)
                )
            }

            val amounts = calculateResult.data
            if (amounts.isNullOrEmpty() || amounts.getOrNull(1).isNullOrBlank()) {
                val errorMessage = amounts?.getOrNull(0)?.takeIf { !it.isNullOrBlank() }
                    ?: context.getString(R.string.unable_to_request)
                return formError(params, errorMessage)
            }

            val payload = params.payload.mergePayload(
                mapOf(
                    "calcDateTimestamp" to calcDateTimestamp.toString(),
                    "totalSalary" to Utility.getNumberWithSeparatorForStringValue(amounts[0]),
                    "amountPayable" to Utility.getNumberWithSeparatorForStringValue(amounts[1]),
                )
            )

            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = "درخواست هدیه ازدواج",
                        data = ServiceData.GenerativeForm(
                            schema = buildWeddingPresentSchema(
                                step = 3,
                                showCancelButton = true,
                            ),
                            payload = payload.mapValues { it.value?.toString() }
                        )
                    )
                )
            )
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
                            step = 3,
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
