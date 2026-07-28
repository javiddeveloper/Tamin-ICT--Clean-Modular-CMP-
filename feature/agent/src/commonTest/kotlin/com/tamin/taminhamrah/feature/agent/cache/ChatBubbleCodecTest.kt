package com.tamin.taminhamrah.feature.agent.cache

import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
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
            items = listOf("سال سابقه" to "۱۴۰۳", "نام کارگاه" to "توسعه فن افزار")
        )

        val restored = roundTrip(original)

        assertIs<ChatBubbleContent.KeyValue>(restored)
        assertEquals("اطلاعات دستمزد", restored.title)
        assertEquals(original.items, restored.items)
    }

    @Test
    fun `key value with a null title round trips`() {
        val restored = roundTrip(ChatBubbleContent.KeyValue(null, listOf("a" to "b")))

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
            path = "/data/voice_1.m4a",
            durationMs = 4200L,
            amplitudes = listOf(10, 900, 32000)
        )

        val restored = roundTrip(original)

        assertIs<ChatBubbleContent.Voice>(restored)
        assertEquals("/data/voice_1.m4a", restored.path)
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
    fun `bubbles carrying arbitrary models are not cached`() {
        // These hold `Any` and cannot be serialized — encode must decline rather than throw.
        assertNull(ChatBubbleCodec.encode(ChatBubbleContent.EmbeddedModel(Any())))
        assertNull(ChatBubbleCodec.encode(ChatBubbleContent.Chart(Any())))
        assertNull(ChatBubbleCodec.encode(ChatBubbleContent.DynamicForm(Any())))
    }

    @Test
    fun `unknown or corrupt rows decode to null instead of crashing`() {
        assertNull(ChatBubbleCodec.decode("some_future_type", "{}"))
        assertNull(ChatBubbleCodec.decode("text", "not json at all"))
        assertNull(ChatBubbleCodec.decode("key_value", """{"unexpected":true}"""))
    }
}
