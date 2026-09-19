package com.tamin.taminhamrah.repository.stories

import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryEngagementDN
import kotlinx.coroutines.flow.Flow

/**
 * The «تازه‌ها» catalogue, and what the reader has done with it.
 *
 * There is no stories web service yet: the implementation answers from a bundled catalogue. The
 * interface is written for the one that will exist, so swapping it is a change to
 * `StoryRepositoryImpl` and nothing above it.
 *
 * ### What is observed and what is asked for
 *
 * [getChannels] is a fetch — one list, cached, re-emitted to anyone still listening. The seen set
 * and the engagement are genuinely observable, because two screens change them for each other:
 * the viewer marks a channel watched and the rail behind it greys that ring, without either
 * knowing about the other.
 */
interface StoryRepository {

    /**
     * The channels to show, in the order the source gives them.
     *
     * Fetches at most once however many callers ask; [forceRefresh] is what the rail's retry
     * button uses, and the only way to make it go again.
     */
    fun getChannels(forceRefresh: Boolean = false): Flow<List<StoryChannelDN>>

    /** Keys of the channels watched through, or closed out of. */
    fun observeSeenChannels(): Flow<Set<String>>

    fun observeEngagement(): Flow<StoryEngagementDN>

    suspend fun markChannelSeen(channelKey: String)

    suspend fun toggleLike(itemId: String)

    suspend fun toggleSave(itemId: String)
}
