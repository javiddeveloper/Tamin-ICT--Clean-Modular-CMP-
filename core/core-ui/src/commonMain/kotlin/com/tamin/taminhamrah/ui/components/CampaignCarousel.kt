package com.tamin.taminhamrah.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.geometry.CornerRadius as DrawCornerRadius
import com.tamin.taminhamrah.mapper.campaign.toPresentation
import com.tamin.taminhamrah.model.campaign.CampaignKind
import com.tamin.taminhamrah.model.campaign.CampaignPR
import com.tamin.taminhamrah.model.common.FeatureFlag
import com.tamin.taminhamrah.ui.PreviewRtlTheme
import com.tamin.taminhamrah.ui.PreviewRtlThemeContent
import com.tamin.taminhamrah.ui.theme.CampaignBadgeBorder
import com.tamin.taminhamrah.ui.theme.CampaignBadgeFill
import com.tamin.taminhamrah.ui.theme.CampaignBodyInk
import com.tamin.taminhamrah.ui.theme.CampaignBubbleFill
import com.tamin.taminhamrah.ui.theme.CampaignCaptionInk
import com.tamin.taminhamrah.ui.theme.CampaignDimens
import com.tamin.taminhamrah.ui.theme.CampaignGlowCore
import com.tamin.taminhamrah.ui.theme.CampaignTextStyles
import com.tamin.taminhamrah.ui.theme.CornerRadius
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.Thickness
import com.tamin.taminhamrah.ui.theme.campaignTextStyles
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import taminx.core.core_ui.Res
import taminx.core.core_ui.campaigns_swipe_hint
import taminx.core.core_ui.campaigns_title
import taminx.core.core_ui.ic_tamin_chevron_forward

private val CardShape = RoundedCornerShape(CornerRadius.card)
private val PillShape = RoundedCornerShape(CornerRadius.max)

/**
 * The home page's «کمپین‌ها» slider: a snapping row of promo cards under a heading, with page dots
 * beneath. The neighboring card peeks at the trailing edge, which is what the "swipe to see" hint
 * is pointing at.
 *
 * Geometry comes from [CampaignDimens] and type from `campaignTextStyles()`; the per-card palette
 * and destination come from [CampaignKind]. Nothing here is a literal.
 *
 * [horizontalPadding] is the distance from this composable's own edge to the resting card. The
 * design puts the card 18 from the screen edge, so a caller that already pads its column has to
 * subtract that padding back off — the carousel deliberately does not assume it owns the full
 * width.
 *
 * Renders nothing when [campaigns] is empty; a campaign whose service the server has switched off
 * is filtered out before it gets here, so an empty list means the section has nothing to say.
 */
@Composable
fun CampaignCarousel(
    campaigns: ImmutableList<CampaignPR>,
    onCampaignClick: (FeatureFlag) -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = Spacing.page,
) {
    if (campaigns.isEmpty()) return

    val pageCount = campaigns.size
    val pagerState = rememberPagerState(pageCount = { pageCount })
    // Remembers which cards have already entered, so a page the pager disposes and recomposes on
    // scroll reappears instantly instead of playing its entrance a second time.
    val entranceState = rememberStaggeredEntranceState(key = pageCount)
    // Resolved once for the section rather than per card: the styles are the same for every card,
    // and the cards are what recompose. [CampaignTextStyles] is stable, so handing it down costs
    // the card nothing.
    val type = campaignTextStyles()

    Column(modifier = modifier.fillMaxWidth()) {
        CampaignHeader(
            type = type,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = CampaignDimens.headerBottomGap,
                ),
        )

        HorizontalPager(
            state = pagerState,
            key = { page -> campaigns[page].kind },
            contentPadding = PaddingValues(
                start = horizontalPadding,
                end = horizontalPadding + CampaignDimens.cardNarrowing,
            ),
            pageSpacing = CampaignDimens.trackGap,
            // The design stretches every card to the tallest one. A pager measures pages
            // independently, so top-alignment is the closest match if a translation ever makes one
            // card taller than the rest.
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(vertical = CampaignDimens.trackVerticalPadding),
        ) { page ->
            val campaign = campaigns[page]
            CampaignCard(
                campaign = campaign,
                type = type,
                onClick = onCampaignClick,
                // Applied here rather than passed in: StaggeredEntranceState holds a mutable set
                // and reads as unstable, so handing it to the card would cost the card its ability
                // to skip. A Modifier does not.
                modifier = Modifier.staggeredItemEntrance(
                    index = page,
                    key = campaign.kind,
                    state = entranceState,
                ),
            )
        }

        if (pageCount > 1) {
            CampaignDots(
                pagerState = pagerState,
                pageCount = pageCount,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = CampaignDimens.dotsTopGap),
            )
        }
    }
}

