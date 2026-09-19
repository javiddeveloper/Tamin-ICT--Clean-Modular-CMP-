package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.agent.AgentAccessDN
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class FakeAgentAccessStore(initial: AgentAccessDN? = null) : AgentAccessStore {
    private val state = MutableStateFlow(initial)
    override val access: StateFlow<AgentAccessDN?> = state

    /** Every token the store held right before a check asked the server, in order. */
    val tokensSeenAtClear = mutableListOf<String?>()

    override fun save(access: AgentAccessDN) {
        state.value = access
    }

    override fun updateChatToken(token: String?) = state.update { it?.copy(chatToken = token) }

    override fun clearChatToken() {
        tokensSeenAtClear.add(state.value?.chatToken)
        updateChatToken(null)
    }

    override fun clear() {
        state.value = null
    }
}
