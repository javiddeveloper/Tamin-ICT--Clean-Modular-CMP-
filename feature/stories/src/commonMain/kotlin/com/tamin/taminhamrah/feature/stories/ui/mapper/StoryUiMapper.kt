package com.tamin.taminhamrah.feature.stories.ui.mapper

import androidx.compose.ui.graphics.Color
import com.tamin.taminhamrah.feature.stories.ui.model.StoryChannelPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryCtaPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryItemPR
import com.tamin.taminhamrah.feature.stories.ui.model.StoryMediaPR
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryPalette
import com.tamin.taminhamrah.model.stories.StoryChannelDN
import com.tamin.taminhamrah.model.stories.StoryCtaDN
import com.tamin.taminhamrah.model.stories.StoryItemDN
import com.tamin.taminhamrah.model.stories.StoryMediaDN
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.DrawableResource
import kotlinx.collections.immutable.toImmutableList
import taminx.feature.stories.generated.resources.Res
import taminx.feature.stories.generated.resources.ic_story_assistant
import taminx.feature.stories.generated.resources.ic_story_employer
import taminx.feature.stories.generated.resources.ic_story_insured
import taminx.feature.stories.generated.resources.ic_story_pensioner
import taminx.feature.stories.generated.resources.ic_story_public_relations

fun List<StoryChannelDN>.toPresentation(): ImmutableList<StoryChannelPR> =
    map { it.toPresentation() }.toImmutableList()

/**
 * Adds everything the domain deliberately left out: the channel's colors and its icon.
 *
 * Both come from [StoryChannelDN.key] through [storyChannelLook], which falls back rather than
 * failing — a channel published after this build shipped renders in the default palette instead of
 * crashing the rail.
 */
fun StoryChannelDN.toPresentation(): StoryChannelPR {
    val look = storyChannelLook(key)
    return StoryChannelPR(
        key = key,
        name = name,
        shortName = shortName,
        time = time,
        palette = look.palette,
        icon = look.icon,
        items = items.map { it.toPresentation() }.toImmutableList(),
    )
}

private fun StoryItemDN.toPresentation() = StoryItemPR(
    id = id,
    title = title,
    body = body,
    media = media.toPresentation(),
    cta = cta?.toPresentation(),
    baseLikes = baseLikes,
)

/**
 * Resolves the domain's media to something the UI can load.
 *
 * The bundled variants become real URIs here, which is why the presentation model has no
 * equivalent of them: past this point a file in the app and a picture off a host are the same
 * thing. `Res.getUri` is an ordinary function, not a suspending one, so this stays a plain mapper
 * — and it is pure string work, so a path that names nothing produces a URI that simply fails to
 * load, which the viewer already handles as a media failure.
 */
private fun StoryMediaDN.toPresentation(): StoryMediaPR = when (this) {
    StoryMediaDN.None -> StoryMediaPR.None
    is StoryMediaDN.Image -> StoryMediaPR.Image(url)
    is StoryMediaDN.Video -> StoryMediaPR.Video(url)
    is StoryMediaDN.BundledImage -> StoryMediaPR.Image(Res.getUri(path))
    is StoryMediaDN.BundledVideo -> StoryMediaPR.Video(Res.getUri(path))
}

private fun StoryCtaDN.toPresentation() = StoryCtaPR(label = label, deepLink = deepLink)

/**
 * A channel's look, keyed by the identity the source gives it.
 *
 * One table rather than parallel lookups, for the reason
 * [com.tamin.taminhamrah.model.campaign.CampaignKind] gives: parallel tables drift the first time
 * one of them is edited. Values come straight from the design reference.
 */
private data class ChannelLook(
    val palette: StoryPalette,
    val icon: DrawableResource,
)

private fun storyChannelLook(key: String): ChannelLook = when (key) {
    "pr" -> ChannelLook(
        palette = StoryPalette(
            ringStart = Color(0xFF7FB4FF),
            ringEnd = Color(0xFF1F4FA3),
            avatarStart = Color(0xFF3B6FE8),
            avatarEnd = Color(0xFF1FB6D8),
            backdropStart = Color(0xFF0B2450),
            backdropMid = Color(0xFF123B77),
            backdropMidStop = 0.55f,
            backdropEnd = Color(0xFF0E5E84),
            ctaTone = Color(0xFF123B77),
            iconTint = Color(0xFFEAF1FF),
            iconTone = Color(0xFF1F4FA3),
        ),
        icon = Res.drawable.ic_story_public_relations,
    )

    "ins" -> ChannelLook(
        palette = StoryPalette(
            ringStart = Color(0xFF7BE3A8),
            ringEnd = Color(0xFF03794A),
            avatarStart = Color(0xFF0B8A57),
            avatarEnd = Color(0xFF3BC98D),
            backdropStart = Color(0xFF04321F),
            backdropMid = Color(0xFF0A6340),
            backdropMidStop = 0.55f,
            backdropEnd = Color(0xFF123B77),
            ctaTone = Color(0xFF0A6340),
            iconTint = Color(0xFFE6F7EE),
            iconTone = Color(0xFF03794A),
        ),
        icon = Res.drawable.ic_story_insured,
    )

    "ai" -> ChannelLook(
        palette = StoryPalette(
            ringStart = Color(0xFFB9A6FF),
            ringEnd = Color(0xFF7C5CFF),
            avatarStart = Color(0xFF7C5CFF),
            avatarEnd = Color(0xFF22B8D6),
            backdropStart = Color(0xFF241A5C),
            backdropMid = Color(0xFF3A2A8F),
            backdropMidStop = 0.52f,
            backdropEnd = Color(0xFF155E7C),
            ctaTone = Color(0xFF3A2A8F),
            iconTint = Color(0xFFF1ECFF),
            iconTone = Color(0xFF7C5CFF),
        ),
        icon = Res.drawable.ic_story_assistant,
    )

    "pen" -> ChannelLook(
        palette = StoryPalette(
            ringStart = Color(0xFF7FE7E0),
            ringEnd = Color(0xFF0E7C82),
            avatarStart = Color(0xFF0E7C82),
            avatarEnd = Color(0xFF5FD8D2),
            backdropStart = Color(0xFF06333A),
            backdropMid = Color(0xFF0E5F66),
            backdropMidStop = 0.55f,
            backdropEnd = Color(0xFF12405F),
            ctaTone = Color(0xFF0E5F66),
            iconTint = Color(0xFFE6F6F5),
            iconTone = Color(0xFF0E7C82),
        ),
        icon = Res.drawable.ic_story_pensioner,
    )

    "emp" -> ChannelLook(
        palette = StoryPalette(
            ringStart = Color(0xFFFFD48A),
            ringEnd = Color(0xFFC97E0A),
            avatarStart = Color(0xFFE7A33A),
            avatarEnd = Color(0xFFC97E0A),
            backdropStart = Color(0xFF4A2E06),
            backdropMid = Color(0xFF8A5A10),
            backdropMidStop = 0.52f,
            backdropEnd = Color(0xFF2F3E60),
            ctaTone = Color(0xFF8A5A10),
            iconTint = Color(0xFFFDF1DE),
            iconTone = Color(0xFFC97E0A),
        ),
        icon = Res.drawable.ic_story_employer,
    )

    // A channel this build has never heard of: it borrows the organisation's own blue and the
    // announcements icon. A story that renders plainly beats a rail that will not draw at all.
    else -> storyChannelLook(DEFAULT_CHANNEL_KEY)
}

private const val DEFAULT_CHANNEL_KEY = "pr"
