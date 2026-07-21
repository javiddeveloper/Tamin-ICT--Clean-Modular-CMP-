package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dastmozd

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.findLastPaidYear
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

//implemented
//mapped to DastmozdInfosLastPay
class LastPayUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val serviceRepository: ServiceRepository,
) :
    ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.DASTMOZD_INFOS_LAST_PAY

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val dastmozdInfos = params.data ?: emptyList()
        return try {
            val result = serviceRepository.getWageAndHistoryInsurance(mutableMapOf())
            if (result.isSuccess) {
                val lastPaidYearModel = result.data?.list?.findLastPaidYear()
                val keyValueParams = lastPaidYearModel?.createLastPayKeyValue() ?: emptyList()

                val responseList: MutableList<ServiceResponse> = mutableListOf()
                val itemsWithData = dastmozdInfos.map {
                    it.toServiceResponse(params.serviceName, params.message) { uriString ->
                        val uriBuilder = uriString.toUri().buildUpon()
                        when {
                            uriString.contains(Constants.OBJECTION_INSURANCE_NON_EXISTENCE_DEEPLINK) -> {
                                uriBuilder.appendQueryParameter(
                                    Constants.TOOLBAR_TITLE,
                                    context.getString(R.string.objection_non_existent_histories)
                                )
                            }
                            uriString.contains(Constants.OBJECTION_INSURANCE_HISTORY) -> {
                                uriBuilder.appendQueryParameter(
                                    Constants.TOOLBAR_TITLE,
                                    context.getString(R.string.objection_insurance_history)
                                )
                            }
                            uriString.contains(Constants.ISSUANCE_WAGE_CERTIFICATE) -> {
                                uriBuilder.appendQueryParameter(
                                    Constants.TOOLBAR_TITLE,
                                    context.getString(R.string.issuance_wage_certificate)
                                )
                            }
                        }
                        uriBuilder.build().toString()
                    }
                }

                if (keyValueParams.isEmpty()) {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه سابقه بیمه پردازی وجود ندارد!")
                    )
                    responseList.add(serviceResponse)
                } else {
                    val item = ServiceResponse(
                        params.serviceName,
                        title = params.message,
                        data = ServiceData.GroupButton(
                            prompts = emptyList(),
                            actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                            content = keyValueParams
                        )
                    )
                    responseList.add(item)
                }
                responseList.addAll(itemsWithData)
                ServiceResult.Success(responseList.collectAllPromptToList())

            } else {
                val errorMessage = result.getMessage().ifBlank { "متاسفانه در دریافت اطلاعات خطایی رخ داده است!" }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