@Composable
private fun CampaignHeader(
    type: CampaignTextStyles,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = stringResource(Res.string.campaigns_title),
            style = type.sectionTitle,
            color = colors.textPrimary,
        )
        TaminText(
            text = stringResource(Res.string.campaigns_swipe_hint),
            style = type.sectionHint,
            color = colors.textMuted,
        )
    }
}

@Composable
private fun CampaignCard(
    campaign: CampaignPR,
    type: CampaignTextStyles,
    onClick: (FeatureFlag) -> Unit,
    modifier: Modifier = Modifier,
) {
    val kind = campaign.kind
    val flag = kind.flag
    val onCard = LocalTaminColors.current.onGradient

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = CampaignDimens.cardMinHeight)
            .campaignSurface(kind)
            .clickable { onClick(flag) }
            .padding(
                horizontal = CampaignDimens.cardPaddingHorizontal,
                vertical = CampaignDimens.cardPaddingVertical,
            ),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            TaminText(
                text = campaign.badge,
                modifier = Modifier
                    .clip(PillShape)
                    .background(CampaignBadgeFill)
                    .border(Thickness.border, CampaignBadgeBorder, PillShape)
                    .padding(
                        horizontal = CampaignDimens.badgePaddingHorizontal,
                        vertical = CampaignDimens.badgePaddingVertical,
                    ),
                style = type.badge,
                color = onCard,
            )
            TaminText(
                text = campaign.title,
                modifier = Modifier.padding(top = CampaignDimens.titleTopGap),
                style = type.cardTitle,
                color = onCard,
            )
            TaminText(
                text = campaign.body,
                modifier = Modifier
                    .padding(top = CampaignDimens.bodyTopGap)
                    .fillMaxWidth(kind.bodyWidthFraction),
                style = type.body,
                color = CampaignBodyInk,
            )
        }

        // `gap:10px` alongside `justify-content:space-between`: the gap is a floor, not the
        // separation. Without it the card collapses to its 146 minimum and the footer sits a
        // hair under the body — the design's card is 154.8 precisely because of these 10.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = CampaignDimens.cardContentGap),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .clip(PillShape)
                    .background(onCard)
                    .padding(
                        horizontal = CampaignDimens.ctaPaddingHorizontal,
                        vertical = CampaignDimens.ctaPaddingVertical,
                    ),
                horizontalArrangement = Arrangement.spacedBy(CampaignDimens.ctaInnerGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TaminText(
                    text = campaign.ctaLabel,
                    style = type.ctaLabel,
                    color = kind.ctaTone,
                )
                // The mock's chevron points left, which under RTL is the open/next affordance.
                // `ic_tamin_chevron_forward` is auto-mirrored, so it is the one that draws that way.
                Icon(
                    imageVector = vectorResource(Res.drawable.ic_tamin_chevron_forward),
                    contentDescription = null,
                    tint = kind.ctaTone,
                    modifier = Modifier.size(CampaignDimens.ctaChevronSize),
                )
            }
            TaminText(
                text = campaign.caption,
                style = type.caption,
                color = CampaignCaptionInk,
            )
        }
    }
}

/**
 * Takes the [pagerState] rather than the current page, and reads it **in the draw phase**.
 *
 * A swipe therefore costs this strip one redraw: no recomposition, and no relayout either. Reading
 * `currentPage` during composition — here or, worse, in the carousel above — would recompose on
 * every page change, and a row of per-dot `Box`es would then re-measure because the active dot is
 * a different width from the rest. The strip's own width never changes: exactly one dot is wide,
 * whichever it is.
 *
 * The width snaps rather than interpolating, which is what the design does — `cpIdx` is
 * `Math.round(scrollLeft / step)`, so the active dot changes at the halfway point and never
 * part-way.
 */
