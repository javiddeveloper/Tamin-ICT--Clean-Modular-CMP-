package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.profile

import android.content.Context
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AddDependentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.ADD_DEPENDENT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val serviceResponse = ServiceResponse(
                action = params.serviceName,
                title = params.message ?: "افزودن افراد تبعی",
                data = ServiceData.StringMessage("برای افزودن افراد تبعی روی دکمه زیر کلیک کنید."),
//                buttonText = "افزودن افراد تبعی",
//                isDeepLink = true,
//                deepLinkUri = "tamin://tamin.ir/addDependent"
            )
            ServiceResult.Success(listOf(serviceResponse))
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
