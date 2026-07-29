package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import javax.inject.Inject

class DependentCancellationSubmitUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName =
        ServiceNameEnum.DEPENDENT_CANCELLATION_SUBMIT

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {

            val nationalCode =
                params.payload?.get("dependentId")?.toString()
                    ?: params.payload?.get("nationalCode")?.toString()

            val cancellationDate =
                params.payload?.get("cancellationDate")
                    ?.toString()
                    ?.replace("/", "")

            if (
                nationalCode.isNullOrEmpty() ||
                cancellationDate.isNullOrEmpty()
            ) {

                return ServiceResult.Failure(
                    Exception("اطلاعات ناقص است")
                )
            }

            val response =
                repository.dependentCancellation(
                    action = DependentCancellationAction.DELETE.value,
                    identifier = nationalCode,
                    date = cancellationDate
                )

            if (response.isSuccess) {

                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "حذف افراد تبعی",
                    data = ServiceData.GenerativeForm(
                        schema = buildDependentCancellationSchema(
                            payload = params.payload,
                            step = 3,
                            showCancelButton = false,
                            message = "فرد تبعی با موفقیت حذف شد"
                        ),
                        payload = params.payload?.mapValues {
                            it.value?.toString()
                        }
                    )
                )

                ServiceResult.Success(listOf(formResponse))

            } else {

                val formResponse = ServiceResponse(
                    action = params.serviceName,
                    title = "حذف افراد تبعی",
                    data = ServiceData.GenerativeForm(
                        schema = buildDependentCancellationSchema(
                            payload = params.payload,
                            step = 2,
                            showCancelButton = true,
                            errorMessage = "خطا در حذف فرد تبعی"
                        ),
                        payload = params.payload?.mapValues {
                            it.value?.toString()
                        }
                    )
                )

                ServiceResult.Success(listOf(formResponse))
            }

        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}