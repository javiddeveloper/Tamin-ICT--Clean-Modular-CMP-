package com.tamin.taminhamrah.feature.stories.data

import com.tamin.taminhamrah.feature.stories.model.StoryChannel

/**
 * Where the rail's channels come from.
 *
 * There is no stories web service yet, so the only implementation is [MockStorySource]. The
 * interface exists so that [StoryCatalog] — which owns the caching, the seen set and the
 * in-flight guard, none of which change when the wire arrives — does not have to be rewritten
 * along with it: a repository-backed source drops in here and nothing above this line moves.
 *
 * Suspending rather than returning a `Flow`: a channel list is fetched, not observed. What *is*
 * observed — the list itself, and which channels have been watched — is exposed by [StoryCatalog]
 * as state.
 */
fun interface StorySource {
    /** Throws on failure. [StoryCatalog] turns that into the rail's error state. */
    suspend fun channels(): List<StoryChannel>
}
