package com.tamin.taminhamrah.ui.aiAgent.mapper

import com.tamin.taminhamrah.data.repository.ai.model.AgentActionContent
import com.tamin.taminhamrah.data.repository.ai.model.AgentClickableModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentGroupButtonModel
import com.tamin.taminhamrah.data.repository.ai.model.AgentLawChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiChatModel
import com.tamin.taminhamrah.data.repository.ai.model.AiErrorModel
import com.tamin.taminhamrah.data.repository.ai.model.AiGenerativeModel
import com.tamin.taminhamrah.data.repository.ai.model.AiHeaderModel
import com.tamin.taminhamrah.data.repository.ai.model.AiKeyValueModel
import com.tamin.taminhamrah.data.repository.ai.model.AiTextModel
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData
import com.tamin.taminhamrah.ui.aiAgent.domain.usecase.base.ServiceResult


fun ServiceResult.toChatMessages(): List<AiChatModel> {
    return when (this) {
        is ServiceResult.Error -> {
            listOf(AiErrorModel(message = errorMessage))
        }

        is ServiceResult.Failure -> {
            listOf(AiErrorModel(message = error.message ?: "خطایی رخ داده است"))
        }

        is ServiceResult.Success -> {
            data.map { entity ->
                when (entity.data) {
                    is ServiceData.StringMessage -> {
                        AiTextModel(title = entity.title, message = entity.data.message)
                    }

                    is ServiceData.DeeplinkWeb -> {
                        AiTextModel(title = entity.title, message = entity.data.link)
                    }

                    is ServiceData.KeyValueMessage -> {
                        AiKeyValueModel(title = entity.title, keyValueItems = entity.data.message)
                    }

                    is ServiceData.Clickable -> {
                        AgentClickableModel(
                            title = entity.title,
                            content = entity.data.message,
                            actionContent = entity.data.actionType
                        )
                    }

                    is ServiceData.GenerativeForm -> {
                        AiGenerativeModel(
                            schema = entity.data.schema,
                            data = entity.data.payload
                        )
                    }

                    ServiceData.HeaderMessage -> {
                        AiHeaderModel(title = entity.title)
                    }

                    is ServiceData.GroupButton -> {
                        AgentGroupButtonModel(
                            title = entity.title,
                            prompts = entity.data.prompts,
                            actionContent = entity.data.actionType,
                            content = entity.data.content
                        )
                    }

                    is ServiceData.Law -> {
                        val agentLaw = entity.data.agentLaw
                        AgentLawChatModel(
                            name = agentLaw.name,
                            reference = agentLaw.reference,
                            content = agentLaw.content,
                            sourceFile = agentLaw.sourceFile,
                            score = agentLaw.score,
                            url = agentLaw.url,
                            actionContent = AgentActionContent.Web(agentLaw.url, agentLaw.name)
                        )
                    }
                }
            }
        }
    }
}
