package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.impl.DastmozdInfosAgentService
import com.tamin.taminhamrah.model.history.DastmozdInfoDN
import com.tamin.taminhamrah.model.history.DastmozdInfoItemDN
import com.tamin.taminhamrah.model.history.WageDetailDN
import com.tamin.taminhamrah.model.request.ApiFilterDN
import com.tamin.taminhamrah.repository.HistoryRepository
import com.tamin.taminhamrah.useCases.history.GetDastmozdInfosUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class FakeHistoryRepository(
    private val expectedResult: DastmozdInfoDN
) : HistoryRepository {
    override suspend fun getDastmozdInfos(filters: List<ApiFilterDN>): DastmozdInfoDN = expectedResult
    override suspend fun getTalfighInfos(filters: List<ApiFilterDN>): com.tamin.taminhamrah.model.history.TalfighInfoDN = TODO()
}

class DastmozdInfosAgentServiceTest {

    @Test
    fun `execute parses payload correctly and returns KeyValue bubble`() = runTest {
        // Arrange
        val expectedItem = DastmozdInfoItemDN(
            wageDetails = listOf(WageDetailDN("فروردین", "1000")),
            hisyear = "1402",
            id = 1,
            risufname = null, risubirthdate = null, risuidserial2 = null, risuidserial1 = null,
            rwshname = "Test Company", expcitycode = null, brhcode = null, risuidno = null,
            risudname = null, risuid = null, risulname = null, risunatcode = null,
            brhname = "Test Branch", historytypedesc = null, rwshid = null
        )
        val expectedData = DastmozdInfoDN(list = listOf(expectedItem), total = 1)
        val fakeRepo = FakeHistoryRepository(expectedData)
        val useCase = GetDastmozdInfosUseCase(fakeRepo)
        val service = DastmozdInfosAgentService(useCase)

        val params = AgentServiceParams(
            payload = null,
            rawData = null,
            message = "This is a test message",
            sessionContext = AgentSessionContext()
        )

        // Act
        val result = service.execute(params)

        // Assert
        val success = assertIs<AgentServiceResult.Success>(result)
        
        // First bubble should be text message
        val textBubble = assertIs<ChatBubbleContent.Text>(success.bubbles[0])
        assertEquals("This is a test message", textBubble.message)
        
        // Second bubble should be KeyValue list
        val keyValueBubble = assertIs<ChatBubbleContent.KeyValue>(success.bubbles[1])
        assertEquals("This is a test message", keyValueBubble.title)
        
        // Assert some key values
        val amountPair = keyValueBubble.items.find { it.first == "مبلغ دستمزد فروردین" }
        assertEquals("1000", amountPair?.second)
        
        val companyPair = keyValueBubble.items.find { it.first == "نام کارگاه" }
        assertEquals("Test Company", companyPair?.second)
    }
    
    @Test
    fun `execute returns message when list is empty`() = runTest {
        // Arrange
        val fakeRepo = FakeHistoryRepository(DastmozdInfoDN(list = emptyList(), total = 0))
        val useCase = GetDastmozdInfosUseCase(fakeRepo)
        val service = DastmozdInfosAgentService(useCase)

        val params = AgentServiceParams(
            payload = null,
            rawData = null,
            message = "No data found",
            sessionContext = AgentSessionContext()
        )

        // Act
        val result = service.execute(params)

        // Assert
        val success = assertIs<AgentServiceResult.Success>(result)
        val textBubble = assertIs<ChatBubbleContent.Text>(success.bubbles[0])
        assertEquals("No data found", textBubble.message)
        assertEquals(1, success.bubbles.size)
    }
}
