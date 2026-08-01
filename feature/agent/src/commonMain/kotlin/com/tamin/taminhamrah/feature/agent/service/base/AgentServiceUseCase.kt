package com.tamin.taminhamrah.feature.agent.service.base

import com.tamin.taminhamrah.model.agent.AgentActionKey

/**
 * Base contract for all Agent service use cases.
 *
 * Every service must implement this interface.
 * [AgentServiceRegistry] uses this interface to find the appropriate handler.
 *
 * Implementation example:
 * ```kotlin
 * class DastmozdInfosService(
 *     private val getDastmozdInfosUseCase: GetDastmozdInfosUseCase
 * ) : AgentServiceUseCase {
 *
 *     override val actionKey = AgentActionKey.DASTMOZD_INFOS
 *
 *     override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
 *         // Execute service and return result
 *     }
 * }
 * ```
 */
interface AgentServiceUseCase {
    /** Service identifier — must be unique */
    val supportedKeys: List<AgentActionKey>

    /**
     * Executes the service.
     * @param params Input parameters from AI + session context
     * @return Result as [AgentServiceResult]
     */
    suspend fun execute(params: AgentServiceParams): AgentServiceResult
}
