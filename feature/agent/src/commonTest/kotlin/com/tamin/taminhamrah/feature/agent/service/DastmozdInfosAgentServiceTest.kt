package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.impl.DastmozdInfosAgentService
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DastmozdInfosAgentServiceTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val service = DastmozdInfosAgentService(json)

    @Test
    fun `execute parses payload correctly and returns KeyValue bubble`() = runTest {
        // Arrange
        val payloadStr = """
            {
              "amount": "1000",
              "date": "1402/01/01",
              "companyName": "Test Company",
              "branchName": "Test Branch",
              "month": "فروردین",
              "year": "1402"
            }
        """.trimIndent()
        
        val payloadElement = json.parseToJsonElement(payloadStr)
        val params = AgentServiceParams(
            payload = payloadElement,
            rawData = null,
            message = "This is a test message",
            sessionContext = AgentSessionContext()
        )

        // Act
        val result = service.execute(params)

        // Assert
        val success = assertIs<AgentServiceResult.Success>(result)
        assertEquals(2, success.bubbles.size)
        
        // First bubble should be text message
        val textBubble = assertIs<ChatBubbleContent.Text>(success.bubbles[0])
        assertEquals("This is a test message", textBubble.message)
        
        // Second bubble should be KeyValue list
        val keyValueBubble = assertIs<ChatBubbleContent.KeyValue>(success.bubbles[1])
        assertEquals("اطلاعات دستمزد", keyValueBubble.title)
        
        // Assert some key values
        val amountPair = keyValueBubble.items.find { it.first == "مبلغ" }
        assertEquals("1000", amountPair?.second)
        
        val companyPair = keyValueBubble.items.find { it.first == "نام کارگاه" }
        assertEquals("Test Company", companyPair?.second)
    }
    
    @Test
    fun `execute returns ServiceError when payload is missing`() = runTest {
        // Arrange
        val params = AgentServiceParams(
            payload = null,
            rawData = null,
            message = null,
            sessionContext = AgentSessionContext()
        )

        // Act
        val result = service.execute(params)

        // Assert
        val success = assertIs<AgentServiceResult.Success>(result)
        val errorBubble = assertIs<ChatBubbleContent.ServiceError>(success.bubbles.first())
        assertEquals("اطلاعات دستمزد دریافت نشد.", errorBubble.message)
    }
}
