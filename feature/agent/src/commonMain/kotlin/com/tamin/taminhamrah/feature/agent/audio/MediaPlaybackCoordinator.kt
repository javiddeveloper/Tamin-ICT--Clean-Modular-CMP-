package com.tamin.taminhamrah.feature.agent.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Makes sure only one thing is heard at a time.
 *
 * A conversation can hold several voice messages and several videos, each with its own
 * player, and nothing stopped them overlapping. Rather than have every player know about
 * every other, each one claims playback before it starts and watches [activeOwner]:
 * whoever is no longer the owner pauses itself.
 *
 * Owner ids are namespaced by kind (`voice:<id>`, `video:<url>`) so a claim is unique
 * even when the same media appears twice in a conversation.
 */
class MediaPlaybackCoordinator {

    private val _activeOwner = MutableStateFlow<String?>(null)

    /** Who may currently make sound; null when nothing is playing. */
    val activeOwner: StateFlow<String?> = _activeOwner.asStateFlow()

    /** Claims playback for [ownerId], implicitly telling everyone else to stop. */
    fun claim(ownerId: String) {
        _activeOwner.value = ownerId
    }

    /** Gives playback up, but only if [ownerId] still holds it. */
    fun release(ownerId: String) {
        if (_activeOwner.value == ownerId) _activeOwner.value = null
    }

    /** True while [ownerId] is allowed to play. */
    fun holdsPlayback(ownerId: String): Boolean = _activeOwner.value == ownerId

    companion object {
        fun voiceOwner(itemId: String): String = "voice:$itemId"
        fun videoOwner(url: String): String = "video:$url"
        /** The recording preview in the input bar. */
        const val PREVIEW_OWNER: String = "voice:preview"
    }
}
