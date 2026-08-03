package com.tamin.taminhamrah.feature.agent.service.base

/**
 * Context of the current chat session.
 *
 * This class stores data that needs to be shared between steps in a pipeline.
 * For example, if step 1 returns an `educationCode`, step 2 can read it from this context.
 *
 * Based on section 8 of the architecture document (Chained Actions & Dependency Management).
 */
class AgentSessionContext {
    private val store: MutableMap<String, Any?> = mutableMapOf()

    /** Stores a value in the context */
    fun put(key: String, value: Any?) {
        store[key] = value
    }

    /** Reads a value from the context */
    @Suppress("UNCHECKED_CAST")
    fun <T> get(key: String): T? = store[key] as? T

    /** Checks for the existence of a key */
    fun contains(key: String): Boolean = store.containsKey(key)

    /** Resets the entire context */
    fun clear() = store.clear()
}
