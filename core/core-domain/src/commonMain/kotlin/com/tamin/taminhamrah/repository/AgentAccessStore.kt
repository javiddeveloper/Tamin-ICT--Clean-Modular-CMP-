package com.tamin.taminhamrah.repository

import com.tamin.taminhamrah.model.agent.AgentAccessDN
import kotlinx.coroutines.flow.StateFlow

/** Persistent cache of the chat-allowed answer and the chat token that came with it. */
interface AgentAccessStore {

    /** The cached answer, or null when the server has never been asked on this install. */
    val access: StateFlow<AgentAccessDN?>

    fun save(access: AgentAccessDN)

    /** Replaces only the token, e.g. after the network layer refreshed an expired one. */
    fun updateChatToken(token: String?)

    /** Drops the token but keeps the permission, so the entry point does not flicker. */
    fun clearChatToken()

    /** Forgets everything, e.g. on sign-out. */
    fun clear()
}
