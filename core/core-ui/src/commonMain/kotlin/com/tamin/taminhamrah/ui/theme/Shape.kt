package com.tamin.taminhamrah.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TaminHamrahShapes = Shapes(
    small = RoundedCornerShape(CornerRadius.sm),
    medium = RoundedCornerShape(CornerRadius.md),
    large = RoundedCornerShape(CornerRadius.lg),
    extraLarge = RoundedCornerShape(CornerRadius.chip),
)

object ListShapes {
    val cornerSize = CornerRadius.md
    val top = RoundedCornerShape(topStart = cornerSize, topEnd = cornerSize)
    val middle = RoundedCornerShape(0.dp)
    val bottom = RoundedCornerShape(bottomStart = cornerSize, bottomEnd = cornerSize)
    val single = RoundedCornerShape(cornerSize)
}

object Spacing {
    val none = 0.dp
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val smPlus = 10.dp
    val md = 12.dp
    val lg = 16.dp
    val xlg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val xxxxl = 48.dp
    val xxxxxl = 64.dp
    val xxxxxxl = 80.dp
    val xxxxxxxl = 96.dp

    val smd = 14.dp
    val page = 18.dp
    val cardGap = 11.dp
    val tabSelector = 6.dp
    val badgeVertical = 5.dp
    val highlightHeight = 36.5.dp
}

object CornerRadius {
    val none = 0.dp
    val xs = 2.dp
    val sm = 4.dp
    val md = 8.dp
    val lg = 12.dp
    val xl = 16.dp
    val xlg = 17.dp
    val x2l = 24.dp
    val x3l = 40.dp
    val full = 9999.dp

    val avatarTile = 10.dp
    val listRow = 13.dp
    val chip = 14.dp
    val iconTile = 18.dp
    val cardCompact = 20.dp
    val card = 22.dp
    val sheet = 28.dp
    val max = 100.dp
    val textFieldIcon = 11.dp
}

object Elevation {
    val none = 0.dp
    val xxs = 1.dp
    val xs = 2.dp
    val sm = 4.dp
    val smPlus = 5.dp
    val md = 6.dp
    val lg = 12.dp
    val xl = 24.dp
    val xxl = 30.dp

    val button = 8.dp
}

object ButtonDimens {
    val height = 56.dp
    val loadingIndicatorSize = 18.dp
    val loadingIndicatorStroke = 2.dp
}

object TextDimens {
    val phoneLetterSpacing = 1.5.sp
    val stepperTitle = 10.5.sp
    val stepperNumber = 13.sp
}

object IconSize {
    val statIcon = 10.dp
    val small = 16.dp
    val badgeInner = 17.dp
    val banner = 20.dp
    val medium = 24.dp
    val badge = 32.dp
    val large = 38.dp
    val largePlus = 42.dp
    val xlarge = 48.dp
    val xxlarge = 56.dp
    val xxxlarge = 80.dp
    val tile = 58.dp
    val tileInner = 28.dp
    val textFieldIconContainer = 34.dp
    val headerIconOuter = 104.dp
    val headerIconInner = 78.dp
    val navBar = 24.dp
    val stepperCircle = 30.dp
    val stepperConnectorHeight = 2.dp
    val stepperConnectorWidth = 44.dp
    /** Segment height for [com.tamin.taminhamrah.ui.components.TaminHeroStepProgress] on hero headers. */
    val heroStepSegmentHeight = 4.dp
    /** Visual box for [com.tamin.taminhamrah.ui.components.TaminCheckBox]. */
    val checkbox = 20.dp
    val checkboxCheck = 14.dp
}

object Thickness {
    val border = 1.dp
    val medium = 2.dp
    val chartBar = 3.dp
}

/** Decorative wash behind [com.tamin.taminhamrah.ui.components.TaminTopAppBar] hero content. */
object HeaderDecoration {
    val circleSize = 190.dp
    val circleXOffset = 450.dp
    val circleYOffset = (-150).dp
}

