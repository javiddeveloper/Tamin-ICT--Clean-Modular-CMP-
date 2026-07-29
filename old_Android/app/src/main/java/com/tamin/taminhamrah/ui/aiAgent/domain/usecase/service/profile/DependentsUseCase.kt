package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.profile

import android.content.Context
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.collectAllPromptToList
import com.tamin.taminhamrah.data.remote.models.ai.agent.toServiceResponse
import com.tamin.taminhamrah.data.remote.models.services.showAndAddDependent.DependentDataModel
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.enums.ServiceStatus
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.getFilters
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import androidx.core.net.toUri

//implemented
//mapped to GetDependent
class DependentsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: ServiceRepository,
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.GET_DEPENDENT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val filters = params.getFilters()
        return try {
            val response = repository.getDependentInfo()
            val dependentsData = params.data ?: emptyList()

            if (response.baseStatus?.serviceStatus == ServiceStatus.SUCCESS) {
                var list = response.data?.list ?: emptyList()

                if (params.message?.contains("فرزند") == true && !filters.containsKey("tendencyCode")) {
                    val childTitles = setOf(
                        "فرزند پسر", "پسر",
                        "فرزند دختر", "دختر",
                        "فرزند خوانده",
                        "فرزند"
                    )


                    list = list.filter { item ->
                        val data = item.extractRelationData()
                        data.title in childTitles
                    }
                }

                val cleanFilters = filters.mapKeys { it.key.trim().lowercase() }

                if (cleanFilters.containsKey("tendencycode")) {
                    val filterCode = cleanFilters["tendencycode"]?.trim()
                    val filterGender = cleanFilters["gendercode"]?.trim()

                    val validTitles = getValidTitlesForFilter(filterCode, filterGender)


                    if (validTitles.isNotEmpty()) {
                        list = list.filter { item ->
                            val data = item.extractRelationData()

                            data.title in validTitles

                        }
                    }
                }

                if (list.isEmpty()) {
                    val serviceResponse = ServiceResponse(
                        params.serviceName,
//                        itemType = ItemType.KeyValue,
                        title = params.message,
                        data = ServiceData.StringMessage("تبعی یافت نشد!!")
                    )
                    ServiceResult.Success(listOf(serviceResponse))
                } else {
                    Timber.tag("DependentsUseCase").d(list.size.toString())

                    val keyValues = createKeyValue(list)
                    val infoResponse = ServiceResponse(
                        action = params.serviceName,
                        title = params.message ?: "",
                        data = ServiceData.GroupButton(
                            prompts = emptyList(),
                            actionType = AgentActionContent.SendPrompt(params.message ?: "", params.message ?: ""),
                            content = keyValues
                        )
                    )
                    
                    val itemsWithData = dependentsData.map {
                        it.toServiceResponse(params.serviceName, params.message) { uriString ->
                            val uriBuilder = uriString.toUri().buildUpon()
                            when {
                                uriString.contains(Constants.ADD_DEPENDENT_DEEPLINK) -> {
                                    uriBuilder.appendQueryParameter(
                                        Constants.TOOLBAR_TITLE,
                                        context.getString(R.string.title_subordinate_people)
                                    )
                                }
                                uriString.contains(Constants.CANCEL_DEPENDENT_DEEPLINK) -> {
                                    uriBuilder.appendQueryParameter(
                                        Constants.TOOLBAR_TITLE,
                                        context.getString(R.string.title_dependent_cancellation)
                                    )
                                }
                            }
                            uriBuilder.build().toString()
                        }
                    }

                    val finalResponses = (listOf(infoResponse) + itemsWithData).collectAllPromptToList()
                    ServiceResult.Success(finalResponses)
                }
            } else {
                val serviceResponse = ServiceResponse(
                    params.serviceName,
//                    itemType = ItemType.KeyValue,
                    title = params.message,
                    data = ServiceData.StringMessage(context.getString(R.string.dependents_fetch_error))
                )
                ServiceResult.Success(listOf(serviceResponse))
            }
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }

    fun createKeyValue(items: List<DependentDataModel>): List<KeyValueModel> {
        val keyValueList = mutableListOf<KeyValueModel>()
        items.forEachIndexed { index, model  ->
            keyValueList.addAll(model.createKeyValue())
            if (index != items.lastIndex){
                keyValueList.add(KeyValueModel("----------------", "", null))
            }
        }
        return keyValueList
    }

    private fun DependentDataModel.extractRelationData() = object {
        val info = dependentInfo.identityInfo
        val genderCode = info.gender.genderCode
        val relationCode = dependentInfo.familyRelationShip.relationDetail.relationCode

        val title = when (relationCode) {
            "101", "104" -> "فرزند پسر"
            "102", "105" -> "دختر"
            "106", "110" -> {
                when (genderCode) {
                    "02" -> "مادر"
                    "01" -> "پدر"
                    else -> "والدین"
                }
            }

            "123", "118", "133" -> "فرزند خوانده"
            "124" -> {
                when (genderCode) {
                    "02" -> "خواهر"
                    "01" -> "برادر"
                    else -> "بازمانده"
                }
            }

            "100", "103", "107", "108", "109" -> "همسر"
            "111", "112", "117" -> {
                when (genderCode) {
                    "01" -> "فرزند پسر"
                    "02" -> "فرزند دختر"
                    else -> "فرزند"
                }
            }

            else -> ""
        }
    }

    private fun getValidTitlesForFilter(tendencyCode: String?, gendercode: String?): Set<String> {
        return when (tendencyCode) {
            "101", "104" -> setOf("فرزند پسر")
            "102", "105" -> setOf("دختر")
            "106", "110" -> {
                when (gendercode) {
                    "01" -> setOf("پدر")
                    "02" -> setOf("مادر")
                    else -> setOf("پدر", "مادر", "والدین")
                }
            }

            "123", "118", "133" -> setOf("فرزند خوانده")
            "124" -> {
                when (gendercode) {
                    "02" -> setOf("خواهر")
                    "01" -> setOf("برادر")
                    else -> setOf("بازمانده")
                }
            }

            "100", "103", "107", "108", "109" -> setOf("همسر")
            "111", "112", "117" -> {
                when (gendercode) {
                    "01" -> setOf("فرزند پسر")
                    "02" -> setOf("فرزند دختر")
                    else -> setOf("فرزند پسر", "فرزند دختر", "فرزند")
                }
            }

            else -> emptySet()
        }
    }
}