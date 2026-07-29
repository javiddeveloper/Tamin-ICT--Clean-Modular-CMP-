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

class WeddingPresentGetUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WEDDING_PRESENT_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        if (WeddingPresentMock.ENABLED) return WeddingPresentMock.mockGet(params) // MOCK
        return try {
            val response = repository.getWeddingPresent()
            val data = response.data

            if (!response.isSuccess || data == null) {
                val errorMessage = response.getMessage().takeIf { it.isNotBlank() }
                    ?: context.getString(R.string.error_select_bank_account)
                return ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            action = params.serviceName,
                            title = "درخواست هدیه ازدواج",
                            data = ServiceData.StringMessage(errorMessage)
                        )
                    )
                )
            }

            if (data.bankAccount.isNullOrBlank()) {
                return ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            action = params.serviceName,
                            title = "درخواست هدیه ازدواج",
                            data = ServiceData.StringMessage(
                                context.getString(R.string.error_select_bank_account)
                            )
                        )
                    )
                )
            }

            val payload = params.payload.mergePayload(data.toPayloadMap())

            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = "درخواست هدیه ازدواج",
                        data = ServiceData.GenerativeForm(
                            schema = buildWeddingPresentSchema(
                                step = 1,
                                showCancelButton = true
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
}
