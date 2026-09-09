package com.tamin.taminhamrah.feature.stories.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tamin.taminhamrah.ui.theme.DEFAULT_FONT_FEATURES
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.applicationFont

/**
 * Geometry for the «تازه‌ها» rail and the story viewer.
 *
 * The design is a CSS layout measured at a 391px viewport, and those pixels map 1:1 to dp — the
 * same convention [com.tamin.taminhamrah.ui.theme.CampaignDimens] follows for the carousel sitting
 * directly beneath this rail. Values are reproduced rather than normalized, so a number that looks
 * arbitrary (2.5, 34%, 6.2s) is the design's and not a rounding of a token.
 *
 * Anything with an equivalent in the shared scale uses the shared scale instead; only what the
 * design invents for this section lives here.
 */
object StoryDimens {

    /* ---- The rail ------------------------------------------------------------------------ */

    /** margin 0 18px 11px on the heading row — the section's own inset from the screen edge. */
    val railHorizontalPadding = Spacing.page
    val railHeaderBottomGap = 11.dp
    val railTrackVerticalPadding = Spacing.xxs
    val railItemGap = 13.dp

    val railItemWidth = 66.dp
    val railLabelTopGap = 7.dp

    /** The outer gradient disc. Its two nested paddings draw the white band separating them. */
    val ringSize = 64.dp
    val ringThickness = 2.5.dp
    val ringGap = 2.5.dp
    val ringIconSize = 27.dp

    /* ---- The viewer ---------------------------------------------------------------------- */

    /** top 86px — where the two tap columns begin, clear of the progress bar and the header. */
    val viewerTapZoneTop = 86.dp

    /**
     * width 34% / 66%. The narrow column steps back, the wide one steps forward — which under a
     * right-to-left page puts the wide half on the left, exactly as the requirement asks.
     */
    const val VIEWER_PREVIOUS_ZONE_FRACTION = 0.34f

    /** top -90px, left -70px, 320x320 — the corner light, resolved to a center and a radius. */
    val viewerGlowCenterX = 90.dp
    val viewerGlowCenterY = 70.dp
    val viewerGlowRadius = 160.dp

    /** Where the glow has faded out completely, as a fraction of [viewerGlowRadius]. */
    const val VIEWER_GLOW_FADE_STOP = 0.68f

    /** The scrim keeping the copy legible over whatever the media happens to be. */
    val viewerScrimHeight = 340.dp
    val viewerScrimAlpha = 0.72f

    val progressPaddingHorizontal = 14.dp
    val progressPaddingTop = Spacing.lg
    val progressGap = Spacing.xs
    val progressHeight = 3.dp

    val headerPaddingHorizontal = 15.dp
    val headerPaddingTop = 13.dp
    val headerGap = Spacing.smPlus
    val headerAvatarSize = 34.dp
    val headerAvatarRingWidth = Spacing.xxs
    val headerAvatarDotSize = Spacing.smPlus
    val headerTimeTopGap = Spacing.xxs
    val closeButtonSize = 34.dp
    val closeIconSize = Spacing.lg

    val contentPaddingHorizontal = Spacing.xlg
    val contentPaddingBottom = 30.dp
    val kickerPaddingHorizontal = 11.dp
    val kickerPaddingVertical = Spacing.xs
    val titleTopGap = 11.dp
    val bodyTopGap = 9.dp

    val ctaTopGap = Spacing.lg
    val ctaHeight = 48.dp
    val ctaGap = 7.dp
    val ctaChevronSize = 15.dp
    val ctaShadowBlur = 26.dp
    val ctaShadowOffsetY = Spacing.smPlus

    val actionsTopGap = 15.dp
    val actionsGap = Spacing.sm
    val actionHeight = 44.dp
    val actionPaddingHorizontal = 12.dp
    val commentPaddingHorizontal = 14.dp
    val commentIconSize = Spacing.lg
    val actionIconSize = 19.dp
    val actionInnerGap = 6.dp

    /**
     * The send button inside the comment field.
     *
     * Not from the design — its comment pill is a display-only stub with nothing to send. Sized to
     * the pill's inner height so the tap target is reachable without the icon growing.
     */
    val commentSendSize = 32.dp

    /** linear-gradient(168deg) and linear-gradient(140deg), in CSS degrees. */
    const val VIEWER_GRADIENT_ANGLE_DEG = 168f
    const val RING_GRADIENT_ANGLE_DEG = 140f
}

/**
 * How long a slide without a clip stays up: the design runs its fill over 6.2 seconds.
 *
 * Also the fallback for a clip whose duration never arrives, so a broken video cannot strand the
 * viewer on one slide forever.
 */
const val STORY_DEFAULT_DURATION_MS: Long = 6_200L

/**
 * How often the segment clock accumulates while a slide plays.
 *
 * Ticking rather than waiting the whole duration out in one delay is what lets a pause report how
 * far the slide already got. Nothing observes a tick — the elapsed figure is a plain field the
 * ViewModel publishes only when the user actually pauses — so 50ms costs no recomposition, and the
 * progress bar animates independently at the display's own rate.
 */
internal const val STORY_TICK_MS: Long = 50L

