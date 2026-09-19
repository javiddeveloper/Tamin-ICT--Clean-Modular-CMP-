package com.tamin.taminhamrah.data.repository.stories

import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryEngagementDN
import com.tamin.taminhamrah.repository.stories.StoryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * How long the bundled catalogue pretends to take.
 *
 * Without it the list would be there before the first frame and the rail's loading state would be
 * unreachable — which is the state most likely to be wrong once a real service is answering.
 */
private const val MOCK_LATENCY_MS = 700L

/**
 * The stories catalogue, answered from [mockStoryChannels] until a service exists.
 *
 * ### Fetching exactly once
 *
 * [loadOnce] is guarded twice over: [mutex] serialises callers that arrive together, and [loaded]
 * stops the ones that arrive afterwards. The rail on the home page and the viewer opened from it
 * therefore produce one fetch between them rather than one each, however their coroutines
 * interleave. A failure leaves the catalogue unloaded, so the retry can genuinely try again.
 *
 * ### What is remembered, and for how long
 *
 * Watched channels, likes and bookmarks live here in memory for the life of the process, which is
 * deliberate: persisting them means choosing a storage shape before the service that owns them
 * exists. When it does, this is where they stop being local — the interface above does not change.
 *
 * A `single` in Koin, and it has to be: the two screens agree only because they share this object.
 */
class StoryRepositoryImpl : StoryRepository {

    private val mutex = Mutex()
    private var loaded = false

    private val channels = MutableStateFlow<List<StoryChannelDN>>(emptyList())
    private val seenChannels = MutableStateFlow<Set<String>>(emptySet())
    private val engagement = MutableStateFlow(StoryEngagementDN())

    override fun getChannels(forceRefresh: Boolean): Flow<List<StoryChannelDN>> = flow {
        loadOnce(forceRefresh)
        // Not a one-shot emit: a caller that stays subscribed sees a later refresh, and the
        // underlying StateFlow is what the retry writes into.
        emitAll(channels.asStateFlow())
    }

    override fun observeSeenChannels(): Flow<Set<String>> = seenChannels.asStateFlow()

    override fun observeEngagement(): Flow<StoryEngagementDN> = engagement.asStateFlow()

    override suspend fun markChannelSeen(channelKey: String) {
        seenChannels.update { it + channelKey }
    }

    override suspend fun toggleLike(itemId: String) {
        engagement.update { it.copy(likedItemIds = it.likedItemIds.toggle(itemId)) }
    }

    override suspend fun toggleSave(itemId: String) {
        engagement.update { it.copy(savedItemIds = it.savedItemIds.toggle(itemId)) }
    }

    /** Rethrows whatever the source threw, so the caller can show the failure. */
    private suspend fun loadOnce(forceRefresh: Boolean) {
        // Cheap pre-check: the common case is a screen re-collecting an already-loaded catalogue,
        // and that should not queue behind whatever else holds the lock.
        if (loaded && !forceRefresh) return
        mutex.withLock {
            if (loaded && !forceRefresh) return
            delay(MOCK_LATENCY_MS)
            channels.value = mockStoryChannels()
            loaded = true
        }
    }

    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value
}
