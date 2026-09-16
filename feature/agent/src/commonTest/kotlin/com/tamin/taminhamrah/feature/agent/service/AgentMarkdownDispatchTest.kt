package com.tamin.taminhamrah.feature.agent.service

import com.tamin.taminhamrah.feature.agent.cache.ChatBubbleCodec
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.model.agent.AgentActionKey
import com.tamin.taminhamrah.model.agent.AgentItemType
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.model.common.FeatureStatus
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AgentMarkdownDispatchTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun markdownEntity(
        texts: List<String>,
        action: AgentActionKey = AgentActionKey.GENERAL_RESPONSE,
        data: String? = null,
    ) = AiEntityDN(
        action = action,
        stepNumber = 1,
        payload = null,
        data = data?.let { Json.parseToJsonElement(it) },
        message = null,
        itemType = AgentItemType.MARKDOWN,
        markdown = texts,
    )

    @Test
    fun `markdown entities become markdown bubbles without running any service`() = runTest {
        val service = FakeAgentService(supportedKeys = listOf(AgentActionKey.GENERAL_RESPONSE))
        val dispatcher = AgentActionDispatcher(AgentServiceRegistry(listOf(service)), FakeFeatureManager(), json)

        val result = dispatcher.dispatch(markdownEntity(listOf("### a", "b")), AgentSessionContext())

        val success = assertIs<AgentServiceResult.Success>(result)
        assertEquals(listOf(ChatBubbleContent.Markdown("### a"), ChatBubbleContent.Markdown("b")), success.bubbles)
        assertEquals(0, service.executeCallCount)
    }

    @Test
    fun `the entity key's flag does not hide server markdown - its links are gated on tap`() = runTest {
        val manager = FakeFeatureManager().apply { setStatus(FeatureFlag.PAY_ROLL, FeatureStatus.Disabled("off")) }
        val dispatcher = AgentActionDispatcher(AgentServiceRegistry(emptyList()), manager, json)

        val result = dispatcher.dispatch(markdownEntity(listOf("متن"), action = AgentActionKey.FISH), AgentSessionContext())

        assertIs<AgentServiceResult.Success>(result)
    }

    @Test
    fun `an entity with only blank markdown yields nothing, not a fallback text`() = runTest {
        val dispatcher = AgentActionDispatcher(
            AgentServiceRegistry(listOf(com.tamin.taminhamrah.feature.agent.service.impl.GeneralResponseAgentService(testStrings))),
            FakeFeatureManager(),
            json,
        )
        assertEquals(AgentServiceResult.NoHandler, dispatcher.dispatch(markdownEntity(emptyList()), AgentSessionContext()))
    }

    @Test
    fun `suggested prompts on a markdown entity are kept`() = runTest {
        val dispatcher = AgentActionDispatcher(AgentServiceRegistry(emptyList()), FakeFeatureManager(), json)
        val result = dispatcher.dispatch(
            markdownEntity(listOf("متن"), data = """[{"item_type":"markdown_item","text":"متن"},{"item_type":"prompt_item","prompt":"بعدی"}]"""),
            AgentSessionContext(),
        )
        val success = assertIs<AgentServiceResult.Success>(result)
        assertEquals(ChatBubbleContent.SuggestedPrompts(listOf("بعدی")), success.bubbles.last())
    }

    @Test
    fun `deeplink items become buttons at the end of the answer, unknown targets become prompts`() = runTest {
        val service = FakeAgentService(supportedKeys = listOf(AgentActionKey.DASTMOZD_INFOS))
        val dispatcher = AgentActionDispatcher(AgentServiceRegistry(listOf(service)), FakeFeatureManager(), json)
        val entity = AiEntityDN(
            action = AgentActionKey.DASTMOZD_INFOS,
            stepNumber = 1,
            payload = null,
            data = Json.parseToJsonElement(
                """[{"item_type":"deeplink","deeplink":{"to":"contract_freelance"},"title":"مشاغل آزاد"},
                    {"item_type":"deeplink","deeplink":{"to":"group_payment"},"title":"پرداخت گروهی"}]"""
            ),
            message = null,
            itemType = null,
        )
        val bubbles = assertIs<AgentServiceResult.Success>(dispatcher.dispatch(entity, AgentSessionContext())).bubbles
        val links = assertIs<ChatBubbleContent.Markdown>(bubbles.last()).text
        assertTrue("- [مشاغل آزاد](@contract_freelance)" in links, links)
        assertTrue("- [پرداخت گروهی](agent://prompt?text=" in links)
    }

    @Test
    fun `a markdown bubble survives the conversation cache`() {
        val original = ChatBubbleContent.Markdown("### عنوان\n\n| a | b |\n|---|---|\n| 1 | 2 |\n\n\$\$2/π = (1 − 1/2²)\$\$")
        val (type, payload) = ChatBubbleCodec.encode(original)!!
        assertEquals("markdown", type)
        assertEquals(original, ChatBubbleCodec.decode(type, payload))
    }
}
