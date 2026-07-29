package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.FormOption
import com.tamin.taminhamrah.data.repository.ai.model.FormSchema
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

//first in the flow
class DependentCancellationGetUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.DEPENDENT_CANCELLATION_GET

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val response = repository.getDependentInfo()
            if (!response.isSuccess || response.data == null) {
                return ServiceResult.Failure(Exception(response.reason ?: "خطا در دریافت اطلاعات"))
            }

            val dependentOptions = response.data?.list?.map {
                val info = it.dependentInfo.identityInfo
                val relation = it.dependentInfo.familyRelationShip.relationDetail.relationCode
                FormOption(
                    id = info.nationalId ?: "",
                    title = "${info.firstName} ${info.lastName}",
                    extras = mapOf(
                        "relation" to relation,
                        "gender" to (info.gender.genderCode ?: "")
                    )
                )
            } ?: emptyList()

            val newData = params.payload?.toMutableMap() ?: mutableMapOf()

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "ابطال کفالت",
                data = ServiceData.GenerativeForm(
                    schema = buildDependentCancellationSchemaWithDependents(
                        dependentOptions = dependentOptions,
                        payload = newData,
                        step = 1,
                        showCancelButton = true
                    ),
                    payload = newData.mapValues { it.value?.toString() }
                )
            )
            ServiceResult.Success(listOf(formResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}


fun buildDependentCancellationSchemaWithDependents(
    dependentOptions: List<FormOption>,
    payload: Map<String, Any?>?,
    step: Int,
    showCancelButton: Boolean,
    errorMessage: String? = null,
    message: String? = null,
    isLoading: Boolean = false
): FormSchema {
    // This will eventually merge with buildDependentCancellationSchema or replace it
    // For now, let's call the one we have and potentially adjust it
    val schema = buildDependentCancellationSchema(payload, step, showCancelButton, errorMessage, message, isLoading)
    
    // Inject dynamic options into Step 1's dependent field if it exists
    val updatedSteps = schema.steps.map { stepModel ->
        if (stepModel.index == 1) {
            val updatedFields = stepModel.fields.map { field ->
                if (field.id == "dependentId" || field.id == "nationalCode") {
                    // Adjusting to use a dropdown for dependent selection in Step 1
                    field.copy(
                        type = com.tamin.taminhamrah.data.repository.ai.model.FormFieldType.DROPDOWN,
                        options = dependentOptions,
                        label = "انتخاب فرد تبعی"
                    )
                } else field
            }
            stepModel.copy(fields = updatedFields)
        } else stepModel
    }
    
    return schema.copy(steps = updatedSteps)
}