object ChartDimens {
    val barChartHeight = 180.dp
    val barWidth = 32.dp
    val barGap = 6.dp
    val barCorner = 10.dp
    val legendDot = 8.dp
    val yAxisLabelWidth = 40.dp
}

/**
 * Placeholder sizes for a value that has not arrived, so a shimmering figure occupies roughly what
 * the real one will and nothing resizes when it lands.
 */
object ShimmerSize {
    val valueWidth = 56.dp
    val valueHeight = 14.dp
    val labelWidth = 72.dp
    val sectionLabelWidth = 48.dp
    val hintWidth = 120.dp
    val titleWidth = 160.dp
    val chipWidth = 64.dp
    val infoBodyHeight = 80.dp
    val copyRowHeight = 36.dp
    val sonCardHeight = 72.dp
    val fieldHeight = ButtonDimens.height
    val helperLineWidth = 200.dp
    val titleHeight = 14.dp
    val subtitleWidth = 180.dp
    val subtitleHeight = 12.dp
    val badgeWidth = 56.dp
    val badgeHeight = 24.dp
    val uploadCardHeight = 120.dp
    val bannerHeight = 56.dp
    val rateChipHeight = 48.dp
    val wageValueHeight = 28.dp
    val sliderTrackHeight = 4.dp
    val stepperButtonSize = 40.dp
}

/** Scrollable list area inside modal option sheets (city / branch pickers). */
object SheetDimens {
    val listMaxHeight = 300.dp
}

/**
 * Geometry for the home campaigns carousel
 * ([com.tamin.taminhamrah.ui.components.CampaignCarousel]).
 *
 * The design is a CSS scroll-snap track whose lengths are pixels at a 391px viewport, and they map
 * 1:1 to dp. Verified against the rendered markup: card 329×154.8 resting 18 from the trailing
 * edge, its neighbor peeking 34, body copy 92%/82% of the 299 content width, dots 18×5.
 *
 * Type lives in [taminx.core.core_ui] `Type.kt` as `campaignTextStyles()`, not here.
 */
object CampaignDimens {
    /** `width: calc(100% - 26px)` — how much narrower each card is than the track holding it. */
    val cardNarrowing = 26.dp
    val trackGap = Spacing.smPlus            // gap: 10px
    val trackVerticalPadding = Spacing.xxs   // padding: 2px 18px
    val headerBottomGap = 11.dp

    val cardMinHeight = 146.dp
    val cardShadowBlur = 26.dp               // box-shadow: 0 12px 26px
    val cardShadowOffsetY = 12.dp
    val cardPaddingHorizontal = 15.dp        // padding: 13px 15px
    val cardPaddingVertical = 13.dp
    val cardContentGap = Spacing.smPlus

    val badgePaddingHorizontal = 9.dp        // padding: 3px 9px
    val badgePaddingVertical = 3.dp
    val titleTopGap = Spacing.sm             // margin-top: 8px
    val bodyTopGap = 5.dp

    val ctaPaddingHorizontal = 13.dp         // padding: 7px 13px
    val ctaPaddingVertical = 7.dp
    val ctaInnerGap = 6.dp
    val ctaChevronSize = 13.dp

    val dotsTopGap = Spacing.smPlus
    val dotGap = 5.dp
    val dotSize = 5.dp
    val dotActiveWidth = 18.dp

    // The two aria-hidden decoration circles every card carries, resolved from the design's
    // negative offsets to a center and a radius.

    /** `top:-46px; left:-30px; 170×170` — the soft corner glow. */
    val glowCenterX = 55.dp
    val glowCenterY = 39.dp
    val glowRadius = 85.dp

    /** Where the glow has faded out completely, as a fraction of [glowRadius]. */
    const val glowFadeStop = 0.68f

    /** `bottom:-58px; left:38%; 150×150` — the flat bubble under the footer row. */
    const val bubbleCenterXFraction = 0.38f
    val bubbleCenterYFromBottom = 17.dp
    val bubbleRadius = 75.dp

    /** `linear-gradient(150deg, …)`, in CSS degrees: 0 points to the top and turns clockwise. */
    const val gradientAngleDeg = 150f
}
