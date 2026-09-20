package com.tamin.taminhamrah.model.agent

/**
 * The `item_type` values the assistant sends, both on an entity and on the items in its `data`.
 * Unknown values map to null rather than failing, so a new server type degrades quietly.
 */
enum class AgentItemType(val wireName: String) {
    /** An entity whose content is already-rendered markdown; no service runs for it. */
    MARKDOWN("markdown"),
    /** One markdown text inside a [MARKDOWN] entity's data. */
    MARKDOWN_ITEM("markdown_item"),
    /** A follow-up prompt suggestion. */
    PROMPT_ITEM("prompt_item"),
    /** A button to a screen; its target is `deeplink.to`. */
    DEEPLINK("deeplink"),
    /** One law or regulation returned for the laws key. */
    LAW_ITEM("law_item"),
    ;

    companion object {
        fun fromWireName(value: String?): AgentItemType? =
            entries.firstOrNull { it.wireName.equals(value?.trim(), ignoreCase = true) }
    }
}

/** Who rendered the response: the server (markdown entities) or the client (service keys). */
enum class AgentRenderMode {
    CLIENT,
    SERVER,
    ;

    companion object {
        fun fromWireName(value: String?): AgentRenderMode =
            if (value.equals("SERVER", ignoreCase = true)) SERVER else CLIENT
    }
}
