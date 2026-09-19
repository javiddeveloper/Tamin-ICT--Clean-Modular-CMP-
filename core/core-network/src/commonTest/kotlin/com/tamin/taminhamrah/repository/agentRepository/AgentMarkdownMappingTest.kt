package com.tamin.taminhamrah.repository.agentRepository

import com.tamin.taminhamrah.dataSource.agent.FAKE_AGENT_MARKDOWN_RESPONSE
import com.tamin.taminhamrah.dataSource.agent.toRequestBody
import com.tamin.taminhamrah.model.agent.AgentPersonalInfoDN
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentItemType
import com.tamin.taminhamrah.model.agent.AgentPollingState
import com.tamin.taminhamrah.model.agent.AgentRenderMode
import com.tamin.taminhamrah.model.agent.AgentRequest
import com.tamin.taminhamrah.model.agent.AgentRequestDTO
import com.tamin.taminhamrah.model.agent.AgentResponseDTO
import com.tamin.taminhamrah.model.agent.ChatTokenExpiredException
import com.tamin.taminhamrah.model.agent.PollingDataDTO
import com.tamin.taminhamrah.model.agent.PollingResponseDTO
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/** The server's markdown entities, from wire JSON to domain, and the prompt request fields. */
class AgentMarkdownMappingTest {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    private suspend fun doneFor(result: AgentResponseDTO) = AgentRepositoryImpl(
        FakeAgentRemoteDataSource().apply {
            sendServicePromptResult = PollingResponseDTO(data = PollingDataDTO(id = "req", eta = 0))
            trackRequestResult = PollingResponseDTO(data = PollingDataDTO(id = "req", status = "DONE", result = result))
        }
    ).sendPrompt(AgentRequest("q")).toList().filterIsInstance<AgentPollingState.Done>().single().response

