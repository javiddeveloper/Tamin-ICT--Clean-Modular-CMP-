package com.tamin.taminhamrah.feature.agent.cache

import com.tamin.taminhamrah.feature.agent.service.base.ChartKind
import com.tamin.taminhamrah.feature.agent.service.base.ChartSeries
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.toKeyValueRows
import com.tamin.taminhamrah.model.agent.AgentActionKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * The codec is the contract between the chat UI and what lands on disk: a bubble that
 * fails to round-trip is silently lost when the conversation is restored.
 */
class ChatBubbleCodecTest {

    private fun roundTrip(content: ChatBubbleContent): ChatBubbleContent? {
        val (type, payload) = ChatBubbleCodec.encode(content) ?: return null
        return ChatBubbleCodec.decode(type, payload)
    }

    @Test
    fun `text survives a round trip`() {
        val restored = roundTrip(ChatBubbleContent.Text("سلام، سابقه‌ام را نشان بده"))

        assertIs<ChatBubbleContent.Text>(restored)
        assertEquals("سلام، سابقه‌ام را نشان بده", restored.message)
    }

    @Test
    fun `key value keeps its title and pair order`() {
        val original = ChatBubbleContent.KeyValue(
            title = "اطلاعات دستمزد",
            items = listOf("سال سابقه" to "۱۴۰۳", "نام کارگاه" to "توسعه فن افزار").toKeyValueRows()
        )

        val restored = roundTrip(original)

        assertIs<ChatBubbleContent.KeyValue>(restored)
        assertEquals("اطلاعات دستمزد", restored.title)
        assertEquals(original.items, restored.items)
    }

    @Test
    fun `key value with a null title round trips`() {
        val restored = roundTrip(ChatBubbleContent.KeyValue(null, listOf("a" to "b").toKeyValueRows()))

        assertIs<ChatBubbleContent.KeyValue>(restored)
        assertNull(restored.title)
    }

    @Test
    fun `suggested prompts survive`() {
        val restored = roundTrip(ChatBubbleContent.SuggestedPrompts(listOf("اول", "دوم")))

        assertIs<ChatBubbleContent.SuggestedPrompts>(restored)
        assertEquals(listOf("اول", "دوم"), restored.prompts)
    }

    @Test
    fun `voice keeps path duration and waveform`() {
        val original = ChatBubbleContent.Voice(
            source = "/data/voice_1.m4a",
            durationMs = 4200L,
            amplitudes = listOf(10, 900, 32000)
        )

        val restored = roundTrip(original)

        assertIs<ChatBubbleContent.Voice>(restored)
        assertEquals("/data/voice_1.m4a", restored.source)
        assertEquals(4200L, restored.durationMs)
        assertEquals(listOf(10, 900, 32000), restored.amplitudes)
    }

    @Test
    fun `deep link and web link stay distinct`() {
        val deep = roundTrip(ChatBubbleContent.DeepLink("برو", "disability_pension"))
        val web = roundTrip(ChatBubbleContent.WebLink("سایت", "https://tamin.ir"))

        assertIs<ChatBubbleContent.DeepLink>(deep)
        assertEquals("disability_pension", deep.destination)
        assertIs<ChatBubbleContent.WebLink>(web)
        assertEquals("https://tamin.ir", web.url)
    }

    @Test
    fun `service error keeps its action key for retry`() {
        val original = ChatBubbleContent.ServiceError(
            message = "خطا در سرویس",
            canRetryPrompt = true,
            actionKey = AgentActionKey.DASTMOZD_INFOS
        )

        val restored = roundTrip(original)

        assertIs<ChatBubbleContent.ServiceError>(restored)
        assertEquals("خطا در سرویس", restored.message)
        assertTrue(restored.canRetryPrompt)
        assertEquals(AgentActionKey.DASTMOZD_INFOS, restored.actionKey)
    }

    @Test
    fun `processing steps survive`() {
        val restored = roundTrip(
            ChatBubbleContent.ProcessingSteps(listOf("گام یک", "گام دو"), 1, false)
        )

        assertIs<ChatBubbleContent.ProcessingSteps>(restored)
        assertEquals(2, restored.steps.size)
        assertEquals(1, restored.currentActiveIndex)
    }

    @Test
    fun `image survives with a null caption`() {
        val restored = roundTrip(ChatBubbleContent.Image("https://x/y.png", null))

        assertIs<ChatBubbleContent.Image>(restored)
        assertNull(restored.caption)
    }

    @Test
    fun `rich text keeps its header body and footnote`() {
        val restored = roundTrip(
            ChatBubbleContent.RichText("سابقه شما", "متن اصلی", "توضیح")
        )

        assertIs<ChatBubbleContent.RichText>(restored)
        assertEquals("سابقه شما", restored.header)
        assertEquals("متن اصلی", restored.body)
        assertEquals("توضیح", restored.footnote)
    }

    @Test
    fun `video keeps its source thumbnail and duration`() {
        val restored = roundTrip(
            ChatBubbleContent.Video("https://x/v.mp4", "https://x/t.jpg", 9000L, "کلیپ")
        )

        assertIs<ChatBubbleContent.Video>(restored)
        assertEquals("https://x/v.mp4", restored.source)
        assertEquals("https://x/t.jpg", restored.thumbnailUrl)
        assertEquals(9000L, restored.durationMs)
    }

    @Test
    fun `chart survives as plain data so a reopened chat still draws it`() {
        val restored = roundTrip(
            ChatBubbleContent.Chart(
                title = "دستمزد",
                kind = ChartKind.LINE,
                labels = listOf("فروردین", "اردیبهشت"),
                series = listOf(ChartSeries("۱۴۰۳", listOf(10.0, 20.0))),
                valueUnit = "ریال"
            )
        )

        assertIs<ChatBubbleContent.Chart>(restored)
        assertEquals(ChartKind.LINE, restored.kind)
        assertEquals(listOf(10.0, 20.0), restored.series.single().values)
        assertEquals("ریال", restored.valueUnit)
    }

    @Test
    fun `every bubble type reports a stable serial name`() {
        // Serial names are written to the database, so a rename silently orphans old rows.
        assertEquals("text", ChatBubbleCodec.contentTypeOf(ChatBubbleContent.Text("x")))
        assertEquals("rich_text", ChatBubbleCodec.contentTypeOf(ChatBubbleContent.RichText("h", "b")))
        assertEquals("voice", ChatBubbleCodec.contentTypeOf(ChatBubbleContent.Voice("p")))
        assertEquals("video", ChatBubbleCodec.contentTypeOf(ChatBubbleContent.Video("v")))
        assertEquals("image", ChatBubbleCodec.contentTypeOf(ChatBubbleContent.Image("i")))
        assertEquals("chart", ChatBubbleCodec.contentTypeOf(ChatBubbleContent.Chart()))
    }

    @Test
    fun `unknown or corrupt rows decode to null instead of crashing`() {
        assertNull(ChatBubbleCodec.decode("some_future_type", "{}"))
        assertNull(ChatBubbleCodec.decode("text", "not json at all"))
        assertNull(ChatBubbleCodec.decode("key_value", """{"unexpected":true}"""))
    }
}
