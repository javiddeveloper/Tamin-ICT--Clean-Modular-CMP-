package com.tamin.taminhamrah.feature.agent.service

import kotlin.coroutines.cancellation.CancellationException
import com.tamin.taminhamrah.deeplink.DeepLinkKey
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceParams
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceRegistry
import com.tamin.taminhamrah.feature.agent.service.base.AgentServiceResult
import com.tamin.taminhamrah.feature.agent.service.base.AgentSessionContext
import com.tamin.taminhamrah.feature.agent.service.base.ChatBubbleContent
import com.tamin.taminhamrah.feature.agent.service.base.agentMarkdown
import com.tamin.taminhamrah.feature.agent.service.base.appLink
import com.tamin.taminhamrah.feature.agent.service.base.promptLink
import com.tamin.taminhamrah.feature.FeatureManager
import com.tamin.taminhamrah.model.agent.AgentItemType
import com.tamin.taminhamrah.model.agent.AiEntityDN
import com.tamin.taminhamrah.model.agent.DeepLinkItemDTO
import com.tamin.taminhamrah.model.agent.PromptItemDTO
import com.tamin.taminhamrah.model.agent.toFeatureFlag
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Central coordinator for the service execution pipeline.
 *
 * Responsibilities:
 * 0. Pass server-rendered markdown straight through — it needs no service and no flag; the links
 *    inside it are checked against their own flags when tapped
 * 1. Check [FeatureFlag] before executing any service
 * 2. Intercept and extract global metadata (e.g., suggested prompts)
 * 3. Invoke the appropriate handler from [AgentServiceRegistry]
 * 4. Pass [AgentSessionContext] to share data between steps
 *
 * Based on sections 3 and 8 of the architecture document (agent.md)
 */
class AgentActionDispatcher(
    private val registry: AgentServiceRegistry,
    private val featureManager: FeatureManager,
    private val json: Json
) {

    /**
     * Dispatches an entity from the AI response.
     *
     * Execution flow:
     * 1. Check FeatureFlag ← If disabled: [AgentServiceResult.FeatureDisabled]
     * 2. Extract [prompt_item]s from data (global interceptor)
     * 3. Find handler ← If not found: handle fallback (e.g. general_response)
     * 4. Execute handler ← [AgentServiceResult.Success] or [AgentServiceResult.Error]
     */
    suspend fun dispatch(
        entity: AiEntityDN,
        context: AgentSessionContext
    ): AgentServiceResult {
        // 0. Server-rendered markdown carries its own content.
        if (entity.itemType == AgentItemType.MARKDOWN) {
            // An entity whose items were all blank yields nothing rather than a fallback text:
            // dispatching it would ask a service for an answer the server already gave.
            val bubbles = entity.markdown.map { ChatBubbleContent.Markdown(it) }
            if (bubbles.isEmpty()) return AgentServiceResult.NoHandler
            return AgentServiceResult.Success(bubbles + listOfNotNull(extractSuggestedPrompts(entity.data)))
        }

        // 1. Check FeatureFlag
        val featureFlag = entity.action.toFeatureFlag()
        if (featureFlag != null) {
            val isEnabled = featureManager.isFeatureEnabled(featureFlag)
            if (!isEnabled) {
                // The server's text, then the menu's reason; the screen supplies a default for neither.
                val disabledMessage = entity.message ?: featureManager.getDisabledMessage(featureFlag)
                return AgentServiceResult.FeatureDisabled(disabledMessage)
            }
        }

        // 2. Extract Suggested Prompts (Centralized Interceptor)
        val promptsBubble = extractSuggestedPrompts(entity.data)

        // 3. Find the handler
        val handler = registry.get(entity.action)

        if (handler == null) {
            // No handler: show the server's text (markdown), its link items and prompts.
            val text = entity.message?.takeIf { it.isNotBlank() }?.let { listOf(ChatBubbleContent.Markdown(it)) }.orEmpty()
            val bubbles = withDeepLinkItems(text, entity.data) + listOfNotNull(promptsBubble)
            return if (bubbles.isNotEmpty()) {
                AgentServiceResult.Success(bubbles)
            } else {
                AgentServiceResult.NoHandler
            }
        }

        // 4. Execute the specific handler
        return try {
            val result = handler.execute(
                AgentServiceParams(
                    payload = entity.payload,
                    rawData = entity.data,
                    message = entity.message,
                    sessionContext = context,
                    requestedKey = entity.action
                )
            )

            // Append the entity's link items and global prompts to a successful answer.
            if (result is AgentServiceResult.Success) {
                AgentServiceResult.Success(withDeepLinkItems(result.bubbles, entity.data) + listOfNotNull(promptsBubble))
            } else {
                result
            }
        } catch (e: CancellationException) {
            // A service already rethrows cancellation; turning it into an error here would hide it.
            throw e
        } catch (e: Exception) {
            AgentServiceResult.Error(
                message = e.message ?: "Service execution error",
                cause = e
            )
        }
    }

    /**
     * Turns the entity's `deeplink` data items into buttons at the end of the answer's markdown.
     * A target the app knows becomes an app link (gated by its flag when tapped); any other target
     * sends the item's title as the next prompt, as the native client did.
     */
    private fun withDeepLinkItems(bubbles: List<ChatBubbleContent>, data: JsonElement?): List<ChatBubbleContent> {
        val items = runCatching { json.decodeFromJsonElement<List<DeepLinkItemDTO>>(data ?: return bubbles) }
            .getOrNull().orEmpty()
            .filter { AgentItemType.fromWireName(it.itemType) == AgentItemType.DEEPLINK }
            .mapNotNull { item ->
                val title = item.title?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val key = DeepLinkKey.fromKey(item.deeplink?.to)
                title to (key?.let { appLink(it.key) } ?: promptLink(title))
            }
        if (items.isEmpty()) return bubbles
        val links = agentMarkdown { links(items) }
        val last = bubbles.lastOrNull() as? ChatBubbleContent.Markdown
            ?: return bubbles + ChatBubbleContent.Markdown(links)
        return bubbles.dropLast(1) + ChatBubbleContent.Markdown(last.text + "\n\n" + links)
    }

    /**
     * Attempts to parse `prompt_item`s from the raw data.
     */
    private fun extractSuggestedPrompts(data: JsonElement?): ChatBubbleContent.SuggestedPrompts? {
        if (data == null) return null
        return try {
            val items = json.decodeFromJsonElement<List<PromptItemDTO>>(data)
            val prompts = items
                .filter { AgentItemType.fromWireName(it.itemType) == AgentItemType.PROMPT_ITEM }
                .mapNotNull { it.prompt }
            if (prompts.isNotEmpty()) {
                ChatBubbleContent.SuggestedPrompts(prompts)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Dispatches a general message (e.g., general_response / message entity)
     * that does not require a dedicated handler and is rendered directly as a text bubble.
     */
    fun dispatchMessage(message: String?): AgentServiceResult {
        if (message.isNullOrBlank()) return AgentServiceResult.NoHandler
        return AgentServiceResult.Success(
            bubbles = listOf(ChatBubbleContent.Text(message))
        )
    }
}
