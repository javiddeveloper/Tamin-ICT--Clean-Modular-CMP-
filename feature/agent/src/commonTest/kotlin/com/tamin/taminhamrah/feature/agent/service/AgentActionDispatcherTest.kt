package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

// ─── Fakes ───────────────────────────────────────────────────────────────────

/**
 * Fake FeatureManager for testing purposes.
 * Allows tests to control the state and message of each feature flag.
 */
class FakeFeatureManager : FeatureManager {
    /** Map from FeatureFlag to its status — default is Enabled for all */
    private val statusMap = mutableMapOf<FeatureFlag, FeatureStatus>()
    /** Map from FeatureFlag to its disabled message */
    private val messageMap = mutableMapOf<FeatureFlag, String?>()

    fun setStatus(flag: FeatureFlag, status: FeatureStatus) {
        statusMap[flag] = status
    }

    fun setDisabledMessage(flag: FeatureFlag, message: String?) {
        messageMap[flag] = message
    }

    override fun getFeatureStatus(flag: FeatureFlag): Flow<FeatureStatus> =
        flowOf(statusMap[flag] ?: FeatureStatus.Enabled)

    override suspend fun isFeatureEnabled(flag: FeatureFlag): Boolean =
        statusMap[flag] is FeatureStatus.Enabled || statusMap[flag] == null

    override suspend fun getDisabledMessage(flag: FeatureFlag): String? =
        messageMap[flag]
}

/**
 * A fake AgentServiceUseCase that always succeeds.
 */
class FakeAgentService(
    override val actionKey: AgentActionKey = AgentActionKey.DASTMOZD_INFOS
) : AgentServiceUseCase {
    var executeCallCount = 0

    override suspend fun execute(params: AgentServiceParams): AgentServiceResult {
        executeCallCount++
        return AgentServiceResult.Success(
            bubbles = listOf(ChatBubbleContent.Text("Fake Dastmozd Response"))
        )
    }
}

// ─── Tests ───────────────────────────────────────────────────────────────────

/**
 * AgentActionDispatcher Tests
 *
 * Covered scenarios:
 * 1. Service enabled -> Success
 * 2. Service disabled + AI server message -> Returns AI message
 * 3. Service disabled + FeatureManager message -> Returns FeatureManager message
 * 4. Service disabled + no message anywhere -> Returns fallback text
 * 5. No handler available -> Returns NoHandler
 * 6. General service (null FeatureFlag) -> Skips check and executes
 */
class AgentActionDispatcherTest {

    private lateinit var fakeFeatureManager: FakeFeatureManager
    private lateinit var fakeService: FakeAgentService
    private lateinit var registry: AgentServiceRegistry
    private lateinit var dispatcher: AgentActionDispatcher
    private val context = AgentSessionContext()

    @BeforeTest
    fun setup() {
        fakeFeatureManager = FakeFeatureManager()
        fakeService = FakeAgentService(AgentActionKey.DASTMOZD_INFOS)
        registry = AgentServiceRegistry(listOf(fakeService))
        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        dispatcher = AgentActionDispatcher(registry, fakeFeatureManager, json)
    }

    // ─── Scenario 1: Service Enabled ─────────────────────────────────────────

    @Test
    fun `dispatch - when feature is ENABLED - executes service and returns Success`() = runTest {
        // Arrange
        fakeFeatureManager.setStatus(FeatureFlag.WAGE_AND_HISTORY, FeatureStatus.Enabled)
        val entity = buildEntity(AgentActionKey.DASTMOZD_INFOS)

        // Act
        val result = dispatcher.dispatch(entity, context)

        // Assert
        val successResult = result as AgentServiceResult.Success
        assertEquals(1, fakeService.executeCallCount, "UseCase should be called exactly once")
        assertEquals("Fake Dastmozd Response", (successResult.bubbles.first() as ChatBubbleContent.Text).message)
    }

    // ─── Scenario 2: Disabled + AI Message ───────────────────────────────────

    @Test
    fun `dispatch - when feature DISABLED - uses entity message from AI server first`() = runTest {
        // Arrange: AI server sends a specific disabled message
        fakeFeatureManager.setStatus(FeatureFlag.WAGE_AND_HISTORY, FeatureStatus.Disabled("Updating..."))
        fakeFeatureManager.setDisabledMessage(FeatureFlag.WAGE_AND_HISTORY, "FeatureManager Message")
        val entity = buildEntity(
            action = AgentActionKey.DASTMOZD_INFOS,
            message = "The history service is temporarily unavailable." // AI server message
        )

        // Act
        val result = dispatcher.dispatch(entity, context)

        // Assert: entity.message takes priority over FeatureManager message
        val disabledResult = result as AgentServiceResult.FeatureDisabled
        assertEquals(
            "The history service is temporarily unavailable.",
            disabledResult.message,
            "Should display the AI server message (entity.message)"
        )
        assertEquals(0, fakeService.executeCallCount, "UseCase should not be called")
    }

