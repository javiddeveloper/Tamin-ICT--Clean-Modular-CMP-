package com.tamin.taminhamrah.ui.aiAgent.domain.usecase.service.contract

import android.content.Context
import androidx.core.net.toUri
import com.tamin.taminhamrah.Constants
import com.tamin.taminhamrah.R
import com.tamin.taminhamrah.data.remote.models.ai.agent.DeeplinkDataModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.PromptModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceResponse
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceNameEnum
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceUseCase
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.param.ServiceParams
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RegisterContractUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) : ServiceUseCase {
    override val serviceName: ServiceNameEnum = ServiceNameEnum.REGISTER_CONTRACT

    override suspend fun execute(params: ServiceParams): ServiceResult {
        return try {
            val prompts = params.data?.mapNotNull { item ->
                if (item is DeeplinkDataModel) {
                    val target = item.deepLink?.to?.trim()?.lowercase()
                    val title = item.title.orEmpty()

                    val (toolbarTitleRes, uriScheme) = when (target) {
                        "contract_freelance" -> Pair(R.string.title_freelance_contract_fragment, "mytamin://contract_freelance")
                        "contract_optional" -> Pair(R.string.title_optional_contract_fragment, "mytamin://contract_optional")
                        "contract_woman" -> Pair(R.string.title_women_contract_fragment, "mytamin://contract_woman")
                        "contract_student" -> Pair(R.string.title_student_contract_fragment, "mytamin://contract_student")
                        else -> Pair(R.string.label_contract_types, "mytamin://contract_freelance")
                    }

                    val toolbarTitle = context.getString(toolbarTitleRes)
                    val uri = uriScheme.toUri().buildUpon()
                        .appendQueryParameter(Constants.TOOLBAR_TITLE, toolbarTitle)
                        .build()
                        .toString()

                    PromptModel(
                        prompt = title,
                        action = AgentActionContent.LocalDeepLink(uri, toolbarTitle)
                    )
                } else {
                    null
                }
            } ?: emptyList()

            val groupButtonData = ServiceData.GroupButton(
                prompts = prompts,
                actionType = AgentActionContent.LocalDeepLink("", "")
            )

            ServiceResult.Success(
                listOf(
                    ServiceResponse(
                        action = params.serviceName,
                        title = params.message,
                        data = groupButtonData
                    )
                )
            )
        } catch (e: Exception) {
            ServiceResult.Failure(e)
        }
    }
}
