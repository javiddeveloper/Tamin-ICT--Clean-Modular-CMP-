package com.tamin.taminhamrah.ui.aiAgent.mapper

import com.tamin.taminhamrah.data.entity.ai.AiChatMessageEntity
import com.tamin.taminhamrah.data.entity.ai.MessageSender
import com.tamin.taminhamrah.data.entity.ai.MessageStatus
import com.tamin.taminhamrah.data.repository.ai.model.*
import com.tamin.taminhamrah.ui.aiAgent.domain.ServiceData

fun AiChatMessageEntity.toAiChatModels(): List<AiChatModel> {
    val models = mutableListOf<AiChatModel>()

    if (!serviceResponses.isNullOrEmpty()) {
        serviceResponses.forEachIndexed { index, response ->
            val chatModel = when (val data = response.data) {
                is ServiceData.StringMessage -> {
                    AiTextModel(
                        title = response.title,
                        message = data.message,
                        isUserMessage = false
                    )
                }

                is ServiceData.DeeplinkWeb -> {
                    AiTextModel(title = response.title, message = data.link, isUserMessage = false)
                }

                is ServiceData.KeyValueMessage -> {
                    AiKeyValueModel(title = response.title, keyValueItems = data.message)
                }

                is ServiceData.Clickable -> {
                    AgentClickableModel(
                        title = response.title,
                        content = data.message,
                        actionContent = data.actionType
                    )
                }

                is ServiceData.HeaderMessage -> {
                    AiHeaderModel(title = response.title)
                }

                is ServiceData.GroupButton -> {
                    val firstColor = data.prompts
                        .map { it.action }
                        .filterIsInstance<AgentActionContent.DisplayReport>()
                        .firstOrNull()?.customColor

                    AgentGroupButtonModel(
                        title = response.title,
                        prompts = data.prompts,
                        actionContent = data.actionType,
                        color = firstColor,
                        content = data.content
                    )
                }

                is ServiceData.GenerativeForm -> {
                    AiGenerativeModel(
                        schema = data.schema,
                        data = data.payload
                    )
                }

                is ServiceData.Law -> {
                    AiTextModel(
                        title = response.title,
                        message = data.agentLaw.content ?: "",
                        isUserMessage = false,
                    )

                }
            }
            // Set stable ID using entity ID and index
           if (chatModel is TypingAnimatable){
               (chatModel as? TypingAnimatable)?.id = "${id}_$index"
           }
            models.add(chatModel)
        }
    } else if (!voicePath.isNullOrEmpty()) {
        val voiceModel = VoiceModel(
            path = voicePath,
            duration = 0
        )
        voiceModel.id = id.toString()
        models.add(voiceModel)
    } else if (!message.isNullOrEmpty()) {
        val chatModel = if (status == MessageStatus.FAILED) {
            AiErrorModel(
                message = message,
                shouldStartTyping = false,
                isTypingComplete = true,
                skipTyping = true
            )
        } else {
            AiTextModel(
                message = message,
                isUserMessage = sender == MessageSender.USER
            )
        }
        chatModel.id = id.toString()
        models.add(chatModel)
    }

    return models
}
