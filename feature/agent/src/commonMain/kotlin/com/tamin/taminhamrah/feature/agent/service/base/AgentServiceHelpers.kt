package com.tamin.taminhamrah.feature.agent.service.base

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Shared helpers for Agent service use cases, ported from old_Android's
 * `ServiceParams.getFilters()` / `collectAllPromptToList()` extensions.
 *
 * The AI sends per-entity `payload` (filters chosen by the model) and `rawData`
 * (a list of items, each of which may carry a follow-up `prompt` suggestion).
 */

/**
 * Extracts the AI filters from the payload.
 *
 * The backend encodes filters as a string array like `["startDate:14020101", "year:1402"]`,
 * which this flattens into a `key -> value` map.
 */
fun AgentServiceParams.getFilters(): Map<String, String> {
    val obj = payload as? JsonObject ?: return emptyMap()
    val result = mutableMapOf<String, String>()

    // Form 1: {"filter": ["key:value", ...]}
    (obj["filter"] as? JsonArray)?.forEach { element ->
        val raw = (element as? JsonPrimitive)?.content ?: return@forEach
        val idx = raw.indexOf(':')
        if (idx > 0) result[raw.substring(0, idx)] = raw.substring(idx + 1)
    }

    // Form 2: plain object entries {"year": "1402"} — never overwrite Form 1 values.
    obj.forEach { (key, value) ->
        if (key == "filter") return@forEach
        val primitive = value as? JsonPrimitive ?: return@forEach
        result.getOrPut(key) { primitive.content }
    }

    return result
}

/** Reads a single filter value by [key], or null when the AI did not send it. */
fun AgentServiceParams.filterValue(key: String): String? = getFilters()[key]

/**
 * Builds the standard bubble list for a service.
 *
 * Note: suggested follow-up prompts are NOT added here — `AgentActionDispatcher`
 * extracts `prompt_item`s centrally and appends them to every successful result,
 * so adding them here too would render them twice.
 */
fun AgentServiceParams.buildBubbles(
    includeMessageBubble: Boolean = false,
    content: MutableList<ChatBubbleContent>.() -> Unit
): List<ChatBubbleContent> {
    val bubbles = mutableListOf<ChatBubbleContent>()
    if (includeMessageBubble && !message.isNullOrBlank()) {
        bubbles.add(ChatBubbleContent.Text(message))
    }
    bubbles.content()
    return bubbles
}

/** Groups a Persian digit string with thousands separators (e.g. 1234567 → "1,234,567"). */
fun Long?.formatAmount(): String {
    if (this == null) return "-"
    val negative = this < 0
    val digits = kotlin.math.abs(this).toString()
    val sb = StringBuilder()
    digits.forEachIndexed { index, c ->
        if (index > 0 && (digits.length - index) % 3 == 0) sb.append(',')
        sb.append(c)
    }
    return if (negative) "-$sb" else sb.toString()
}

/** Convenience for Int amounts. */
fun Int?.formatAmount(): String = this?.toLong().formatAmount()

