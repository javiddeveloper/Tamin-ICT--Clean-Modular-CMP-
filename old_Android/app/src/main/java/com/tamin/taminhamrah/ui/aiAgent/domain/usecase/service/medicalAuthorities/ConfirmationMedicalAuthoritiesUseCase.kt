package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.medicalAuthorities

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ConfirmationMedicalAuthoritiesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.CONFIRMATION_MEDICAL_AUTHORITIES

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        params.serviceName,
                        title = params.message,
                        data = ServiceData.Clickable(
                            message = emptyList(),
                            actionType = AgentActionContent.LocalDeepLink(
                                "mytamin://confirmation_medical_authorities".toUri().buildUpon()
                                    .appendQueryParameter("TOOLBAR_TITLE", context.getString(R.string.confirmations_medical_authorities))
                                    .build().toString(),
                                "مشاهده تائیدیه های مراجع پزشکی"
                            )
                        )
                    )
                )
            )
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
