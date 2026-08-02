package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceUseCase
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.buildBubbles
import com.tamin.taminhamrah.feature.agent.service.base.getFilters
import com.tamin.taminhamrah.feature.agent.AgentDestination
import com.tamin.taminhamrah.feature.agent.service.impl.DeepLinkAgentService
import com.tamin.taminhamrah.feature.agent.service.impl.GeneralResponseAgentService
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Guards the service registry wiring: an action key must be claimed by at most one
 * handler, because [AgentServiceRegistry.get] resolves with `firstOrNull` and a
 * duplicate would silently shadow the other handler.
 */
class AgentServiceKeyCoverageTest {

    private fun params(
        key: AgentActionKey,
        message: String? = null,
        payload: kotlinx.serialization.json.JsonElement? = null
    ) = AgentServiceParams(
        payload = payload,
        rawData = null,
        message = message,
        sessionContext = AgentSessionContext(),
        requestedKey = key
    )

    @Test
    fun `no action key is claimed by more than one service`() {
        // Services with no external dependencies can be constructed directly; the
        // dependency-injected ones are represented by their declared key lists.
        val allKeyLists: List<List<AgentActionKey>> = listOf(
            GeneralResponseAgentService().supportedKeys,
            DeepLinkAgentService().supportedKeys,
            LAW_KEYS,
            DASTMOZD_KEYS,
            AVERAGE_WAGE_KEYS,
            PROFILE_KEYS,
            DEPENDENT_KEYS,
            PENSION_INQUIRY_KEYS,
            PAYROLL_KEYS,
            EDICT_KEYS,
            BOOKLET_KEYS,
            JOB_HISTORY_KEYS
        )

        val duplicates = allKeyLists.flatten()
            .groupingBy { it }
            .eachCount()
            .filterValues { it > 1 }
            .keys

        assertTrue(
            duplicates.isEmpty(),
            "These action keys are handled by more than one service: $duplicates"
        )
    }

    @Test
    fun `registry resolves a deep link key to the deep link service`() {
        val registry = AgentServiceRegistry(listOf(DeepLinkAgentService()))
        val handler = registry.get(AgentActionKey.DISABILITY_PENSION)
        assertIs<DeepLinkAgentService>(handler)
    }

    @Test
    fun `deep link service emits a text bubble and a deep link bubble`() = runTest {
        val result = DeepLinkAgentService().execute(params(AgentActionKey.DISABILITY_PENSION))

        assertIs<AgentServiceResult.Success>(result)
        assertEquals(2, result.bubbles.size)
        assertIs<ChatBubbleContent.Text>(result.bubbles[0])
        val link = result.bubbles[1]
        assertIs<ChatBubbleContent.DeepLink>(link)
        assertEquals(AgentDestination.DISABILITY_PENSION, link.destination)
    }

    @Test
    fun `general response renders the model message`() = runTest {
        val result = GeneralResponseAgentService().execute(
            params(AgentActionKey.GENERAL_RESPONSE, message = "سلام، چطور می‌توانم کمک کنم؟")
        )

        assertIs<AgentServiceResult.Success>(result)
        val bubble = result.bubbles.single()
        assertIs<ChatBubbleContent.Text>(bubble)
        assertEquals("سلام، چطور می‌توانم کمک کنم؟", bubble.message)
    }

    @Test
    fun `general response falls back when the model sent nothing`() = runTest {
        val result = GeneralResponseAgentService().execute(params(AgentActionKey.MESSAGE))

        assertIs<AgentServiceResult.Success>(result)
        assertTrue(result.bubbles.single() is ChatBubbleContent.Text)
    }

    @Test
    fun `getFilters parses the colon separated filter array`() {
        val payload = Json.parseToJsonElement(
            """{"filter":["startDate:14020101","endDate:14031229"],"year":"1403"}"""
        )
        val filters = params(AgentActionKey.DASTMOZD_INFOS, payload = payload).getFilters()

        assertEquals("14020101", filters["startDate"])
        assertEquals("14031229", filters["endDate"])
        assertEquals("1403", filters["year"])
    }

    @Test
    fun `buildBubbles does not add suggested prompts because the dispatcher does`() {
        // Prompts live in rawData; the dispatcher appends them centrally, so a service
        // must not emit them again or the chips would render twice.
        val raw = Json.parseToJsonElement(
            """[{"item_type":"prompt_item","prompt":"سابقه من را نشان بده"}]"""
        )
        val p = AgentServiceParams(
            payload = null,
            rawData = raw,
            message = null,
            sessionContext = AgentSessionContext(),
            requestedKey = AgentActionKey.DASTMOZD_INFOS
        )

        val bubbles = p.buildBubbles { add(ChatBubbleContent.Text("x")) }

        assertEquals(1, bubbles.size)
        assertTrue(bubbles.none { it is ChatBubbleContent.SuggestedPrompts })
    }

    private companion object {
        val LAW_KEYS = listOf(AgentActionKey.LAW)
        val DASTMOZD_KEYS = listOf(
            AgentActionKey.DASTMOZD_INFOS,
            AgentActionKey.DASTMOZD_INFOS_LAST,
            AgentActionKey.DASTMOZD_INFOS_PER_YEAR,
            AgentActionKey.DASTMOZD_INFOS_SALARY,
            AgentActionKey.DASTMOZD_INFOS_SUM_TOTAL
        )
        val AVERAGE_WAGE_KEYS = listOf(
            AgentActionKey.AVERAGE_DASTMOZD_INFOS,
            AgentActionKey.AVERAGE_DASTMOZD_INFOS_PER_DATE,
            AgentActionKey.DASTMOZD_INFOS_LAST_PAY
        )
        val PROFILE_KEYS = listOf(AgentActionKey.PROFILE_INFO)
        val DEPENDENT_KEYS = listOf(AgentActionKey.GET_DEPENDENT)
        val PENSION_INQUIRY_KEYS = listOf(
            AgentActionKey.PENSION_INQUIRY_ALL,
            AgentActionKey.PENSION_INQUIRY_LAST
        )
        val PAYROLL_KEYS = listOf(AgentActionKey.FISH, AgentActionKey.FISH_LAST)
        val EDICT_KEYS = listOf(AgentActionKey.HOKM, AgentActionKey.HOKM_LAST)
        val BOOKLET_KEYS = listOf(AgentActionKey.BOOKLET)
        val JOB_HISTORY_KEYS = listOf(
            AgentActionKey.HISTORY_JOB_INFOS,
            AgentActionKey.HISTORY_JOB_INFOS_LAST
        )
    }
}
