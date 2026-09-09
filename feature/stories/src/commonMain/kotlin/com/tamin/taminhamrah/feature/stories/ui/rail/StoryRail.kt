package com.tamin.taminhamrah.feature.stories.ui.rail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tamin.taminhamrah.feature.stories.model.StoryChannel
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailContent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailEvent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailIntent
import com.tamin.taminhamrah.feature.stories.ui.rail.contract.StoryRailUiState
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryDimens
import com.tamin.taminhamrah.feature.stories.ui.theme.StorySeenRing
import com.tamin.taminhamrah.feature.stories.ui.theme.StoryTextStyles
import com.tamin.taminhamrah.feature.stories.ui.theme.storyTextStyles
import com.tamin.taminhamrah.ui.collectWithLifecycleAware
import com.tamin.taminhamrah.ui.components.TaminText
import com.tamin.taminhamrah.ui.components.angledLinearGradient
import com.tamin.taminhamrah.ui.theme.LocalTaminColors
import com.tamin.taminhamrah.ui.theme.Spacing
import com.tamin.taminhamrah.ui.theme.shimmer
import com.tamin.taminhamrah.util.toPersianDigits
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import taminx.feature.stories.generated.resources.Res
import taminx.feature.stories.generated.resources.stories_channel_count
import taminx.feature.stories.generated.resources.stories_empty
import taminx.feature.stories.generated.resources.stories_error
import taminx.feature.stories.generated.resources.stories_retry
import taminx.feature.stories.generated.resources.stories_title

/** How many rings the shimmer stands in for, which is how many the catalogue has. */
private const val PLACEHOLDER_COUNT = 5

/**
 * The «تازه‌ها» strip: a scrolling row of channels above the campaigns carousel on the home page.
 *
 * Full-bleed by design. [horizontalPadding] is the distance from this composable's own edge to the
 * first ring, so a caller whose column already insets its children has to subtract that back off —
 * the same contract [com.tamin.taminhamrah.ui.components.CampaignCarousel] states, and for the
 * same reason: a row that scrolls has to be able to run a ring off the edge of the screen.
 *
 * Opening a channel is raised to the caller rather than navigated to from here, because the viewer
 * is a destination in the host graph.
 */
@Composable
fun StoryRail(
    onOpenViewer: (channelIndex: Int) -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = StoryDimens.railHorizontalPadding,
    viewModel: StoryRailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.events.collectWithLifecycleAware { event ->
        when (event) {
            is StoryRailEvent.OpenViewer -> onOpenViewer(event.channelIndex)
        }
    }

    StoryRailBody(
        state = state,
        onChannelClick = { index -> viewModel.sendIntent(StoryRailIntent.OpenChannel(index)) },
        onRetry = { viewModel.sendIntent(StoryRailIntent.Retry) },
        modifier = modifier,
        horizontalPadding = horizontalPadding,
    )
}

/**
 * The rail without its ViewModel, so every state it can be in is one call away in a preview.
 */
@Composable
internal fun StoryRailBody(
    state: StoryRailUiState,
    onChannelClick: (Int) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = StoryDimens.railHorizontalPadding,
) {
    val type = storyTextStyles()

    Column(modifier = modifier.fillMaxWidth()) {
        StoryRailHeader(
            channelCount = state.channels.size,
            type = type,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalPadding,
                    end = horizontalPadding,
                    bottom = StoryDimens.railHeaderBottomGap,
                ),
        )

        when (state.content) {
            StoryRailContent.Loading -> StoryRailPlaceholders(horizontalPadding)

            StoryRailContent.Channels -> LazyRow(
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = StoryDimens.railTrackVerticalPadding,
                ),
                horizontalArrangement = Arrangement.spacedBy(StoryDimens.railItemGap),
            ) {
                // Keyed on the channel rather than the position, so a reload that reorders the
                // list moves the rings instead of recycling them into each other.
                itemsIndexed(state.channels, key = { _, channel -> channel.key }) { index, channel ->
                    StoryRailItem(
                        channel = channel,
                        isSeen = state.isSeen(channel),
                        type = type,
                        onClick = { onChannelClick(index) },
                    )
                }
            }

            StoryRailContent.Empty -> StoryRailMessage(
                message = stringResource(Res.string.stories_empty),
                type = type,
                horizontalPadding = horizontalPadding,
            )

            StoryRailContent.Error -> StoryRailMessage(
                message = stringResource(Res.string.stories_error),
                type = type,
                horizontalPadding = horizontalPadding,
                actionLabel = stringResource(Res.string.stories_retry),
                onAction = onRetry,
            )
        }
    }
}

