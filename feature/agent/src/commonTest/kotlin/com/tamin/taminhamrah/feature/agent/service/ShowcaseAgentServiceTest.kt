package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.impl.ShowcaseAgentService
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * The showcase builds bubbles purely from the response payload, the same way a real
 * backend would drive them — so these cases double as documentation of that contract.
 */
class ShowcaseAgentServiceTest {

    private val service = ShowcaseAgentService()

    private suspend fun render(payloadJson: String): ChatBubbleContent {
        val result = service.execute(
            AgentServiceParams(
                payload = Json.parseToJsonElement(payloadJson),
                rawData = null,
                message = "پیام",
                sessionContext = AgentSessionContext(),
                requestedKey = AgentActionKey.SHOWCASE
            )
        )
        return assertIs<AgentServiceResult.Success>(result).bubbles.single()
    }

    @Test
    fun `rich text is taken from the payload title and text`() = runTest {
        val bubble = render(
            """{"type":"rich_text","title":"عنوان خبر","text":"متن خبر","footnote":"منبع"}"""
        )

        assertIs<ChatBubbleContent.RichText>(bubble)
        assertEquals("عنوان خبر", bubble.header)
        assertEquals("متن خبر", bubble.body)
        assertEquals("منبع", bubble.footnote)
    }

    @Test
    fun `image uses the url from the payload`() = runTest {
        val bubble = render("""{"type":"image","image":"https://x/a.jpg","caption":"شرح"}""")

        assertIs<ChatBubbleContent.Image>(bubble)
        assertEquals("https://x/a.jpg", bubble.source)
        assertEquals("شرح", bubble.caption)
    }

    @Test
    fun `table keeps its columns and row order`() = runTest {
        val bubble = render(
            """{"type":"table","title":"بدهی‌ها","columns":["عنوان","مبلغ"],
                "rows":[["دولت","۷۵۰ همت"],["کارفرمایان","۲۰۰ همت"]]}"""
        )

        assertIs<ChatBubbleContent.Table>(bubble)
        assertEquals(listOf("عنوان", "مبلغ"), bubble.columns)
        assertEquals(2, bubble.rows.size)
        assertEquals(listOf("دولت", "۷۵۰ همت"), bubble.rows.first().cells)
    }

    @Test
    fun `chart reads its kind labels and values`() = runTest {
        val bubble = render(
            """{"type":"chart","title":"روند","kind":"line","labels":["الف","ب"],
                "series":"بدهی","values":[200,750],"unit":"همت"}"""
        )

        assertIs<ChatBubbleContent.Chart>(bubble)
        assertEquals(ChartKind.LINE, bubble.kind)
        assertEquals(listOf("الف", "ب"), bubble.labels)
        assertEquals(listOf(200.0, 750.0), bubble.series.single().values)
        assertEquals("همت", bubble.valueUnit)
    }

    @Test
    fun `key value rows come from the payload items`() = runTest {
        val bubble = render(
            """{"type":"key_value","title":"ارقام","items":[{"key":"کسری","value":"۹۰ همت"}]}"""
        )

        assertIs<ChatBubbleContent.KeyValue>(bubble)
        assertEquals("کسری", bubble.items.single().key)
        assertEquals("۹۰ همت", bubble.items.single().value)
    }

    @Test
    fun `a malformed payload degrades to text instead of throwing`() = runTest {
        // Fixtures and backends both drift; a bad row must not take the answer down.
        val bubble = render("""{"type":"table","columns":"not-a-list"}""")

        assertIs<ChatBubbleContent.Table>(bubble)
        assertTrue(bubble.columns.isEmpty())
        assertTrue(bubble.rows.isEmpty())
    }

    @Test
    fun `an unknown type falls back to the plain message`() = runTest {
        val bubble = render("""{"type":"something_new"}""")

        assertIs<ChatBubbleContent.Text>(bubble)
        assertEquals("پیام", bubble.message)
    }

    @Test
    fun `an error is retryable unless the payload says otherwise`() = runTest {
        val retryable = assertIs<ChatBubbleContent.ServiceError>(render("""{"type":"error","text":"x"}"""))
        assertTrue(retryable.canRetryPrompt)
        assertEquals(AgentActionKey.SHOWCASE, retryable.actionKey)

        val note = assertIs<ChatBubbleContent.ServiceError>(render("""{"type":"error","text":"x","retryable":false}"""))
        assertTrue(!note.canRetryPrompt)
        assertEquals(null, note.actionKey)
    }

    @Test
    fun `a throw payload fails like a broken service`() = runTest {
        // The dispatcher is what turns this into an error bubble; the service itself must throw.
        assertFailsWith<IllegalStateException> { render("""{"type":"throw","text":"boom"}""") }
    }
}
