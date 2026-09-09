package com.tamin.taminhamrah.feature.stories.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryPalette
import com.tamin.taminhamrah.model.common.FeatureFlag
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource

/**
 * One publisher on the «تازه‌ها» rail, and everything the viewer needs to show it.
 *
 * A channel is the unit the rail draws and the unit "seen" is tracked against — watching a
 * channel through to its last slide greys its ring out, exactly as leaving part-way does not.
 *
 * [key] is the stable identity: the rail keys its list on it, [StoryItem.id] is built from it, and
 * it is what the seen-set holds. It must survive a reload, so it comes from the publisher rather
 * than from the position in the list.
 */
@Immutable
data class StoryChannel(
    val key: String,
    /** How the viewer's header and kicker name this publisher. */
    val name: String,
    /** How the rail labels it, which the design keeps shorter than [name]. */
    val shortName: String,
    /** Relative publication time, already formatted for display. */
    val time: String,
    val palette: StoryPalette,
    /**
     * What sits inside the ring on the rail.
     *
     * A bundled drawable while the catalogue is bundled with it. When a service starts publishing
     * channels this becomes the URL it sends, and the rail loads it the way the viewer already
     * loads a slide's picture — nothing else on this model moves.
     */
    val icon: DrawableResource,
    val items: ImmutableList<StoryItem>,
)

/**
 * A single slide.
 *
 * [id] is `channelKey:index` and identifies the slide across a reload, which is what the local
 * like and bookmark sets are keyed on.
 */
@Immutable
data class StoryItem(
    val id: String,
    val title: String,
    val body: String,
    val media: StoryMedia,
    val cta: StoryCta? = null,
    /**
     * How many likes the slide carries before this user's own. Bundled with the mock copy today;
     * a real service would send it.
     */
    val baseLikes: Int = 0,
)

/**
 * What fills the slide behind the copy.
 *
 * [None] is not a missing value — it is the design's own default, a slide that is nothing but the
 * channel gradient and its words. Image and video are drawn over that same gradient, which
 * doubles as their loading placeholder and as the fallback when the media cannot be fetched.
 */
@Immutable
sealed interface StoryMedia {
    data object None : StoryMedia
    data class Image(val url: String) : StoryMedia
    data class Video(val url: String) : StoryMedia
}

/**
 * The slide's call to action.
 *
 * Carries a [FeatureFlag] rather than a route, so the story feature never imports another feature:
 * the viewer raises an event with the flag and the host graph decides where it goes — and, on the
 * way, whether the server has that service switched on at all.
 */
@Immutable
data class StoryCta(
    val label: String,
    val target: FeatureFlag,
)
