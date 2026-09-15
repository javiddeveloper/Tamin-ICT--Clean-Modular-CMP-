package com.tamin.taminhamrah.feature.stories
import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryCtaDN
import com.tamin.taminhamrah.model.stories.StoryEngagementDN
import com.tamin.taminhamrah.model.stories.StoryItemDN
import com.tamin.taminhamrah.model.stories.StoryMediaDN
import com.tamin.taminhamrah.repository.stories.StoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
/**
 * Channels built to order, so a test states the shape it needs — how many slides, which of them
 * carry a clip — instead of depending on the bundled catalogue staying the way it is today.
 *
 * Deliberately never uses [StoryMediaDN.SampleImage] or [StoryMediaDN.SampleVideo]: those are the
 * only variants whose mapping touches Compose resources, and a JVM unit test has no business
 * reaching for the app's asset bundle.
 */
internal fun testChannel(
    key: String,
    itemCount: Int = 3,
    videoIndices: Set<Int> = emptySet(),
    ctaIndices: Set<Int> = emptySet(),
): StoryChannelDN = StoryChannelDN(
    key = key,
    name = "channel $key",
    shortName = key,
    time = "امروز",
    items = (0 until itemCount).map { index ->
        StoryItemDN(
            id = "$key:$index",
            title = "$key title $index",
            body = "$key body $index",
            media = if (index in videoIndices) {
                StoryMediaDN.Video("video://$key/$index")
            } else {
                StoryMediaDN.None
            },
            cta = if (index in ctaIndices) StoryCtaDN("cta", "@agent") else null,
        )
    },
)
/**
 * A repository a test can drive.
 *
 * Counts how many times the catalogue was actually fetched — which is how the
 * no-duplicate-request rule is checked — and can be made to fail and then recover. The seen and
 * engagement sets behave as the real implementation's do, because the two screens genuinely talk
 * to each other through them.
 */
internal class FakeStoryRepository(
    private val channels: List<StoryChannelDN> = listOf(testChannel("a"), testChannel("b")),
) : StoryRepository {
    var fetchCount = 0
        private set
    /** When set, the next fetch throws it instead of answering. */
    var failWith: Throwable? = null
    private var loaded = false
    private val cached = MutableStateFlow<List<StoryChannelDN>>(emptyList())
    private val seen = MutableStateFlow<Set<String>>(emptySet())
    private val engagement = MutableStateFlow(StoryEngagementDN())
    override fun getChannels(forceRefresh: Boolean): Flow<List<StoryChannelDN>> = flow {
        if (!loaded || forceRefresh) {
            fetchCount++
            failWith?.let { throw it }
            cached.value = channels
            loaded = true
        }
        emitAll(cached.asStateFlow())
    }
    override fun observeSeenChannels(): Flow<Set<String>> = seen.asStateFlow()
    override fun observeEngagement(): Flow<StoryEngagementDN> = engagement.asStateFlow()
    override suspend fun markChannelSeen(channelKey: String) {
        seen.update { it + channelKey }
    }
    override suspend fun toggleLike(itemId: String) {
        engagement.update { it.copy(likedItemIds = it.likedItemIds.toggle(itemId)) }
    }
    override suspend fun toggleSave(itemId: String) {
        engagement.update { it.copy(savedItemIds = it.savedItemIds.toggle(itemId)) }
    }
    /** What the viewer has marked watched, for a test to assert on. */
    val seenChannels: Set<String> get() = seen.value
    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value
}
