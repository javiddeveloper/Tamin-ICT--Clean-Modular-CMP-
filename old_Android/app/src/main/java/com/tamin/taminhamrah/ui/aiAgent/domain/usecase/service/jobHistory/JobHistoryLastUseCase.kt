package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.jobHistory

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.mapper.AgentSMDictionary
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class JobHistoryLastUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
    val dictionary: AgentSMDictionary,


    ) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.HISTORY_JOB_INFOS_LAST

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {
            val response = repository.getTitlesJob(getDefaultParamsMap())

            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                val lastJob = if (response.data?.list?.isNotEmpty() == true) {
                    response.data?.list?.last()
                } else {
                    null
                }
                if (lastJob == null) {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("متاسفانه سابقه شغلی یافت نشد.")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                } else {

                    val response = ServiceResponse(
                        action = params.serviceName,
//                        itemType = params.itemType,
                        title = params.message,
                        data = ServiceData.KeyValueMessage(
                            lastJob.createKeyValue()
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
    fun getDefaultParamsMap(): MutableMap<String, String> {
        val paramsMap = mutableMapOf<String, String>()
        paramsMap[Constants.PAGE] = Constants.DEFAULT_START_INDEX
        paramsMap[Constants.QUERY_PAGE_SIZE] = Constants.QUERY_PAGE_SIZE_60.toString()
        paramsMap[Constants.START] = Constants.DEFAULT_START_INDEX
        return paramsMap
    }
}
