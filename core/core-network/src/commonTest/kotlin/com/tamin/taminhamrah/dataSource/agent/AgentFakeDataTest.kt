package com.tamin.taminhamrah.dataSource.agent

import com.tamin.taminhamrah.model.agent.PollingDataDTO
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
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

        val result = assertNotNull(data.result)
        assertEquals(4, assertNotNull(result.entities).size)
    }

    @Test
    fun `fake data source returns the complete list from trackRequest`() = runTest {
        val source = AgentRemoteDataSourceFakeImpl(json)

        val response = source.trackRequest("any-id")

        val entities = assertNotNull(response.data?.result?.entities, "entities must be present")
        assertTrue(
            entities.all { it.key == "showcase" },
            "the fake source currently serves the showcase fixture"
        )
    }
}
