package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.fish

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.getDeductionListAi
import com.tamin.taminhamrah.data.remote.models.services.getLoanListAi
import com.tamin.taminhamrah.data.remote.models.services.getPaymentListAi
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getFilters
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import saman.zamani.persiandate.PersianDate
import java.util.concurrent.CancellationException
import javax.inject.Inject

class FishLastUseCase @Inject constructor(
    private val repository: ServiceRepository,
    @ApplicationContext private val context: Context
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.FISH_LAST

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {

            val paymentType = params.getFilters()["paymentType"]
            val fishParams = params.data ?: emptyList()
            val pensionerIdResult = repository.getPensionerId()
            val pensionerId =
                pensionerIdResult.data?.list?.firstOrNull()?.pensionerId

            if (!pensionerIdResult.isSuccess || pensionerId == null) {
                return createEmptyResponse(params)
            }

            val today = PersianDate()
            var currentYear = today.shYear
            var currentMonth = today.shMonth

//            val minYear = 1380

            var keyValueParams: List<KeyValueModel> = emptyList()
// if there was a min year
//            currentYear >= minYear
            var retryCount = 0
            val maxRetries = 12
            while (retryCount < maxRetries) {

                val dateValue =
                    "${currentYear}${currentMonth.toString().padStart(2, '0')}"

                val filters =
                    "[{\"property\":\"pensionerId\",\"value\":\"$pensionerId\",\"operator\":\"EQUAL\"}," +
                            "{\"property\":\"startDate\",\"value\":\"$dateValue\",\"operator\":\"EQUAL\"}," +
                            "{\"property\":\"paymentType\",\"value\":\"$paymentType\",\"operator\":\"EQUAL\"}]"

                val response = repository.getPensionerPayRoll(filters)

                if (response.isSuccess) {
                    val dataList = response.data?.list ?: emptyList()

                    if (dataList.isNotEmpty()) {
                        val firstItem = dataList.first()

                        keyValueParams = buildList {
                            add(
                                KeyValueModel(
                                    "میزان سابقه:",
                                    "${firstItem.hisYear} سال و ${firstItem.hisMon} ماه"
                                )
                            )
                            addAll(getPaymentListAi(dataList))
                            addAll(getDeductionListAi(dataList))
                            addAll(getLoanListAi(dataList))

                        }

                        break
                    }
                } else {
                    break
                }

                val (y, m) = previousMonth(currentYear, currentMonth)
                currentYear = y
                currentMonth = m
                retryCount++
            }

            if (keyValueParams.isNotEmpty()) {
                val items = mutableListOf<ServiceResponse>()


                items.add(
                    ServiceResponse(
                        action = params.serviceName,
                        title = buildString {
                            append(params.message)
                            append(" تاریخ آخرین فیش ")
                            append(MyPersianHelper.toPersianNumber(currentYear.toString()))
                            append("/")
                            append(MyPersianHelper.toPersianNumber(currentMonth.toString()))
                            append(" می باشد.")
                        },
                        data = ServiceData.GroupButton(
                            prompts = emptyList(),
                            actionType = AgentActionContent.SendPrompt(
                                params.message ?: "",
                                params.message ?: ""
                            ),
                            content = keyValueParams
                        )
                    )
                )


                val itemsWithData = fishParams.map {
                    it.toServiceResponse(params.serviceName, params.message) { uriString ->
                        val uriBuilder = uriString.toUri().buildUpon()
                        when {
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
                items.addAll(itemsWithData)

                ServiceResult.Success(items.collectAllPromptToList())


            } else {
                createEmptyResponse(params)
            }

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun previousMonth(year: Int, month: Int): Pair<Int, Int> =
        if (month == 1) year - 1 to 12 else year to month - 1


    private fun createEmptyResponse(params: ServiceParams): ServiceResult {
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    action = params.serviceName,
                    title = params.message,
                    data = ServiceData.StringMessage("فیشی یافت نشد.")
                )
            )
        )
    }
}
