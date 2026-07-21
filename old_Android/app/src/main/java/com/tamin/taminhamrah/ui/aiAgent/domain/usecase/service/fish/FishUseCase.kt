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
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getFilters
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.utils.myDatePicker.utils.MyPersianHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import saman.zamani.persiandate.PersianDate
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.collections.isNotEmpty

//implemented
class FishUseCase @Inject constructor(
    private val repository: ServiceRepository,
    @ApplicationContext private val context: Context
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.FISH


    override suspend fun execute(params: ServiceParams): ServiceResult {
        val filter = params.getFilters()
        val date = params.getDateFilter()
        val paymentType = filter["paymentType"]

        val fishParams = params.data ?: emptyList()
        return try {
            val pensionerIdResult = repository.getPensionerId()
            val pensionerId = if (pensionerIdResult.isSuccess) {
                pensionerIdResult.data?.list?.firstOrNull()?.pensionerId
            } else null

            if (!pensionerIdResult.isSuccess || pensionerId == null) {
                return createEmptyResponse(params)
            }

            var currentYear: Int
            var currentMonth: Int
            val isAutoSearch = date.startDate == null && date.endDate == null

            if (isAutoSearch) {
                val todayShamsi =
                    PersianDate()
                currentYear = todayShamsi.shYear
                currentMonth = todayShamsi.shMonth
            } else {
                currentYear = date.startDate?.year?.toInt() ?: 0
                currentMonth = date.startDate?.month?.toInt() ?: 0
            }

            var retryCount = 0
            val maxRetries = 12
            var keyValueParams: List<KeyValueModel> = emptyList()

            do {
                // Format: YYYYMM (e.g., 140208)
                val dateValue = "${currentYear}${currentMonth.toString().padStart(2, '0')}"

                val filters =
                    "[{\"property\":\"pensionerId\",\"value\":\"$pensionerId\",\"operator\":\"EQUAL\"},{\"property\":\"startDate\",\"value\":\"$dateValue\",\"operator\":\"EQUAL\"},{\"property\":\"paymentType\",\"value\":\"$paymentType\",\"operator\":\"EQUAL\"}]"

                val response = repository.getPensionerPayRoll(filters)

                if (response.isSuccess) {
                    val dataList = response.data?.list ?: emptyList()

                    if (dataList.isNotEmpty()) {
                        val firstItem = dataList.first()
//                        val fishDate = "${firstItem.hisMon?.padStart(2, '0')}/${firstItem.hisYear}"

                        val tempParams = buildList {
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

                        if (tempParams.isNotEmpty()) {
                            keyValueParams = tempParams
                            break
                        }
                    }
                }

                if (isAutoSearch) {

                    if (currentMonth == 1) {
                        currentMonth = 12
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                    retryCount++
                } else {
                    break
                }
            } while (retryCount < maxRetries)

            if (keyValueParams.isNotEmpty()) {
//                val serviceResponse = ServiceResponse(
//                    params.serviceName,
////                    itemType = ItemType.KeyValue,
//                    title = params.message,
//                    data = ServiceData.KeyValueMessage(keyValueParams)
//                )
                val items = mutableListOf<ServiceResponse>()


                items.add(
                    ServiceResponse(
                        action = params.serviceName,
                        title =  buildString {
                            append(params.message)
                            append(" تاریخ فیش ")
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

    fun createEmptyResponse(params: ServiceParams): ServiceResult {
        return ServiceResult.Success(
            listOf(
                ServiceResponse(
                    params.serviceName,
//                    itemType = ItemType.KeyValue,
                    title = params.message,
                    data = ServiceData.StringMessage("متاسفانه فیشی یافت نشد.")
                )
            )
        )
    }
}