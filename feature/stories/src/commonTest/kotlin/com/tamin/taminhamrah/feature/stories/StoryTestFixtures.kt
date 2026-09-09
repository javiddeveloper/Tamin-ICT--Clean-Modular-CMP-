package com.tamin.taminhamrah.feature.stories

import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.feature.stories.data.StorySource
import com.tamin.taminhamrah.feature.stories.model.StoryChannel
import com.tamin.taminhamrah.feature.stories.model.StoryCta
import com.tamin.taminhamrah.feature.stories.model.StoryItem
import com.tamin.taminhamrah.feature.stories.model.StoryMedia
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryPalette
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.collections.immutable.toImmutableList
import taminx.feature.stories.generated.resources.Res
import taminx.feature.stories.generated.resources.ic_story_assistant

/**
 * Channels built to order, so a test states the shape it needs — how many slides, which of them
 * carry a clip — instead of depending on the bundled catalogue staying the way it is today.
 */
internal fun testChannel(
    key: String,
    itemCount: Int = 3,
    videoIndices: Set<Int> = emptySet(),
    ctaIndices: Set<Int> = emptySet(),
): StoryChannel = StoryChannel(
    key = key,
    name = "channel $key",
    shortName = key,
    time = "امروز",
    palette = TestPalette,
    icon = TestIcon,
    items = (0 until itemCount).map { index ->
        StoryItem(
            id = "$key:$index",
            title = "$key title $index",
            body = "$key body $index",
            media = if (index in videoIndices) {
                StoryMedia.Video("video://$key/$index")
            } else {
                StoryMedia.None
            },
            cta = if (index in ctaIndices) StoryCta("cta", FeatureFlag.AGENT) else null,
            baseLikes = 10,
        )
    }.toImmutableList(),
)

/**
 * A source a test can drive: it counts how many times it was asked, which is how the
 * no-duplicate-request rule is checked, and it can be made to fail and then recover.
 */
internal class FakeStorySource(
    private val channels: List<StoryChannel> = listOf(testChannel("a"), testChannel("b")),
) : StorySource {
    var callCount = 0
        private set

    /** When set, the next call throws it instead of answering. */
    var failWith: Throwable? = null

    override suspend fun channels(): List<StoryChannel> {
        callCount++
        failWith?.let { throw it }
        return channels
    }
}

private val TestPalette = StoryPalette(
    ringStart = Color.Blue,
    ringEnd = Color.Cyan,
    avatarStart = Color.Blue,
    avatarEnd = Color.Cyan,
    backdropStart = Color.Black,
    backdropMid = Color.DarkGray,
    backdropMidStop = 0.5f,
    backdropEnd = Color.Gray,
    ctaTone = Color.Blue,
    iconTint = Color.White,
    iconTone = Color.Blue,
)

/** Never loaded — no test renders anything — but the model asks for one. */
private val TestIcon = Res.drawable.ic_story_assistant
