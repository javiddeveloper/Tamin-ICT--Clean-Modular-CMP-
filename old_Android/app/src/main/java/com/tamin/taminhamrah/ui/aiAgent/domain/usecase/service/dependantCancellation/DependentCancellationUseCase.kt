package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.dependantCancellation

import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import javax.inject.Inject

class DependentCancellationUseCase @Inject constructor(
    private val repository: ServiceRepository
) : ServiceUseCase {

    override val serviceName =
        ServiceNameEnum.DEPENDENT_CANCELLATION

    override suspend fun execute(params: ServiceParams): ServiceResult {

        return try {

            val nationalCode =
                params.payload?.get("nationalCode")?.toString()

            if (nationalCode.isNullOrEmpty()) {
                return ServiceResult.Failure(
                    Exception("کد ملی معتبر نیست")
                )
            }

            val response = repository.getDependentInfo()

            if (!response.isSuccess || response.data == null) {

                return ServiceResult.Failure(
                    Exception(response.reason ?: "خطا در دریافت اطلاعات")
                )
            }

            val dependent =
                response.data?.list
                    ?.map { it.dependentInfo }
                    ?.firstOrNull {
                        it.identityInfo.nationalId == nationalCode
                    }

            if (dependent == null) {

                return ServiceResult.Failure(
                    Exception("فرد مورد نظر یافت نشد")
                )
            }

            val payload = mutableMapOf<String, Any?>()

            payload["fullName"] =
                "${dependent.identityInfo.firstName} ${dependent.identityInfo.lastName}"

            payload["nationalCode"] =
                dependent.identityInfo.nationalId

            payload["relation"] =
                dependent.familyRelationShip.getRelationRes(
                    dependent.identityInfo.gender.genderCode
                )

            val formResponse = ServiceResponse(
                action = params.serviceName,
                title = "حذف افراد تبعی",
                data = ServiceData.GenerativeForm(
                    schema = buildDependentCancellationSchema(
                        payload = payload,
                        step = 1,
                        showCancelButton = true
                    ),
                    payload = payload.mapValues {
                        it.value?.toString()
                    }
                )
            )

            ServiceResult.Success(listOf(formResponse))

        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}