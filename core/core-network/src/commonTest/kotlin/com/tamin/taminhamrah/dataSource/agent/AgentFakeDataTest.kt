package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentMockMode
import com.tamin.taminhamrah.model.agent.AgentPromptTypeDTO
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * The fake agent data source serves a hand-written JSON fixture. A parse failure there
 * breaks the whole assistant at runtime, so both fixtures are decoded here with the same
 * [Json] configuration the app uses.
 */
class AgentFakeDataTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Test
    fun `complete fake response decodes and carries every entity`() {
        val data = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_RESPONSE)

        assertEquals("DONE", data.status)
        val result = assertNotNull(data.result, "result must be present")
        val entities = assertNotNull(result.entities, "entities must be present")
        assertTrue(
            entities.size > 50,
            "expected the complete fixture, got ${entities.size} entities"
        )
        assertTrue(
            entities.all { !it.key.isNullOrBlank() },
            "every entity must carry an action key"
        )
    }

    @Test
    fun `showcase fixture decodes and asks for one bubble variant per entity`() {
        val data = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_SHOWCASE_RESPONSE)

        val entities = assertNotNull(data.result?.entities)
        assertTrue(entities.all { it.key == "showcase" }, "every entity drives the showcase")

        // Every entity carries its own content, keyed by the bubble type it drives.
        val types = entities.mapNotNull { entity ->
            (entity.payload as? kotlinx.serialization.json.JsonObject)
                ?.get("type")
                ?.let { (it as? kotlinx.serialization.json.JsonPrimitive)?.content }
        }
        assertEquals(entities.size, types.size, "every entity needs a payload type")
        assertTrue(types.size >= 10, "expected the full bubble tour, got ${types.size}")
        // The tour must cover the media and data views, not just text.
        assertTrue(types.containsAll(listOf("rich_text", "image", "table", "chart", "video", "voice")))
    }

    @Test
    fun `short fake response decodes`() {
        val data = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_ONE_RESPONSE)

        val entities = assertNotNull(assertNotNull(data.result).entities)
        assertTrue(entities.isNotEmpty())
        assertTrue(entities.all { !it.key.isNullOrBlank() }, "every entity needs an action key")
    }

    @Test
    fun `fake data source serves a usable fixture whichever one is selected`() = runTest {
        // Which fixture is wired up is a development toggle, so this asserts the shape
        // every one of them must have rather than pinning today's choice.
        val source = AgentRemoteDataSourceFakeImpl(json)

        val response = source.trackRequest("any-id")

        assertEquals("DONE", response.data?.status)
        val entities = assertNotNull(response.data?.result?.entities, "entities must be present")
        assertTrue(entities.isNotEmpty(), "a fixture with no entities renders an empty chat")
        assertTrue(entities.all { !it.key.isNullOrBlank() }, "every entity needs an action key")
    }

    @Test
    fun `edge case fixture decodes and covers the shapes the other fixtures miss`() {
        val data = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_EDGE_CASES_RESPONSE)

        val entities = assertNotNull(data.result?.entities)
        val keys = entities.map { it.key }
        // Data-driven client services and the unknown-key fallbacks, none of which need a backend.
        assertTrue(keys.containsAll(listOf("law", "appoinmet", "general_response", "message", "disability_pension")))
        assertTrue(keys.any { it !in KNOWN_KEYS }, "an unknown key must be part of the tour")
        val payloadTypes = entities.mapNotNull { (it.payload as? JsonObject)?.get("type")?.let { t -> (t as? JsonPrimitive)?.content } }
        assertTrue(payloadTypes.containsAll(listOf("throw", "hologram")), "a throwing service and an unknown payload type")
        assertTrue(payloadTypes.count { it == "error" } >= 1)
    }

    @Test
    fun `a prompt without a keyword gets every fixture in one answer, renumbered`() = runTest {
        val source = AgentRemoteDataSourceFakeImpl(json)
        source.sendServicePrompt(request("سوابق من را نشان بده"))

        val entities = assertNotNull(source.trackRequest("id").data?.result?.entities)

        val expected = listOf(FAKE_AGENT_MARKDOWN_RESPONSE, FAKE_AGENT_SHOWCASE_RESPONSE, FAKE_AGENT_EDGE_CASES_RESPONSE)
            .sumOf { json.decodeFromString<PollingDataDTO>(it).result?.entities?.size ?: 0 }
        assertEquals(expected, entities.size)
        // The repository sorts on step_number; every fixture starts at 1, so they must be renumbered.
        assertEquals((1..expected).toList(), entities.map { it.stepNumber })
    }

    @Test
    fun `keywords pick the polling outcome`() = runTest {
        suspend fun statusFor(prompt: String): String? {
            val source = AgentRemoteDataSourceFakeImpl(json)
            source.sendServicePrompt(request(prompt))
            return source.trackRequest("id").data?.status
        }

        assertEquals("FAILED", statusFor("failed"))
        assertEquals("FAILED", statusFor("یک خطای سرور بده"))
        assertEquals("CANCEL", statusFor("لغو"))
        assertEquals("PENDING", statusFor("timeout"))
        assertEquals("DONE", statusFor("راهنما"))
    }

    @Test
    fun `a failed silent prompt carries no server message but failed does`() = runTest {
        val silent = AgentRemoteDataSourceFakeImpl(json)
        silent.sendServicePrompt(request("failed-silent"))
        assertEquals(null, silent.trackRequest("id").data?.message)

        val loud = AgentRemoteDataSourceFakeImpl(json)
        loud.sendServicePrompt(request("failed"))
        assertTrue(!loud.trackRequest("id").data?.message.isNullOrBlank())
    }

    @Test
    fun `token scenario rejects the first send and answers the resend`() = runTest {
        val source = AgentRemoteDataSourceFakeImpl(json)

        assertFailsWith<ChatTokenExpiredException> { source.sendServicePrompt(request("توکن")) }

        assertEquals("PENDING", source.sendServicePrompt(request("توکن")).data?.status)
        assertEquals("DONE", source.trackRequest("id").data?.status)
    }

    @Test
    fun `chat allowed follows the mock mode`() = runTest {
        var mode = AgentMockMode.RESPONSES
        val source = AgentRemoteDataSourceFakeImpl(json) { mode }

        assertEquals(true, source.checkChatAllowed().data?.canSendVoice)

        mode = AgentMockMode.NO_VOICE
        assertEquals(false, source.checkChatAllowed().data?.canSendVoice)

        mode = AgentMockMode.ACCESS_DENIED
        val refused = assertNotNull(source.checkChatAllowed().data)
        assertEquals(false, refused.canStartChat)
        assertTrue(!refused.errorMessage.isNullOrBlank(), "a refusal needs a reason to show")

        mode = AgentMockMode.OFFLINE
        assertFailsWith<IllegalStateException> { source.checkChatAllowed() }
    }

    private fun request(prompt: String) = AgentRequestDTO(
        prompt = prompt,
        sessionId = "category_test",
        lastEntity = "",
        chatToken = "fake-token-0",
        userType = "INSURED",
        personalInfo = null,
        promptType = AgentPromptTypeDTO.TEXT,
        state = null,
    )

    private companion object {
        val KNOWN_KEYS = AgentActionKey.entries.map { it.key }.toSet()
    }
}
