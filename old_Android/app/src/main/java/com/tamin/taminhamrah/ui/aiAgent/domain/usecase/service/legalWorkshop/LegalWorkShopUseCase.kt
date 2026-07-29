package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.legalWorkshop

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

class LegalWorkShopUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.COMPLETE_INFO_OF_REAL_WORKSHOP

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
                                "mytamin://complete_workshop_info".toUri().buildUpon()
                                    .appendQueryParameter("TOOLBAR_TITLE", context.getString(R.string.label_complete_legal_workshop_info))
                                    .build().toString(),
                                context.getString(R.string.label_complete_legal_workshop_info)
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
