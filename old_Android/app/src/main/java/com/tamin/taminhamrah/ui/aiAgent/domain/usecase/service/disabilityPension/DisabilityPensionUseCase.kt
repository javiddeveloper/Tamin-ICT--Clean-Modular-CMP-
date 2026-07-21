package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.disabilityPension

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.KeyValueModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class DisabilityPensionUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.DISABILITY_PENSION

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = params.message,
                        data = ServiceData.Clickable(
                            message = listOf(
                                KeyValueModel(
                                    context.getString(R.string.label_description),
                                    "برای ثبت درخواست برقراری مستمری از کارافتادگی، روی دکمه زیر کلیک کنید."
                                )
                            ),
                            actionType = AgentActionContent.LocalDeepLink(
                                uri = "mytamin://disability_pension".toUri().buildUpon()
                                    .appendQueryParameter(
                                        Constants.TOOLBAR_TITLE,
                                        "مستمری از کارافتادگی"
                                    )
                                    .build()
                                    .toString(),
                                actionText = "برقراری مستمری از کارافتادگی"
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
