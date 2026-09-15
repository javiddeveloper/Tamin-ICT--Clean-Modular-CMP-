package com.tamin.taminhamrah.useCases.agent

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.agent.AgentAccessDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import com.tamin.taminhamrah.repository.FakeAgentAccessStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ObserveAgentAvailabilityUseCaseTest {

    private class AgentFlagManager(private val status: FeatureStatus?) : FeatureManager {
        override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> =
            if (status == null) flow { throw IllegalStateException("menu unavailable") } else flowOf(status)
        override suspend fun isFeatureEnabled(flag: FeatureFlag) = status is FeatureStatus.Enabled
        override suspend fun getDisabledMessage(flag: FeatureFlag): String? = null
    }

    private fun access(canStartChat: Boolean) =
        AgentAccessDN(canStartChat = canStartChat, canSendVoice = false, chatToken = null, errorMessage = null)

    private suspend fun availability(status: FeatureStatus?, access: AgentAccessDN?): Boolean =
        ObserveAgentAvailabilityUseCase(AgentFlagManager(status), FakeAgentAccessStore(access))().first()

    @Test
    fun `shown only when the flag is enabled and the server allowed chatting`() = runTest {
        assertEquals(true, availability(FeatureStatus.Enabled, access(true)))
    }

    @Test
    fun `hidden when the server refused or was never asked`() = runTest {
        assertEquals(false, availability(FeatureStatus.Enabled, access(false)))
        assertEquals(false, availability(FeatureStatus.Enabled, null))
    }

    @Test
    fun `hidden when the flag is off, even for an allowed user`() = runTest {
        assertEquals(false, availability(FeatureStatus.Disabled("off"), access(true)))
        assertEquals(false, availability(FeatureStatus.TemporaryDisabled(null), access(true)))
        assertEquals(false, availability(FeatureStatus.EnabledWithError("err"), access(true)))
    }

    @Test
    fun `hidden when the menu cannot be read`() = runTest {
        assertEquals(false, availability(null, access(true)))
    }
}
