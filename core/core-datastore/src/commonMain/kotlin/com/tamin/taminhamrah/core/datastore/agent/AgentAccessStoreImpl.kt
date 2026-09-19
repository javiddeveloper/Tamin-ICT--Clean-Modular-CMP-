package com.tamin.taminhamrah.core.datastore.agent

import com.russhwolf.settings.Settings
import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.repository.AgentAccessStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AgentAccessStoreImpl(private val settings: Settings) : AgentAccessStore {

    private val _access = MutableStateFlow(read())

    override val access: StateFlow<AgentAccessDN?> = _access.asStateFlow()

    override fun save(access: AgentAccessDN) {
        settings.putBoolean(KEY_CAN_START_CHAT, access.canStartChat)
        settings.putBoolean(KEY_CAN_SEND_VOICE, access.canSendVoice)
        putOrRemove(KEY_CHAT_TOKEN, access.chatToken)
        putOrRemove(KEY_ERROR_MESSAGE, access.errorMessage)
        _access.value = access
    }

    override fun updateChatToken(token: String?) {
        putOrRemove(KEY_CHAT_TOKEN, token)
        _access.update { it?.copy(chatToken = token) }
    }

    override fun clearChatToken() = updateChatToken(null)

    override fun clear() {
        listOf(KEY_CAN_START_CHAT, KEY_CAN_SEND_VOICE, KEY_CHAT_TOKEN, KEY_ERROR_MESSAGE)
            .forEach(settings::remove)
        _access.value = null
    }

    private fun read(): AgentAccessDN? {
        if (!settings.hasKey(KEY_CAN_START_CHAT)) return null
        return AgentAccessDN(
            canStartChat = settings.getBoolean(KEY_CAN_START_CHAT, false),
            canSendVoice = settings.getBoolean(KEY_CAN_SEND_VOICE, false),
            chatToken = settings.getStringOrNull(KEY_CHAT_TOKEN),
            errorMessage = settings.getStringOrNull(KEY_ERROR_MESSAGE),
        )
    }

    private fun putOrRemove(key: String, value: String?) {
        if (value.isNullOrBlank()) settings.remove(key) else settings.putString(key, value)
    }

    private companion object {
        const val KEY_CAN_START_CHAT = "AGENT_CAN_START_CHAT"
        const val KEY_CAN_SEND_VOICE = "AGENT_CAN_SEND_VOICE"
        const val KEY_CHAT_TOKEN = "AGENT_CHAT_TOKEN"
        const val KEY_ERROR_MESSAGE = "AGENT_CHAT_ERROR_MESSAGE"
    }
}
