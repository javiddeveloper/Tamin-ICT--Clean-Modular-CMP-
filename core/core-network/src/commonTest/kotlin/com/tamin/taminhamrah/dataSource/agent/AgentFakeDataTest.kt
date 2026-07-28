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
            entities.size > 50,
            "the fake source must serve the complete fixture, got ${entities.size}"
        )
    }
}
