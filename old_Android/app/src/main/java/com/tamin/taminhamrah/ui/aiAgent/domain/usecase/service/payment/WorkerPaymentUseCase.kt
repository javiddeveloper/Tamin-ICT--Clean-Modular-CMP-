package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.payment

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.construction.WorkersPaymentInfo
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.utils.Utility
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkerPaymentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.WORKER_PAYMENT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val response = repository.getWorkersPaymentInfo(null)
            val list = response.data?.list ?: emptyList()

            if (list.isEmpty()) {
                createEmptyResponse(params)
            } else {
                val responses = list.map { buildResponse(params, it) }
                ServiceResult.Success(responses)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun buildResponse(
        params: ServiceParams,
        item: WorkersPaymentInfo
    ): ServiceResponse {
        val keyValueList = mutableListOf<KeyValueModel>()
        
        keyValueList.add(KeyValueModel(context.getString(R.string.from_date), item.fromDatePersian ?: "-"))
        keyValueList.add(KeyValueModel(context.getString(R.string.to_date), item.toDatePersian ?: "-"))
        keyValueList.add(KeyValueModel(context.getString(R.string.label_job_title), item.professionalTitle ?: "-"))
        
        val amount = item.amount + (item.amountFines?.toLongOrNull() ?: 0L)
        keyValueList.add(KeyValueModel(context.getString(R.string.label_total_pay), Utility.getRialWithSeparator(amount)))
        keyValueList.add(KeyValueModel(context.getString(R.string.payable), item.payableDes ?: "-"))

        // The payment flow in legacy app is:
        // 1. getWorkersPayDebit
        // 2. saveWorkerPayInfo
        // 3. getPaymentInfo (preview)
        // 4. launchUrl
        // Since we can't easily do all this in a single deep link without a dedicated fragment/activity 
        // that handles this sequence, we'll use a deep link to the existing WorkersPaymentInfoFragment 
        // or a new specialized handler if available. 
        // Given the requirements to "Preserve existing behavior", we'll deep link to the payment flow.
        
        return ServiceResponse(
            action = params.serviceName,
            title = params.message,
            data = ServiceData.Clickable(
                message = keyValueList,
                actionType = AgentActionContent.LocalDeepLink(
                    uri = "mytamin://workers_payment_info".toUri().buildUpon()
                        .appendQueryParameter(
                            Constants.TOOLBAR_TITLE,
                            context.getString(R.string.label_insurance_payment2)
                        )
                        .build().toString(),
                    actionText = "پرداخت حق بیمه"
                )
            )
        )
    }

    private fun createEmptyResponse(params: ServiceParams) =
        ServiceResult.Success(
            listOf(
                ServiceResponse(
                    params.serviceName,
                    title = params.message,
                    data = ServiceData.StringMessage("لیست پرداخت کارگران ساختمانی خالی است.")
                )
            )
        )
}