    @Test
    fun `the markdown fixture decodes with snake case entity fields`() {
        val data = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_MARKDOWN_RESPONSE)
        val result = data.result!!
        assertEquals("SERVER", result.renderMode)
        val entities = result.entities!!
        assertEquals(4, entities.size)
        assertTrue(entities.all { it.itemType == "markdown" })
        assertEquals(listOf(1, 2, 3, 4), entities.map { it.stepNumber })
    }

    @Test
    fun `markdown entities carry their non blank texts in order`() = runTest {
        val result = json.decodeFromString<PollingDataDTO>(FAKE_AGENT_MARKDOWN_RESPONSE).result!!
        val response = doneFor(result)

        assertEquals(AgentRenderMode.SERVER, response.renderMode)
        assertTrue(response.entities.all { it.itemType == AgentItemType.MARKDOWN })
        assertTrue(response.entities.all { it.action == AgentActionKey.GENERAL_RESPONSE })
        // The last entity has a blank markdown item and a prompt item: only one text survives.
        assertEquals(listOf(1, 1, 1, 1), response.entities.map { it.markdown.size })
        assertTrue(response.entities.first().markdown.single().startsWith("### "))
    }

    @Test
    fun `camel case entities from the older contract still map`() = runTest {
        val result = json.decodeFromString<AgentResponseDTO>(
            """{"sessionId":"s","entities":[{"key":"message","stepNumber":7,"itemType":"markdown",
               "data":[{"item_type":"markdown_item","text":"متن"}]}]}"""
        )
        val entity = doneFor(result).entities.single()
        assertEquals(7, entity.stepNumber)
        assertEquals(listOf("متن"), entity.markdown)
    }

    @Test
    fun `non markdown entities get no markdown and unknown item types stay null`() = runTest {
        val result = json.decodeFromString<AgentResponseDTO>(
            """{"entities":[{"key":"fish","item_type":"something_new","data":[{"item_type":"markdown_item","text":"x"}]}]}"""
        )
        val response = doneFor(result)
        val entity = response.entities.single()
        assertEquals(null, entity.itemType)
        assertEquals(emptyList(), entity.markdown)
        assertEquals(AgentRenderMode.CLIENT, response.renderMode)
    }

    @Test
    fun `the request carries the user type and token it was given`() = runTest {
        val sent = mutableListOf<AgentRequestDTO>()
        val source = object : com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource by FakeAgentRemoteDataSource() {
            override suspend fun sendServicePrompt(request: AgentRequestDTO, voiceBytes: ByteArray?, voiceFileName: String?): PollingResponseDTO {
                sent.add(request)
                return PollingResponseDTO(data = PollingDataDTO(id = "r", eta = 0))
            }
        }
        AgentRepositoryImpl(source).sendPrompt(AgentRequest("q", chatToken = "t", userType = "PENSIONER")).toList()

        assertEquals("PENSIONER", sent.single().userType)
        assertEquals("t", sent.single().chatToken)
    }

    /** The native app's body, key for key; the server rejects or misreads a body missing any of them. */
    @Test
    fun `a first text prompt writes every key the native app sent, nulls included`() = runTest {
        val body = sentBody(
            AgentRequest(
                prompt = "fdfdf",
                sessionId = "category_1",
                chatToken = "t",
                userType = "INSURED",
                personalInfo = AgentPersonalInfoDN("0012345678", null, "علی", "رضایی"),
            )
        )
        assertEquals(
            Json.parseToJsonElement(
                """{"prompt":"fdfdf","sessionId":"category_1","lastEntity":"","chatToken":"t","userType":"INSURED",
                   "personal_info":{"national_id":"0012345678","pensioner_id":null,"first_name":"علی","last_name":"رضایی"},
                   "prompt_type":"text","state":null,"history":[],"device_type":"MOBILE","response_type":"show_to_user"}"""
            ),
            body,
        )
    }

    @Test
    fun `a later voice prompt returns the server's state and history untouched`() = runTest {
        val body = sentBody(
            AgentRequest(
                prompt = "",
                lastEntity = "fish",
                state = """{"version":2,"pending_form":{"type":"calc29"}}""",
                history = """[{"user":"سلام","assistant":"درود"}]""",
                voiceBytes = byteArrayOf(1),
            )
        ).jsonObject
        assertEquals("\"voice\"", body["prompt_type"].toString())
        assertEquals("\"fish\"", body["lastEntity"].toString())
        assertEquals(Json.parseToJsonElement("""{"version":2,"pending_form":{"type":"calc29"}}"""), body["state"])
        assertEquals(Json.parseToJsonElement("""[{"user":"سلام","assistant":"درود"}]"""), body["history"])
        assertEquals(JsonNull, body["personal_info"])
    }

    @Test
    fun `state and history in the answer reach the domain as their JSON`() = runTest {
        val result = json.decodeFromString<AgentResponseDTO>(
            """{"entities":[],"state":{"version":1,"resume":"x"},"history":[{"user":"a","assistant":"b"}]}"""
        )
        val response = doneFor(result)
        assertEquals(Json.parseToJsonElement("""{"version":1,"resume":"x"}"""), Json.parseToJsonElement(response.state!!))
        assertEquals(Json.parseToJsonElement("""[{"user":"a","assistant":"b"}]"""), Json.parseToJsonElement(response.history!!))
    }

    private suspend fun sentBody(request: AgentRequest): JsonElement {
        val sent = mutableListOf<AgentRequestDTO>()
        val source = object : com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource by FakeAgentRemoteDataSource() {
            override suspend fun sendServicePrompt(request: AgentRequestDTO, voiceBytes: ByteArray?, voiceFileName: String?): PollingResponseDTO {
                sent.add(request)
                return PollingResponseDTO(data = PollingDataDTO(id = "r", eta = 0))
            }
        }
        AgentRepositoryImpl(source).sendPrompt(request).toList()
        // The app's shared client leaves defaults and nulls out; the prompt body must not.
        val appJson = Json { ignoreUnknownKeys = true; explicitNulls = false }
        return Json.parseToJsonElement(sent.single().toRequestBody(appJson))
    }

    @Test
    fun `an expired chat token is raised, not turned into a failure`() = runTest {
        val source = object : com.tamin.taminhamrah.dataSource.agent.AgentRemoteDataSource by FakeAgentRemoteDataSource() {
            override suspend fun sendServicePrompt(request: AgentRequestDTO, voiceBytes: ByteArray?, voiceFileName: String?): PollingResponseDTO =
                throw ChatTokenExpiredException()
        }
        assertFailsWith<ChatTokenExpiredException> { AgentRepositoryImpl(source).sendPrompt(AgentRequest("q")).toList() }
    }

    @Test
    fun `a failed request keeps the server message`() = runTest {
        val repository = AgentRepositoryImpl(
            FakeAgentRemoteDataSource().apply {
                sendServicePromptResult = PollingResponseDTO(data = PollingDataDTO(id = "req", eta = 0))
                trackRequestResult = PollingResponseDTO(data = PollingDataDTO(id = "req", status = "FAILED", message = "سرویس در دسترس نیست"))
            }
        )
        val failed = assertIs<AgentPollingState.Failed>(repository.sendPrompt(AgentRequest("q")).toList().last())
        assertEquals("سرویس در دسترس نیست", failed.message)
    }
}
