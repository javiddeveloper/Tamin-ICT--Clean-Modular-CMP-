package com.tamin.taminhamrah.feature.stories.ui.model

import androidx.compose.runtime.Immutable
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryPalette
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource

/**
 * A channel as the rail and the viewer draw it.
 *
 * Everything here beyond the copy is presentation the domain has no business knowing: the
 * [palette] its ring and backdrop are built from, and the [icon] inside the ring. The mapper adds
 * both from [key], so a channel the app has never heard of still renders — with the default
 * palette rather than a crash.
 */
@Immutable
data class StoryChannelPR(
    val key: String,
    val name: String,
    val shortName: String,
    val time: String,
    val palette: StoryPalette,
    val icon: DrawableResource,
    val items: ImmutableList<StoryItemPR>,
)

/** A single slide, ready to draw. */
@Immutable
data class StoryItemPR(
    val id: String,
    val title: String,
    val body: String,
    val media: StoryMediaPR,
    val cta: StoryCtaPR? = null,
)

/**
 * What fills the slide behind the copy, as an address the UI can actually load.
 *
 * Only two cases here where the domain has four: the mapper has already turned its bundled-sample
 * variants into real URIs, so nothing downstream has to know a sample from a served picture.
 */
@Immutable
sealed interface StoryMediaPR {
    data object None : StoryMediaPR
    data class Image(val url: String) : StoryMediaPR
    data class Video(val url: String) : StoryMediaPR
}

@Immutable
data class StoryCtaPR(
    val label: String,
    val deepLink: String?,
)
