package com.tamin.taminhamrah.feature.agent.service.base

import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/**
 * Where agent services read their labels from.
 *
 * Services build answers outside composition, so they cannot call `stringResource`; going through
 * this interface keeps every label in `strings.xml` while letting tests supply plain text without
 * loading platform resources.
 */
fun interface AgentStrings {
    suspend fun get(resource: StringResource, vararg args: Any): String
}

/** Production [AgentStrings], backed by compose resources. */
class ComposeAgentStrings : AgentStrings {
    override suspend fun get(resource: StringResource, vararg args: Any): String = getString(resource, *args)
}