@Composable
private fun StoryRailHeader(
    channelCount: Int,
    type: StoryTextStyles,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = stringResource(Res.string.stories_title),
            style = type.sectionTitle,
            color = colors.textPrimary,
        )
        // The count is the hint's whole content, so it is dropped rather than shown as zero while
        // the list is still on its way or has failed.
        if (channelCount > 0) {
            TaminText(
                text = stringResource(
                    Res.string.stories_channel_count,
                    channelCount.toString().toPersianDigits(),
                ),
                style = type.sectionHint,
                color = colors.textMuted,
            )
        }
    }
}

/**
 * One channel: a gradient ring around a tinted disc, with the publisher's name under it.
 *
 * A watched channel loses the gradient for [StorySeenRing] and its label drops to the muted ink —
 * the same two-part signal the design uses, so the difference reads at a glance without relying on
 * color alone being noticed.
 */
@Composable
private fun StoryRailItem(
    channel: StoryChannel,
    isSeen: Boolean,
    type: StoryTextStyles,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalTaminColors.current
    val palette = channel.palette
    // Shared on purpose: the whole column is the target — the label opens the channel too — but
    // the press is drawn on the ring, so the ripple is the circle the reader sees rather than the
    // column's rectangle around it.
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .width(StoryDimens.railItemWidth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClickLabel = channel.shortName,
                onClick = onClick,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(StoryDimens.ringSize)
                .clip(CircleShape)
                .indication(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = true,
                        color = if (isSeen) colors.chevron else palette.iconTone,
                    ),
                )
                .drawWithCache {
                    // Rebuilt only when the ring changes size, so scrolling the row costs a
                    // redraw and nothing else.
                    val brush = if (isSeen) {
                        null
                    } else {
                        angledLinearGradient(
                            angleDeg = StoryDimens.RING_GRADIENT_ANGLE_DEG,
                            stops = listOf(0f to palette.ringStart, 1f to palette.ringEnd),
                            width = size.width,
                            height = size.height,
                        )
                    }
                    onDrawBehind {
                        if (brush != null) drawCircle(brush) else drawCircle(StorySeenRing)
                    }
                }
                .padding(StoryDimens.ringThickness),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    // The band separating the ring from the icon is the page showing through, so
                    // it takes the page color rather than a fixed white.
                    .drawBehind { drawCircle(colors.bgPage) }
                    .padding(StoryDimens.ringGap)
                    .clip(CircleShape)
                    .background(if (isSeen) colors.bgPage else palette.iconTint),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(channel.icon),
                    contentDescription = null,
                    modifier = Modifier.size(StoryDimens.ringIconSize),
                    alpha = if (isSeen) SEEN_ICON_ALPHA else 1f,
                )
            }
        }

        TaminText(
            text = channel.shortName,
            modifier = Modifier.padding(top = StoryDimens.railLabelTopGap),
            style = type.railLabel,
            color = if (isSeen) colors.textMuted else colors.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}

/** A watched channel's icon recedes with its ring rather than staying at full strength. */
private const val SEEN_ICON_ALPHA = 0.55f

/**
 * What stands in for the rings while the catalogue is on its way.
 *
 * Same count, same size and same spacing as the real thing, so the section does not resize under
 * the reader when the list lands.
 */
@Composable
private fun StoryRailPlaceholders(horizontalPadding: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = horizontalPadding,
                vertical = StoryDimens.railTrackVerticalPadding,
            ),
        horizontalArrangement = Arrangement.spacedBy(StoryDimens.railItemGap),
    ) {
        repeat(PLACEHOLDER_COUNT) {
            Box(
                modifier = Modifier
                    .size(StoryDimens.ringSize)
                    .clip(CircleShape)
                    .shimmer(),
            )
        }
    }
}

/**
 * The empty and failed states.
 *
 * A line rather than the app's full-page [com.tamin.taminhamrah.ui.components.EmptyStateMessage] or
 * the [com.tamin.taminhamrah.ui.components.ErrorStateView] dialog: this is one strip on a page full
 * of working sections, and neither an illustration nor a modal is a proportionate answer to news
 * being unavailable. It keeps the section's height roughly steady either way.
 */
@Composable
private fun StoryRailMessage(
    message: String,
    type: StoryTextStyles,
    horizontalPadding: Dp,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val colors = LocalTaminColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TaminText(
            text = message,
            style = type.sectionHint,
            color = colors.textMuted,
        )
        if (actionLabel != null && onAction != null) {
            TaminText(
                text = actionLabel,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onAction)
                    .padding(horizontal = Spacing.sm, vertical = Spacing.xs),
                style = type.sectionHint,
                color = colors.blueText,
            )
        }
    }
}
