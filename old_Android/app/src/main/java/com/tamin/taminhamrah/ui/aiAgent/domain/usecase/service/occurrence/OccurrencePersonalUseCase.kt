package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

/**
 * Step 2 → Step 3. Fetches official personal info and workshop specifications based on
 * the user's workshop selection, carries the payload forward, and renders Step 3.
 * Mirrors OccurrenceReportViewModel.getWorkshopAndUserInfo.
 */
class OccurrencePersonalUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT_PERSONAL

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val payload = params.payload?.toMutableMap() ?: mutableMapOf()
            val workshopId = payload[OccurrenceFormKeys.WORKSHOP_ID]?.toString()
            val nationalCode = payload[OccurrenceFormKeys.NATIONAL_ID]?.toString()
            val birthDateStr = payload[OccurrenceFormKeys.BIRTH_DATE]?.toString()

            val workshopParts = workshopId?.split(OccurrenceFormKeys.WORKSHOP_ID_SEPARATOR)
            val workshopCode = workshopParts?.getOrNull(0)?.takeIf { it.isNotBlank() }
            val branchCode = workshopParts?.getOrNull(1)?.takeIf { it.isNotBlank() }

            if (workshopCode != null && branchCode != null && nationalCode != null) {
                coroutineScope {
                    val specDeferred = async { repository.getWorkshopSpecification(workshopCode, branchCode) }
                    val personalDeferred = async {
                        val birthTimestamp = birthDateStr?.let { dateStr ->
                            try {
                                val clean = com.tamin.taminhamrah.utils.ValidationUtil.persianToEnglish(dateStr).trim()
                                val parts = clean.split('/')
                                if (parts.size == 3) {
                                    val year = parts[0].toIntOrNull() ?: 1370
                                    val month = parts[1].toIntOrNull() ?: 1
                                    val day = parts[2].toIntOrNull() ?: 1
                                    val pDate = saman.zamani.persiandate.PersianDate()
                                    pDate.shYear = year
                                    pDate.shMonth = month
                                    pDate.shDay = day
                                    pDate.time
                                } else null
                            } catch (e: Exception) {
                                null
                            }
                        }
                        repository.getOfficePersonalInfo(nationalCode, birthTimestamp, workshopCode, branchCode)
                    }

                    val spec = specDeferred.await()
                    if (spec.isSuccess) {
                        payload[OccurrenceFormKeys.WORKSHOP_NAME] = spec.data?.workshopName
                        payload[OccurrenceFormKeys.REPORTER_TYPE] = if (spec.data?.nation?.nationCode == "01") "1" else "2"
                        payload[OccurrenceFormKeys.NATION_CODE] = 1 // Matches native fragment hardcode
                    }

                    val personal = personalDeferred.await()
                    if (personal.isSuccess) {
                        payload[OccurrenceFormKeys.FIRST_NAME] = personal.data?.firstName
                        payload[OccurrenceFormKeys.LAST_NAME] = personal.data?.lastName
                        payload[OccurrenceFormKeys.GENDER] = if (personal.data?.gender == "02") 2 else 1
                    }
                }
            }

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "اعلام حادثه",
                data = ServiceData.GenerativeForm(
                    schema = buildOccurrenceSchema(
                        payload = payload,
                        step = 3,
                        showCancelButton = true
                    ),
                    payload = payload.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
