package com.tamin.taminhamrah.feature.stories.data

import com.tamin.taminhamrah.feature.stories.model.StoryChannel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The one place the story list lives, shared by the rail on the home page and the full-screen
 * viewer.
 *
 * Registered as a Koin `single`, which is what makes the two screens agree: the viewer marks a
 * channel watched, and the ring on the home page behind it goes grey without either screen
 * knowing about the other. It also means opening the viewer costs no second fetch — the list is
 * already here.
 *
 * ### Fetching exactly once
 *
 * [ensureLoaded] is the only way in, and it is guarded twice over: [mutex] serialises callers that
 * arrive together, and [loaded] stops the ones that arrive afterwards. Two rails composing in the
 * same frame, or a viewer opening while the rail is still loading, therefore produce one call to
 * [source] between them rather than one each. `force` exists for the retry button, which is the
 * only caller that genuinely wants a second attempt.
 *
 * ### What is remembered, and for how long
 *
 * Watched channels, likes and bookmarks are held here in memory for the life of the process. That
 * is deliberate for a front-end-only feature: persisting them would mean choosing a storage shape
 * before the service that owns them exists. When it does, this class is where they move from.
 */
class StoryCatalog(
    private val source: StorySource,
) {
    private val mutex = Mutex()
    private var loaded = false

    private val _channels = MutableStateFlow<ImmutableList<StoryChannel>>(persistentListOf())
    val channels: StateFlow<ImmutableList<StoryChannel>> = _channels.asStateFlow()

    private val _seenChannels = MutableStateFlow<ImmutableSet<String>>(persistentSetOf())

    /** Keys of the channels whose stories have been watched through, or closed out of. */
    val seenChannels: StateFlow<ImmutableSet<String>> = _seenChannels.asStateFlow()

    private val _likedItems = MutableStateFlow<ImmutableSet<String>>(persistentSetOf())
    val likedItems: StateFlow<ImmutableSet<String>> = _likedItems.asStateFlow()

    private val _savedItems = MutableStateFlow<ImmutableSet<String>>(persistentSetOf())
    val savedItems: StateFlow<ImmutableSet<String>> = _savedItems.asStateFlow()

    /**
     * Fills [channels] if they are not there yet. Rethrows whatever [source] threw, so the caller
     * can show the failure; a failed attempt leaves the catalogue unloaded and retryable.
     */
    suspend fun ensureLoaded(force: Boolean = false) {
        // Cheap pre-check: the common case is a screen re-collecting an already-loaded catalogue,
        // and that should not queue behind whatever else holds the lock.
        if (loaded && !force) return
        mutex.withLock {
            if (loaded && !force) return
            _channels.value = source.channels().toImmutableList()
            loaded = true
        }
    }

    /** Watching a channel through, or closing out of it, greys its ring on the rail. */
    fun markChannelSeen(key: String) {
        _seenChannels.value = _seenChannels.value.toPersistentSet().add(key)
    }

    fun toggleLike(itemId: String) {
        _likedItems.value = _likedItems.value.toggle(itemId)
    }

    fun toggleSave(itemId: String) {
        _savedItems.value = _savedItems.value.toggle(itemId)
    }

    private fun ImmutableSet<String>.toggle(value: String): ImmutableSet<String> =
        toPersistentSet().let { if (value in it) it.remove(value) else it.add(value) }
}
