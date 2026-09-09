package com.tamin.taminhamrah.model.stories

import com.tamin.taminhamrah.model.common.FeatureFlag

/**
 * One publisher on the «تازه‌ها» rail, and its stories.
 *
 * A channel is the unit "seen" is tracked against — watching one through to its last slide, or
 * closing out of it, greys its ring on the rail.
 *
 * [key] is the stable identity: the rail keys its list on it, [StoryItemDN.id] is built from it,
 * and the seen set holds it. It must survive a reload, so it comes from the publisher rather than
 * from a position in a list.
 *
 * No colors, no icons, no formatting here — those are the presentation layer's, and the mapper in
 * the stories feature adds them.
 */
data class StoryChannelDN(
    val key: String,
    /** How the viewer's header and kicker name this publisher. */
    val name: String,
    /** How the rail labels it, which the design keeps shorter than [name]. */
    val shortName: String,
    /** Relative publication time, already worded by the source. */
    val time: String,
    val items: List<StoryItemDN>,
)

/**
 * A single slide.
 *
 * [id] is `channelKey:index` and identifies the slide across a reload, which is what the like and
 * bookmark sets are keyed on.
 */
data class StoryItemDN(
    val id: String,
    val title: String,
    val body: String,
    val media: StoryMediaDN,
    val cta: StoryCtaDN? = null,
    /** How many likes the slide carries before this reader's own. */
    val baseLikes: Int = 0,
)

/**
 * What fills the slide behind the copy.
 *
 * [None] is not a missing value — it is a slide that is nothing but its channel's gradient and its
 * words, which is the majority of them.
 */
sealed interface StoryMediaDN {
    data object None : StoryMediaDN

    data class Image(val url: String) : StoryMediaDN

    data class Video(val url: String) : StoryMediaDN

    /**
     * A picture bundled with the app rather than fetched.
     *
     * Exists only while the catalogue is a mock, so that the image path is reachable without a
     * service and without a network. A real service always sends [Image].
     *
     * Deliberately a variant rather than a path string: the app's assets are the presentation
     * layer's business, and this way the domain names *what* the slide is without knowing where
     * the file lives. When the service arrives, deleting this and [SampleVideo] makes the compiler
     * point at every place that has to change.
     */
    data object SampleImage : StoryMediaDN

    /** A clip bundled with the app. See [SampleImage]. */
    data object SampleVideo : StoryMediaDN
}

/**
 * The slide's call to action.
 *
 * Carries a [FeatureFlag] rather than a route: where a service opens is decided by the host
 * navigation graph, which is also where the server's answer about that service is checked.
 */
data class StoryCtaDN(
    val label: String,
    val target: FeatureFlag,
)

/**
 * What this reader has done with the stories they have seen.
 *
 * One model rather than two flows so a screen observes engagement once and gets a consistent pair
 * rather than two independently-timed updates.
 */
data class StoryEngagementDN(
    val likedItemIds: Set<String> = emptySet(),
    val savedItemIds: Set<String> = emptySet(),
)