/**
 * One channel's colors: the ring on the rail, the avatar in the viewer header, the full-bleed
 * gradient behind a slide, and the tone its white call-to-action pill prints in.
 *
 * Held as a single table per channel rather than as parallel lookups, for the reason
 * [com.tamin.taminhamrah.model.campaign.CampaignKind] gives: parallel tables drift the first time
 * one of them is edited.
 */
@Immutable
data class StoryPalette(
    val ringStart: Color,
    val ringEnd: Color,
    val avatarStart: Color,
    val avatarEnd: Color,
    val backdropStart: Color,
    val backdropMid: Color,
    /** Where [backdropMid] sits along the gradient line. The design sets it per channel. */
    val backdropMidStop: Float,
    val backdropEnd: Color,
    /** What the white CTA pill prints in — the channel's own dark tone. */
    val ctaTone: Color,
    /** The soft disc the rail icon sits on, inside the ring. */
    val iconTint: Color,
    val iconTone: Color,
)

/** A channel whose stories have all been watched loses its ring color, as on the design. */
val StorySeenRing = Color(0xFFDCE3EC)

/* The viewer chrome is drawn on the channel gradient, so its ink is fixed rather than themed. */
val StoryOnBackdrop = Color(0xFFFFFFFF)
val StoryBodyInk = Color(0xFFFFFFFF).copy(alpha = 0.82f)
val StoryTimeInk = Color(0xFFFFFFFF).copy(alpha = 0.60f)
val StoryHintInk = Color(0xFFFFFFFF).copy(alpha = 0.74f)
val StoryGlassFill = Color(0xFFFFFFFF).copy(alpha = 0.15f)
val StoryGlassBorder = Color(0xFFFFFFFF).copy(alpha = 0.28f)
val StoryKickerFill = Color(0xFFFFFFFF).copy(alpha = 0.16f)
val StoryKickerBorder = Color(0xFFFFFFFF).copy(alpha = 0.24f)
val StoryCloseFill = Color(0xFFFFFFFF).copy(alpha = 0.14f)
val StoryCloseBorder = Color(0xFFFFFFFF).copy(alpha = 0.22f)
val StoryAvatarRing = Color(0xFFFFFFFF).copy(alpha = 0.35f)
val StoryAvatarDot = Color(0xFFFFFFFF).copy(alpha = 0.90f)
val StoryProgressTrack = Color(0xFFFFFFFF).copy(alpha = 0.28f)
val StoryGlowCore = Color(0xFFFFFFFF).copy(alpha = 0.22f)
val StoryScrimInk = Color(0xFF040C1C)
val StoryCtaShadow = Color(0xFF000000).copy(alpha = 0.28f)
val StoryLikeActive = Color(0xFFFF3B5C)

/**
 * Type for the rail and the viewer.
 *
 * A table for the reason campaignTextStyles gives: none of the design's sizes — 9.5 / 11.5 / 12.5
 * / 13 / 26 — has a role in the app typography, and leaving the rest to LocalTextStyle would drag
 * Material3's bodyLarge line height onto a 9.5sp label.
 *
 * 700 and 800 are synthesized here as they are everywhere else in this app: the bundled font
 * registers Light through SemiBold only.
 */
@Immutable
data class StoryTextStyles(
    val sectionTitle: TextStyle,
    val sectionHint: TextStyle,
    val railLabel: TextStyle,
    val channelName: TextStyle,
    val channelTime: TextStyle,
    val kicker: TextStyle,
    val slideTitle: TextStyle,
    val slideBody: TextStyle,
    val ctaLabel: TextStyle,
    val actionHint: TextStyle,
    val likeCount: TextStyle,
)

@Composable
fun storyTextStyles(): StoryTextStyles {
    val fontFamily = applicationFont()
    return remember(fontFamily) { storyTextStyles(fontFamily) }
}

private fun storyTextStyles(fontFamily: FontFamily) = StoryTextStyles(
    sectionTitle = storyStyle(fontFamily, 16.sp, 19.sp, weight = 700),
    sectionHint = storyStyle(fontFamily, 11.5.sp, 14.sp, weight = 600),
    // line-height: 1.4
    railLabel = storyStyle(fontFamily, 9.5.sp, 13.3.sp, weight = 700),
    channelName = storyStyle(fontFamily, 12.5.sp, 15.sp, weight = 800),
    channelTime = storyStyle(fontFamily, 9.5.sp, 12.sp, weight = 600),
    kicker = storyStyle(fontFamily, 9.5.sp, 12.sp, weight = 800),
    // line-height: 1.35
    slideTitle = storyStyle(
        fontFamily,
        fontSize = 26.sp,
        lineHeight = 35.1.sp,
        weight = 800,
        letterSpacing = (-0.3).sp,
    ),
    // line-height: 2
    slideBody = storyStyle(fontFamily, 13.sp, 26.sp, weight = 500),
    ctaLabel = storyStyle(fontFamily, 12.5.sp, 15.sp, weight = 800),
    actionHint = storyStyle(fontFamily, 11.sp, 14.sp, weight = 600),
    likeCount = storyStyle(fontFamily, 11.sp, 14.sp, weight = 800),
)

private fun storyStyle(
    fontFamily: FontFamily,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    weight: Int,
    letterSpacing: TextUnit = 0.sp,
) = TextStyle(
    fontFamily = fontFamily,
    fontFeatureSettings = DEFAULT_FONT_FEATURES,
    fontWeight = FontWeight(weight),
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = letterSpacing,
)
