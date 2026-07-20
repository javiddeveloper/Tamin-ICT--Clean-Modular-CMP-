package com.tamin.taminhamrah.feature.agent.service.base

import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Central registry for all Agent service use cases.
 *
 * This class replaces `GetServiceUseCase` from the legacy Android version.
 * Instead of Hilt with `@Multibindings`, we use Koin and an explicit list.
 *
 * DI is handled in [AgentModule].
 */
class AgentServiceRegistry(
    private val services: List<AgentServiceUseCase>
) {
    /**
     * Finds the appropriate handler for an [AgentActionKey].
     * @return The corresponding service or null if no handler is registered.
     */
    fun get(actionKey: AgentActionKey): AgentServiceUseCase? =
        services.firstOrNull { it.actionKey == actionKey }

    /** Checks if a handler exists for this key. */
    fun hasHandler(actionKey: AgentActionKey): Boolean =
        services.any { it.actionKey == actionKey }
}