    // ─── Scenario 3: Disabled + FeatureManager Message ───────────────────────

    @Test
    fun `dispatch - when DISABLED and no AI message - falls back to FeatureManager message`() = runTest {
        // Arrange: AI sends no message, but FeatureManager has a predefined message
        fakeFeatureManager.setStatus(FeatureFlag.WAGE_AND_HISTORY, FeatureStatus.Disabled(null))
        fakeFeatureManager.setDisabledMessage(FeatureFlag.WAGE_AND_HISTORY, "Service is currently updating")
        val entity = buildEntity(
            action = AgentActionKey.DASTMOZD_INFOS,
            message = null // AI sent no message
        )

        // Act
        val result = dispatcher.dispatch(entity, context)

        // Assert
        val disabledResult = result as AgentServiceResult.FeatureDisabled
        assertEquals("Service is currently updating", disabledResult.message)
    }

    // ─── Scenario 4: Disabled + No Message -> Fallback ───────────────────────

    @Test
    fun `dispatch - when DISABLED with no messages anywhere - uses fallback text`() = runTest {
        // Arrange: Neither AI nor FeatureManager provides a message
        fakeFeatureManager.setStatus(FeatureFlag.WAGE_AND_HISTORY, FeatureStatus.TemporaryDisabled(null))
        fakeFeatureManager.setDisabledMessage(FeatureFlag.WAGE_AND_HISTORY, null)
        val entity = buildEntity(AgentActionKey.DASTMOZD_INFOS, message = null)

        // Act
        val result = dispatcher.dispatch(entity, context)

        // Assert: A default fallback text should be shown instead of null
        val disabledResult = result as AgentServiceResult.FeatureDisabled
        assertTrue(disabledResult.message.isNotBlank(), "Fallback message should not be blank")
    }

    // ─── Scenario 5: No Handler Available ────────────────────────────────────

    @Test
    fun `dispatch - when no handler registered - returns NoHandler`() = runTest {
        // Arrange: An action that has no registered handler
        val entity = buildEntity(AgentActionKey.HOKM) // No service is registered for HOKM in the setup

        // Act
        val result = dispatcher.dispatch(entity, context)

        // Assert
        assertIs<AgentServiceResult.NoHandler>(result)
        assertEquals(0, fakeService.executeCallCount)
    }

    // ─── Scenario 6: General Action (No FeatureFlag Required) ────────────────

    @Test
    fun `dispatch - GENERAL_RESPONSE has null FeatureFlag - skips flag check and executes`() = runTest {
        // Arrange: GENERAL_RESPONSE does not require a FeatureFlag
        // FeatureManager should not block the execution
        val generalService = FakeAgentService(AgentActionKey.GENERAL_RESPONSE)
        val localRegistry = AgentServiceRegistry(listOf(generalService))
        val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
        val localDispatcher = AgentActionDispatcher(localRegistry, fakeFeatureManager, json)
        val entity = buildEntity(AgentActionKey.GENERAL_RESPONSE)

        // Act
        val result = localDispatcher.dispatch(entity, context)

        // Assert: Executes directly
        assertIs<AgentServiceResult.Success>(result)
        assertEquals(1, generalService.executeCallCount)
    }

    // ─── Scenario 7: dispatchMessage Edge Cases ──────────────────────────────

    @Test
    fun `dispatchMessage - with blank string - returns NoHandler`() {
        val result = dispatcher.dispatchMessage("")
        assertIs<AgentServiceResult.NoHandler>(result)
    }

    @Test
    fun `dispatchMessage - with null - returns NoHandler`() {
        val result = dispatcher.dispatchMessage(null)
        assertIs<AgentServiceResult.NoHandler>(result)
    }

    @Test
    fun `dispatchMessage - with valid message - returns Success with Text bubble`() {
        val result = dispatcher.dispatchMessage("Hello!")
        val successResult = result as AgentServiceResult.Success
        val bubble = successResult.bubbles.firstOrNull()
        val textBubble = bubble as ChatBubbleContent.Text
        assertEquals("Hello!", textBubble.message)
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun buildEntity(
        action: AgentActionKey,
        message: String? = null
    ) = AiEntityDN(
        action = action,
        stepNumber = 0,
        payload = null,
        data = null,
        message = message,
        itemType = null
    )
}
