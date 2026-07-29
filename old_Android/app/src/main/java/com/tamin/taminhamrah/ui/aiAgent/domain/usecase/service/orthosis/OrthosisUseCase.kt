package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.orthosis

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ServiceRepository
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class OrthosisUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.SHORT_TERM_ORTHOSIS
    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {

                ServiceResult.Success(
                    listOf(
                        ServiceResponse(
                            params.serviceName,
//                        itemType = ItemType.KeyValue,
                            title = params.message,
                            data = ServiceData.Clickable(
                                message = emptyList(),
                                actionType = AgentActionContent.LocalDeepLink(
                                    "mytamin://orotez_protez".toUri().buildUpon()
                                        .appendQueryParameter("TOOLBAR_TITLE", context.getString(R.string.label_allowances_orthotics_prosthesis))
                                        .build().toString(),
                                    "مشاهده کمک هزینه اروتز و پروتز"
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
