package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.impl.LawAgentService
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class LawAgentServiceTest {

    private val json = Json { ignoreUnknownKeys = true }
    private val service = LawAgentService(json)

    @Test
    fun `execute parses payload correctly and returns Markdown bubble`() = runTest {
        // Arrange
        val payloadStr = """
            {
              "description": "قانون شماره ۱: این یک تست است."
            }
        """.trimIndent()
        
        val payloadElement = json.parseToJsonElement(payloadStr)
        val params = AgentServiceParams(
            payload = payloadElement,
            rawData = null,
            message = "پاسخ سوال شما:",
            sessionContext = AgentSessionContext()
        )

        // Act
        val result = service.execute(params)

        // Assert
        val success = assertIs<AgentServiceResult.Success>(result)
        assertEquals(2, success.bubbles.size)
        
        // First bubble should be text message
        val textBubble = assertIs<ChatBubbleContent.Text>(success.bubbles[0])
        assertEquals("پاسخ سوال شما:", textBubble.message)
        
        // Second bubble should be Markdown/Text list
        val markdownBubble = assertIs<ChatBubbleContent.Text>(success.bubbles[1])
        assertEquals("قانون شماره ۱: این یک تست است.", markdownBubble.message)
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
        assertEquals("اطلاعات قانون دریافت نشد.", errorBubble.message)
    }
}
