package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.services.TitlesJobModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getDateFilter
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class JobHistoryAllUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,

    ) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.HISTORY_JOB_INFOS

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {
            val response = repository.getTitlesJob(getDefaultParamsMap())

            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                val fullList = response.data?.list ?: emptyList()
                val filteredList = applyFilters(fullList, params)

                if (filteredList.isEmpty()) {
                    val lastJob = if (response.data?.list?.isNotEmpty() == true){response.data?.list?.last() } else { null}
                    if (lastJob == null) {
                        val serviceResponse = ServiceResponse(params.serviceName, title = params.message, data = ServiceData.StringMessage("متاسفانه سوابق شغلی یافت نشد."))
                        ServiceResult.Success(listOf(serviceResponse))
                    } else {

                        val response = ServiceResponse(
                            action = params.serviceName,
//                            itemType = params.itemType,
                            title = params.message,
                            data = ServiceData.KeyValueMessage(
                                lastJob.createKeyValue()
                            )
                        )
                        ServiceResult.Success(listOf(response))
                    }
                } else {

                    val keyValueItems = filteredList.flatMap { item ->
                        item.createKeyValue()
                    }
                    val response = ServiceResponse(
                        action = params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message ?: "",
                        data = ServiceData.KeyValueMessage(
                            keyValueItems
                        )
                    )
                    ServiceResult.Success(listOf(response))
                }
            } else {
                val errorMessage = response.getMessage().ifBlank { context.getString(R.string.job_titles_fetch_error) }
                ServiceResult.Error(errorMessage)
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    private fun applyFilters(
        list: List<TitlesJobModel>,
        params: ServiceParams
    ): List<TitlesJobModel> {
        val dateFilter = params.getDateFilter()
        val startDateObj = dateFilter.startDate

        val filterStartDateString: String? = if (
            startDateObj?.year != null
        ) {
            "${startDateObj.year}${startDateObj.month}"
        } else {
            null
        }

        return list.filter { item ->
            if (filterStartDateString == null) return@filter true
            Timber.tag("filterStartDateString").e(filterStartDateString)
            val itemDate = item.startDate ?: return@filter false
            Timber.tag("itemDate").e(itemDate)
            itemDate >= filterStartDateString
        }
    }
    fun getDefaultParamsMap(): MutableMap<String, String> {
        val paramsMap = mutableMapOf<String, String>()
        paramsMap[Constants.PAGE] = Constants.DEFAULT_START_INDEX
        paramsMap[Constants.QUERY_PAGE_SIZE] = Constants.QUERY_PAGE_SIZE_60.toString()
        paramsMap[Constants.START] = Constants.DEFAULT_START_INDEX
        return paramsMap
    }
}
