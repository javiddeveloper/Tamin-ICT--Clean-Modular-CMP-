package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.repository.AgentAccessStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Whether the assistant's entry point may be shown: the [FeatureFlag.AGENT] menu flag is enabled
 * **and** the server's last chat-allowed answer permitted this user. Either one alone is not
 * enough — the flag is per service, the permission is per user.
 */
class ObserveAgentAvailabilityUseCase(
    private val featureManager: FeatureManager,
    private val agentAccessStore: AgentAccessStore,
) {
    operator fun invoke(): Flow<Boolean> = combine(
        featureManager.getFeatureStatus(FeatureFlag.AGENT)
            .map { it is FeatureStatus.Enabled }
            .catch { emit(false) },
        agentAccessStore.access.map { it?.canStartChat == true },
    ) { flagEnabled, chatAllowed -> flagEnabled && chatAllowed }
        .distinctUntilChanged()
}
