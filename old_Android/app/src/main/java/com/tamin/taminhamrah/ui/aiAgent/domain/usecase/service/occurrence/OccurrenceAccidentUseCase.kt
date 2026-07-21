package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.occurrence

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

/**
 * Step 3 → Step 4. Carries the accumulated payload forward and renders the accident
 * details + document upload step (the final input step before submission).
 */
class OccurrenceAccidentUseCase @Inject constructor(
    private val serviceRepository: ServiceRepository
) : ServiceUseCase {

    override val serviceName: ServiceNameEnum = ServiceNameEnum.OCCURRENCE_REPORT_ACCIDENT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        val payload = params.payload ?: emptyMap()
        val docTypesResponse = try {
            serviceRepository.getDocumentType(null)
        } catch (e: Exception) {
            null
        }
        val docTypes = if (docTypesResponse?.isSuccess == true) {
            docTypesResponse.data?.list?.map {
                FormOption(id = it.docTypeId.toString(), title = it.docDesc.orEmpty())
            } ?: emptyList()
        } else {
            emptyList()
        }

        val formResponse = ServiceResponse(
            action = params.serviceName,
            title = "اعلام حادثه",
            data = ServiceData.GenerativeForm(
                schema = buildOccurrenceSchema(
                    payload = payload,
                    step = 4,
                    showCancelButton = true,
                    docTypes = docTypes
                ),
                payload = payload.mapValues { it.value?.toString() }
            )
        )
        return ServiceResult.Success(listOf(formResponse))
    }
}