@Composable
private fun CampaignDots(
    pagerState: PagerState,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val activeColor = colors.blueText
    val idleColor = colors.campaignDotIdle
    val stripWidth = CampaignDimens.dotActiveWidth +
        (CampaignDimens.dotSize + CampaignDimens.dotGap) * (pageCount - 1)

    Canvas(
        modifier = modifier.size(width = stripWidth, height = CampaignDimens.dotSize),
    ) {
        val gap = CampaignDimens.dotGap.toPx()
        val idleWidth = CampaignDimens.dotSize.toPx()
        val activeWidth = CampaignDimens.dotActiveWidth.toPx()
        val radius = DrawCornerRadius(size.height / 2f)
        val rtl = layoutDirection == LayoutDirection.Rtl
        val current = pagerState.currentPage

        var offset = 0f
        repeat(pageCount) { page ->
            val selected = page == current
            val width = if (selected) activeWidth else idleWidth
            // The first dot belongs to the first page, which under RTL is the rightmost one.
            val left = if (rtl) size.width - offset - width else offset
            drawRoundRect(
                color = if (selected) activeColor else idleColor,
                topLeft = Offset(left, 0f),
                size = Size(width, size.height),
                cornerRadius = radius,
            )
            offset += width + gap
        }
    }
}

/**
 * Everything a campaign card is made of below its content: the shadow it casts, the rounded clip,
 * its gradient, and the two decoration circles the design layers on top.
 *
 * One `drawWithCache` rather than a background plus two child `Box`es — the brushes are rebuilt
 * only when the card's size changes, so a redraw costs no recomposition and the decoration costs no
 * layout nodes at all.
 *
 * Both circles are pinned to the card's *physical* left edge (`left:-30px`, `left:38%`), which is
 * why they are drawn at explicit coordinates instead of positioned with a modifier: `DrawScope`
 * coordinates never mirror, and that is exactly what an RTL page needs here.
 */
private fun Modifier.campaignSurface(kind: CampaignKind): Modifier = this
    .coloredShadow(
        color = kind.shadowTint,
        borderRadius = CornerRadius.card,
        blurRadius = CampaignDimens.cardShadowBlur,
        offsetY = CampaignDimens.cardShadowOffsetY,
    )
    .clip(CardShape)
    .drawWithCache {
        val gradient = angledLinearGradient(
            angleDeg = CampaignDimens.gradientAngleDeg,
            stops = listOf(
                0f to kind.gradientStart,
                kind.gradientMidStop to kind.gradientMid,
                1f to kind.gradientEnd,
            ),
            width = size.width,
            height = size.height,
        )
        val glowCenter = Offset(
            CampaignDimens.glowCenterX.toPx(),
            CampaignDimens.glowCenterY.toPx(),
        )
        val glowRadius = CampaignDimens.glowRadius.toPx()
        val glow = Brush.radialGradient(
            0f to CampaignGlowCore,
            CampaignDimens.glowFadeStop to Color.Transparent,
            center = glowCenter,
            radius = glowRadius,
        )
        val bubbleRadius = CampaignDimens.bubbleRadius.toPx()
        val bubbleCenter = Offset(
            x = size.width * CampaignDimens.bubbleCenterXFraction + bubbleRadius,
            y = size.height - CampaignDimens.bubbleCenterYFromBottom.toPx(),
        )
        onDrawBehind {
            drawRect(brush = gradient)
            drawCircle(brush = glow, radius = glowRadius, center = glowCenter)
            drawCircle(color = CampaignBubbleFill, radius = bubbleRadius, center = bubbleCenter)
        }
    }

/* ---- Previews -------------------------------------------------------------------------------- */
// The copy comes through the real mapper, so a preview that still looks right is evidence the
// strings and the palette table line up — not just that the layout does.

@Composable
private fun CampaignCarouselPreviewBody(kinds: ImmutableList<CampaignKind>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(LocalTaminColors.current.bgPage)
            .padding(vertical = Spacing.xlg),
    ) {
        CampaignCarousel(
            campaigns = kinds.toPresentation(),
            onCampaignClick = {},
        )
    }
}

@PreviewRtlTheme
@Composable
private fun CampaignCarouselLightPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        CampaignCarouselPreviewBody(AllCampaignKinds)
    }
}

@PreviewRtlTheme
@Composable
private fun CampaignCarouselDarkPreview() {
    PreviewRtlThemeContent(darkTheme = true) {
        CampaignCarouselPreviewBody(AllCampaignKinds)
    }
}

/** A server that has switched two of the three services off leaves a single card and no dots. */
@PreviewRtlTheme
@Composable
private fun CampaignCarouselSingleCardPreview() {
    PreviewRtlThemeContent(darkTheme = false) {
        CampaignCarouselPreviewBody(persistentListOf(CampaignKind.STUDENT))
    }
}

private val AllCampaignKinds = CampaignKind.entries.toPersistentList()
