package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.hokm

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import saman.zamani.persiandate.PersianDate
import javax.inject.Inject

class HokmLastUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.HOKM_LAST

    override suspend fun execute(params: ServiceParams): ServiceResult {


        return try {
            val pensionerIdResult = repository.getPensionerId()
            val pensionerId = if (pensionerIdResult.isSuccess) {
                pensionerIdResult.data?.list?.firstOrNull()?.pensionerId
            } else null
            val persianDate = PersianDate()
            val currentDate = "${persianDate.shYear}${Constants.FIRST_OF_FARVARDIN}"
            val map = HashMap<String, String>()
            map["pensionerId"] = pensionerId ?: ""
            map["startDate"] = currentDate
            if (!pensionerIdResult.isSuccess || pensionerId == null) {
                ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            params.serviceName,
//                        itemType = ItemType.KeyValue,
                            title = params.message,
                            ServiceData.StringMessage(context.getString(R.string.error_active_relation_user_is_insured))
                        )
                    )
                )
            } else {
                val response = repository.getEdictPensioner(map)
                if (response.isSuccess) {
                    val data = response.data
                    if (data == null) {
                        ServiceResult.Success(
                            listOf(
                                ServiceResponse(
                                    params.serviceName,
//                        itemType = ItemType.KeyValue,
                                    title = params.message,
                                    ServiceData.StringMessage(context.getString(R.string.error_recive_data))
                                )
                            )
                        )
                    } else if (data.edictInfo == null && data.survivorInfo == null) {
                        ServiceResult.Success(
                            listOf(
                                ServiceResponse(
                                    params.serviceName,
//                        itemType = ItemType.KeyValue,
                                    title = params.message,
                                    ServiceData.StringMessage(context.getString(R.string.dont_have_edict_info))
                                )
                            )
                        )
                    } else {
                        val responseData = data.createKeyValue()
                        val serviceResponse = ServiceResponse(
                            action = params.serviceName,
//                        itemType = ItemType.KeyValue,
                            title = params.message,
                            data = ServiceData.KeyValueMessage(
                                message = responseData.toList()
                            )
                        )
                        ServiceResult.Success(listOf(serviceResponse))
                    }

                } else {
                    if (response.getCode() == 500) {
                        ServiceResult.Success(
                            listOf(
                                ServiceResponse(
                                    params.serviceName,
//                        itemType = ItemType.KeyValue,
                                    title = params.message,
                                    ServiceData.StringMessage(context.getString(R.string.error_edict_not_found))
                                )
                            )
                        )
                    } else {
                        ServiceResult.Error(
                            response.getMessage()
                                .ifBlank { context.getString(R.string.error_recive_data) }
                        )
                    }
                }
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}